package tn.steg.opsconsole.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.steg.opsconsole.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @EntityGraph(attributePaths = {"role", "region"})
    List<User> findAll();

    @EntityGraph(attributePaths = {"role", "region"})
    Optional<User> findByEmail(String email);
}
