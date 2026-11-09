package tn.steg.opsconsole.repository;

import tn.steg.opsconsole.domain.UpdateJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UpdateJobRepository extends JpaRepository<UpdateJob, UUID> {
    List<UpdateJob> findTop50ByOrderByStartedAtDesc();
    Optional<UpdateJob> findByAwxJobId(String awxJobId);

    @Query("""
        SELECT j
        FROM UpdateJob j
        JOIN FETCH j.server s
        JOIN FETCH s.region
        JOIN FETCH j.triggeredBy
        WHERE j.id = :id
    """)
    Optional<UpdateJob> findByIdWithDetails(@Param("id") UUID id);
}
