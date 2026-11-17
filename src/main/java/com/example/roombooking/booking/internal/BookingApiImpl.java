package com.example.roombooking.booking.internal;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.roombooking.booking.BookingApi;

@Service
class BookingApiImpl implements BookingApi {
    private final BookingRepository repo;
    BookingApiImpl(BookingRepository repo) { this.repo = repo; }

    @Override
    @Transactional(readOnly = true)
    public List<UpcomingBooking> findActiveStartingBetween(Instant from, Instant to) {
        return repo.findByStatusAndStartAtGreaterThanAndStartAtLessThanEqualOrderByStartAt(
                        BookingStatus.ACTIVE, from, to).stream()
                .map(b -> new UpcomingBooking(b.getId(), b.getUserId(), b.getRoomId(),
                        b.getStartAt(), b.getConferenceUrl()))
                .toList();
    }
}
