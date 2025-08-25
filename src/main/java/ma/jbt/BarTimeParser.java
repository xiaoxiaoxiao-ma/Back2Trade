package ma.jbt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class BarTimeParser {

    // 解析小于一天的 bar
    public static LocalDateTime parseBarTimeToDateTime(String timeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss z");
        return LocalDateTime.parse(timeStr, formatter);
    }

    // 解析一天或更长周期的 bar（只含日期）
    public static LocalDate parseBarTimeToDate(String timeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(timeStr, formatter);
    }

    // 自动判断
    public static LocalDateTime autoParseBarTime(String timeStr) {
        if (timeStr.length() == 8) { // e.g., 20240513
            return LocalDate.parse(timeStr, DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay();
        } else { // e.g., 20240513 13:00:00
            return LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss z"));
        }
    }
    
    public static String convertToCsvFormat(String timeStr) {
        //DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss z", Locale.US);
    	DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss", Locale.US);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 解析字符串为 ZonedDateTime
        ZonedDateTime zdt = ZonedDateTime.parse(timeStr, inputFormatter);

        // 按目标格式输出（本地时间）
        return outputFormatter.format(zdt);
    }
}