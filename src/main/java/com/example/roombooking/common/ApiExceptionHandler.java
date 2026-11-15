package com.example.roombooking.common;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Map<String, String> MESSAGES = Map.ofEntries(
            Map.entry("users_email_uq", "Пользователь с таким email уже существует"),
            Map.entry("rooms_name_uq", "Комната с таким названием уже существует"),
            Map.entry("rooms_name_not_blank", "Название комнаты не может быть пустым"),
            Map.entry("rooms_capacity_range", "Вместимость должна быть от 1 до 500"),
            Map.entry("room_schedules_no_overlap", "Окно пересекается с уже существующим в этот день"),
            Map.entry("room_schedules_slot_aligned", "Границы окна должны быть кратны 30 минутам"),
            Map.entry("room_schedules_minutes_range", "Начало должно быть раньше конца в пределах суток"),
            Map.entry("room_schedules_dow_check", "Некорректный день недели"),
            Map.entry("bookings_no_overlap", "Это время уже занято"),
            Map.entry("bookings_within_slots", "Выбранное время вне доступных слотов комнаты"),
            Map.entry("bookings_must_be_future", "Нельзя бронировать время в прошлом"),
            Map.entry("bookings_aligned_30m", "Время брони должно быть кратно 30 минутам"),
            Map.entry("bookings_max_duration", "Максимальная длительность брони — 4 часа"),
            Map.entry("bookings_time_order", "Время окончания должно быть позже начала"),
            Map.entry("bookings_cancel_before_start", "Нельзя отменить уже начавшуюся встречу"),
            Map.entry("bookings_immutable_fields", "Нельзя менять время, комнату и владельца брони"),
            Map.entry("bookings_no_reactivation", "Отменённую бронь нельзя восстановить"),
            Map.entry("bookings_room_fk", "Комната не найдена")
    );

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail onDb(DataIntegrityViolationException e) {
        DbViolation v = DbViolation.from(e);
        HttpStatus status = switch (String.valueOf(v.sqlState())) {
            case "23505", "23P01" -> HttpStatus.CONFLICT;
            default -> HttpStatus.UNPROCESSABLE_ENTITY;
        };
        String msg = MESSAGES.getOrDefault(v.constraint(),
                "Нарушено ограничение данных: " + v.constraint());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, msg);
        pd.setProperty("constraint", v.constraint());
        return pd;
    }

    @ExceptionHandler(NotFoundException.class)
    ProblemDetail onNotFound(NotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    ProblemDetail onBadCredentials(BadCredentialsException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Неверный email или пароль");
    }

    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail onDenied(AccessDeniedException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Недостаточно прав");
    }
}
