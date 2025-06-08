package com.flyfire.tool.stream;


import com.flyfire.tool.util.TimeIntervalExec;
import com.flyfire.tool.util.dt.YDateTimes;
import com.flyfire.tool.util.dt.YTimes;
import com.flyfire.tool.util.io.YLogWriter;
import com.flyfire.tool.util.core.YLimitByteQueue;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;


public class SuvOutStream extends OutputStream {

    private boolean writeToConsole = true;

    public volatile YLimitByteQueue queue = new YLimitByteQueue(2048);

    private volatile ThreadOut threadOut = null;

    public YLogWriter logWriter;

    private TimeIntervalExec writerInterval = new TimeIntervalExec().setInterval(45).setType(ChronoUnit.SECONDS);

    public SuvOutStream() {

    }

    @Override
    public void write(int b) throws IOException {
        if (writeToConsole) {
            queue.fi((byte) b);
            if(logWriter!=null)
                logWriter.write((byte) b);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (writeToConsole) {
            for (byte b1 : b) {
                write(b1);
            }
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (writeToConsole) {
            for (int i = off; i < off + len; i++) {
                write(b[i]);
            }
        }
    }

    public void writeLine(String line) throws IOException {
        write(line.getBytes(StandardCharsets.UTF_8));
        write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    public synchronized void addListener(OutputStream outputStream) {
        if (threadOut != null) {
            threadOut.thread.interrupt();
            threadOut.isExit = true;
            threadOut = null;
        }
        ThreadOut out = new ThreadOut();
        out.outputStream = outputStream;
        threadOut = out;
        threadOut.thread.start();
    }

    public void addSyncListener(OutputStream outputStream) {
        if (threadOut != null) {
            threadOut.isExit = true;
            threadOut = null;
        }
        synchronized (this) {
            ThreadOut out = new ThreadOut();
            out.outputStream = outputStream;
            threadOut = out;
            threadOut.thread.run();
        }
    }

    public class ThreadOut {
        private OutputStream outputStream;
        public volatile boolean isExit = false;
        private Thread thread = new Thread(() -> {
            LocalDateTime end = LocalDateTime.now().plusMinutes(20);
            try {
                outputStream.write(("连接成功!将会滚动更新至" + end.format(YDateTimes.YMD_HMS) + "\r\n").getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                while (!Thread.interrupted()) {
                    if (isExit) {
                        outputStream.write(("已经被其他请求抢占信道").getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                        break;
                    }
                    var list = queue.foAll();
                    writerInterval.tryExec(() -> {
                        System.out.println(LocalTime.now().format(YTimes.HMS) + "读取到新的消息:" + list.length);
                    });
                    for (Byte b : list) {
                        outputStream.write(b);
                    }
                    if (YDateTimes.compare(end, "<", LocalDateTime.now())) {
                        System.out.println("坚持到实时日志已经超时");
                        outputStream.write("坚持到实时日志已经超时".getBytes(StandardCharsets.UTF_8));
                        break;
                    }
                    try {
                        outputStream.flush();
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.println("出现中断异常");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                System.out.println("开始关闭进程");
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


}
