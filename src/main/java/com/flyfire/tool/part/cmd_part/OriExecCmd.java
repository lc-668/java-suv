package com.flyfire.tool.part.cmd_part;

import com.flyfire.tool.model.SuvExitRes;
import com.flyfire.tool.model.SuvProgramCmd;
import com.flyfire.tool.stream.SuvOutStream;
import lombok.SneakyThrows;

import java.io.*;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * 原生的命令执行器
 */
public class OriExecCmd implements IExecCmd {

    @SneakyThrows
    @Override
    public void exec(SuvProgramCmd suvProgramCmd, SuvOutStream ps) {
        System.out.println("通过java原生的方式执行 " + Arrays.toString(suvProgramCmd.commands));
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(suvProgramCmd.execDir));
        if (suvProgramCmd.environment != null) {
            builder.environment().putAll(suvProgramCmd.environment);
        }
        builder.command(suvProgramCmd.commands);
        Process process = builder.start();
        suvProgramCmd.pid = process.pid();
        redirectOutput(process.getInputStream(), process.getErrorStream(), ps, suvProgramCmd.fnFinish);
    }


    /*public static void redirectOutput(InputStream stdInp,InputStream errInp, OutputStream output, Runnable funEnd) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.write(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                if (funEnd != null) {
                    funEnd.run();
                }
            }
        }).start();
    }*/


    public static void redirectOutput(InputStream stdInp, InputStream errInp, SuvOutStream output, Consumer<SuvExitRes> funEnd) {
        Thread stdThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stdInp))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.writeLine(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread errThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(errInp))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.writeLine(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        stdThread.start();
        errThread.start();

        new Thread(() -> {
            try {
                stdThread.join();
                errThread.join();
                funEnd.accept(new SuvExitRes().setExitValue(1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                funEnd.accept(new SuvExitRes().setException(e));
            }
        }).start();
    }


}
