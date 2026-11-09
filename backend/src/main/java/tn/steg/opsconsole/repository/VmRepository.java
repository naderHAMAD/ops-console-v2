package tn.steg.opsconsole.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import tn.steg.opsconsole.domain.VirtualMachine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VmRepository extends JpaRepository<VirtualMachine, UUID> {
    @EntityGraph(attributePaths = {"region"})
    List<VirtualMachine> findAll();

    List<VirtualMachine> findByRegionId(UUID regionId);
}
