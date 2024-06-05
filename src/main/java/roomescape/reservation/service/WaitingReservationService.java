package roomescape.reservation.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.controller.dto.response.ReservationResponse;
import roomescape.reservation.controller.dto.response.WaitingResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.Status;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.dto.request.WaitingReservationRequest;

@Service
@Transactional(readOnly = true)
public class WaitingReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationFactoryService reservationFactoryService;
    private final ReservationSchedulerService reservationSchedulerService;

    public WaitingReservationService(
            ReservationRepository reservationRepository,
            ReservationFactoryService reservationFactoryService,
            ReservationSchedulerService reservationSchedulerService
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationFactoryService = reservationFactoryService;
        this.reservationSchedulerService = reservationSchedulerService;
    }

    @Transactional
    public ReservationResponse save(WaitingReservationRequest request) {
        Reservation reservation = reservationFactoryService.createWaiting(request);
        reservationSchedulerService.validateSaveWaiting(reservation);
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponse.toResponse(savedReservation);
    }

    public List<WaitingResponse> findAll() {
        return reservationRepository.findAllByStatus(Status.WAIT)
                .stream()
                .map(WaitingResponse::toResponse)
                .toList();
    }

    @Transactional
    public void approveReservation(Long id) {
        Reservation waitingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약 대기 내역이 없습니다."));
        reservationSchedulerService.validateApproveReservation(waitingReservation);
        waitingReservation.updatePaymentPending();
    }
}
