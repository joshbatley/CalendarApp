package com.example.calendar_server;

import com.example.calendar_server.models.Event;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VJournal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventTests {

    private List<CalendarComponent> components;

    @BeforeEach
    void setUp() {
        components = new ArrayList<>();
        components.add(new VEvent(LocalDateTime.now(), LocalDateTime.now(), "SummaryWithNoKeyIn"));
        components.add(new VEvent(LocalDate.parse("2024-01-01"), LocalDate.parse("2024-02-01"), "DateOnly"));
        components.add(new VEvent(LocalDateTime.now().minusYears(12), LocalDateTime.now().plusYears(-10), "hasPassed"));
        components.add(new VEvent(LocalDateTime.now().minusYears(10), LocalDateTime.now().plusYears(10), "isActive"));
        components.add(new VJournal());

    }

    @Test
    void should_only_returns_events_for_vevents() {
        var ev = components.stream().map(Event::fromCalendarComponent).filter(Objects::nonNull).toList();
        assertEquals(4, ev.size());
    }

    @Test
    void should_parse_Date_as_DateTime_at_start_of_day() {
        var ev = components.stream().map(Event::fromCalendarComponent)
                .filter(Objects::nonNull)
                .filter(c -> Objects.equals(c.summary(), "DateOnly")).toList().getFirst();
        assertEquals(ev.start(), LocalDate.parse("2024-01-01").atStartOfDay());
    }

    @Test
    void should_remove_key_name_from_properties() {
        var ev = components.stream().map(Event::fromCalendarComponent)
                .filter(Objects::nonNull)
                .filter(c -> c.summary().contains("SummaryWithNoKeyIn")).toList().getFirst();
        assertTrue(!ev.summary().contains("SUMMARY:"));
        assertTrue(!ev.id().contains("UID:"));
    }

    @Test
    void should_set_isActive_true_if_event_range_is_now() {
        var ev = components.stream().map(Event::fromCalendarComponent)
                .filter(Objects::nonNull)
                .filter(c -> c.summary().contains("isActive")).toList().getFirst();
        assertTrue(ev.isActive());
    }

    @Test
    void should_set_hasPassed_true_if_EndDate_is_before_now() {
        var ev = components.stream().map(Event::fromCalendarComponent)
                .filter(Objects::nonNull)
                .filter(c -> c.summary().contains("hasPassed")).toList().getFirst();
        assertTrue(ev.hasPassed());
    }

}
