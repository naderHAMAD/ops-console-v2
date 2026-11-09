package tn.steg.opsconsole.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import tn.steg.opsconsole.domain.Server;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServerRepository extends JpaRepository<Server, UUID> {
    @EntityGraph(attributePaths = {"region"})
    List<Server> findAll();

    List<Server> findByRegionId(UUID regionId);
}
