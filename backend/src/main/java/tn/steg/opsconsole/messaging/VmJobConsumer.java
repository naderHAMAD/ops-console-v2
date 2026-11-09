package tn.steg.opsconsole.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tn.steg.opsconsole.config.RabbitMQConfig;
import tn.steg.opsconsole.domain.VmJob;
import tn.steg.opsconsole.repository.VmJobRepository;
import tn.steg.opsconsole.service.AwxClientService;
import tn.steg.opsconsole.websocket.JobLogWebSocketHandler;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class VmJobConsumer {

    private final VmJobRepository vmJobRepository;
    private final AwxClientService awxClientService;
    private final JobLogWebSocketHandler webSocketHandler;

    @Value("${app.awx.job-template.create-vm}")
    private long createVmJobTemplateId;

    @RabbitListener(queues = RabbitMQConfig.VM_QUEUE)
    public void handle(JobMessage message) {
    	VmJob job = vmJobRepository.findByIdWithVm(message.jobId())
    	        .orElseThrow(() ->
    	                new IllegalStateException(
    	                        "Job VM introuvable: " + message.jobId()
    	                )
    	        );

        job.setStatus("running");
        job.setStartedAt(Instant.now());
        vmJobRepository.save(job);
        webSocketHandler.broadcastJobUpdate(job.getId(), "running", "Provisioning en cours...");

        try {
            Long awxJobId = awxClientService.launchJobTemplate(createVmJobTemplateId, Map.of(
                    "vm_name", job.getVm().getName(),
                    "cpu", job.getVm().getCpu(),
                    "ram_gb", job.getVm().getRamGb(),
                    "disk_gb", job.getVm().getDiskGb(),
                    "disk_type", job.getVm().getDiskType(),
                    "template", job.getVm().getTemplate(),
                    "network", job.getVm().getNetwork(),
                    "environment", job.getVm().getEnvironment(),
                    "backup_enabled", job.getVm().isBackupEnabled()
            )).block();

            job.setAwxJobId(String.valueOf(awxJobId));
            vmJobRepository.save(job);

            var status = awxClientService.getJobStatus(awxJobId).block();
            job.setStatus(status != null && "successful".equals(status.status()) ? "success" : "running");
            vmJobRepository.save(job);
            webSocketHandler.broadcastJobUpdate(job.getId(), job.getStatus(), "AWX a pris en charge le provisioning");

        } catch (Exception ex) {
            log.error("Échec du déclenchement AWX pour le job VM {}", job.getId(), ex);
            job.setStatus("failed");
            job.setFinishedAt(Instant.now());
            vmJobRepository.save(job);
            webSocketHandler.broadcastJobUpdate(job.getId(), "failed", "Erreur: " + ex.getMessage());
        }
    }
}
