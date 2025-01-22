package com.example.calendar_server.clients;

import com.example.calendar_server.configuration.CalDavOptions;
import com.example.calendar_server.models.Event;
import com.example.calendar_server.models.Request;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.util.Base64;
import java.util.Objects;

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
        return xmlToEvents(response);
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

    private List<Event> xmlToEvents(String xmlString) {
        if (xmlString == null || xmlString.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            var doc = parseXml(xmlString);
            return mapToEvents(doc);
        } catch (Exception e) {
            logger.error("Failed to parse events", e);
            return new ArrayList<>();
        }
    }

    private Document parseXml(String xmlString) throws ParserConfigurationException, IOException, SAXException {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlString)));
    }

    private List<Event> mapToEvents(Document doc) throws ParserException, IOException {
        var events = new ArrayList<Event>();
        var calendarDataElement = doc.getElementsByTagNameNS("urn:ietf:params:xml:ns:caldav", "calendar-data");

        for (var i = 0; i < calendarDataElement.getLength(); i++) {
            events.addAll(parseEvents(calendarDataElement.item(i)));
        }
        return events;
    }

    private List<Event> parseEvents(Node data) throws ParserException, IOException {
        if (data == null || data.getFirstChild() == null) {
            return new ArrayList<>();
        }
        var calendar = new CalendarBuilder().build(new StringReader(data.getTextContent()));
        return calendar.getComponents().stream()
                .map(Event::fromCalendarComponent)
                .filter(Objects::nonNull)
                .toList();
    }
}
