package dev.canable.cablepunishments.utils;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeFormatter {
    public static String formatDuration(long millis) {
        if (millis < 1000) return "0s";

        LinkedHashMap<String, Long> timeUnits = getStringLongLinkedHashMap(millis);

        StringBuilder result = new StringBuilder();
        int count = 0;
        for (Map.Entry<String, Long> entry : timeUnits.entrySet()) {
            if (entry.getValue() > 0) {
                result.append(entry.getValue()).append(entry.getKey()).append(" ");
                count++;
                if (count == 3) break;
            }
        }

        return result.toString().trim();
    }

    private static @NotNull LinkedHashMap<String, Long> getStringLongLinkedHashMap(long millis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        long hours = TimeUnit.MINUTES.toHours(minutes);
        long days = TimeUnit.HOURS.toDays(hours);
        long months = days / 30;
        long years = months / 12;

        days %= 30;
        months %= 12;
        hours %= 24;
        minutes %= 60;
        seconds %= 60;

        LinkedHashMap<String, Long> timeUnits = new LinkedHashMap<>();
        timeUnits.put("y", years);
        timeUnits.put("mo", months);
        timeUnits.put("d", days);
        timeUnits.put("h", hours);
        timeUnits.put("m", minutes);
        timeUnits.put("s", seconds);
        return timeUnits;
    }

    public static long parseDuration(String duration) {
        if (duration == null || duration.isBlank()) return 0;

        long totalMillis = 0L;
        Pattern pattern = Pattern.compile("(\\d+)([a-zA-Z]+)");
        Matcher matcher = pattern.matcher(duration);

        try {
            int matches = 0;
            while (matcher.find()) {
                matches++;
                long value = Long.parseLong(matcher.group(1));
                String unit = matcher.group(2).toLowerCase();

                switch (unit) {
                    case "y" -> totalMillis += TimeUnit.DAYS.toMillis(value * 365);
                    case "mo" -> totalMillis += TimeUnit.DAYS.toMillis(value * 30);
                    case "d" -> totalMillis += TimeUnit.DAYS.toMillis(value);
                    case "h" -> totalMillis += TimeUnit.HOURS.toMillis(value);
                    case "m" -> totalMillis += TimeUnit.MINUTES.toMillis(value);
                    case "s" -> totalMillis += TimeUnit.SECONDS.toMillis(value);
                    default -> {
                        return -1; // Unknown unit
                    }
                }
            }

            // If the string didn't match anything meaningful
            return matches == 0 ? -1 : totalMillis;
        } catch (Exception e) {
            return -1;
        }
    }
}