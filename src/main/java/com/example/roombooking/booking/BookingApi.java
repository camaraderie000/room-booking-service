package com.example.roombooking.booking;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BookingApi {
    record UpcomingBooking(UUID bookingId, UUID userId, UUID roomId, Instant startAt, String conferenceUrl) {}

    List<UpcomingBooking> findActiveStartingBetween(Instant from, Instant to);
}
