package tn.steg.opsconsole.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vm_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VmJob {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vm_id", nullable = false)
    private VirtualMachine vm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triggered_by", nullable = false)
    private User triggeredBy;

    @Builder.Default
    private String status = "pending";

    @Column(columnDefinition = "TEXT")
    private String logs;

    @Column(name = "awx_job_id")
    private String awxJobId;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;
}
