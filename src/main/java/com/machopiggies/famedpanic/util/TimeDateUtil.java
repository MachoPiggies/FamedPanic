package com.machopiggies.famedpanic.util;

import java.text.SimpleDateFormat;

public class TimeDateUtil {

    public static String getSimpleDurationStringFromSeconds(long seconds) {
        if (seconds >= 60) {
            double mins = MathUtil.round((double) seconds / 60, 1);
            if (mins >= 60) {
                double hours = MathUtil.round(mins / 60, 1);
                if (hours >= 24) {
                    return trimTime(MathUtil.round(hours / 24, 1) == 1 ? MathUtil.round(hours / 24, 1) + " day" : MathUtil.round(hours / 24, 1) + "days");
                } else {
                    return trimTime(hours == 1 ? hours + " hour" : hours + " hours");
                }
            } else {
                return trimTime(mins == 1 ? mins + " minute" : mins + " minutes");
            }
        } else {
            return trimTime(seconds == 1 ? seconds + " second" : seconds + " seconds");
        }
    }

    @Deprecated
    public static String getSimpleDurationStringFromMilis(long milis) {
        return getSimpleDurationStringFromMilis(milis, true);
    }

    @Deprecated
    public static String getSimpleDurationStringFromMilis(long milis, boolean includeType) {
        if (milis >= 60000) {
            double mins = MathUtil.round((double) milis / 60000, 1);
            if (mins >= 60) {
                double hours = MathUtil.round(mins / 60, 1);
                if (hours >= 24) {
                    return trimTime(MathUtil.round(hours / 24, 1) == 1 ? MathUtil.round(hours / 24, 1) + " day" : MathUtil.round(hours / 24, 1) + " days");
                } else {
                    if (includeType) {
                        return trimTime(hours == 1 ? hours + " hour" : hours + " hours");
                    } else {
                        return trimTime(Double.toString(hours));
                    }
                }
            } else {
                if (includeType) {
                    return trimTime(mins == 1 ? mins + " minute" : mins + " minutes");
                } else {
                    return trimTime(Double.toString(mins));
                }
            }
        } else {
            if (includeType) {
                return trimTime(milis / 1000 == 1 ? milis / 1000 + " second" : milis / 1000 + " seconds");
            } else {
                return trimTime(Double.toString(milis / 1000D));
            }
        }
    }

    public static String trimTime(String time) {
        int lowest = Integer.MAX_VALUE;
        String[] numbersToLookFor = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        for (String num : numbersToLookFor) {
            if (time.contains(num) && time.indexOf(num) < lowest) {
                lowest = time.indexOf(num);
            }
        }
        if (lowest != Integer.MAX_VALUE) {
            time = time.substring(lowest);
            return time.replace(".0", "");
        } else {
            return "0";
        }
    }

    public static String getTimeAndDateFromEpoch(long seconds) {
        return new SimpleDateFormat("MM'/'dd'/'yyyy '('h:mm:ss a') [EST]'").format(seconds * 1000 - 7200000);
    }
}
