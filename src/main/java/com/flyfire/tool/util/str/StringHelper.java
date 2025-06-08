package com.flyfire.tool.util.str;

public class StringHelper {

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 判断是否字符串的内容为空
     *
     * @param cs
     * @return
     */
    public static boolean isBlank(final CharSequence cs) {
        if (cs == null) {
            return true;
        } else {
            int l = cs.length();
            if (l > 0) {
                for (int i = 0; i < l; ++i) {
                    if (!Character.isWhitespace(cs.charAt(i))) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 判断字符串是否相等
     * @param str1 字符串1
     * @param str2 字符串2
     * @param ignoreCase 是否忽略大小写
     * @return 是否相等
     */
    public static boolean equals(CharSequence str1, CharSequence str2, boolean ignoreCase) {
        if (null == str1) {
            // 只有两个都为null才判断相等
            return str2 == null;
        }
        if (null == str2) {
            // 字符串2空，字符串1非空，直接false
            return false;
        }

        if (ignoreCase) {
            return str1.toString().equalsIgnoreCase(str2.toString());
        } else {
            return str1.toString().contentEquals(str2);
        }
    }

    public static boolean equals(CharSequence str1, CharSequence str2) {
        return equals(str1, str2, false);
    }

    /**
     * 字符串分片
     * Slice规则数：如果是正数 n，代表第 n 个字；如果是负数 -n，代表倒数第 n 个字
     * @param ori 原始字符串
     * @param startSlice 如果是空，默认为第 0 个字符，有值就是 Slice 规则数
     * @param endSlice 如果是空，默认为最后一个字符，有值就是 Slice 规则数
     * @return
     */
    public static String slice(String ori,Integer startSlice,Integer endSlice){
        if (ori == null || ori.isEmpty()) {
            return "";
        }

        int length = ori.length();

        // 处理 startSlice，如果为空，默认为第 0 个字符；如果为负数，转换为正数
        int start = (startSlice == null) ? 0 : (startSlice < 0) ? length + startSlice : startSlice;

        // 处理 endSlice，如果为空，默认为最后一个字符；如果为负数，转换为正数
        int end = (endSlice == null) ? length : (endSlice < 0) ? length + endSlice : endSlice;

        // 对 start 和 end 进行边界检查
        start = Math.max(0, Math.min(start, length));
        end = Math.max(0, Math.min(end, length));

        // 确保 start 不大于 end
        int temp;
        if (start > end) {
            temp = start;
            start = end;
            end = temp;
        }

        return ori.substring(start, end);

    }

    public static void main(String[] args) {
        String path="D:/temp2/ir5240104_135254.015_CameraF10IP205.bin";
        System.out.println(slice(path,0,-4));
    }


}