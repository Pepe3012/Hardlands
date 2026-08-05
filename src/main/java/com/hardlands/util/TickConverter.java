package com.hardlands.util;

/**
 * Provides conversions between Minecraft ticks and common time units.
 */
public final class TickConverter {
    public static final int TICKS_PER_SECOND = 20;
    public static final int SECONDS_PER_MINUTE = 60;
    public static final int MINUTES_PER_HOUR = 60;

    private TickConverter() {}

    public static int secondsToTicks(int seconds) {
        return seconds * TICKS_PER_SECOND;
    }

    public static int minutesToTicks(int minutes) {
        return secondsToTicks(minutes * SECONDS_PER_MINUTE);
    }

    public static int hoursToTicks(int hours) {
        return minutesToTicks(hours * MINUTES_PER_HOUR);
    }

    public static double ticksToSeconds(int ticks) {
        return (double) ticks / TICKS_PER_SECOND;
    }

    public static double ticksToMinutes(int ticks) {
        return ticksToSeconds(ticks) / SECONDS_PER_MINUTE;
    }

    public static double ticksToHours(int ticks) {
        return ticksToMinutes(ticks) / MINUTES_PER_HOUR;
    }
}