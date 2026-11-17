package com.example.roombooking.notification.internal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.roombooking.booking.BookingApi;

@Service
class ReminderService {
    private final JdbcTemplate jdbc;
    private final NotificationSender sender;

    ReminderService(JdbcTemplate jdbc, NotificationSender sender) {
        this.jdbc = jdbc; this.sender = sender;
    }

    @Transactional
    void remind(BookingApi.UpcomingBooking b) {
        int inserted = jdbc.update("""
            INSERT INTO booking_notifications (booking_id, user_id, kind)
            VALUES (?, ?, 'REMINDER_5M')
            ON CONFLICT (booking_id, kind) DO NOTHING
            """, b.bookingId(), b.userId());
        if (inserted == 0) return;

        sender.send(b);
        jdbc.update("UPDATE booking_notifications SET sent_at = now() WHERE booking_id = ? AND kind = 'REMINDER_5M'",
                b.bookingId());
    }
}
