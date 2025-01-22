using System.Net.Http.Headers;
using System.Text;
using System.Xml.Linq;
using CalendarCore.Configuration;
using CalendarCore.Models;
using Ical.Net;
using Microsoft.Extensions.Caching.Memory;
using Microsoft.Extensions.Options;
using ILogger = Serilog.ILogger;

namespace CalendarCore.Clients;

public interface ICalDavClient
{
    public Task<IReadOnlyList<Event>> GetEvents(CalDavRequest calDavRequest);
}

public class CalDavClient : ICalDavClient
{
    private const string BaseUrl = "https://caldav.icloud.com/";
    private const string PathUrl = "calendars";
    private readonly CalDavOptions _options;
    private readonly HttpClient _httpClient;
    private readonly IMemoryCache _cache;
    private readonly ILogger _logger;
    
    private readonly MemoryCacheEntryOptions _cacheOptions = new MemoryCacheEntryOptions().SetSlidingExpiration(TimeSpan.FromMinutes(5));
    
    public CalDavClient(HttpClient httpClient, IOptions<CalDavOptions> options, IMemoryCache cache, ILogger logger)
    {
        _httpClient = httpClient;
        _cache = cache;
        _logger = logger;
        _options = options.Value;
        
        httpClient.BaseAddress = new Uri(BaseUrl);
        httpClient.DefaultRequestHeaders.Add("Depth", "1");
        httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", BasicAuth);
    }
    public async Task<IReadOnlyList<Event>> GetEvents(CalDavRequest calDavRequest)
    {
        var response = await FetchDataWithCaching(calDavRequest.KeyFromRequest, calDavRequest.ToXmlString());
        return ParseXmlString(response);
    }
    private string ConstructUrl => $"{_options.UserId}/{PathUrl}/{_options.CalendarId}";
    private string BasicAuth => Convert.ToBase64String(Encoding.UTF8.GetBytes($"{_options.Username}:{_options.Password}"));
    private async Task<string> FetchDataWithCaching(string key, string message)
    {
        if (_cache.TryGetValue(key, out string? cachedData))
        {
            _logger.Information("Key found in cache: {Key}, returning cached response", key);
            return cachedData ?? "";
        }
        
        var response =  await _httpClient.SendAsync(new HttpRequestMessage(new HttpMethod("REPORT"), ConstructUrl)
        {
            Content = new StringContent(message, Encoding.UTF8, "application/xml")
        });
        
        if (!response.IsSuccessStatusCode)
        {
            throw new Exception($"Failed to fetch calendar data. Status code: {response.StatusCode}");
        }

        var content = await response.Content.ReadAsStringAsync();
        _cache.Set(key, content, _cacheOptions);

        return content;
    }
    private static IReadOnlyList<Event> ParseXmlString(string xmlString) => 
        XDocument.Parse(xmlString)
            .Descendants(XName.Get("calendar-data", "urn:ietf:params:xml:ns:caldav"))
            .SelectMany(e =>
                CalendarCollection.Load(e.Value)
                    .SelectMany(q =>
                        q.Events.Select(Event.FromCalendarEvent)))
            .ToList();
}
