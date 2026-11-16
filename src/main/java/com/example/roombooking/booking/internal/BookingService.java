package com.example.roombooking.booking.internal;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.roombooking.conference.ConferenceLinkProvider;

import jakarta.validation.constraints.NotNull;

@Service
class BookingService {
    record CreateBookingRequest(@NotNull UUID roomId, @NotNull Instant startAt, @NotNull Instant endAt) {}
    record BookingResponse(UUID id, UUID roomId, UUID userId, Instant startAt, Instant endAt,
                           BookingStatus status, String conferenceUrl) {
        static BookingResponse from(BookingEntity b) {
            return new BookingResponse(b.getId(), b.getRoomId(), b.getUserId(), b.getStartAt(),
                    b.getEndAt(), b.getStatus(), b.getConferenceUrl());
        }
    }

    private final BookingRepository repo;
    private final ConferenceLinkProvider conference;

    BookingService(BookingRepository repo, ConferenceLinkProvider conference) {
        this.repo = repo; this.conference = conference;
    }

    @Transactional
    BookingResponse create(UUID userId, CreateBookingRequest r) {
        String link = conference.createLink(r.roomId(), r.startAt());
        BookingEntity saved = repo.saveAndFlush(
                new BookingEntity(r.roomId(), userId, r.startAt(), r.endAt(), link));
        return BookingResponse.from(saved);
    }

    @Transactional(readOnly = true)
    List<BookingResponse> listMine(UUID userId) {
        return repo.findByUserIdOrderByStartAtDesc(userId).stream().map(BookingResponse::from).toList();
    }
}
