package com.example.roombooking.booking.internal;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookings")
@DynamicUpdate
class BookingEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "room_id", nullable = false, updatable = false) private UUID roomId;
    @Column(name = "user_id", nullable = false, updatable = false) private UUID userId;
    @Column(name = "start_at", nullable = false, updatable = false) private Instant startAt;
    @Column(name = "end_at", nullable = false, updatable = false) private Instant endAt;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private BookingStatus status = BookingStatus.ACTIVE;
    @Column(name = "conference_url", nullable = false) private String conferenceUrl;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "cancelled_at") private Instant cancelledAt;

    protected BookingEntity() {}
    BookingEntity(UUID roomId, UUID userId, Instant startAt, Instant endAt, String conferenceUrl) {
        this.roomId = roomId; this.userId = userId; this.startAt = startAt;
        this.endAt = endAt; this.conferenceUrl = conferenceUrl;
    }

    void cancel(Instant now) { this.status = BookingStatus.CANCELLED; this.cancelledAt = now; }

    UUID getId() { return id; }
    UUID getRoomId() { return roomId; }
    UUID getUserId() { return userId; }
    Instant getStartAt() { return startAt; }
    Instant getEndAt() { return endAt; }
    BookingStatus getStatus() { return status; }
    String getConferenceUrl() { return conferenceUrl; }
}
