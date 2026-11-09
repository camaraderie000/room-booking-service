package com.example.roombooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RoomBookingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoomBookingServiceApplication.class, args);
    }
}
