package com.example.roombooking.conference;

import java.time.Instant;
import java.util.UUID;

public interface ConferenceLinkProvider {
    String createLink(UUID roomId, Instant startAt);
}
