package com.example.roombooking.booking.internal;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface BookingRepository extends JpaRepository<BookingEntity, UUID> {
    List<BookingEntity> findByUserIdOrderByStartAtDesc(UUID userId);
    List<BookingEntity> findByStatusAndStartAtGreaterThanAndStartAtLessThanEqualOrderByStartAt(
            BookingStatus status, Instant from, Instant to);
}
