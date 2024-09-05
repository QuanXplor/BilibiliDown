package nicelee.bilibili.push;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 获取推送模板
 *
 * @author: QuanXplor
 * @date: 2023-12-25 14:33
 */
public interface IPushTemplate {
    default String acquirePushMsg(Map<String, Object> param){
        StringBuilder content=new StringBuilder();
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
        return content.toString();
    }
}
