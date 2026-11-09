package tn.steg.opsconsole.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "releases")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Release {
    @Id @GeneratedValue
    private UUID id;
    @Column(nullable = false, unique = true)
    private String version;
    private String label;
    @Column(name = "is_stable")
    private boolean isStable;
    @Builder.Default
    private Instant createdAt = Instant.now();
}