package tn.steg.opsconsole.repository;

import tn.steg.opsconsole.domain.VmJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface VmJobRepository extends JpaRepository<VmJob, UUID> {

    @Query("""
        SELECT j
        FROM VmJob j
        JOIN FETCH j.vm
        WHERE j.id = :id
    """)
    Optional<VmJob> findByIdWithVm(@Param("id") UUID id);

    Optional<VmJob> findByAwxJobId(String awxJobId);
}