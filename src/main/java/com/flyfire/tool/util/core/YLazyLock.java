package com.flyfire.tool.util.core;

import lombok.Setter;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 可以安全的懒加载
 * 适用场景，替代双重检查锁的繁琐使用,
 * 需要传入一个java8的函数接口方便，同时这个函数接口,设置值，
 */
public class YLazyLock<T> {


    protected volatile T instance;
    private Lock lock = new ReentrantLock();

    /**
     * 执行懒加载的函数,必传
     * 推荐使用构造的原因是因为
     * 但是配合spring的时候不方便使用spring 这个时候可以如此加载
     */
    @Setter
    private Supplier<T> initFun;

    /**
     * 如果不为空,init时回注入这个值
     */
    @Setter
    private Consumer<T> setLazyValFun;

    public YLazyLock() {
    }

    public YLazyLock(Supplier<T> initFun) {
        this.initFun = initFun;
    }

    /**
     * 双重检查锁的关键就是进入锁前和进入锁后分别判断一次null
     *
     * @return 本次懒加载是否成功
     */
    public boolean init() {
        var tempLock = lock;
        if (instance != null) {
            return false; // 已经初始化，不需要再执行懒加载
        }
        tempLock.lock();
        try {
            if (instance != null) {
                return false;// 第二次判断空
            }
            T value = initFun.get();
            if (setLazyValFun != null) {
                setLazyValFun.accept(value);
            }
            instance = value;
            //  清空变量,方便gc
            this.lock = null; //lock 清空可能暂时会有gc的问题,所以使用tempLock代替捕捉
            setLazyValFun = null;
            initFun = null;
            return true; // 懒加载成功
        } finally {
            tempLock.unlock();
        }
    }


}
