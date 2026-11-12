package com.example.roombooking.room.internal;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.roombooking.common.NotFoundException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api")
class RoomController {
    record CreateRoomRequest(@NotNull String name, @NotNull Integer capacity) {}
    record RoomResponse(UUID id, String name, int capacity, boolean active) {
        static RoomResponse from(RoomEntity r) {
            return new RoomResponse(r.getId(), r.getName(), r.getCapacity(), r.isActive());
        }
    }

    private final RoomRepository repo;
    RoomController(RoomRepository repo) { this.repo = repo; }

    @PostMapping("/admin/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    RoomResponse create(@Valid @RequestBody CreateRoomRequest r) {
        return RoomResponse.from(repo.saveAndFlush(new RoomEntity(r.name(), r.capacity())));
    }

    @GetMapping("/rooms")
    List<RoomResponse> list() {
        return repo.findAll().stream().map(RoomResponse::from).toList();
    }

    @GetMapping("/rooms/{id}")
    RoomResponse get(@PathVariable UUID id) {
        return repo.findById(id).map(RoomResponse::from)
                .orElseThrow(() -> new NotFoundException("Комната не найдена"));
    }
}
