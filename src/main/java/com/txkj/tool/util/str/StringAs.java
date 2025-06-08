package com.txkj.tool.util.str;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.txkj.tool.util.str.StringHelper.isBlank;

public class StringAs {

    public final static DateTimeFormatter YMD_HMS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * 字符串常量：{@code "null"} <br>
     * 注意：{@code "null" != null}
     */
    public static final String NULL = "null";

    /**
     * 字符串常量：空字符串 {@code ""}
     */
    public static final String EMPTY = "";

    /**
     * 字符串常量：空格符 {@code " "}
     */
    public static final String SPACE = " ";

    /*public final static DateTimeFormatter YMD_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");*/

    /**
     * 驼峰转下滑线
     *
     * @param param
     * @return
     */
    public static String camelToUnderline(String param) {
        if (isBlank(param)) {
            return "";
        } else {
            int len = param.length();
            StringBuilder sb = new StringBuilder(len);

            for (int i = 0; i < len; ++i) {
                char c = param.charAt(i);
                if (Character.isUpperCase(c) && i > 0) {
                    sb.append('_');
                }

                sb.append(Character.toLowerCase(c));
            }

            return sb.toString();
        }
    }

    /**
     * 下滑线转驼峰
     *
     * @param param
     * @return
     */
    public static String underlineToCamel(String param) {
        if (isBlank(param)) {
            return "";
        } else {
            String temp = param.toLowerCase();
            int len = temp.length();
            StringBuilder sb = new StringBuilder(len);

            for (int i = 0; i < len; ++i) {
                char c = temp.charAt(i);
                if (c == '_') {
                    ++i;
                    if (i < len) {
                        sb.append(Character.toUpperCase(temp.charAt(i)));
                    }
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        }
    }

    /**
     * 首字母转为小写
     *
     * @param param
     * @return
     */
    public static String firstToLowerCase(String param) {
        return isBlank(param) ? "" : param.substring(0, 1).toLowerCase() + param.substring(1);
    }

    /**
     * 简单的字符串替换,无正则表达式
     *
     * @param ori
     * @param matchStr
     * @param replaceStr
     * @return
     */
    public static String simpleReplaceAll(String ori, String matchStr, String replaceStr) {
        if (ori == null) {
            return null;
        }

        String[] strings = simpleSplit(ori, matchStr);

        if (strings.length == 1) {
            return strings[0];
        }
        StringBuilder strBud = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i > 0) {
                strBud.append(replaceStr);
            }
            strBud.append(strings[i]);
        }
        return strBud.toString();
    }

    /**
     * 将第一个字母转为大写
     *
     * @param src
     * @return
     */
    public static String firstToUpperCase(String src) {
        if (Character.isLowerCase(src.charAt(0))) {
            return 1 == src.length() ? src.toUpperCase() : Character.toUpperCase(src.charAt(0)) + src.substring(1);
        } else {
            return src;
        }
    }

    /**
     * 根据get方法获取属性名
     *
     * @param getMethodName
     * @return
     */
    public static String resolveFieldName(String getMethodName) {
        if (getMethodName.startsWith("get")) {
            getMethodName = getMethodName.substring(3);
        } else if (getMethodName.startsWith("is")) {
            getMethodName = getMethodName.substring(2);
        }
        return firstToLowerCase(getMethodName);
    }

    /**
     * 百分比计算法 根据一个值返回百分比
     *
     * @param x
     * @param total
     * @return
     */
    public static String getPercent(int x, int total) {
        if (total == 0) {
            return "0.00%";
        }
        String result = "";//接受百分比的值
        double x_double = x * 1.0;
        //  注意如果直接用x/total 会变成0（所以两者之一至少转换为double）  这个原生方法没有写好 还是我补充的，吐槽一波
        double tempresult = x_double / total;

        DecimalFormat df1 = new DecimalFormat("0.00%");    //##.00%   百分比格式，后面不足2位的用0补齐
        result = df1.format(tempresult);

        return result;
    }

    /**
     * 拼接map
     *
     * @param map
     * @param cs
     * @return
     */
    public static String joinMap(Map map, CharSequence cs) {
        if (map == null || map.isEmpty()) {
            //  因为大部分的场景是用于url拼接，参数不常见的情况，经常容易出现
            return "";
        }
        StringBuilder strBud = new StringBuilder();
        for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            strBud.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        strBud.delete(strBud.length() - 1, strBud.length());
        return strBud.toString();
    }


    /**
     * 题目:实现方法split
     * 题目描述:实现方法split,能够指定分割符将字符串拆分成字符串数组
     * 注意:不必支持正则表达式
     * 通过字符串匹配算法
     */
    public static String[] simpleSplit(String str, String splitSymbol) {
        if (str == null || splitSymbol == null) {
            throw new IllegalArgumentException("Input strings cannot be null");
        }

        if (splitSymbol.isEmpty()) {
            throw new IllegalArgumentException("Split symbol cannot be empty");
        }

        List<String> result = new ArrayList<>();
        int index = 0;
        int nextIndex;

        // 在循环外定义 tempStr，并初始为 str
        StringBuilder tempStr = new StringBuilder(str);

        while ((nextIndex = tempStr.indexOf(splitSymbol, index)) != -1) {
            // 将匹配项之前的部分添加到结果中
            result.add(tempStr.substring(index, nextIndex));

            // 更新下一次搜索的起始位置
            index = nextIndex + splitSymbol.length();
        }

        // 添加剩余的字符串到结果中
        result.add(tempStr.substring(index));

        return result.toArray(new String[0]);


    }

    /**
     * 去除两侧空格 同时包含 换行符 制表符 等
     *
     * @param str
     * @return
     */
    public static String trimFull(String str) {
        if (isBlank(str)) {
            return "";
        }
        // 使用正则表达式匹配开头和结尾的空白字符，包括换行符、制表符等
        String regex = "(?U)^\\s+|\\s+$";
        return str.replaceAll(regex, "");
    }

    public static void main(String[] args) {
        String originalString = "Hello, World! Hello, Java! Hello, Hello!";
        System.out.println(originalString);
        String[] hes = simpleSplit(originalString, "He");
        System.out.println(Arrays.toString(originalString.split("He")));
        System.out.println(Arrays.toString(hes));
        String replacedString = simpleReplaceAll(originalString, "Hello", "Hi");
        System.out.println(replacedString);
        System.out.println(originalString.replaceAll("Hello", "Hi"));

    }

}