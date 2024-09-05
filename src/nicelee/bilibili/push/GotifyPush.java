package nicelee.bilibili.push;

import nicelee.bilibili.util.HttpRequestUtil;
import nicelee.bilibili.util.Logger;

import java.util.HashMap;
import java.util.Map;

import static nicelee.ui.Global.batchDownloadConfigPushGotifyServiceUrl;
import static nicelee.ui.Global.batchDownloadConfigPushGotifyToken;

/**
 * PushPlus推送
 *
 * @author: QuanXplor
 * @date: 2023-11-28 21:02
 */
public class GotifyPush implements IPush,IPushTemplate {
    public static final String ADDR = "/message";
    private final String serviceHost;
    private final String token;
    private HttpRequestUtil util = new HttpRequestUtil();


    public GotifyPush(){
        this(batchDownloadConfigPushGotifyServiceUrl,batchDownloadConfigPushGotifyToken);
    }

    public GotifyPush(String serviceHost, String token){
        this.serviceHost=serviceHost;
        this.token=token;
    }

    @Override
    public boolean push(Map<String, Object> param) {
        try{
            if(this.token==null || this.token.trim().isEmpty()){
                Logger.println("Gotify推送token为空，放弃推送");
            }
            else if(this.serviceHost==null || this.serviceHost.trim().isEmpty()){
                Logger.println("Gotify推送服务地址为空，放弃推送");
            }else {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                String json = this.acquirePushMsg(param);
                String pushUrl=this.serviceHost+ ADDR+"?token="+this.token;
                String result = util.postContent(pushUrl, headers, json, null, true);
                System.out.println(result);
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
        return "{\n" +
                "  \"extras\": {\n" +
                "    \"client::display\": {\n" +
                "      \"contentType\": \"text/markdown\"\n" +
                "    }\n"+
                "  },\n" +
                "  \"message\": \""+content+"\",\n" +
                "  \"priority\": 5,\n" +
                "  \"title\": \"下载结果\"\n" +
                "}";
    }
}
