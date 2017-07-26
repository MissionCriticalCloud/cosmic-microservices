package com.github.missioncriticalcloud.cosmic.usage.core.utils;

import java.math.RoundingMode;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class FormatUtils {

    public static final int DEFAULT_SCALE = 2;
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String HUMAN_READABLE_DATE_FORMAT = "MMMM yyyy";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT);
    public static final DateTimeFormatter HUMAN_READABLE_DATE_FORMATTER = DateTimeFormat.forPattern(HUMAN_READABLE_DATE_FORMAT);


    private FormatUtils() {
        // Empty constructor
    }

}
