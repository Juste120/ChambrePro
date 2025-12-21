package juste.chambrepro.repository;

import juste.chambrepro.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByTrackingId(UUID trackingId);
    Optional<Client> findByEmail(String email);
    boolean existsByEmail(String email);
}
