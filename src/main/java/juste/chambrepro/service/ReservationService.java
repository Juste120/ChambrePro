package juste.chambrepro.service;

import juste.chambrepro.dto.requests.ReservationRequest;
import juste.chambrepro.dto.responses.ReservationResponse;
import java.util.List;
import java.util.UUID;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request);
    ReservationResponse getReservationByTrackingId(UUID trackingId);
    List<ReservationResponse> getAllReservations();
    List<ReservationResponse> getReservationsActives(UUID clientTrackingId);
    ReservationResponse annulerReservation(UUID trackingId);
}