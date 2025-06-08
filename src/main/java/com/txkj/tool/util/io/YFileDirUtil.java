package com.txkj.tool.util.io;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class YFileDirUtil {

    /**
     * 深度创建文件夹
     * 那一级别不存在，则额外创建，
     * 比如 /root/.temp/aaa/ccc/ff 如果只存在root那么会从.temp层次开始依次创建道ff完毕
     * rec: Recursion 递归
     */
    @SneakyThrows
    public static void recCreateDir(Path path) {
        if (Files.exists(path)) {
            return;
        }
        Path parent = path.getParent();
        if (!Files.exists(parent)) {
            recCreateDir(parent);
        }
        try {
            Files.createDirectories(path);
        } catch (FileAlreadyExistsException ignored) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void recCreateDir(String path) {
        recCreateDir(Paths.get(path));
    }

    @SneakyThrows
    public static String createDirOnEveryDay(String parentDir) {
        String pathStr = parentDir + "\\" + LocalDate.now();
        Path path = Paths.get(pathStr);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("动态创建目录{}成功");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return pathStr;
    }

    /**
     * 对某个文件夹的按天分类的文件夹进行淘汰, 淘汰需要满足的条件
     * - 文件夹的命名必须是 yyyy-mm-dd 的格式
     * - 比 saveDay 旧的文件
     *
     * @param saveDay
     */
    @SneakyThrows
    public static int evictionOnDay(String dirPath, LocalDate saveDay) {
        File rootDir = new File(dirPath);

        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path");
        }

        File[] subDirs = rootDir.listFiles(File::isDirectory);
        int num = 0;
        if (subDirs == null) {
            return num;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (File subDir : subDirs) {
            String folderName = subDir.getName();
            try {
                LocalDate folderDate = LocalDate.parse(folderName, formatter);
                if (folderDate.isBefore(saveDay)) {
                    num = YFileUtils.clearDir(subDir.getAbsolutePath());
                    subDir.delete();
                    num++;
                }
            } catch (Exception e) {
                // Ignore folders with invalid names
            }
        }
        return num;
    }

    /**
     * 对某个文件夹的按天分类的文件夹进行淘汰, 淘汰需要满足的条件
     * - 文件夹的命名必须是 yyyy-mm-dd 的格式
     * - 比 saveDay 旧的文件
     *
     * @param savTime 保留的时间点
     */
    @SneakyThrows
    public static int evictionOnTime(String dirPath, DateTimeFormatter formatter, LocalDateTime savTime) {
        File rootDir = new File(dirPath);

        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path");
        }

        File[] subDirs = rootDir.listFiles();
        int num = 0;
        if (subDirs == null) {
            return num;
        }
        for (File file : subDirs) {
            String folderName = file.getName();
            try {
                LocalDateTime folderDate = LocalDateTime.parse(folderName, formatter);
                if (folderDate.isBefore(savTime)) {
                    if (file.isDirectory()) {
                        num = YFileUtils.clearDir(file.getAbsolutePath());
                    }
                    file.delete();
                    num++;
                }
            } catch (Exception e) {
                // Ignore folders with invalid names
            }
        }
        return num;
    }

    public static int evictionOnDay(String dirPath, int saveDay) {
        LocalDate date = LocalDate.now().plusDays(-saveDay + 1);
        return evictionOnDay(dirPath, date);
    }


    public static void main(String[] args) {
        /*recCreateDir("D:\\temp3\\6款vue大屏猿乐享资源\\6款vue大屏\\Vue电力大数据可视化\\源码\\dianli\\mdImg");*/
        /*evictionOnDay("D:\\temp3", 1);*/
        try {
            //  发现创建一个已经存在的目录不会报错
            Files.createDirectories(Paths.get("D:\\temp"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
