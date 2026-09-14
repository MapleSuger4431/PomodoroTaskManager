package com.pomodoro.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类，提供日期格式化、解析、计算与比较等常用功能
 */
public class DateUtil {

    /**
     * 私有构造方法，防止工具类被实例化
     */
    private DateUtil() {
    }

    /**
     * 按指定模式将日期格式化为字符串，日期为 null 时返回空字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 按指定模式将字符串解析为日期，解析失败时抛出 ParseException
     */
    public static Date parse(String text, String pattern) throws ParseException {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        return sdf.parse(text.trim());
    }

    /**
     * 在指定日期的基础上增加或减少天数
     */
    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    /**
     * 判断两个日期是否为同一天，任一日期为 null 时返回 false
     */
    public static boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取指定日期所在周的周一 00:00:00
     */
    public static Date getWeekStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SUNDAY) {
            // 周日属于上一个以周一开头的周，需回退 6 天
            calendar.add(Calendar.DAY_OF_MONTH, -6);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, Calendar.MONDAY - dayOfWeek);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取指定日期所在周的周日 23:59:59
     */
    public static Date getWeekEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getWeekStart(date));
        calendar.add(Calendar.DAY_OF_MONTH, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 判断指定日期上 HH:mm 格式的每日截止时间相对于当前时间是否已经过去
     */
    public static boolean isPastDeadline(Date date, String hhmm) {
        if (date == null || hhmm == null || hhmm.trim().isEmpty()) {
            return false;
        }
        String[] parts = hhmm.trim().split(":");
        if (parts.length != 2) {
            return false;
        }
        int hour;
        int minute;
        try {
            hour = Integer.parseInt(parts[0].trim());
            minute = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            return false;
        }
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().before(new Date());
    }

    /**
     * 将日期格式化为 yyyy-MM-dd 字符串
     */
    public static String formatDate(Date date) {
        return format(date, "yyyy-MM-dd");
    }

    /**
     * 将日期格式化为 yyyy-MM-dd HH:mm 字符串
     */
    public static String formatDateTime(Date date) {
        return format(date, "yyyy-MM-dd HH:mm");
    }

    /**
     * 将秒数格式化为 X小时Y分 形式
     */
    public static String formatDuration(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        return hours + "小时" + minutes + "分";
    }
}
