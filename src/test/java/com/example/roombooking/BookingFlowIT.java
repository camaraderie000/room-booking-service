package com.example.roombooking;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.roombooking.common.AppProperties;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class BookingFlowIT {

    @Container @ServiceConnection
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16");

    @Autowired MockMvc mvc;
    @Autowired AppProperties app;
    @Autowired JdbcTemplate jdbc;

    private String call(MockHttpServletRequestBuilder req, String token, String body, int expected) throws Exception {
        if (token != null) req.header("Authorization", "Bearer " + token);
        if (body != null) req.contentType(MediaType.APPLICATION_JSON).content(body);
        return mvc.perform(req).andExpect(status().is(expected)).andReturn().getResponse().getContentAsString();
    }

    private String login(String email, String password) throws Exception {
        return JsonPath.read(call(post("/api/auth/login"), null,
                """
                {"email":"%s","password":"%s"}""".formatted(email, password), 200), "$.accessToken");
    }

    @Test
    void fullFlow() throws Exception {
        String admin = login("admin@example.com", "change-me-please");

        String roomId = JsonPath.read(call(post("/api/admin/rooms"), admin,
                """
                {"name":"Alpha","capacity":8}""", 201), "$.id");

        LocalDate tomorrow = LocalDate.now(app.zoneId()).plusDays(1);
        call(post("/api/admin/rooms/" + roomId + "/schedules"), admin,
                """
                {"dayOfWeek":"%s","startTime":"08:00","endTime":"20:00"}""".formatted(tomorrow.getDayOfWeek()), 201);

        call(post("/api/admin/rooms/" + roomId + "/schedules"), admin,
                """
                {"dayOfWeek":"%s","startTime":"19:00","endTime":"21:00"}""".formatted(tomorrow.getDayOfWeek()), 409);

        call(post("/api/auth/register"), null,
                """
                {"email":"u1@example.com","password":"password123"}""", 201);
        String user = login("u1@example.com", "password123");

        call(post("/api/admin/rooms"), user, """
                {"name":"Beta","capacity":4}""", 403);

        String availability = "/api/rooms/" + roomId + "/availability?date=" + tomorrow;
        int before = JsonPath.<List<?>>read(call(get(availability), user, null, 200), "$").size();
        assertThat(before).isEqualTo(24);

        Instant start = tomorrow.atTime(10, 0).atZone(app.zoneId()).toInstant();
        String booking = """
                {"roomId":"%s","startAt":"%s","endAt":"%s"}""".formatted(roomId, start, start.plus(1, ChronoUnit.HOURS));
        String created = call(post("/api/bookings"), user, booking, 201);
        String bookingId = JsonPath.read(created, "$.id");
        assertThat((String) JsonPath.read(created, "$.conferenceUrl")).startsWith("https://meet.mock.local/");

        call(post("/api/bookings"), user, booking, 409);

        UUID userId = jdbc.queryForObject("select id from users where email='u1@example.com'", UUID.class);
        OffsetDateTime s = OffsetDateTime.ofInstant(start.plus(30, ChronoUnit.MINUTES), ZoneOffset.UTC);
        assertThatThrownBy(() -> jdbc.update("""
                INSERT INTO bookings (room_id, user_id, start_at, end_at, conference_url)
                VALUES (?::uuid, ?, ?, ?, 'x')""", roomId, userId, s, s.plusMinutes(60)))
            .hasMessageContaining("bookings_no_overlap");

        int busy = JsonPath.<List<?>>read(call(get(availability), user, null, 200), "$").size();
        assertThat(busy).isEqualTo(22);

        call(delete("/api/bookings/" + bookingId), user, null, 204);
        int freed = JsonPath.<List<?>>read(call(get(availability), user, null, 200), "$").size();
        assertThat(freed).isEqualTo(24);
    }
}
