package nicelee.bilibili.push;

import java.util.Map;

/**
 * 推送接口
 *
 * @author: QuanXplor
 * @date: 2023-11-28 20:56
 */
public interface IPush {
    boolean push(Map<String,Object> param);
}
