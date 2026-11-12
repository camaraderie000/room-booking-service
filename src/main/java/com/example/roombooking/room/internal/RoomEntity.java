package com.example.roombooking.room.internal;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
class RoomEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private int capacity;
    @Column(nullable = false) private boolean active = true;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected RoomEntity() {}
    RoomEntity(String name, int capacity) { this.name = name; this.capacity = capacity; }
    UUID getId() { return id; }
    String getName() { return name; }
    int getCapacity() { return capacity; }
    boolean isActive() { return active; }
}
