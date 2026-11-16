package com.example.roombooking.booking.internal;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.roombooking.common.CurrentUser;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
class BookingController {
    private final BookingService service;
    BookingController(BookingService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    BookingService.BookingResponse create(@AuthenticationPrincipal Jwt jwt,
                                          @Valid @RequestBody BookingService.CreateBookingRequest r) {
        return service.create(CurrentUser.from(jwt).id(), r);
    }

    @GetMapping("/my")
    List<BookingService.BookingResponse> my(@AuthenticationPrincipal Jwt jwt) {
        return service.listMine(CurrentUser.from(jwt).id());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancel(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        service.cancel(id, CurrentUser.from(jwt));
    }
}
