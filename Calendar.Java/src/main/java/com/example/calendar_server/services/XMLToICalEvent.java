package com.example.calendar_server.services;

import com.example.calendar_server.models.Event;
import net.fortuna.ical4j.data.CalendarBuilder;
import org.w3c.dom.Node;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class XMLToICalEvent {
    public static List<Event> parse(Node data) {
        try {
            if (data == null || data.getFirstChild() == null) {
                return new ArrayList<>();
            }
            var calendar = new CalendarBuilder().build(new StringReader(data.getTextContent()));
            return calendar.getComponents().stream()
                    .map(Event::fromCalendarComponent)
                    .toList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
