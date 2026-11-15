package com.example.roombooking.conference.internal;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.roombooking.conference.ConferenceLinkProvider;

@Component
class MockConferenceLinkProvider implements ConferenceLinkProvider {
    private final String baseUrl;

    MockConferenceLinkProvider(@Value("${app.conference.base-url:https://meet.mock.local}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String createLink(UUID roomId, Instant startAt) {
        return baseUrl + "/" + UUID.randomUUID();
    }
}
