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
            Map.entry("users_email_uq", "Пользователь с таким email уже существует")
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
