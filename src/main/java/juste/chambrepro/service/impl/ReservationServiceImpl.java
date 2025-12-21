package juste.chambrepro.service.impl;

import juste.chambrepro.dto.requests.ReservationRequest;
import juste.chambrepro.dto.responses.ReservationResponse;
import juste.chambrepro.entity.Reservation;
import juste.chambrepro.enums.StatutReservation;
import juste.chambrepro.exception.BadRequestException;
import juste.chambrepro.exception.ConflictException;
import juste.chambrepro.exception.ResourceNotFoundException;
import juste.chambrepro.mapper.ReservationMapper;
import juste.chambrepro.repository.ChambreRepository;
import juste.chambrepro.repository.ReservationRepository;
import juste.chambrepro.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ChambreRepository chambreRepository;
    private final ReservationMapper reservationMapper;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        log.info("Création d'une nouvelle réservation du {} au {}",
                request.dateDebut(), request.dateFin());

        validateReservationDates(request.dateDebut(), request.dateFin());
        validateChambreDisponible(request.chambreTrackingId(),
                request.dateDebut(), request.dateFin());

        Reservation reservation = reservationMapper.toEntity(request);
        Reservation saved = reservationRepository.save(reservation);

        log.info("Réservation créée avec succès: {}", saved.getTrackingId());
        return reservationMapper.toResponse(saved);
    }

    @Override
    public ReservationResponse getReservationByTrackingId(UUID trackingId) {
        log.debug("Recherche de la réservation: {}", trackingId);

        Reservation reservation = reservationRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation", "trackingId", trackingId));

        return reservationMapper.toResponse(reservation);
    }

    @Override
    public List<ReservationResponse> getAllReservations() {
        log.debug("Récupération de toutes les réservations");
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    public List<ReservationResponse> getReservationsActives(UUID clientTrackingId) {
        log.debug("Recherche des réservations actives du client: {}", clientTrackingId);
        return reservationRepository.findReservationsActivesClient(clientTrackingId).stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReservationResponse annulerReservation(UUID trackingId) {
        log.info("Annulation de la réservation: {}", trackingId);

        Reservation reservation = reservationRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation", "trackingId", trackingId));

        if (reservation.getStatut() != StatutReservation.RESERVE) {
            throw new BadRequestException(
                    "Seules les réservations avec le statut RESERVE peuvent être annulées");
        }

        if (reservation.getDateDebut().isBefore(LocalDate.now())) {
            throw new BadRequestException(
                    "Impossible d'annuler une réservation déjà commencée");
        }

        reservation.setStatut(StatutReservation.ANNULE);
        Reservation updated = reservationRepository.save(reservation);

        log.info("Réservation annulée avec succès: {}", trackingId);
        return reservationMapper.toResponse(updated);
    }

    private void validateReservationDates(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new BadRequestException(
                    "Les dates de début et de fin sont obligatoires");
        }

        if (dateDebut.isAfter(dateFin)) {
            throw new BadRequestException(
                    "La date de début (" + dateDebut +
                            ") doit être antérieure à la date de fin (" + dateFin + ")");
        }

        if (dateDebut.isBefore(LocalDate.now())) {
            throw new BadRequestException(
                    "La date de début ne peut pas être dans le passé");
        }

        long jours = ChronoUnit.DAYS.between(dateDebut, dateFin);
        if (jours > 30) {
            throw new BadRequestException(
                    "Une réservation ne peut pas dépasser 30 jours");
        }
    }

    private void validateChambreDisponible(UUID chambreTrackingId,
                                           LocalDate dateDebut,
                                           LocalDate dateFin) {

        var chambre = chambreRepository.findByTrackingId(chambreTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chambre", "trackingId", chambreTrackingId));

        boolean dejaReservee = reservationRepository
                .existsByChambreIdAndDateChevauchement(
                        chambre.getId(), dateDebut, dateFin, StatutReservation.RESERVE);

        if (dejaReservee) {
            throw new ConflictException(
                    "La chambre est déjà réservée pour cette période");
        }
    }
}