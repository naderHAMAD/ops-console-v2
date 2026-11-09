package tn.steg.opsconsole.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "virtual_machines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualMachine {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private int cpu;

    @Column(name = "ram_gb")
    private int ramGb;

    @Column(name = "disk_gb")
    private int diskGb;

    private String template;

    @Builder.Default
    private String status = "pending"; // pending | active | failed | deleted

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
    
    private String environment;

    @Column(name = "disk_type")
    private String diskType;

    private String network;

    @Column(name = "backup_enabled")
    @Builder.Default
    private boolean backupEnabled = false;

    private String description;
    
}
