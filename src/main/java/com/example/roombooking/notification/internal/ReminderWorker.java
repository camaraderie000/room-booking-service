package com.example.roombooking.notification.internal;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.roombooking.booking.BookingApi;

@Component
class ReminderWorker {
    private static final Logger log = LoggerFactory.getLogger(ReminderWorker.class);

    private final BookingApi bookings;
    private final ReminderService reminders;
    private final NotificationProperties props;

    ReminderWorker(BookingApi bookings, ReminderService reminders, NotificationProperties props) {
        this.bookings = bookings; this.reminders = reminders; this.props = props;
    }

    @Scheduled(fixedDelayString = "${app.notification.poll-interval:PT15S}")
    void run() {
        Instant now = Instant.now();
        for (var b : bookings.findActiveStartingBetween(now, now.plus(props.leadTime()))) {
            try {
                reminders.remind(b);
            } catch (Exception e) {
                log.error("Failed to send reminder for booking {}", b.bookingId(), e);
            }
        }
    }
}
