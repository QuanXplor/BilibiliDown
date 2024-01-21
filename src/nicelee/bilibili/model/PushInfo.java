package nicelee.bilibili.model;

import java.util.List;

/**
 * 推送信息
 *
 * @author: QuanXplor
 * @date: 2023-11-30 00:11
 */
public class PushInfo {
    private String startTime;
    private String cost;
    private int num;
    private List<SubPushInfo> list;

    public static class SubPushInfo{
        private String title;
        private String pageUrl;
        private String result;

        public SubPushInfo(String title, String pageUrl) {
            this.title = title;
            this.pageUrl = pageUrl;
        }

        public SubPushInfo(String title, String pageUrl, String result) {
            this.title = title;
            this.pageUrl = pageUrl;
            this.result = result;
        }

        public String getTitle() {
            return title;
        }

        public String getPageUrl() {
            return pageUrl;
        }

        public String getResult() {
            return result;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setPageUrl(String pageUrl) {
            this.pageUrl = pageUrl;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

    public PushInfo(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getNum() {
        return list.size();
    }

    public List<SubPushInfo> getList() {
        return list;
    }

    public void setList(List<SubPushInfo> list) {
        this.list = list;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
