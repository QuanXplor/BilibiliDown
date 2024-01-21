package nicelee.bilibili.util;

import nicelee.bilibili.exceptions.PeertubeTokenValidException;
import nicelee.bilibili.exceptions.PeertubeUnknowException;
import nicelee.ui.Global;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @author: A11181121050450
 * @date: 2023-12-25 17:11
 */
public class Peertube {
    private HashMap<String,String> COMMON_HEADER=new HashMap<>();
    private HttpRequestUtil http=new HttpRequestUtil();
    private String apiUrl ="";
    private String password ="";
    private String username ="";
    private String channel ="";
    private boolean printLog=true;

    public Peertube(String apiUrl,  String username, String password, String channel) {
        this.apiUrl = apiUrl;
        this.password = password;
        this.username = username;
        this.channel = channel;
    }

    public void setPrintLog(boolean printLog) {
        this.printLog = printLog;
    }

    public String getUserInfo(){
        // https://docs.joinpeertube.org/api-rest-reference.html#tag/My-User/operation/getUserInfo
        String url=apiUrl+"/api/v1/users/me";
        String content = http.getContent(url, COMMON_HEADER);
        JSONObject jsonObject=new JSONObject(content);
        assertStatus(jsonObject);
        return content;
    }

    public void login(){
        // 获取client信息 https://docs.joinpeertube.org/api-rest-reference.html#tag/Session/operation/getOAuthClient
        String preInfoUrl = apiUrl +"/api/v1/oauth-clients/local";
        String content = http.getContent(preInfoUrl, null);
        JSONObject jsonObject = new JSONObject(content);
        String clientId = jsonObject.optString("client_id");
        String clientSecret=jsonObject.optString("client_secret");

        // https://docs.joinpeertube.org/api-rest-reference.html#tag/Session/operation/getOAuthToken
        String loginUrl= apiUrl +"/api/v1/users/token";
        HashMap<String,String> header= new HashMap<>();
        header.put("Content-Type","application/x-www-form-urlencoded");
        Map<String,String> param=new HashMap<>();
        param.put("client_id",clientId);
        param.put("client_secret",clientSecret);
        param.put("grant_type","password");
        param.put("password", password);
        param.put("username", username);
        String loginResult = http.postContent(loginUrl, header, contentCovert(param), null, true);
        jsonObject=new JSONObject(loginResult);
        String access_token = jsonObject.optString("access_token");
        if(access_token==null || access_token.length()<1){
            Logger.printf("Peertube login failed. Resp content : \n%s",loginResult);
            throw new RuntimeException("Peertube login failed");
        }
        COMMON_HEADER.put("Authorization"," Bearer "+access_token);
    }

    public void loginOut(){
        // https://docs.joinpeertube.org/api-rest-reference.html#tag/Session/operation/revokeOAuthToken
        String url = apiUrl +"/api/v1/users/revoke-token";
        String content = http.getContent(url, COMMON_HEADER);
    }

    public String upload(String channelId,File file,String title,String description){
        // https://docs.joinpeertube.org/api-rest-reference.html#tag/Video-Upload/operation/uploadLegacy
        String url = apiUrl +"/api/v1/videos/upload";
        HashMap<String, String> header = new HashMap<>(COMMON_HEADER);
        header.put("Content-Type","multipart/form-data");
        HashMap<String,String> params=new HashMap<>();
        params.put("channelId",channelId);
        params.put("name",title);
        if(description!=null && description.length()>0) {
            params.put("description", description);
        }
        params.put("commentsEnabled","true");
        params.put("downloadEnabled","true");
        params.put("privacy","1"); // 公开
        params.put("waitTranscoding","false");
        String s = http.uploadMoreFile(url, header, params, Collections.singletonMap("videofile",file), null);
        if(printLog){
            Logger.printf("upload resp: %s",s);
        }
        JSONObject jsonObject= new JSONObject(s);
        assertStatus(jsonObject);
        return jsonObject.optJSONObject("video").optString("id");
    }

    public String acquireChannel(){
        // https://docs.joinpeertube.org/api-rest-reference.html#tag/Accounts/paths/~1api~1v1~1accounts~1%7Bname%7D~1video-playlists/get
        String url= apiUrl +"/api/v1/accounts/"+ username +"/video-channels";
        String query="search="+ channel;
        String content = http.getContent(url+"?"+query, null);
        JSONObject jsonObject=new JSONObject(content);
        if(jsonObject.getInt("total")>0){
            JSONArray data = jsonObject.getJSONArray("data");
            String channelId = IntStream.range(0, data.length())
                    .filter(o -> channel.equals(data.getJSONObject(o).optString("displayName")))
                    .mapToObj(o -> data.getJSONObject(o).optString("id")).findFirst().orElse(null);
            if(channelId !=null && channelId.length()>0){
                return channelId;
            }
        }
        Logger.printf("Peertube acquire channel failed. Resp content : \n%s",content);
        throw new RuntimeException("Peertube acquire channel failed.");
    }

    public void createComment(String id,String text){
        // https://docs.joinpeertube.org/api-rest-reference.html#tag/Video-Comments/paths/~1api~1v1~1videos~1%7Bid%7D~1comment-threads/post
        String url=apiUrl+"/api/v1/videos/"+id+"/comment-threads";
        JSONObject jObject=new JSONObject();
        jObject.put("text",text);
        String param=jObject.toString();
        HashMap<String, String> header = new HashMap<>(COMMON_HEADER);
        header.put("Content-Type","application/json");
        String s = http.postContent(url,header , param, null, true);
        JSONObject jsonObject= new JSONObject(s);
        assertStatus(jsonObject);
    }

    private void assertStatus(JSONObject jsonObject){
        String status = jsonObject.optString("status");
        if(status!=null && status.length()>0){
            if("401".equals(status)) {
                throw new PeertubeTokenValidException("Peertube operation failed");
            }
            System.out.println("Peertube unknow exception:\n"+jsonObject.toString());
            throw new PeertubeUnknowException("Peertube unknow exception");
        }
    }

    private static Peertube DEFAULT_INSTANCE=new Peertube(Global.peertubeApiUrl,Global.peertubeUsername,Global.peertubePassword,Global.peertubeChannel);

    public static class PeertubeUploader{
        public static boolean upload(File file, String title, String description, List<String> comments){
            String s = DEFAULT_INSTANCE.acquireChannel();
            String upload =null;
            try{
                DEFAULT_INSTANCE.getUserInfo();
            }catch (PeertubeTokenValidException e){
                DEFAULT_INSTANCE.login();
            }
            try {
                upload = DEFAULT_INSTANCE.upload(s, file, title, description);
            }catch (PeertubeTokenValidException e){
                DEFAULT_INSTANCE.login();
                upload = DEFAULT_INSTANCE.upload(s, file, title, description);
            }
            if(comments!=null && comments.size()>0) {
                for (String comment : comments) {
                    try {
                        DEFAULT_INSTANCE.createComment(upload, comment);
                    } catch (PeertubeTokenValidException e) {
                        DEFAULT_INSTANCE.login();
                        DEFAULT_INSTANCE.createComment(upload, comment);
                    }
                }
            }
            return true;
        }
    }

    private String contentCovert(Map<String,String> map){
        return map.entrySet().stream()
                .map(o->o.getKey()+"="+Optional.ofNullable(o.getValue()).orElse(""))
                .reduce((r, i)-> r + (r.length() > 0 ? "&" : "") + i).get();
    }

    public static void main(String[] args) {
//        File file = new File("E:\\project\\source\\tmp\\BilibiliDown\\workspace\\download1\\珍藏-电视家跑路？终极解决方案！！！-p01-80.mp4");
//        PeertubeUploader.upload(file,"电视家跑路","desc",Collections.singletonList("comment\tcomment\ncomment"));
        JSONObject jObject=new JSONObject();
        String value = "comment\tcomment\ncomment";
        System.out.println(value);
        jObject.put("text", value);
//        String param="{\"text\":\""+text+"\"}";
        String param=jObject.toString();
        System.out.println(param);
    }
}
