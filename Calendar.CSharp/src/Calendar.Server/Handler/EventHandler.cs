using CalendarCore.Clients;
using CalendarCore.Models;
using ILogger = Serilog.ILogger;

namespace CalendarCore.Handler;

public interface IEventHandler
{
    public Task<IEnumerable<Event>> GetEvents(DateTime? start, DateTime? end);
}

public class EventHandler : IEventHandler
{
    private readonly ICalDavClient _calDavClient;
    private readonly ILogger _logger;

    public EventHandler(ICalDavClient calDavClient, ILogger logger) 
        => (_calDavClient, _logger) = (calDavClient, logger);
    
    public async Task<IEnumerable<Event>> GetEvents(DateTime? start, DateTime? end)
    {
        try
        {
            var req = new CalDavRequest(start, end);
            var res = await _calDavClient.GetEvents(req);
            return res;
        } 
        catch (Exception ex)
        {
            _logger.Error("Failed to get events. Exception: {Ex}", ex.Message);
            return [];
        }
    }
}
