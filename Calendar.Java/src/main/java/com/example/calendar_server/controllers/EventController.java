package com.example.calendar_server.controllers;

import com.example.calendar_server.clients.ICalDavClient;
import com.example.calendar_server.models.Request;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/events")
public class EventController {

    private final ICalDavClient calDevClient;

    public EventController(ICalDavClient calDevClient) {
        this.calDevClient = calDevClient;
    }

    @GetMapping("")
    public ResponseEntity GetEvents(
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate end
    ) {
        if ((end != null || start != null) && end.isBefore(start)) {
            return ResponseEntity.badRequest().body("{\"Error\": \"End time must be before start time\"}");
        }

        var response = calDevClient.getEvents(new Request(
                start != null ? start.atStartOfDay() : null,
                end != null ? end.atStartOfDay() : null));
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("past")
    public ResponseEntity GetPastEvents() {
        var response = calDevClient.getEvents(new Request(LocalDate.EPOCH.atStartOfDay(), LocalDate.now().atStartOfDay()));
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("all")
    public ResponseEntity GetAllEvents() {
        var response = calDevClient.getEvents(new Request(LocalDate.now().atStartOfDay(), null));
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }
}
