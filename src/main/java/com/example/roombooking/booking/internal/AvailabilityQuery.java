package com.example.roombooking.booking.internal;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
class AvailabilityQuery {
    record FreeSlot(Instant startAt, Instant endAt) {}

    private static final String SQL = """
        SELECT s.start_at, s.end_at
        FROM slots s
        WHERE s.room_id = :roomId
          AND s.start_at >= :from AND s.start_at < :to
          AND s.start_at > now()
          AND NOT EXISTS (
            SELECT 1 FROM bookings b
            WHERE b.room_id = s.room_id AND b.status = 'ACTIVE'
              AND tstzrange(b.start_at, b.end_at, '[)') && tstzrange(s.start_at, s.end_at, '[)')
          )
        ORDER BY s.start_at
        """;

    private final NamedParameterJdbcTemplate jdbc;
    AvailabilityQuery(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    List<FreeSlot> findFree(UUID roomId, Instant from, Instant to) {
        var params = Map.<String, Object>of("roomId", roomId,
                "from", OffsetDateTime.ofInstant(from, ZoneOffset.UTC),
                "to", OffsetDateTime.ofInstant(to, ZoneOffset.UTC));
        return jdbc.query(SQL, params, (rs, i) -> new FreeSlot(
                rs.getObject("start_at", OffsetDateTime.class).toInstant(),
                rs.getObject("end_at", OffsetDateTime.class).toInstant()));
    }
}
