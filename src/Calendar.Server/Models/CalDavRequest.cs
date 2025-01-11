namespace CalendarCore.Models;

public class CalDavRequest(DateTime? startDate, DateTime? endDate)
{
    private readonly string _startDate = startDate?.ToString("yyyyMMddTHHmmssZ") ?? string.Empty;
    private readonly string _endDate  = endDate?.ToString("yyyyMMddTHHmmssZ") ?? string.Empty;

    public string KeyFromRequest => _startDate + "__" + _endDate;
    
    private string TimeRangeString() => 
        (_startDate, _endDate) switch
        {
            ("", "") => string.Empty,
            (_, "") => $"<C:time-range start='{_startDate}' />",
            ("", _) => $"<C:time-range end='{_endDate}' />",
            _ => $"<C:time-range start='{_startDate}' end='{_endDate}' />"
        };
    
    public string ToXmlString() => $@"
        <C:calendar-query xmlns:D='DAV:' xmlns:C='urn:ietf:params:xml:ns:caldav'>
            <D:prop>
                <D:getetag/>
                <C:calendar-data/>
            </D:prop>
            <C:filter>
                <C:comp-filter name='VCALENDAR'>
                    <C:comp-filter name='VEVENT'>
                        {TimeRangeString()}
                    </C:comp-filter>
                </C:comp-filter>
            </C:filter>
        </C:calendar-query>";
}
