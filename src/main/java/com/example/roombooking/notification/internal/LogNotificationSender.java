package com.example.roombooking.notification.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.roombooking.booking.BookingApi;

@Component
class LogNotificationSender implements NotificationSender {
    private static final Logger log = LoggerFactory.getLogger(LogNotificationSender.class);

    @Override
    public void send(BookingApi.UpcomingBooking b) {
        log.info("REMINDER user={} room={} starts at {} link={}",
                b.userId(), b.roomId(), b.startAt(), b.conferenceUrl());
    }
}
