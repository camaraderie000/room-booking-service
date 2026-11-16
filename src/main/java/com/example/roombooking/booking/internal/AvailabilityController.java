package com.example.roombooking.booking.internal;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.roombooking.common.AppProperties;

@RestController
@RequestMapping("/api")
class AvailabilityController {
    private final AvailabilityQuery query;
    private final AppProperties app;

    AvailabilityController(AvailabilityQuery query, AppProperties app) {
        this.query = query; this.app = app;
    }

    @GetMapping("/rooms/{roomId}/availability")
    List<AvailabilityQuery.FreeSlot> availability(
            @PathVariable UUID roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Instant from = date.atStartOfDay(app.zoneId()).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(app.zoneId()).toInstant();
        return query.findFree(roomId, from, to);
    }
}
