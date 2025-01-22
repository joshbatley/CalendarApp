using CalendarCore.Handler;
using Microsoft.AspNetCore.Mvc;

namespace CalendarCore.Controllers;

[ApiController]
[Route("/events")]
public class EventsController(IEventHandler eventHandler) : ControllerBase
{
    [HttpGet("")]
    public async Task<IActionResult> GetEvents([FromQuery]DateTime? start, [FromQuery]DateTime? end)
    {
        if (start?.CompareTo(end?.Date) > 0)
        {
            return BadRequest("End date is before start date");
        }
        var res = await eventHandler.GetEvents(start?.Date, end?.Date);
        return res.Any() ? Ok(res) : NoContent();
    } 
    
    [HttpGet("past")]
    public async Task<IActionResult> GetPastEvents()
    {
        var res = await eventHandler.GetEvents(DateTime.MinValue.Date, DateTime.Now.Date);
        return res.Any() ? Ok(res) : NoContent();
    }
    
    [HttpGet("all")]
    public async Task<IActionResult> GetAllEvents()
    {
        var res = await eventHandler.GetEvents(DateTime.Now.Date, null);
        return res.Any() ? Ok(res) : NoContent();
    }
}
