package nicelee.bilibili.util;

import java.util.Formatter;

/**
 * 时间工具
 *
 * @author: QuanXplor
 * @date: 2023-11-29 00:07
 */
public class TimeUtil {
    public static String secondsToHMS(Long seconds){
        Long h = seconds / 3600;
        Long m = (seconds % 3600) / 60;
        Long s = seconds % 60;

        // 使用 Formatter 格式化字符串
        StringBuilder formattedTime = new StringBuilder();
        Formatter formatter = new Formatter(formattedTime);
        formatter.format("%02d:%02d:%02d", h, m, s);

        return formattedTime.toString();
    }
}
