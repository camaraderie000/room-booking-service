package com.example.roombooking.common;

import org.postgresql.util.PSQLException;

public record DbViolation(String constraint, String sqlState) {
    public static DbViolation from(Throwable t) {
        for (Throwable c = t; c != null; c = c.getCause()) {
            if (c instanceof PSQLException p) {
                var msg = p.getServerErrorMessage();
                return new DbViolation(msg != null ? msg.getConstraint() : null, p.getSQLState());
            }
        }
        return new DbViolation(null, null);
    }
}
