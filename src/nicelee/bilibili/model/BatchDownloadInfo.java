package nicelee.bilibili.model;

import nicelee.ui.item.DownloadInfoPanel;

import java.util.concurrent.Future;

/**
 * 批量下载信息
 *
 * @author: QuanXplor
 * @date: 2023-11-29 23:55
 */
public class BatchDownloadInfo {
    private ClipInfo clip;
    private Future<DownloadInfoPanel> downPanel;

    public BatchDownloadInfo(ClipInfo clip, Future<DownloadInfoPanel> downPanel) {
        this.clip = clip;
        this.downPanel = downPanel;
    }

    public ClipInfo getClip() {
        return clip;
    }

    public void setClip(ClipInfo clip) {
        this.clip = clip;
    }

    public Future<DownloadInfoPanel> getDownPanel() {
        return downPanel;
    }

    public void setDownPanel(Future<DownloadInfoPanel> downPanel) {
        this.downPanel = downPanel;
    }
}
