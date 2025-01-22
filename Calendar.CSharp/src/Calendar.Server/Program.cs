using CalendarCore.Clients;
using CalendarCore.Configuration;
using CalendarCore.Handler;
using Serilog;
using EventHandler = CalendarCore.Handler.EventHandler;
using ILogger = Serilog.ILogger;

var builder = WebApplication.CreateBuilder(args);
var logger = new LoggerConfiguration()
    .ReadFrom.Configuration(builder.Configuration)
    .CreateLogger();

builder.Configuration.AddJsonFile("appsettings.json", optional: true, reloadOnChange: true);
builder.Configuration.AddEnvironmentVariables();

builder.Logging.AddSerilog(logger);
builder.Services.AddSingleton<ILogger>(logger);
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowLocalhost",
        policy =>
        {
            policy.AllowAnyOrigin()
                .AllowAnyHeader()
                .AllowAnyMethod();
        });
});


builder.Services.Configure<CalDavOptions>(builder.Configuration.GetSection(CalDavOptions.Key));
builder.Services.AddScoped<ICalDavClient, CalDavClient>();
builder.Services.AddScoped<IEventHandler, EventHandler>();


builder.Services.AddHttpClient();
builder.Services.AddSwaggerGen();
builder.Services.AddControllers();
builder.Services.AddMemoryCache();
builder.Services.AddEndpointsApiExplorer();

var app = builder.Build();

app.UseHttpsRedirection();
app.UseSwagger();
app.UseSwaggerUI();
app.MapControllers();
app.UseCors("AllowLocalhost");

app.Run();