package nicelee.bilibili.parsers.impl;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.HttpHeaders;
import nicelee.bilibili.util.convert.ConvertUtil;
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
@Bilibili(name = "cmbv")
public class CMBVParser extends CMAVParser {

    //	public AVParser(HttpRequestUtil util,IParamSetter paramSetter, int pageSize) {
    public CMBVParser(Object... obj) {
        super(obj);
        setPattern(Pattern.compile("cm-(bv[0-9A-Za-z]+)"));
    }

    public boolean matches(String input){
        return super.matches(input);
    }

    @Override
    public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
        String s = String.valueOf(ConvertUtil.Bv2Av(validStr(input)));
        System.out.println(this.getClass().getSimpleName()+"正在获取结果" + s);
        return super.getAVDetail(s, videoFormat, getVideoLink);
    }

}
