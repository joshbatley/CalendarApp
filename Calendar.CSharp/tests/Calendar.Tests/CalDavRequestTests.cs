using CalendarCore.Models;
using FluentAssertions;

namespace Calendar.Tests;

public class CalDavRequestTests
{
    [Theory]
    [InlineData(null, null, "","<C:time-range start='20240101T000000Z' end='20240101T000000Z' />")]
    [InlineData("2024/01/01", null, "<C:time-range start='20240101T000000Z' />", "end='20240101T000000Z'")]
    [InlineData(null, "2024/01/01", "<C:time-range end='20240101T000000Z' />", "start='20240101T000000Z'")]
    [InlineData("2024/01/01", "2024/01/01", "<C:time-range start='20240101T000000Z' end='20240101T000000Z' />", "")]
    public void CalDavRequest_ShouldIncludeRangeXml_BasedOnDatesPassedIn(string? start, string? end, string expected, string notExpected)
    {
        DateTime? startDate =  start is null ? null : DateTime.Parse(start).Date;
        DateTime? endDate = end is null ? null : DateTime.Parse(end).Date;
        var request = new CalDavRequest(startDate, endDate);

        if (!string.IsNullOrEmpty(expected))
        {   
            request.ToXmlString().Should().Contain(expected);
        }

        if (!string.IsNullOrEmpty(notExpected))
        {
            request.ToXmlString().Should().NotContain(notExpected);
        }
    }

    [Theory]
    [InlineData(null, null, "__")]
    [InlineData("2024/01/01", null, "20240101T000000Z__")]
    [InlineData(null, "2024/01/01", "__20240101T000000Z")]
    [InlineData("2024/01/01", "2024/01/01", "20240101T000000Z__20240101T000000Z")]
    public void CalDavRequest_ShouldReturnKey_BasedOnDatesPassedIn(string? start, string? end, string expected) 
    {
        DateTime? startDate =  start is null ? null : DateTime.Parse(start).Date;
        DateTime? endDate = end is null ? null : DateTime.Parse(end).Date; 
        var request = new CalDavRequest(startDate, endDate);
        request.KeyFromRequest.Should().Contain(expected); 
    }
}
