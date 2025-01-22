using CalendarCore.Clients;
using CalendarCore.Handler;
using CalendarCore.Models;
using FluentAssertions;
using Moq;
using Serilog;
using EventHandler = CalendarCore.Handler.EventHandler;

namespace Calendar.Tests;

public class EventHandlerTests
{
    [Fact]
    public async Task EventHandler_ShouldReturnEmptyList_IfCallFails()
    {
        var (eventHandler, _) = SetupWithThrow();
        var response = await eventHandler.GetEvents(DateTime.Now, DateTime.Now);
        response.Should().BeEmpty();

    }
    
    [Fact]
    public async Task EventHandler_ShouldReturnEmptyList_IfCallSucceeds()
    {
        var events = new[]
        {
            new Event("summary", new DateTimeOffset(), new DateTimeOffset(), "location", "description","", false, false),
            new Event("", new DateTimeOffset(), new DateTimeOffset(), "", "","", false, false),
            new Event("", new DateTimeOffset(), new DateTimeOffset(), "", "","", false, false)
        };
        var (eventHandler, _) = SetupWithSuccess(events);
        var response = await eventHandler.GetEvents(DateTime.Now, DateTime.Now);
        response.Should().HaveCount(events.Length);
        response.First().Should().Be(events.First());
    }

    private static (IEventHandler, Mock<ICalDavClient>) SetupWithThrow()
    {
        var client = new Mock<ICalDavClient>();
        client.Setup(l => l.GetEvents(It.IsAny<CalDavRequest>())).ThrowsAsync(new Exception("Throwing")).Verifiable();
        return (new EventHandler(client.Object, new Mock<ILogger>().Object), client);
    }
    
    private static  (IEventHandler, Mock<ICalDavClient>) SetupWithSuccess(IReadOnlyList<Event> events)
    {
        var client = new Mock<ICalDavClient>();
        client.Setup(l => l.GetEvents(It.IsAny<CalDavRequest>())).ReturnsAsync(events).Verifiable();
        return (new EventHandler(client.Object, new Mock<ILogger>().Object), client);
    }
}
