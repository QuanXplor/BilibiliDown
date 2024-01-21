package nicelee.bilibili.parsers.impl;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.StrUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author: A11181121050450
 * @date: 2023-12-30 01:09
 */
@Bilibili(name = "cmav")
public class CMAVParser extends AVParser {

    private Pattern pattern = Pattern.compile("cm-(AV[0-9]+)");
    private String avId;

    void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    //	public AVParser(HttpRequestUtil util,IParamSetter paramSetter, int pageSize) {
    public CMAVParser(Object... obj) {
        super(obj);
    }

    @Override
    public boolean matches(String input) {
        matcher = pattern.matcher(input);
        boolean matches = matcher.find();
        if (matches) {
            avId = matcher.group(1);
            avId = StrUtil.toggleCase(avId);
            System.out.println("匹配"+this.getClass().getSimpleName()+": " + avId);
        }
        return matches;
    }

    @Override
    public String validStr(String input) {
        return avId;
    }

    @Override
    public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
        System.out.println(this.getClass().getSimpleName()+"正在获取结果" + avId);
        return getAVDetail(avId.substring(2), videoFormat, getVideoLink);
    }

    @Override
    public VideoInfo getAVDetail(String avId, int videoFormat, boolean getVideoLink) {
        VideoInfo result = new VideoInfo();

        // 获取av下的评论
        String url = String.format("https://api.bilibili.com/x/v2/reply?type=1&oid=%s", avId);
        HashMap<String, String> headers_json = new HttpHeaders().getBiliJsonAPIHeaders(avId);
        String json = util.getContent(url, headers_json, HttpCookies.getGlobalCookies());
        JSONArray tops = new JSONObject(json).getJSONObject("data").optJSONArray("top_replies");
        if(tops!=null && tops.length()>0) {
            List<String> collect = IntStream.range(0, tops.length())
                    .mapToObj(tops::getJSONObject)
                    .map(o -> o.optJSONObject("content"))
                    .map(o -> o.optString("message"))
                    .collect(Collectors.toList());
            result.setCommont(collect);
        }
        return result;
    }
}
