package com.example.calendar_server.clients;

import com.example.calendar_server.models.Event;
import com.example.calendar_server.models.Request;

import java.util.List;

public interface ICalDavClient {
    public List<Event> getEvents(Request request);
}
