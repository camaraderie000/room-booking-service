package com.example.roombooking.room.internal;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.roombooking.common.AppProperties;

@Service
class SlotService {
    private static final String GENERATE_SQL = """
        INSERT INTO slots (room_id, start_at, end_at)
        SELECT s.room_id, t.start_at, t.start_at + interval '30 minutes'
        FROM room_schedules s
        JOIN rooms r ON r.id = s.room_id AND r.active
        CROSS JOIN generate_series(CAST(:from AS timestamp), CAST(:to AS timestamp), interval '1 day') AS d(day)
        CROSS JOIN LATERAL generate_series(s.start_minute, s.end_minute - 30, 30) AS m(minute)
        CROSS JOIN LATERAL (
            SELECT (d.day + make_interval(mins => m.minute)) AT TIME ZONE :zone AS start_at
        ) t
        WHERE extract(isodow FROM d.day) = s.day_of_week %s
        ON CONFLICT (room_id, start_at) DO NOTHING
        """;

    private static final String CLEANUP_SQL = """
        DELETE FROM slots sl
        WHERE sl.room_id = :roomId AND sl.start_at > now()
          AND NOT EXISTS (
            SELECT 1
            FROM room_schedules s,
                 LATERAL (SELECT sl.start_at AT TIME ZONE :zone AS ts) l
            WHERE s.room_id = sl.room_id
              AND s.day_of_week = extract(isodow FROM l.ts)
              AND extract(hour FROM l.ts) * 60 + extract(minute FROM l.ts) >= s.start_minute
              AND extract(hour FROM l.ts) * 60 + extract(minute FROM l.ts) <  s.end_minute
          )
        """;

    private final NamedParameterJdbcTemplate jdbc;
    private final AppProperties app;
    private final SlotProperties props;

    SlotService(NamedParameterJdbcTemplate jdbc, AppProperties app, SlotProperties props) {
        this.jdbc = jdbc; this.app = app; this.props = props;
    }

    @Transactional
    void sync(UUID roomId) {
        jdbc.update(CLEANUP_SQL, new MapSqlParameterSource()
                .addValue("roomId", roomId).addValue("zone", app.zoneId().getId()));
        generate(roomId);
    }

    @Transactional
    void generateAll() { generate(null); }

    private void generate(UUID roomId) {
        LocalDate today = LocalDate.now(app.zoneId());
        var params = new MapSqlParameterSource()
                .addValue("from", today.atStartOfDay())
                .addValue("to", today.plusDays(props.horizonDays() - 1).atStartOfDay())
                .addValue("zone", app.zoneId().getId());
        String filter = "";
        if (roomId != null) {
            filter = "AND s.room_id = :roomId";
            params.addValue("roomId", roomId);
        }
        jdbc.update(GENERATE_SQL.formatted(filter), params);
    }
}
