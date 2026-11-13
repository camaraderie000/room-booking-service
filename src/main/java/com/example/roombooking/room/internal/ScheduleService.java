package com.example.roombooking.room.internal;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.roombooking.common.NotFoundException;

import jakarta.validation.constraints.NotNull;

@Service
class ScheduleService {
    record ScheduleRequest(@NotNull DayOfWeek dayOfWeek, @NotNull LocalTime startTime, @NotNull LocalTime endTime) {}
    record ScheduleResponse(UUID id, UUID roomId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {}

    private final RoomRepository rooms;
    private final RoomScheduleRepository repo;

    ScheduleService(RoomRepository rooms, RoomScheduleRepository repo) {
        this.rooms = rooms; this.repo = repo;
    }

    @Transactional
    ScheduleResponse add(UUID roomId, ScheduleRequest r) {
        rooms.findById(roomId).orElseThrow(() -> new NotFoundException("Комната не найдена"));
        var e = repo.saveAndFlush(new RoomScheduleEntity(roomId, r.dayOfWeek().getValue(),
                toMinutes(r.startTime(), false), toMinutes(r.endTime(), true)));
        return toResponse(e);
    }

    @Transactional
    void delete(UUID roomId, UUID id) {
        var e = repo.findByIdAndRoomId(id, roomId)
                .orElseThrow(() -> new NotFoundException("Окно расписания не найдено"));
        repo.delete(e);
        repo.flush();
    }

    @Transactional(readOnly = true)
    List<ScheduleResponse> list(UUID roomId) {
        return repo.findByRoomIdOrderByDayOfWeekAscStartMinuteAsc(roomId).stream()
                .map(this::toResponse).toList();
    }

    private static int toMinutes(LocalTime t, boolean isEnd) {
        int m = t.getHour() * 60 + t.getMinute();
        return (isEnd && m == 0) ? 1440 : m;
    }

    private ScheduleResponse toResponse(RoomScheduleEntity e) {
        return new ScheduleResponse(e.getId(), e.getRoomId(), DayOfWeek.of(e.getDayOfWeek()),
                LocalTime.ofSecondOfDay(e.getStartMinute() * 60L),
                LocalTime.ofSecondOfDay((e.getEndMinute() % 1440) * 60L));
    }
}
