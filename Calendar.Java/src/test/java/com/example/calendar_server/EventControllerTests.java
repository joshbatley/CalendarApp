package com.example.calendar_server;

import com.example.calendar_server.clients.ICalDavClient;
import com.example.calendar_server.controllers.EventController;
import com.example.calendar_server.models.Event;
import com.example.calendar_server.models.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventControllerTests {
    private ICalDavClient calDavClient;

    @BeforeEach
    void setUp() {
        calDavClient = mock(ICalDavClient.class);
    }

    void setUpWithNoResults() {
        when(calDavClient.getEvents(any(Request.class)))
                .thenReturn(new ArrayList<>());
    }

    void setUpWithResults() {
        var events = new ArrayList<Event>();
        events.add(mock(Event.class));
        events.add(mock(Event.class));
        events.add(mock(Event.class));
        when(calDavClient.getEvents(any(Request.class)))
                .thenReturn(new ArrayList<>(events));
    }

    @Test
    void GetAllEvents_returns_204_when_empty_array() {
        setUpWithNoResults();
        var c = new EventController(calDavClient);
        var res = c.GetAllEvents();
        assertEquals(res.getStatusCode(), HttpStatusCode.valueOf(204));
    }

    @Test
    void GetPastEvents_returns_204_when_empty_array() {
        setUpWithNoResults();
        var c = new EventController(calDavClient);
        var res = c.GetPastEvents();
        assertEquals(res.getStatusCode(), HttpStatusCode.valueOf(204));
    }

    @Test
    void GetEvents_returns_204_when_empty_array() {
        setUpWithNoResults();
        var c = new EventController(calDavClient);
        var res = c.GetEvents(LocalDate.now(), LocalDate.now());
        assertEquals(res.getStatusCode(), HttpStatusCode.valueOf(204));
    }

    @Test
    void GetAllEvents_returns_200_when_empty_array() {
        setUpWithResults();
        var c = new EventController(calDavClient);
        var res = c.GetAllEvents();
        assertEquals(res.getStatusCode(), HttpStatusCode.valueOf(200));
    }

    @Test
    void GetPastEvents_returns_200_when_empty_array() {
        setUpWithResults();
        var c = new EventController(calDavClient);
        var res = c.GetPastEvents();
        assertEquals(res.getStatusCode(), HttpStatusCode.valueOf(200));
    }

    @Test
    void GetEvents_returns_200_when_empty_array() {
        setUpWithResults();
        var c = new EventController(calDavClient);
        var res = c.GetEvents(LocalDate.now(), LocalDate.now());
        assertEquals(res.getStatusCode(), HttpStatusCode.valueOf(200));
    }

    @Test
    void GetEvents_returns_4xx_when_end_is_before_start() {
        setUpWithResults();
        var c = new EventController(calDavClient);
        var res = c.GetEvents(LocalDate.now(), LocalDate.now().minusDays(20));
        assertEquals(res.getStatusCode(), HttpStatusCode.valueOf(400));
    }
}
