package com.example.roombooking.notification.internal;

import com.example.roombooking.booking.BookingApi;

interface NotificationSender {
    void send(BookingApi.UpcomingBooking booking);
}
