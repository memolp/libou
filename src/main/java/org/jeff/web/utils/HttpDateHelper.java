package org.jeff.web.utils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class HttpDateHelper
{
    public static String formatToRFC822()
    {
        return formatToRFC822(new Date().getTime());
    }

    public static String formatToRFC822(long millis)
    {
        ZonedDateTime time = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
        return formatter.format(time);
    }
}
