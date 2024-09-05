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
            if(this.token==null || this.token.trim().isEmpty()){
                Logger.println("PlushPlus推送token为空，放弃推送");
            }else {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                String json = this.acquirePushMsg(param);
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
        String content=IPushTemplate.super.acquirePushMsg(param);
        content="## 下载信息\\n "+content;
        return "{\n" +
                "    \"token\":\""+ this.token+"\",\n" +
                "    \"title\":\"下载结果\",\n" +
                "    \"template\":\""+ this.templateType+"\",\n" +
                "    \"content\":\""+content+"\"\n" +
                "}";
    }
}
