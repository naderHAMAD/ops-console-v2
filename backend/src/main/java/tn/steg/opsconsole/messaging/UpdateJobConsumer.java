package tn.steg.opsconsole.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tn.steg.opsconsole.config.RabbitMQConfig;
import tn.steg.opsconsole.domain.UpdateJob;
import tn.steg.opsconsole.repository.UpdateJobRepository;
import tn.steg.opsconsole.service.AwxClientService;
import tn.steg.opsconsole.websocket.JobLogWebSocketHandler;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateJobConsumer {

    private final UpdateJobRepository updateJobRepository;
    private final AwxClientService awxClientService;
    private final JobLogWebSocketHandler webSocketHandler;

    @Value("${app.awx.job-template.update-jboss}")
    private long updateJobTemplateId;

    @RabbitListener(queues = RabbitMQConfig.UPDATE_QUEUE)
    public void handle(JobMessage message) {
        UpdateJob job = updateJobRepository.findByIdWithDetails(message.jobId())
                .orElseThrow(() -> new IllegalStateException("Job introuvable: " + message.jobId()));

        job.setStatus("running");
        job.setStartedAt(Instant.now());
        updateJobRepository.save(job);
        webSocketHandler.broadcastJobUpdate(job.getId(), "running", "Démarrage du playbook...");

        try {
            Long awxJobId = awxClientService.launchJobTemplate(updateJobTemplateId, Map.of(
                    "target_hostname", job.getServer().getHostname(),
                    "target_version", job.getTargetVersion()
            )).block(); // appel bloquant simple ; passer en réactif pur si volume élevé

            job.setAwxJobId(String.valueOf(awxJobId));
            updateJobRepository.save(job);

            // Poll simplifié — en prod, préférer les callbacks AWX (notification templates)
            pollUntilFinished(job, awxJobId);

        } catch (Exception ex) {
            log.error("Échec du déclenchement AWX pour le job {}", job.getId(), ex);
            job.setStatus("failed");
            job.setFinishedAt(Instant.now());
            updateJobRepository.save(job);
            webSocketHandler.broadcastJobUpdate(job.getId(), "failed", "Erreur: " + ex.getMessage());
        }
    }

    private void pollUntilFinished(UpdateJob job, Long awxJobId) throws InterruptedException {
        for (int i = 0; i < 60; i++) { // ~5 min max avec un poll toutes les 5s
            Thread.sleep(5000);
            var status = awxClientService.getJobStatus(awxJobId).block();
            if (status == null) continue;

            webSocketHandler.broadcastJobUpdate(job.getId(), status.status(), "AWX status: " + status.status());

            if ("successful".equals(status.status()) || "failed".equals(status.status()) || "error".equals(status.status())) {
                job.setStatus("successful".equals(status.status()) ? "success" : "failed");
                job.setFinishedAt(Instant.now());
                String stdout = awxClientService.getJobStdout(awxJobId).block();
                job.setLogs(stdout);
                updateJobRepository.save(job);
                webSocketHandler.broadcastJobUpdate(job.getId(), job.getStatus(), "Terminé");
                return;
            }
        }
        log.warn("Timeout de suivi du job AWX {}", awxJobId);
    }
}
