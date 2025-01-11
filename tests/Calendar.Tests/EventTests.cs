using CalendarCore.Models;
using FluentAssertions;
using Ical.Net.CalendarComponents;
using Ical.Net.DataTypes;

namespace Calendar.Tests;

public class EventTests
{
    [Theory]
    [InlineData("2024/01/01", true)]
    [InlineData("4000/01/01", false)]
    public void Event_CanBeCreatedFromICalCalendarEvent_AndUpdatesHasPasssedCorrectly(string startDate, bool hasPassed)
    {
        var calEv = new CalendarEvent
        {
            Summary = "event",
            Description = "",
            End = new CalDateTime(DateTime.Now.AddYears(99)),
            Location = "",
            Start = new CalDateTime(DateTime.Parse(startDate)),
        };
        var ev = Event.FromCalendarEvent(calEv);

        ev.Summary.Should().Be("event");
        ev.HasPassed.Should().Be(hasPassed);
    }
    
    [Theory]
    [InlineData(true, true)]
    [InlineData(false, false)]
    public void Event_CanBeCreatedFromICalCalendarEvent_AndUpdatesIsActiveCorrectly(bool useHugeRange, bool isActive)
    {
        var calEv = new CalendarEvent
        {
            Summary = "event",
            Description = "",
            End = useHugeRange ? new CalDateTime(DateTime.Now.AddYears(99)) : new CalDateTime(DateTime.Now.AddYears(-1).AddDays(-10)),
            Location = "",
            Start = useHugeRange ? new CalDateTime(DateTime.Now.AddYears(-99)) : new CalDateTime(DateTime.Now.AddYears(-1).AddDays(-8)),
        };
        var ev = Event.FromCalendarEvent(calEv);

        ev.Summary.Should().Be("event");
        ev.IsActive.Should().Be(isActive);
    }
}
