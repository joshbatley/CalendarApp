package com.example.calendar_server.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Request {

    private final String startTime;
    private final String endTime;

    public Request(LocalDateTime startTime, LocalDateTime endTime) {
        var formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        this.startTime = startTime == null ? "" : startTime.format(formatter);
        this.endTime = endTime == null ? "" : endTime.format(formatter);
    }

    public String key() {
        return startTime + "__" + endTime;
    }

    private String TimeRange() {
        if (startTime.isEmpty() && endTime.isEmpty()) {
            return "";
        }

        return "<C:time-range " +
                (startTime.isEmpty() ? "" : "start=\"" + startTime + "\" ") +
                (endTime.isEmpty() ? "" : "end=\"" + endTime + "\" ") +
                "/>";
    }

    public String body() {
        return String.format("""
                <C:calendar-query xmlns:D='DAV:' xmlns:C='urn:ietf:params:xml:ns:caldav'>
                            <D:prop>
                                <D:getetag/>
                                <C:calendar-data/>
                            </D:prop>
                            <C:filter>
                                <C:comp-filter name='VCALENDAR'>
                                    <C:comp-filter name='VEVENT'>
                                        %s
                                    </C:comp-filter>
                                </C:comp-filter>
                            </C:filter>
                        </C:calendar-query>\
                """, TimeRange());
    }
}
