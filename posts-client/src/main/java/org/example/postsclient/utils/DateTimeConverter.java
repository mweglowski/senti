package org.example.postsclient.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class DateTimeConverter {
    public LocalDateTime convertUnixTimestamp(double timestamp) {
        long seconds = (long) timestamp;

        Instant instant = Instant.ofEpochSecond(seconds);

        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
