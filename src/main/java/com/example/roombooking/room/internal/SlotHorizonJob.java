package com.example.roombooking.room.internal;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class SlotHorizonJob {
    private final SlotService slots;
    SlotHorizonJob(SlotService slots) { this.slots = slots; }

    @Scheduled(cron = "0 10 0 * * *", zone = "${app.zone-id}")
    void extendHorizon() { slots.generateAll(); }

    @EventListener(ApplicationReadyEvent.class)
    void onStart() { slots.generateAll(); }
}
