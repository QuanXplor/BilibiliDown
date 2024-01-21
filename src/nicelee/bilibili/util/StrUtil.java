package nicelee.bilibili.util;

/**
 * 字符串工具
 *
 * @author: A11181121050450
 * @date: 2024-01-01 16:01
 */
public class StrUtil {
    /**
     * 大小写互转
     * @param input
     * @return
     */
    public static String toggleCase(String input) {
        if (input == null) {
            return null;
        }

        char[] charArray = input.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (Character.isUpperCase(charArray[i])) {
                charArray[i] = Character.toLowerCase(charArray[i]);
            } else if (Character.isLowerCase(charArray[i])) {
                charArray[i] = Character.toUpperCase(charArray[i]);
            }
            // 如果是其他字符（例如数字、符号等），则不进行大小写转换
        }

        return new String(charArray);
    }

    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static boolean isBlank(String str){
        if(str != null){
            str=str.trim();
            if(str.length()>0){
                return false;
            }
        }
        return true;
    }
}
