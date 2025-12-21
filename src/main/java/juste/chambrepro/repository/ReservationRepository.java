package juste.chambrepro.repository;

import juste.chambrepro.entity.Reservation;
import juste.chambrepro.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByTrackingId(UUID trackingId);
    List<Reservation> findByClientId(Long clientId);
    List<Reservation> findByChambreId(Long chambreId);

    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM Reservation r
        WHERE r.chambre.id = :chambreId
        AND r.statut = :statut
        AND (r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut)
        """)
    boolean existsByChambreIdAndDateChevauchement(
            @Param("chambreId") Long chambreId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            @Param("statut") StatutReservation statut
    );

    @Query("""
        SELECT r FROM Reservation r
        WHERE r.client.trackingId = :clientTrackingId
        AND r.statut = 'RESERVE'
        AND r.dateFin >= CURRENT_DATE
        ORDER BY r.dateDebut
        """)
    List<Reservation> findReservationsActivesClient(@Param("clientTrackingId") UUID clientTrackingId);
}