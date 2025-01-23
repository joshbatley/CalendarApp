package com.example.calendar_server;

import com.example.calendar_server.models.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestTests {

    private List<List<LocalDateTime>> dates;

    @BeforeEach
    void setUp() {
        dates = new ArrayList<>();
        dates.add(new ArrayList<>());
        dates.add(new ArrayList<>());
        dates.add(new ArrayList<>());
        dates.add(new ArrayList<>());

        dates.get(0).add(null);
        dates.get(0).add(null);

        dates.get(1).add(LocalDate.parse("2024-01-01").atStartOfDay());
        dates.get(1).add(null);

        dates.get(2).add(null);
        dates.get(2).add(LocalDate.parse("2024-01-01").atStartOfDay());

        dates.get(3).add(LocalDate.parse("2024-01-01").atStartOfDay());
        dates.get(3).add(LocalDate.parse("2024-01-01").atStartOfDay());
    }


    @Test
    void key_should_contain_DateTime() {
        var keys = new ArrayList<>();
        keys.add("__");
        keys.add("20240101T000000Z__");
        keys.add("__20240101T000000Z");
        keys.add("20240101T000000Z__20240101T000000Z");

        var res = dates.stream().map(c -> new Request(
                c.getFirst(), c.getLast()
        )).toList();

        for (int i = 0; i < res.size(); i++) {
            assertEquals(keys.get(i), res.get(i).key());
        }
    }


    @Test
    void body_should_contain_DateTime() {
        var shouldContain = new ArrayList<String>();
        shouldContain.add("");
        shouldContain.add("<C:time-range start=\"20240101T000000Z\" />");
        shouldContain.add("<C:time-range end=\"20240101T000000Z\" />");
        shouldContain.add("<C:time-range start=\"20240101T000000Z\" end=\"20240101T000000Z\" />");

        var res = dates.stream().map(c -> new Request(
                c.getFirst(), c.getLast()
        )).toList();

        for (int i = 0; i < res.size(); i++) {
            assertTrue(res.get(i).body().contains(shouldContain.get(i)));
        }
    }

}
