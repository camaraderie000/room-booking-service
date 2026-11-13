package com.example.roombooking.room.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("app.slots")
public record SlotProperties(@DefaultValue("14") int horizonDays) {}
