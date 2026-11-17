package com.example.roombooking.notification.internal;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("app.notification")
public record NotificationProperties(@DefaultValue("PT5M") Duration leadTime) {}
