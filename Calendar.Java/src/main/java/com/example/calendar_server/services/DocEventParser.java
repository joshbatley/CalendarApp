package com.example.calendar_server.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class DocEventParser {

    private Document document;
    private static final Logger logger = LogManager.getLogger(DocEventParser.class);

    public DocEventParser(String xml)  {
        try {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            logger.error("Failed to parse events", e);
            document = null;
        }
    }

    public <T> List<T> mapEventsTo(EventParser<T> parser) {
        if (document == null) {
            return new ArrayList<>();
        }
        var events = new ArrayList<T>();
        var calendarDataElement = document.getElementsByTagNameNS("urn:ietf:params:xml:ns:caldav", "calendar-data");

        for (var i = 0; i < calendarDataElement.getLength(); i++) {
            events.addAll(parser.parseEvents(calendarDataElement.item(i)));
        }
        return events.stream().filter(Objects::nonNull).toList();
    }
}
