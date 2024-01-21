package nicelee.ui;

import nicelee.ui.item.DownloadInfoPanel;

/**
 * 获取下载完毕地址
 *
 * @author: QuanXplor
 * @date: 2023-12-30 19:23
 */
public class AcquireFilePath {
    public static String acquire(DownloadInfoPanel dip) {
        try {
            String result = dip.downloadPath[0];
            if (result == null) {
                synchronized (dip.downloadPath) {
                    result = dip.downloadPath[0];
                    if (result == null) {
                        dip.downloadPath.wait();
                        result = dip.downloadPath[0];
                    }
                }
            }
            return result;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage(),e);
        }
    }
}
