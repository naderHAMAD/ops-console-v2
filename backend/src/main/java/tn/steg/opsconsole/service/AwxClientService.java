package tn.steg.opsconsole.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Encapsule tous les appels à l'API REST d'AWX (Ansible Automation Platform).
 * AWX expose un job_template par action (update JBoss, création VM...) déjà
 * configuré avec l'inventaire, les credentials Vault et le playbook associé.
 * Ce service ne fait que déclencher l'exécution et interroger le statut ;
 * AWX gère lui-même la concurrence et les logs bruts d'Ansible.
 */
@Service
public class AwxClientService {

    private final WebClient webClient;

    public AwxClientService(
            @Value("${app.awx.base-url}") String baseUrl,
            @Value("${app.awx.token}") String token
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Lance un job_template AWX avec des extra_vars, et retourne l'ID du job créé.
     * @param jobTemplateId identifiant du job_template dans AWX (configuré en amont)
     * @param extraVars variables passées au playbook (ex: server hostname, version cible)
     */
    public Mono<Long> launchJobTemplate(long jobTemplateId, Map<String, Object> extraVars) {
        return webClient.post()
                .uri("/api/v2/job_templates/{id}/launch/", jobTemplateId)
                .bodyValue(Map.of("extra_vars", extraVars))
                .retrieve()
                .bodyToMono(AwxLaunchResponse.class)
                .map(AwxLaunchResponse::id);
    }

    /** Récupère le statut courant d'un job AWX (pending/running/successful/failed). */
    public Mono<AwxJobStatus> getJobStatus(long awxJobId) {
        return webClient.get()
                .uri("/api/v2/jobs/{id}/", awxJobId)
                .retrieve()
                .bodyToMono(AwxJobStatus.class);
    }

    /** Récupère la sortie texte (stdout) d'un job pour affichage dans les logs. */
    public Mono<String> getJobStdout(long awxJobId) {
        return webClient.get()
                .uri("/api/v2/jobs/{id}/stdout/?format=txt", awxJobId)
                .retrieve()
                .bodyToMono(String.class);
    }

    public record AwxLaunchResponse(Long id, String status) {}

    public record AwxJobStatus(Long id, String status, String started, String finished) {}
}
