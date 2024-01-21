package nicelee.bilibili.model;

/**
 * 视频文件基础信息
 *
 * @author: QuanXplor
 * @date: 2023-11-30 01:20
 */
public class VideoBaseInfo {
    // 分辨率
    private String resolution;
    // 时长
    private String duration;

    public VideoBaseInfo(String resolution, String duration) {
        this.resolution = resolution;
        this.duration = duration;
    }

    public String getResolution() {
        return resolution;
    }

    public String getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "resolution:"+resolution+",duration:"+duration;
    }
}
