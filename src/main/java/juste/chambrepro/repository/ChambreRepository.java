package juste.chambrepro.repository;

import juste.chambrepro.entity.Chambre;
import juste.chambrepro.enums.TypeChambre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChambreRepository extends JpaRepository<Chambre, Long> {
    Optional<Chambre> findByTrackingId(UUID trackingId);
    Optional<Chambre> findByNumero(String numero);
    boolean existsByNumero(String numero);
    List<Chambre> findByType(TypeChambre type);

    @Query("""
        SELECT DISTINCT c FROM Chambre c
        WHERE c.id NOT IN (
            SELECT r.chambre.id FROM Reservation r
            WHERE r.statut = 'RESERVE'
            AND (r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut)
        )
        """)
    List<Chambre> findChambresDisponibles(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin
    );
}