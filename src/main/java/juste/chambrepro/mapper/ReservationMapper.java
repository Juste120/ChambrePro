package juste.chambrepro.mapper;

import juste.chambrepro.dto.requests.ReservationRequest;
import juste.chambrepro.dto.responses.ReservationResponse;
import juste.chambrepro.entity.Chambre;
import juste.chambrepro.entity.Client;
import juste.chambrepro.entity.Reservation;
import juste.chambrepro.enums.StatutReservation;
import juste.chambrepro.exception.ResourceNotFoundException;
import juste.chambrepro.repository.ChambreRepository;
import juste.chambrepro.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationMapper {

    private final ClientRepository clientRepository;
    private final ChambreRepository chambreRepository;

    public Reservation toEntity(ReservationRequest request) {
        Client client = clientRepository.findByTrackingId(request.clientTrackingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client", "trackingId", request.clientTrackingId()
                ));

        Chambre chambre = chambreRepository.findByTrackingId(request.chambreTrackingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chambre", "trackingId", request.chambreTrackingId()
                ));

        Reservation reservation = new Reservation();
        reservation.setTrackingId(UUID.randomUUID());
        reservation.setDateDebut(request.dateDebut());
        reservation.setDateFin(request.dateFin());
        reservation.setStatut(StatutReservation.RESERVE);
        reservation.setClient(client);
        reservation.setChambre(chambre);

        return reservation;
    }

    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getTrackingId(),
                reservation.getDateDebut(),
                reservation.getDateFin(),
                reservation.getStatut(),
                reservation.getClient().getTrackingId(),
                reservation.getClient().getNom(),
                reservation.getChambre().getTrackingId(),
                reservation.getChambre().getNumero(),
                reservation.getCreatedAt()
        );
    }
}