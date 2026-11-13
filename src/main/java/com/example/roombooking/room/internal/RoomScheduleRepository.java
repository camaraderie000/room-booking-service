package com.example.roombooking.room.internal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface RoomScheduleRepository extends JpaRepository<RoomScheduleEntity, UUID> {
    List<RoomScheduleEntity> findByRoomIdOrderByDayOfWeekAscStartMinuteAsc(UUID roomId);
    Optional<RoomScheduleEntity> findByIdAndRoomId(UUID id, UUID roomId);
}
