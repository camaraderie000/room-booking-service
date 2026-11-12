package com.example.roombooking.room.internal;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface RoomRepository extends JpaRepository<RoomEntity, UUID> {}
