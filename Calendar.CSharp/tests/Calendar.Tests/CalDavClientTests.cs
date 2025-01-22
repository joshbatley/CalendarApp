using System.Net;
using CalendarCore.Clients;
using CalendarCore.Configuration;
using CalendarCore.Models;
using FluentAssertions;
using Microsoft.Extensions.Caching.Memory;
using Microsoft.Extensions.Options;
using Moq;
using Moq.Protected;
using Serilog;

namespace Calendar.Tests;

public class CalDavClientTests
{
    private readonly IOptions<CalDavOptions> _options = new OptionsWrapper<CalDavOptions>(new CalDavOptions
    {
        Username = "username",
        Password = "password",
        UserId = "userId",
        CalendarId = "calendarId"
    });
    private readonly Mock<ILogger> _logger = new();
    private readonly IMemoryCache _cache = new MemoryCache(new MemoryCacheOptions
    {
        TrackStatistics = true
    });

    [Fact]
    public async Task CalDavClient_ReturnsAListOfEvents()
    {
        var (client, _) = SetupWithSuccessfulApiCall(ValidResponse);
        var response = await client.GetEvents(new CalDavRequest(DateTime.Now.Date, DateTime.Now.Date));
        response.Should().NotBeEmpty();
    }

    [Fact]
    public async Task CalDavClient_ShouldGetCacheAfterSecondCall()
    {
        var (client, httpClient) = SetupWithSuccessfulApiCall(ValidResponse);
        httpClient.Invocations.Should().BeEmpty();

        var stats = _cache.GetCurrentStatistics();
        stats?.CurrentEntryCount.Should().Be(0);
        stats?.TotalHits.Should().Be(0);
        stats?.TotalMisses.Should().Be(0);
        
        await client.GetEvents(new CalDavRequest(DateTime.Now.Date, DateTime.Now.Date));
        httpClient.Invocations.Should().HaveCount(1);
       
        stats = _cache.GetCurrentStatistics();
        stats?.CurrentEntryCount.Should().Be(1);
        stats?.TotalHits.Should().Be(0);
        stats?.TotalMisses.Should().Be(1);
        
        await client.GetEvents(new CalDavRequest(DateTime.Now.Date, DateTime.Now.Date));
        
        httpClient.Invocations.Should().HaveCount(1);
        stats = _cache.GetCurrentStatistics();
        stats.CurrentEntryCount.Should().Be(1);
        stats.TotalHits.Should().Be(1);
        
    }

    [Fact]
    public async Task CalDavClient_ThrowsIfRequestIsNotSuccessful()
    {
        var (client, _) = SetupWithFailureApiCall();
        await Assert.ThrowsAsync<Exception>(async () =>
            await client.GetEvents(new CalDavRequest(DateTime.Now.Date, DateTime.Now.Date)));
    }
    
    private (ICalDavClient, Mock<HttpMessageHandler>) SetupWithSuccessfulApiCall(string content)
    {
        var mock = new Mock<HttpMessageHandler>();
        var response = new HttpResponseMessage
        {
            Content = new StringContent(content),
            StatusCode = HttpStatusCode.OK,
        };
        mock.Protected()
            .Setup<Task<HttpResponseMessage>>(
                "SendAsync",
                ItExpr.Is<HttpRequestMessage>(req => req.Method == new HttpMethod("REPORT")),
                ItExpr.IsAny<CancellationToken>()
            ).ReturnsAsync(response).Verifiable();

        return (new CalDavClient( new HttpClient(mock.Object), _options, _cache, _logger.Object), mock);
    }
    
    private (ICalDavClient, Mock<HttpMessageHandler>) SetupWithFailureApiCall()
    {
        var mock = new Mock<HttpMessageHandler>();
        var response = new HttpResponseMessage
        {
            Content = new StringContent(""),
            StatusCode = HttpStatusCode.BadRequest,
        };
        mock.Protected()
            .Setup<Task<HttpResponseMessage>>(
                "SendAsync",
                ItExpr.Is<HttpRequestMessage>(req => req.Method == new HttpMethod("REPORT")),
                ItExpr.IsAny<CancellationToken>()
            ).ReturnsAsync(response).Verifiable();

        return (new CalDavClient( new HttpClient(mock.Object), _options, _cache, _logger.Object), mock);
    }

    private string ValidResponse = @"<?xml version=""1.0"" encoding=""UTF-8"" standalone=""yes""?>
<multistatus xmlns=""DAV:"">
    
        <response>
            <href>/0000000000/calendars/0000000-0000-0000-00000-000000000/11111111-1111-1111-1111-11111111111.ics</href>
            <propstat>
                <prop>
                    
                        
                            <getetag xmlns=""DAV:"">""m3zrp1qg""</getetag>
                        
                    
                        
                            <calendar-data xmlns=""urn:ietf:params:xml:ns:caldav""><![CDATA[BEGIN:VCALENDAR
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
    
</multistatus>";
}
