package com.notbadcode.explorewithme.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public final class CommonDateTime {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime ofString(String dateTime) {
        return LocalDateTime.parse(dateTime, getFormatter());
    }

    public static String ofLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }


    public static DateTimeFormatter getFormatter() {
        return formatter;
    }
}
