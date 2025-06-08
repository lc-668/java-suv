package com.flyfire.tool.util;



import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的cmd
 */
public class Cmder {

    /**
     * 执行一个cmd命令
     *
     * @param cmdCommand cmd命令
     * @return 命令执行结果字符串，如出现异常返回null
     */
    public static String executeCmdCommand(String cmdCommand,String... param) {
        StringBuilder stringBuilder = new StringBuilder();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmdCommand,param);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行一个cmd命令
     *
     * @param cmdCommand cmd命令
     * @return 命令执行结果字符串，如出现异常返回null
     */
    public static List<String> executeCmdCommandRetList(String cmdCommand) {
        List<String> ret=new ArrayList<>();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmdCommand);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                //stringBuilder.append(line).append("\n");
                ret.add(line);
            }
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行bat文件，
     *
     * @param file          bat文件路径
     * @param isCloseWindow 执行完毕后是否关闭cmd窗口
     * @return bat文件输出log
     */
    public static String executeBatFile(String file, boolean isCloseWindow) {
        String cmdCommand = null;
        if (isCloseWindow) {
            cmdCommand = "cmd.exe /c " + file;
        } else {
            cmdCommand = "cmd.exe /k " + file;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmdCommand);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(" ");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 执行bat文件,新开窗口
     *
     * @param file          bat文件路径
     * @param isCloseWindow 执行完毕后是否关闭cmd窗口
     * @return bat文件输出log
     */
    public static String executeBatFileWithNewWindow(String file, boolean isCloseWindow) {
        String cmdCommand;
        if (isCloseWindow) {
            cmdCommand = "cmd.exe /c start" + file;
        } else {
            cmdCommand = "cmd.exe /k start" + file;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Process process;
        try {
            process = Runtime.getRuntime().exec(cmdCommand);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(" ");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 执行bat脚本
     *
     * @param batScript 脚本内容
     * @param location  脚本存储路径
     * @return 结果
     */
    public static String executeBatScript(String batScript, String location) {
        StringBuilder stringBuilder = new StringBuilder();
        FileWriter fw = null;
        try {
            //生成bat文件
            fw = new FileWriter(location);
            fw.write(batScript);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Process process;
        try {
            process = Runtime.getRuntime().exec(location);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(" ");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 执行脚本,不停止,并输出执行结果
     *
     * @param batScript 脚本内容
     * @param location  bat文件生成地址
     */
    public void executeBatScriptAlways(String batScript, String location) {
        FileWriter fw = null;
        try {
            //生成bat文件
            fw = new FileWriter(location);
            fw.write(batScript);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StringBuilder stringBuilder = new StringBuilder();
        //运行bat文件
        Process process;
        try {
            process = Runtime.getRuntime().exec(location);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    public void apacheExec(String cmd){
//        //接收正常结果流
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        //接收异常结果流
//        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
//
//        CommandLine commandLine=new CommandLine(cmd);
//        DefaultExecutor executor = new DefaultExecutor();
//        executor.setExitValue(1);
//        //设置60秒超时，执行超过60秒后会直接终止
//        ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
//        executor.setWatchdog(watchdog);
//        PumpStreamHandler handler = new PumpStreamHandler(outputStream,errorStream);
//        String out=null,error=null;
//        try {
//            executor.setStreamHandler(handler);
//            //命令执行返回前一直阻塞
//            executor.setWatchdog(watchdog);
//            out=outputStream.toString("GBK");
//            error=errorStream.toString("GBK");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("正常输出:"+out);
//        System.out.println("错误输出:"+error);
//    }

}
