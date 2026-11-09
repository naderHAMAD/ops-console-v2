package tn.steg.opsconsole.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tn.steg.opsconsole.dto.response.JobStatusDto;

import java.time.Instant;
import java.util.UUID;

/**
 * Diffuse les changements de statut de job (update JBoss ou VM) vers les clients
 * abonnés au topic /topic/jobs/{jobId}. Le frontend Angular s'y connecte dès
 * qu'il ouvre l'écran de suivi d'exécution.
 */
@Component
@RequiredArgsConstructor
public class JobLogWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastJobUpdate(UUID jobId, String status, String lastLogLine) {
        JobStatusDto dto = new JobStatusDto(jobId, null, status, lastLogLine, null, Instant.now());
        messagingTemplate.convertAndSend("/topic/jobs/" + jobId, dto);
    }
}
