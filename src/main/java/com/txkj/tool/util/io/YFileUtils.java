package com.txkj.tool.util.io;

import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class YFileUtils {

    /**
     * 清空某个文件夹下的所有文件以及子文件夹，会迭代清除
     *
     * @param path
     */
    public static int clearDir(String path) {
        File dir = new File(path);

        // 检查文件夹是否存在
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("目录不存在！");
            return 0;
        }

        // 获取文件夹下的所有文件和子文件夹
        File[] files = dir.listFiles();
        int num = 0;
        // 遍历并删除
        if (files == null) {
            return num;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                // 递归清空子文件夹
                num += clearDir(file.getAbsolutePath());
                file.delete();
            } else {
                // 删除文件
                if (!file.delete()) {
                    System.out.println("无法删除文件：" + file.getName());
                }
            }
            num++;
        }
        return num;
    }

    /**
     * 获取打包后的资源路径的内容
     *
     * @param resourcePath 需要传入资源的相对路径
     * @return
     */
    public static String readJarResContent(String resourcePath) {
        ClassLoader classLoader = YFileUtils.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            byte[] buffer = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len = inputStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len, "UTF-8"));
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 默认使用UTF-8编码
     *
     * @param file
     * @param charset
     * @return
     */
    public static String readContent(File file, Charset charset) {
        if (charset == null) {
            charset = Charset.forName("UTF-8");
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len = inputStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len, charset));
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将内容写入指定的路径，且是覆盖写入
     * 如果文件不存在就会创建,使用utf-8
     *
     * @param filePath
     */
    @SneakyThrows
    public static void delete(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 将内容写入指定的路径，且是覆盖写入
     * 如果文件不存在就会创建,使用utf-8
     *
     * @param filePath
     * @param content
     */
    @SneakyThrows
    public static void overwrite(String filePath, String content) {
        //  第二个参数为false表示不进行追加模式，而是覆盖模式。如果指定的文件不存在，它将被创建。如果文件已经存在，那么将会被覆盖。
        //  try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8)) {
            // 使用UTF-8编码写入内容
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace(); // 处理异常，比如文件无法创建或写入失败
        }
    }

}
