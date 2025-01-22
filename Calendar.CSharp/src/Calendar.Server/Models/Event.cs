using Ical.Net.CalendarComponents;

namespace CalendarCore.Models;

public record Event(string Summary,
    DateTimeOffset Start,
    DateTimeOffset End,
    string Location,
    string Description,
    string Id,
    bool HasPassed,
    bool IsActive
)
{
    
    
    
    public static Event FromCalendarEvent(CalendarEvent ev)
    {
        return new Event(
            ev.Summary, 
            ev.Start.AsDateTimeOffset, 
            ev.End.AsDateTimeOffset, 
            ev.Location, 
            ev.Description,
            ev.Uid,
            DateTime.Now.Date.CompareTo(ev.Start.Date) >= 0,
            DateTime.Now.Date.CompareTo(ev.Start.Date) >= 0 && DateTime.Now.Date.CompareTo(ev.End.Date) <= 0
        );
    }
}