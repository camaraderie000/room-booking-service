package com.example.roombooking.room.internal;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
class ScheduleController {
    private final ScheduleService service;
    ScheduleController(ScheduleService service) { this.service = service; }

    @PostMapping("/admin/rooms/{roomId}/schedules")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    ScheduleService.ScheduleResponse add(@PathVariable UUID roomId,
                                         @Valid @RequestBody ScheduleService.ScheduleRequest r) {
        return service.add(roomId, r);
    }

    @DeleteMapping("/admin/rooms/{roomId}/schedules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID roomId, @PathVariable UUID id) { service.delete(roomId, id); }

    @GetMapping("/rooms/{roomId}/schedules")
    List<ScheduleService.ScheduleResponse> list(@PathVariable UUID roomId) { return service.list(roomId); }
}
