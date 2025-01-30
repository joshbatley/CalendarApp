package com.example.calendar_server.services;

import org.w3c.dom.Node;

import java.util.List;

@FunctionalInterface
public interface EventParser<T> {
    List<T> parseEvents(Node data);
}
