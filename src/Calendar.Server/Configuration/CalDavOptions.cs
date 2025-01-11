namespace CalendarCore.Configuration;

public class CalDavOptions
{
    public const string Key = "CalDav";
    public required string Username { get; init; }
    public required string Password { get; init; }
    public required string UserId { get; init; }
    public required string CalendarId { get; init; }
}
