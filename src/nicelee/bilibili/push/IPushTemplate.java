package nicelee.bilibili.push;

import java.util.Map;

/**
 * 获取推送模板
 *
 * @author: QuanXplor
 * @date: 2023-12-25 14:33
 */
public interface IPushTemplate {
    String acquirePushMsg(Map<String, Object> param);
}
