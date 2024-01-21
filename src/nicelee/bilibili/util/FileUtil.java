package nicelee.bilibili.util;

import java.io.File;

/**
 * 文件工具类
 *
 * @author: QuanXplor
 * @date: 2023-11-30 01:09
 */
public class FileUtil {
    public static String acquireDiskFileSize(File file) {
        long fsize = file.length();  // 返回的是字节大小

        /*
        为了更好地显示，应该时刻保持显示一定整数形式，即单位自适应
        */
        if (fsize < 1024) {
            return String.format("%.2f Byte", (double) fsize);
        } else {
            double KBX = fsize / 1024.0;
            if (KBX < 1024) {
                return String.format("%.2f K", KBX);
            } else {
                double MBX = KBX / 1024.0;
                if (MBX < 1024) {
                    return String.format("%.2f M", MBX);
                } else {
                    return String.format("%.2f G", MBX / 1024.0);
                }
            }
        }
    }

}
