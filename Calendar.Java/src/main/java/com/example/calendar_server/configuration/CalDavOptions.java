package com.example.calendar_server.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CalDavOptions {
    public static final String KEY = "CalDav";

    @Value("${"+ KEY + ".Username}")
    public String Username;

    @Value("${"+ KEY + ".Password}")
    public String Password;

    @Value("${"+ KEY + ".UserId}")
    public String UserId;

    @Value("${"+ KEY + ".CalendarId}")
    public String CalendarId;
}
