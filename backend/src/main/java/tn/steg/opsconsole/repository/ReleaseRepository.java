package tn.steg.opsconsole.repository;

import tn.steg.opsconsole.domain.Release;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ReleaseRepository extends JpaRepository<Release, UUID> {
    List<Release> findAllByOrderByCreatedAtDesc();
}