package tn.steg.opsconsole.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "update_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateJob {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triggered_by", nullable = false)
    private User triggeredBy;

    @Column(name = "target_version", nullable = false)
    private String targetVersion;

    @Builder.Default
    private String status = "pending"; // pending | running | success | failed | rolled_back

    @Column(columnDefinition = "TEXT")
    private String logs;

    @Column(name = "awx_job_id")
    private String awxJobId;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;
}
