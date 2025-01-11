using CalendarCore.Controllers;
using CalendarCore.Handler;
using CalendarCore.Models;
using FluentAssertions;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Moq;

namespace Calendar.Tests;

public class EventControllerTests
{
    [Fact]
    public async Task GetAllEvent_returns200_WhenEvents()
    {
        var client = SetupWithEvents();
        var response = await client.GetAllEvents();
        response.Should().BeOfType<OkObjectResult>();
    }
    
    [Fact]
    public async Task GetAllEvent_returns204_WhenNoEvents()
    {
        var client = SetupWithNoEvents();
        var response = await client.GetAllEvents();
        response.Should().BeOfType<NoContentResult>();
    }
    
    [Fact]
    public async Task GePastEvent_returns200_WhenEvents()
    {
        var client = SetupWithEvents();
        var response = await client.GetPastEvents();
        response.Should().BeOfType<OkObjectResult>();
    }
    
    
    [Fact]
    public async Task GetPastEvent_returns204_WhenNoEvents()
    {
        var client = SetupWithNoEvents();
        var response = await client.GetPastEvents();
        response.Should().BeOfType<NoContentResult>();
    }
    
    [Fact]
    public async Task GetEvent_returns200_WhenEvents()
    {
        var client = SetupWithEvents();
        var response = await client.GetPastEvents();
        response.Should().BeOfType<OkObjectResult>();
    }
    
    [Fact]
    public async Task GetEvent_returns204_WhenNoEvents()
    {
        var client = SetupWithNoEvents();
        var response = await client.GetEvents(DateTime.Now, DateTime.Now.AddDays(2));
        response.Should().BeOfType<NoContentResult>();
    }
    
    [Fact]
    public async Task GetEvent_returns400_WhenEndDateIsBeforeStart()
    {
        var client = SetupWithNoEvents();
        var response = await client.GetEvents(DateTime.Now, DateTime.Now.AddDays(-10));
        response.Should().BeOfType<BadRequestObjectResult>();
    }

    
    private IEnumerable<Event> events =
    [
        new ("summary", new DateTimeOffset(), new DateTimeOffset(), "location", "description", "", false, false),
        new ("", new DateTimeOffset(), new DateTimeOffset(), "", "","", false, false),
        new ("", new DateTimeOffset(), new DateTimeOffset(), "", "","", false, false),
    ];

    private EventsController SetupWithEvents()
    {
        var mock = new Mock<IEventHandler>();
            mock.Setup(x => x.GetEvents(It.IsAny<DateTime?>(), It.IsAny<DateTime?>()))
            .ReturnsAsync(events).Verifiable();
        
        return new EventsController(mock.Object);
    }
    
    private EventsController SetupWithNoEvents()
    {
        var mock = new Mock<IEventHandler>();
        mock.Setup(x => x.GetEvents(It.IsAny<DateTime?>(), It.IsAny<DateTime?>()))
            .ReturnsAsync([]).Verifiable();
        
        return new EventsController(mock.Object);
    }
}
