package tn.steg.opsconsole.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "servers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Server {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String hostname;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "jboss_version")
    private String jbossVersion;

    @Builder.Default
    private String status = "unknown"; // up | down | unknown

    @Column(name = "last_update")
    private Instant lastUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;
}
