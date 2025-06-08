package com.flyfire.tool.util.core;

/**
 * 有界字节队列（线程安全），基于 YByteLowQueue 实现
 */
public class YLimitByteQueue {

    private final YByteLowQueue queue;  // 复用 YByteLowQueue 的底层逻辑
    private final int limit;            // 队列容量上限
    private ByteConsumer foEvent;      // 元素淘汰时的回调

    public YLimitByteQueue(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        this.queue = new YByteLowQueue(limit);
        this.limit = limit;
    }

    public synchronized boolean fi(byte b) {
        // 超限时触发淘汰
        while (queue.size() >= limit) {
            byte removed = fo();
            if (foEvent != null) {
                foEvent.accept(removed);
            }
        }
        queue.add(b);
        return true;
    }

    //  唯一的非特化,因为需要一个null
    public synchronized Byte fo() {
        return queue.isEmpty() ? null : queue.remove();
    }

    public synchronized byte[] foAll() {
        return queue.removeAll();
    }

    public byte peekFirst() {
        return queue.isEmpty() ? null : queue.peek();
    }

    public synchronized int size() {
        return queue.size();
    }

    public synchronized void clear() {
        queue.clear();
    }

    // Setter for foEvent
    public void setFoEvent(ByteConsumer foEvent) {
        this.foEvent = foEvent;
    }

    // 自定义字节消费者接口
    @FunctionalInterface
    public interface ByteConsumer {
        void accept(byte b);
    }

    // 测试代码
    public static void main(String[] args) {
        YLimitByteQueue queue = new YLimitByteQueue(3);
        queue.setFoEvent(b -> System.out.println("淘汰: " + b));

        // 测试超限淘汰
        queue.fi((byte) 1);
        queue.fi((byte) 2);
        queue.fi((byte) 3);
        queue.fi((byte) 4); // 触发淘汰 1

        // 输出当前队列
        System.out.println("当前队列: " + queue.foAll()); // [2, 3, 4]
    }


}