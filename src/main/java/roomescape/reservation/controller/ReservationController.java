package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.common.dto.ResourcesResponse;
import roomescape.reservation.controller.dto.request.ReservationPaymentSaveRequest;
import roomescape.reservation.controller.dto.request.ReservationSearchCondRequest;
import roomescape.reservation.controller.dto.response.MemberReservationResponse;
import roomescape.reservation.controller.dto.response.ReservationResponse;
import roomescape.reservation.service.ReservationService;
import roomescape.reservation.service.dto.request.ReservationPaymentRequest;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public ResponseEntity<ResourcesResponse<ReservationResponse>> findAll() {
        List<ReservationResponse> reservations = reservationService.findAll();
        ResourcesResponse<ReservationResponse> response = new ResourcesResponse<>(reservations);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservations/search")
    public ResponseEntity<ResourcesResponse<ReservationResponse>> findAllBySearchCond(
            @Valid @ModelAttribute ReservationSearchCondRequest searchCondRequest
    ) {
        List<ReservationResponse> reservations = reservationService.findAllBySearchCond(searchCondRequest);
        ResourcesResponse<ReservationResponse> response = new ResourcesResponse<>(reservations);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservations/mine")
    public ResponseEntity<ResourcesResponse<MemberReservationResponse>> findReservationsAndWaitingsByMember(
            LoginMember loginMember
    ) {
        List<MemberReservationResponse> reservations =
                reservationService.findReservationsAndWaitingsByMember(loginMember);
        ResourcesResponse<MemberReservationResponse> response = new ResourcesResponse<>(reservations);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> save(
            @Valid @RequestBody ReservationPaymentSaveRequest saveRequest,
            LoginMember loginMember
    ) {
        ReservationPaymentRequest request = ReservationPaymentRequest.of(saveRequest, loginMember);
        ReservationResponse response = reservationService.save(request);

        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        reservationService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
