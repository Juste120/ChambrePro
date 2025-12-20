package juste.chambrepro.entity;

import jakarta.persistence.*;
import juste.chambrepro.enums.StatutReservation;
import juste.chambrepro.shared.utils.BaseEntity;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "reservations",
        indexes = {
                @Index(name = "idx_reservation_dates", columnList = "date_debut, date_fin"),
                @Index(name = "idx_reservation_statut", columnList = "statut")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation extends BaseEntity {

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutReservation statut = StatutReservation.RESERVE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reservation_client"))
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chambre_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reservation_chambre"))
    private Chambre chambre;

}