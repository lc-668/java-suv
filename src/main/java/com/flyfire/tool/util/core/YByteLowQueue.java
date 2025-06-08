package com.flyfire.tool.util.core;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class YByteLowQueue {

    private volatile byte[] data;
    private volatile int head;    // 队列头部索引
    private volatile int tail;    // 队列尾部索引（下一个要插入的位置）
    private static final int DEFAULT_CAPACITY = 10;
    private int shrinkThreshold; // 压缩缩放倍数阈值
    //  用于原子化读取
    private static final VarHandle ARRAY_HANDLE =
            MethodHandles.arrayElementVarHandle(byte[].class);


    public YByteLowQueue() {
        this(DEFAULT_CAPACITY, 10); // 默认阈值为10
    }

    public YByteLowQueue(int initialCapacity) {
        this(initialCapacity, 10); // 默认阈值为10
    }

    public YByteLowQueue(int initialCapacity, int shrinkThreshold) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        this.data = new byte[initialCapacity];
        this.head = 0;
        this.tail = 0;
        this.shrinkThreshold = shrinkThreshold;
    }

    /**
     * 添加一个字节到队列末尾
     * @param b 要添加的字节
     */
    public synchronized void add(byte b) {
        ensureCapacity(tail + 1);
        data[tail++] = b;
    }

    /**
     * 添加一个字节数组到队列末尾
     * @param bytes 要添加的字节数组
     */
    public synchronized void add(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("Input byte array cannot be null");
        }
        ensureCapacity(tail + bytes.length);
        System.arraycopy(bytes, 0, data, tail, bytes.length);
        tail += bytes.length;
    }

    /**
     * 从队列头部移除并返回一个字节
     * @return 队列头部的字节
     * @throws NoSuchElementException 如果队列为空
     */
    public synchronized byte remove() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        byte value = data[head];
        data[head] = 0; // 可选：清除引用（对基本类型byte不是必须的）
        head++;

        // 当队列为空时，重置指针以节省空间
        if (isEmpty()) {
            head = 0;
            tail = 0;
        }

        return value;
    }

    /**
     * 查看队列头部的字节但不移除
     * @return 队列头部的字节
     * @throws NoSuchElementException 如果队列为空
     */
    public byte peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        int h = head; // volatile 读
        if (h == tail) throw new NoSuchElementException();
        return (byte) ARRAY_HANDLE.getVolatile(data, h); // 原子性读取
    }

    /**
     * 设置压缩缩放倍数阈值
     * @param threshold 当有效容量是头部无效容量的多少倍数时触发压缩，<=0表示不触发
     */
    public void setShrinkThreshold(int threshold) {
        this.shrinkThreshold = threshold;
    }

    /**
     * 获取当前压缩缩放倍数阈值
     * @return 当前阈值
     */
    public int getShrinkThreshold() {
        return shrinkThreshold;
    }

    // 修改ensureCapacity方法，加入压缩逻辑
    private void ensureCapacity(int minCapacity) {
        // 如果数组前面有空闲空间，检查是否需要压缩
        if (head > 0 && shrinkThreshold > 0) {
            int validSize = tail - head; // 有效数据大小
            int invalidSize = head;      // 头部无效空间大小

            // 当有效容量是无效容量的shrinkThreshold倍时，触发压缩
            // 避免真实容量过低时的频繁压缩
            if (invalidSize > 0 && validSize >= invalidSize * shrinkThreshold&& getRealSize()>1024) {
                resize(data.length); // 保持原大小，只是压缩空间
                return;
            }
        }

        // 原有扩容逻辑
        if (minCapacity > data.length) {
            int newCapacity = Math.max(data.length * 2, minCapacity);
            resize(newCapacity);
        }
    }

    /**
     * 调整数组大小
     * @param newCapacity 新的容量
     */
    private void resize(int newCapacity) {
        byte[] newData = new byte[newCapacity];
        int size = size();

        // 复制有效数据到新数组
        System.arraycopy(data, head, newData, 0, size);

        // 更新指针
        head = 0;
        tail = size;
        data = newData;
    }

    /**
     * 获取队列中的字节数
     * @return 当前队列大小
     */
    public int size() {
        return tail - head;
    }

    /**
     * 获取底层数组的真实容量
     * @return 底层数组的长度
     */
    public int getRealSize() {
        return data.length;
    }

    /**
     * 检查队列是否为空
     * @return 如果队列为空返回true
     */
    public boolean isEmpty() {
        return head == tail;
    }

    /**
     * 将队列内容转换为字节数组
     * @return 包含所有元素的字节数组
     */
    public byte[] toByteArray() {
        byte[] result = new byte[size()];
        System.arraycopy(data, head, result, 0, size());
        return result;
    }

    /**
     * 清空队列并释放多余空间（可选）
     */
    public void clear() {
        head = 0;
        tail = 0;
        // 可选：缩小数组到默认大小以节省内存
        data = new byte[DEFAULT_CAPACITY];
    }

    /**
     * 压缩数组以节省空间（可选方法）
     */
    public void trimToSize() {
        if (size() < data.length) {
            resize(size());
        }
    }

    /**
     * 移除并返回队列中的所有元素
     * @return 包含所有元素的字节数组
     * @throws NoSuchElementException 如果队列为空
     */
    public synchronized byte[] removeAll() {
        if (isEmpty()) {
            return new byte[0];
        }

        byte[] result = toByteArray();
        head = 0;
        tail = 0;

        return result;
    }

    public static void main(String[] args) {
        // 测试压缩阈值功能
        YByteLowQueue queue = new YByteLowQueue(20, 3); // 阈值为3

        // 添加元素填满队列
        for (int i = 0; i < 20; i++) {
            queue.add((byte)i);
        }
        System.out.println("初始容量: " + queue.getRealSize()); // 20

        // 移除部分元素，使头部有空闲空间
        for (int i = 0; i < 5; i++) {
            queue.remove();
        }
        System.out.println("移除5个后 - 有效大小: " + queue.size() +
                ", 实际容量: " + queue.getRealSize()); // 15, 20

        // 添加元素，此时有效大小(15)是无效大小(5)的3倍，触发压缩
        queue.add((byte)20);
        System.out.println("添加1个后 - 有效大小: " + queue.size() +
                ", 实际容量: " + queue.getRealSize()); // 16, 20 (但head=0)

        // 测试阈值为0不触发压缩
        queue.setShrinkThreshold(0);
        for (int i = 0; i < 5; i++) {
            queue.remove();
        }
        queue.add((byte)21);
        System.out.println("阈值为0时 - 有效大小: " + queue.size() +
                ", 实际容量: " + queue.getRealSize()); // 12, 20 (head=10)

        // 弹出所有元素
        byte[] allElements = queue.removeAll();
        System.out.println(Arrays.toString(allElements));
        System.out.println("弹出的元素数量: " + allElements.length); // 输出: 3

        // 再次检查队列
        System.out.println("队列是否为空: " + queue.isEmpty()); // 输出: true
    }

}
