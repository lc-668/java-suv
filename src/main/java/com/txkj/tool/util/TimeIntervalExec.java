package com.txkj.tool.util;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 时间间隔锁
 */
@Accessors(chain = true)
public class TimeIntervalExec {


    @Setter
    @Getter
    private ChronoUnit type = ChronoUnit.SECONDS;

    @Setter
    @Getter
    private int interval = 60;

    private AtomicReference<LocalDateTime> lastRecord = new AtomicReference<>();

    public boolean tryExec(Runnable runnable) {
        if (lastRecord.get() == null) {
            synchronized (this){
                if(lastRecord.get()!=null){
                    return tryExec(runnable);
                }
                runnable.run();
                lastRecord=new AtomicReference<>(LocalDateTime.now());
                return false;
            }
        }
        if (type == ChronoUnit.MINUTES) {
            return execFun(ChronoUnit.MINUTES, LocalDateTime.now().withSecond(0).withNano(0), runnable);
        } else if (type == ChronoUnit.SECONDS) {
            return execFun(ChronoUnit.SECONDS, LocalDateTime.now().withNano(0), runnable);
        } else if (type == ChronoUnit.MILLIS) {
            return execFun(ChronoUnit.MILLIS, LocalDateTime.now(), runnable);
        } else {
            throw new RuntimeException("暂未实现该方式");
        }
    }

    public boolean execFun(ChronoUnit unit, LocalDateTime current, Runnable runnable) {
        long between = unit.between(lastRecord.get(), current);
        if (between < interval) {
            return false;
        }
        synchronized (this){
            if (between < interval) {
                return false;
            }
            try {
                runnable.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                lastRecord.set(current);
                return true;
            }
        }
    }

    public LocalDateTime getLastRecord() {
        return lastRecord.get();
    }

    @SneakyThrows
    public static void main(String[] args) {
        TimeIntervalExec exec = new TimeIntervalExec().setType(ChronoUnit.MILLIS);
        exec.setInterval(200);
        for (int i = 0; i < 10000; i++) {
            Thread.sleep(1);
            final int flag = i;
            exec.tryExec(() -> {
                System.out.println(flag + " 开始执行" + exec.lastRecord.get());
            });
        }
    }


}
