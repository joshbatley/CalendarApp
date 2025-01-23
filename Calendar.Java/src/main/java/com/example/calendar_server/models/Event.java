package com.example.calendar_server.models;

import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Event(
        LocalDateTime start,
        LocalDateTime end,
        String summary,
        String location,
        String description,
        String id,
        boolean hasPassed,
        boolean isActive
) {
    public static Event fromCalendarComponent(CalendarComponent component) {
        if (!(component instanceof VEvent)) {
            return null;
        }
        VEvent ev = (VEvent) component;

        var start = dateTimeFromString(ev.getDateTimeStart().orElseThrow().getDate().toString());
        var end = dateTimeFromString(ev.getDateTimeEnd().orElseThrow().getDate().toString());

        return new Event(
                start,
                end,
                formatProperty(ev,"SUMMARY"),
                formatProperty(ev, "LOCATION"),
                formatProperty(ev,"DESCRIPTION"),
                formatProperty(ev,"UID"),
                LocalDateTime.now().isAfter(end),
                !LocalDateTime.now().isBefore(start) && !LocalDateTime.now().isAfter(end)
        );
    }

    private static String formatProperty(VEvent property, String key) {
        var prop = property.getProperty(key);
        var str = prop.isEmpty() ? "" : prop.orElseThrow().toString();
        return str.replace(key + ":", "").trim();
    }

    private static LocalDateTime dateTimeFromString(String dateString) {
        if (dateString.contains("T")) {
            var cutoffPoint = dateString.indexOf('Z') == -1 ? dateString.length() : dateString.indexOf('Z');
            return LocalDateTime.parse(dateString.substring(0, cutoffPoint), DateTimeFormatter.ISO_DATE_TIME);
        }
        return LocalDate.parse(dateString).atStartOfDay();
    }
}
