package tn.steg.opsconsole.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.steg.opsconsole.domain.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    @EntityGraph(attributePaths = {"user"})
    List<AuditLog> findTop100ByOrderByTimestampDesc();
}
