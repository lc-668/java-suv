package com.txkj.tool.util.io;

import com.txkj.tool.util.str.StringAs;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class YPathUtils {

    /**
     * 获取资源路径，仅仅只能支持在开发模式中使用，不支持打包后的jar包
     *
     * @param resFileName
     * @return
     */
    public static Path getResPath(String resFileName) {
        try {
            //  先寻找resouce文件
            URI uri = ClassLoader.getSystemResource(resFileName).toURI();
            Path resPath = Paths.get(uri);
            System.out.println("项目的配置文件路径:" + resPath);
            return resPath;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("该项目没有配置文件:" + resFileName);
            return null;// 说明不存在
        }
    }


    public static List<Path> list(Path parentPath) {
        List<Path> childPaths = new ArrayList<>();
        try (Stream<Path> stream = Files.list(parentPath)) {
            stream.forEach(childPaths::add);
        } catch (IOException e) {
            throw new RuntimeException(e);
            // 处理异常，根据需要进行处理
        }
        return childPaths;
    }

    public static List<String> list(String parentPath) {
        List<String> childPaths = new ArrayList<>();
        try (Stream<Path> stream = Files.list(Paths.get(parentPath))) {
            stream.forEach((item) -> {
                childPaths.add(item.toString());
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
            // 处理异常，根据需要进行处理
        }
        return childPaths;
    }

    /*public static String joinPath(String rootPath, String realPath) {
        // 如果rootPath末尾有/或者\，需要去掉
        rootPath = removeTrailingSeparator(rootPath);

        // 如果realPath的前面有/或者\，需要去掉
        realPath = removeLeadingSeparator(realPath);

        // 使用StringBuilder来拼接路径
        StringBuilder pathBuilder = new StringBuilder(rootPath);
        if (!rootPath.endsWith("/") && !rootPath.endsWith("\\")) {
            pathBuilder.append("/");
        }
        pathBuilder.append(realPath);

        // 返回拼接后的路径字符串
        return pathBuilder.toString();
    }*/

    public static String joinStrPath(String rootPath, String... childPathArr) {
        // 如果rootPath末尾有/或者\，需要去掉
        // 使用StringBuilder来拼接路径
        StringBuilder pathBuilder = new StringBuilder(formatRootPath(rootPath));
        for (String item : childPathArr) {
            pathBuilder.append("/").append(formatAppendPath(item));
        }
        // 返回拼接后的路径字符串
        return pathBuilder.toString();
    }

    public static String formatRootPath(String path) {
        if (path == null) {
            return null;
        }
        while (true) {
            if (path.contains("\\")) {
                path = StringAs.simpleReplaceAll(path, "\\", "/");
            }
            if (path.contains("//")) {
                path = StringAs.simpleReplaceAll(path, "//", "/");
            }
            if (!path.contains("//")) {
                break;
            }
        }
        return path;
    }

    /**
     * 优化拼接追加的子路径
     *
     * @param path
     * @return
     */
    private static String formatAppendPath(String path) {
        if (path == null) {
            return null;
        }
        while (true) {
            int length = path.length();
            if (length == 0) {
                return path;
            }
            char firstChar = path.charAt(0);
            if (firstChar == '/' || firstChar == '\\') {
                path = path.substring(1, length);
            } else {
                return path;
            }
        }
    }

    /**
     * 将文件名去掉后缀
     *
     * @param filename
     * @return
     */
    public static String removeSuffix(String filename) {
        // 查找"."字符最后一次出现的位置（即扩展名分隔符的位置）
        int lastDotIndex = filename.lastIndexOf(".");
        // 如果"."不存在于文件名中，则直接返回原文件名
        if (lastDotIndex == -1) {
            return filename;
        } else {
            // 否则，截取从字符串开头到"."之前的部分，去除后缀
            return filename.substring(0, lastDotIndex);
        }
    }

    public static void main(String[] args) {
        String s = joinStrPath("-1asdfsadf", "-2asdf", "/////-3asdfasdf", "\\\\\\\\///-4sfds");

        System.out.println(s);
    }

}
