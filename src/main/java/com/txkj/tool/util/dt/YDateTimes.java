package com.txkj.tool.util.dt;

import com.txkj.tool.util.str.StringHelper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class YDateTimes {

    public static final String YMD_HMS_STR = "yyyy-MM-dd HH:mm:ss";

    // 创建一个日期时间格式器
    public static final DateTimeFormatter YMD_HMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter MD_HMS = DateTimeFormatter.ofPattern("MMdd HH:mm:ss");
    public static final DateTimeFormatter YMD_HM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter YMD_HMS_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static final DateTimeFormatter YMD_HMS_MS_FILE = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    public static String toYmdHms(LocalDateTime localDateTime) {
        // 使用格式器将LocalDateTime对象转换为字符串
        return localDateTime.format(YMD_HMS);
    }

    /**
     * 转换为带有毫秒数SSS的时间字符串
     *
     * @param localDateTime
     * @return
     */
    public static String toYmdHmsMs(LocalDateTime localDateTime) {
        // 使用格式器将LocalDateTime对象转换为字符串
        return localDateTime.format(YMD_HMS_MS);
    }

    /**
     * 通过类似于符合重载的方式进行自然比较时间的大小
     *
     * @param ldt1
     * @param expression 支持 < = >
     * @param ldt2
     * @return
     */
    public static boolean compare(LocalDateTime ldt1, String expression, LocalDateTime ldt2) {
        switch (expression) {
            case "<":
                return ldt1.isBefore(ldt2);
            case "<=":
                return !ldt2.isBefore(ldt1);
            case ">":
                return ldt1.isAfter(ldt2);
            case ">=":
                return !ldt2.isAfter(ldt1);
            case "==":
                return ldt1.isEqual(ldt2);
            default:
                throw new IllegalArgumentException("Unsupported comparison expression: " + expression);
        }
    }


    /**
     * 转换为带有毫秒数SSS的时间字符串
     *
     * @param localDateTime
     * @return
     */
    public static LocalDateTime commParse(String str) {
        if (StringHelper.isBlank(str)) {
            return null;
        }
        str = str.strip();
        if (str.contains("T")) {
            return LocalDateTime.parse(str);
        }
        str = str.replace(" ", "T");
        return LocalDateTime.parse(str);
    }

    /**
     * 得到两个时间段的差异 毫秒
     *
     * @param a
     * @param expression 仅支持 【- ，a-b,b-a】 a是after，b是before的缩写，如果是- 则是a-b的缩写形式
     * @param b
     * @return
     */
    public static long diffMill(LocalDateTime a, String expression, LocalDateTime b) {
        if (expression.equals("-") || expression.equals("a-b")) {
            return Duration.between(b, a).toMillis();
        } else if (expression.equals("b-a")) {
            return Duration.between(a, b).toMillis();
        } else {
            throw new IllegalArgumentException("Invalid expression, only support [-, a-b, b-a]");
        }
    }

    public static String toFileName(LocalDateTime ldt, String splitSimple) {
        /*if (splitSimple == null) {
            splitSimple = "_";
        }*/
        String fmtStr = ldt.format(YMD_HMS_MS_FILE);
        return fmtStr;
    }

    public static void main(String[] args) {
        System.out.println(toFileName(LocalDateTime.now(), null));
    }

}
