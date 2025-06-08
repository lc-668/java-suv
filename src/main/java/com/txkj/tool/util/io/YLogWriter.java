package com.txkj.tool.util.io;

import com.txkj.tool.util.TimeIntervalExec;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class YLogWriter implements AutoCloseable {

    private WritableByteChannel channel;
    private String logName;
    private final ByteBuffer buffer;
    private final int bufferSize;
    private int writtenBytes;
    public TimeIntervalExec bufferInterval;
    public TimeIntervalExec clearInterval = new TimeIntervalExec().setInterval(10).setType(ChronoUnit.MINUTES);
    private String outLogDir;
    public static final DateTimeFormatter YMD_H_FILE = DateTimeFormatter.ofPattern("yyyyMMdd_HH'.log'");

    @SneakyThrows
    public YLogWriter(String outLogDir, int bufferSize, Integer bufferSec) {
        this.bufferSize = bufferSize;
        File logDir = new File(outLogDir);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        /*this.channel = new FileOutputStream(logFile, true).getChannel();*/
        this.outLogDir = outLogDir;
        this.buffer = ByteBuffer.allocate(bufferSize);
        if (bufferSec == null) {
            bufferSec = 60;
        }
        this.bufferInterval = new TimeIntervalExec().setInterval(bufferSec).setType(ChronoUnit.SECONDS);
    }

    public void write(String logEntry) {
        byte[] bytes = (logEntry + "\n").getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            write(b);
        }
    }

    public synchronized void write(byte b) {
        if (writtenBytes >= bufferSize) {
            flush();
        }
        bufferInterval.tryExec(() -> {
            if (writtenBytes == 0) {
                return;
            }
            /*System.out.println("超时写入,当前size:" + writtenBytes);*/
            this.flush();
            writtenBytes = 0;
        });
        if (buffer.hasRemaining()) {
            buffer.put(b);
            writtenBytes++;
        }
    }

    @SneakyThrows
    public synchronized void flush() {
        if (writtenBytes == 0) {
            return;
        }
        buffer.flip(); // Prepare for writing
        channel = getCurrentLogFile();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        buffer.clear(); // Prepare for next write
        writtenBytes = 0;
        clearInterval.tryExec(this::remoteLog);
    }

    @Override
    public void close() throws IOException {
        if (buffer.position() > 0) { // If there's anything left in the buffer, flush it.
            flush();
        }
        channel.close();
    }

    @SneakyThrows
    private WritableByteChannel getCurrentLogFile() {
        LocalDateTime now = LocalDateTime.now();
        String fileName = now.format(YMD_H_FILE);
        if (logName != null) {
            if (fileName.equals(logName)) {
                return channel;
            } else {
                /*System.out.println("关闭上一个日志输出器:" + logName);*/
                channel.close();
            }
        }
        logName = fileName;
        File logDir = new File(outLogDir);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        File file = new File(logDir, fileName);
        return new FileOutputStream(file, true).getChannel();
    }

    /**
     * 只保留最近十个小时的日志文件
     */
    public void remoteLog() {
        YFileDirUtil.evictionOnTime(outLogDir, YMD_H_FILE, LocalDateTime.now().plusHours(-6));
    }

}

