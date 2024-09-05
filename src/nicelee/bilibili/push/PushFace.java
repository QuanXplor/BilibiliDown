package nicelee.bilibili.push;

import nicelee.ui.Global;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 推送适配器
 *
 * @author: QuanXplor
 * @date: 2023-11-28 23:21
 */
public class PushFace implements IPush {
    private Map<String,IPush> data=new HashMap<>();

    public PushFace() {
        data.put("PushPlus",new PushPlusPush());
        data.put("Gotify",new GotifyPush());
    }

    @Override
    public boolean push(Map<String, Object> param) {
        // 将来多了可以根据配置文件选择情况，选择使用那个具体实现
        data.forEach((key, value) -> value.push(param));
        return true;
    }

    public static void main(String[] args) {
        Global.batchDownloadConfigPushGotifyToken="abcde";
        Global.batchDownloadConfigPushGotifyServiceUrl="https://localhost";
        Map<String,Object> map=new HashMap<>();
        map.put("startTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        map.put("num",2);
        map.put("cost","00:00:03");
        List<Map<String,String>> list=new ArrayList<>();
        Map<String,String> map1=new HashMap<>();
        map1.put("title","123");
        map1.put("pageUrl","http://localhost");
        map1.put("result","下载成功");
        map1.put("fileSize","100M");
        map1.put("duration","00:12:32");
        map1.put("resolution","1920×1080");
        Map<String,String> map2=new HashMap<>();
        map2.put("title","abc");
        map2.put("pageUrl","http://localhost");
        map2.put("result","下载失败");
        list.add(map1);
        list.add(map2);
        map.put("list",list);
        new PushFace().push(map);
    }
}
