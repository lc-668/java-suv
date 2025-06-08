package com.txkj.tool.util.core;

import java.util.function.Supplier;

/**
 * 懒加载值
 * 可以安全的懒加载
 * 适用场景，减少双重检查锁的繁琐使用,
 */
public class YLazyValue<T> extends YLazyLock<T> {

    public YLazyValue(Supplier<T> initFun) {
        super(initFun);
    }

    public YLazyValue() {
        super();
    }

    public T getValue() {
        if (instance != null) {
            return instance;
        }
        init();
        return instance;
    }



}
