package nicelee.bilibili.push;

import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;
import nicelee.ui.Global;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PushPlus推送
 *
 * @author: QuanXplor
 * @date: 2023-11-28 21:02
 */
public class PushPlusPush implements IPush,IPushTemplate {
    private final String token;
    private final String pushUrl="http://www.pushplus.plus/send/";
    private final String templateType="markdown";
    private HttpRequestUtil util = new HttpRequestUtil();

    public PushPlusPush(){
        this(Global.batchDownloadConfigPushPlushPlusToken);
    }

    public PushPlusPush(String token){
        this.token=token;
    }
    @Override
    public boolean push(Map<String, Object> param) {
        try{
            if(this.token==null || this.token.trim().length()<1){
                Logger.println("PlushPlus推送token为空，放弃推送");
            }else {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                String json = this.acquirePushMsg(param);
                System.out.println(json);
                util.postContent(pushUrl, headers, json, null, true);
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public String acquirePushMsg(Map<String, Object> param) {
        StringBuilder content=new StringBuilder();
        content.append("## 下载信息\\n ");
        content.append("- **开始时间**：").append(param.getOrDefault("startTime","")).append(" \\n " );
        content.append("- **下载数量**：").append(param.getOrDefault("num","")).append(" \\n ");
        content.append("- **下载耗时**：").append(param.getOrDefault("cost","")).append(" \\n");
        content.append("- **下载清单**： \\n" );
        List<Map<String,String>> list= (List<Map<String, String>>) param.getOrDefault("list", Collections.emptyList());
        for(Map<String,String> item : list) {
            content.append("\\t 1. [").append(item.getOrDefault("title", "")).append("]")
                    .append("(").append(item.getOrDefault("pageUrl", "")).append(")")
                    .append(" - ").append(item.getOrDefault("result", ""));
            if ("下载成功".equals(item.getOrDefault("result", ""))) {
                content.append(" [大小:").append(item.getOrDefault("fileSize", ""))
                        .append(" 时长:").append(item.getOrDefault("duration", ""))
                        .append(" 分辨率:").append(item.getOrDefault("resolution", "")).append("]\\n");
            }
        }
        return "{\n" +
                "    \"token\":\""+ this.token+"\",\n" +
                "    \"title\":\"下载结果\",\n" +
                "    \"template\":\""+ this.templateType+"\",\n" +
                "    \"content\":\""+content+"\"\n" +
                "}";
    }
}
