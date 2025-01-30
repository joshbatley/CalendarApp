package com.example.calendar_server.clients;

import com.example.calendar_server.configuration.CalDavOptions;
import com.example.calendar_server.models.Event;
import com.example.calendar_server.models.Request;
import com.example.calendar_server.services.DocEventParser;
import com.example.calendar_server.services.XMLToICalEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

import java.util.Base64;

@Service
public class CalDavClient implements ICalDavClient {

    private final RestClient restClient;
    private final CalDavOptions options;
    private static final String baseUrl = "https://caldav.icloud.com/";
    private static final String pathUri = "calendars";
    private static final Logger logger = LogManager.getLogger(CalDavClient.class);

    private String basicAuth() {
        return Base64.getEncoder().encodeToString((options.Username + ":" + options.Password).getBytes());
    }

    private String getPath() {
        return options.UserId + "/" + pathUri + "/" + options.CalendarId;
    }

    public CalDavClient(RestClient.Builder builder, CalDavOptions options) {
        this.options = options;
        this.restClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("Depth", "1")
                .defaultHeader("Authorization", "Basic " + basicAuth())
                .build();
    }

    @Override
    @Cacheable(value = "response", key = "#request.key()")
    public List<Event> getEvents(Request request) {
        var response = fetch(request.body());

        // Move into handler or controller
        return new DocEventParser(response)
                    .mapEventsTo(XMLToICalEvent::parse);

    }

    private String fetch(String body) {
        return restClient
                .method(HttpMethod.valueOf("REPORT"))
                .uri(getPath())
                .contentType(MediaType.APPLICATION_XML)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    logger.error("Failed to fetch response", response.getBody());
                })
                .body(new ParameterizedTypeReference<>() {});
    }


}
