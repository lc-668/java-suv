package com.flyfire.tool.util.io;

import java.util.function.Supplier;

/**
 * 动态目录
 * 可以用来实现每日新增的目录结构这种操作
 * 通过函数式接口进行调用计算的
 * 要么存在静态的dirPath 路径,要么一定是dynamicDirFun方法
 * 为了避免安全事故，文件只能允许一级动态目录
 * 同时不建议层级过深,后期做限制
 */
public class YDynamicDir {

    /**
     * 离自己最近的一级动态目录
     */
    private YDynamicDir parentDir = null;

    /**
     * 用来生产动态目录方法用的
     */
    private Supplier<String> dynamicDir;

    private String staticDir;

    private boolean isDynamicCreateDir;

    private YDynamicDir() {
    }

    public YDynamicDir(String staticDir) {
        this(null, null, staticDir, true);
    }


    /**
     * @param staticDir   静态路径
     * @param isCreateDir
     */
    public YDynamicDir(String staticDir, boolean isCreateDir) {
        this(null, null, staticDir, isCreateDir);
    }

    public YDynamicDir(Supplier<String> dynamicPath) {
        this(null, dynamicPath, null, true);
    }

    public YDynamicDir(Supplier<String> dynamicPath, boolean isDynamicCreateDir) {
        this(null, dynamicPath, null, isDynamicCreateDir);
    }

    private YDynamicDir(YDynamicDir parentDir, Supplier<String> dynamicDir, String staticDir, boolean isDynamicCreateDir) {
        this.parentDir = parentDir;
        this.dynamicDir = dynamicDir;
        this.staticDir = staticDir;
        this.isDynamicCreateDir = isDynamicCreateDir;
    }

    public YDynamicDir getChildDir(String appendStr) {
        YDynamicDir yDynamicDir = new YDynamicDir(appendStr, this.isDynamicCreateDir);
        yDynamicDir.parentDir = this;
        return yDynamicDir;
    }

    public String appendFilePath(String filename) {
        return YPathUtils.joinStrPath(getDynamicDir(), filename);
    }



    /**
     * getDirPath
     *
     * @return
     */
    public String getDynamicDir() {
        //  如果既没有父级动态节点,也不存在
        String dynamicDir = getDynamicDir0();
        if (isDynamicCreateDir) {
            YFileDirUtil.recCreateDir(dynamicDir);
        }
        return dynamicDir;
    }

    private String getDynamicDir0() {
        //  如果既没有父级动态节点,也不存在
        if (this.parentDir == null) {
            return getSelfPath();
        }
        //  能够走到这里说明父级节点一点存在
        String parentDir = this.parentDir.getDynamicDir0();
        return YPathUtils.joinStrPath(parentDir, getSelfPath());
    }

    public String getSelfPath() {
        if (dynamicDir != null) {
            return dynamicDir.get();
        }
        return staticDir;
    }




}