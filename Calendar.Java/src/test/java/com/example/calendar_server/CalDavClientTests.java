package com.example.calendar_server;

import com.example.calendar_server.clients.CalDavClient;
import com.example.calendar_server.configuration.CalDavOptions;
import com.example.calendar_server.models.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CalDavClientTests {

    private RestClient.Builder restClientBuilder;
    private CalDavOptions calDavOptions;
    private RestClient restClient;
    private Request request;
    private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        this.restClientBuilder = mock(RestClient.Builder.class);
        this.calDavOptions = mock(CalDavOptions.class);
        this.restClient = mock(RestClient.class);
        this.request = mock(Request.class);
        when(request.body()).thenReturn("Some string");
        when(restClientBuilder.baseUrl(any(String.class))).thenReturn(restClientBuilder);
        when(restClientBuilder.defaultHeader(any(String.class),any(String.class))).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);
        var requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        var RequestBodySpec = mock(RestClient.RequestBodySpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.method(HttpMethod.valueOf("REPORT"))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(RequestBodySpec);
        when(RequestBodySpec.contentType(MediaType.APPLICATION_XML)).thenReturn(RequestBodySpec);
        when(RequestBodySpec.body(any(String.class))).thenReturn(RequestBodySpec);
        when(RequestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    }

    void setupWithValidResponse() {
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(validResponse);
    }

    void setupWithInvalidResponse() {
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn("Some Invalid response");
    }

    void setupWithValidResponseNoData() {
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(validResponseNoData);
    }

    @Test
    void should_return_empty_array_when_request_fails_or_invalid() {
        setupWithInvalidResponse();
        var c = new CalDavClient(restClientBuilder ,calDavOptions);
        var res = c.getEvents(request);
        assertTrue(res.isEmpty());
    }

    @Test
    void should_return_empty_array_when_no_events() {
        setupWithValidResponseNoData();
        var c = new CalDavClient(restClientBuilder ,calDavOptions);
        var res = c.getEvents(request);
        assertTrue(res.isEmpty());
    }

    @Test
    void should_return_array_when_request_valid() {
        setupWithValidResponse();
        var c = new CalDavClient(restClientBuilder ,calDavOptions);
        var res = c.getEvents(request);
        assertTrue(!res.isEmpty());
    }


    private String validResponseNoData= """
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<multistatus xmlns="DAV:">
    
        <response>
            <href>/0000000000/calendars/0000000-0000-0000-00000-000000000/11111111-1111-1111-1111-11111111111.ics</href>
            <propstat>
                <prop>
                    
                            <getetag xmlns="DAV:">"m3zrp1qg"</getetag>
                        
                    
                        <calendar-data xmlns="urn:ietf:params:xml:ns:caldav"></calendar-data>
                            
                        
                    
                </prop>
                <status>HTTP/1.1 200 OK</status>
            </propstat>
            
        </response>
    
</multistatus>
            """;

    private String validResponse = """
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<multistatus xmlns="DAV:">
    
        <response>
            <href>/0000000000/calendars/0000000-0000-0000-00000-000000000/11111111-1111-1111-1111-11111111111.ics</href>
            <propstat>
                <prop>
                    
                            <getetag xmlns="DAV:">"m3zrp1qg"</getetag>
                        
                    
                        
                            <calendar-data xmlns="urn:ietf:params:xml:ns:caldav"><![CDATA[BEGIN:VCALENDAR
CALSCALE:GREGORIAN
PRODID:-//Apple Inc.//iPhone OS 17.6.1//EN
VERSION:2.0
BEGIN:VEVENT
CREATED:20241127T105310Z
DTEND;VALUE=DATE:20250111
DTSTAMP:20241127T105310Z
DTSTART;VALUE=DATE:20250110
LAST-MODIFIED:20241127T105310Z
SEQUENCE:0
SUMMARY:Naomi night out
UID:0CE89719-0000-4A3B-A1F9-27F2948FE077
URL;VALUE=URI:
X-APPLE-CREATOR-IDENTITY:com.apple.mobilecal
X-APPLE-CREATOR-TEAM-IDENTITY:0000000000
END:VEVENT
END:VCALENDAR
]]></calendar-data>
                        
                    
                </prop>
                <status>HTTP/1.1 200 OK</status>
            </propstat>
            
        </response>
    
</multistatus>
           """;
}
