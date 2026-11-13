package com.example.roombooking.room.internal;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "room_schedules")
class RoomScheduleEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "room_id", nullable = false) private UUID roomId;
    @Column(name = "day_of_week", nullable = false) private int dayOfWeek;
    @Column(name = "start_minute", nullable = false) private int startMinute;
    @Column(name = "end_minute", nullable = false) private int endMinute;

    protected RoomScheduleEntity() {}
    RoomScheduleEntity(UUID roomId, int dayOfWeek, int startMinute, int endMinute) {
        this.roomId = roomId; this.dayOfWeek = dayOfWeek;
        this.startMinute = startMinute; this.endMinute = endMinute;
    }
    UUID getId() { return id; }
    UUID getRoomId() { return roomId; }
    int getDayOfWeek() { return dayOfWeek; }
    int getStartMinute() { return startMinute; }
    int getEndMinute() { return endMinute; }
}
