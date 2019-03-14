package com.wks.test.limiter;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.max;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class MyRateLimiter {

    /**
     * 创建一个 限流器 默认使用 SmoothRateLimiter  ,突发时长 默认使用1秒
     *
     * @param permitsPerSecond
     * @return
     */
    public static MyRateLimiter create(double permitsPerSecond) {
        return create(permitsPerSecond, 1.0);
    }

    /**
     * 允许设定 频率 和 突发时长
     *
     * @param permitsPerSecond
     * @param maxBurstSeconds
     * @return
     */
    public static MyRateLimiter create(double permitsPerSecond, double maxBurstSeconds) {
        MyRateLimiter MyRateLimiter = new MySmoothRateLimiter(maxBurstSeconds);
        MyRateLimiter.initRate(permitsPerSecond);
        return MyRateLimiter;
    }

    /**
     * 用来做锁 的互斥信号对象
     */
    private volatile Object mutexDoNotUseDirectly;

    /**
     * getter of mutexDoNotUseDirectly
     *
     * @return
     */
    private Object mutex() {
        Object mutex = mutexDoNotUseDirectly;
        if (mutex == null) {
            synchronized (this) {
                mutex = mutexDoNotUseDirectly;
                if (mutex == null) {
                    mutexDoNotUseDirectly = mutex = new Object();
                }
            }
        }
        return mutex;
    }

    public MyRateLimiter() {
    }

    /**
     * 设置频率
     *
     * @param permitsPerSecond
     */
    public final void initRate(double permitsPerSecond) {

        /*checkArgument( FIXME 因为这里的加载类，导致初始后nextFreeTicket 变4500
                permitsPerSecond > 0.0 && !Double.isNaN(permitsPerSecond), "rate must be positive");*/

        synchronized (mutex()) {
            doSetRate(permitsPerSecond, readMicros());
        }
    }

    abstract void doSetRate(double permitsPerSecond, long nowMicros);


    abstract double doGetRate();


    /**
     * 获取 一个令牌，如果没有 会阻塞等待
     *
     * @return
     */
    public double acquire() {
        return acquire(1);
    }

    /**
     * 获取 指定的令牌数 ，没有就阻塞
     *
     * @param permits
     * @return
     */
    public double acquire(int permits) {
        long microsToWait = reserve(permits);
        sleepMicrosUninterruptibly(microsToWait);
        return 1.0 * microsToWait / SECONDS.toMicros(1L);
    }


    /**
     * 内部使用 ，获得令牌 并 返回 需要等待的时间
     *
     * @param permits
     * @return
     */
    final long reserve(int permits) {
        checkPermits(permits);
        synchronized (mutex()) {
            return reserveAndGetWaitLength(permits, readMicros());
        }
    }


    /**
     * 非阻塞 获取令牌 ， 判断在 timeout 时间内 是否可以获得令牌
     *
     * @param timeout
     * @param unit
     * @return
     */
    public boolean tryAcquire(long timeout, TimeUnit unit) {
        return tryAcquire(1, timeout, unit);
    }

    /**
     * 非阻塞 获取令牌 ， 判断是否可以获得 指定 令牌数
     *
     * @param permits
     * @return
     */
    public boolean tryAcquire(int permits) {
        return tryAcquire(permits, 0, MICROSECONDS);
    }

    /**
     * 非阻塞 获取令牌 ， 判断是否可以获得 1个令牌数
     *
     * @return
     */
    public boolean tryAcquire() {
        return tryAcquire(1, 0, MICROSECONDS);
    }

    /**
     * timeout 时间内 是否可以获得指定数量的 令牌
     *
     * @param permits
     * @param timeout
     * @param unit
     * @return
     */
    public boolean tryAcquire(int permits, long timeout, TimeUnit unit) {
        long timeoutMicros = max(unit.toMicros(timeout), 0);
        checkPermits(permits);
        long microsToWait;

        //带锁，所以不会产生并发导致nowMicros 一样，
        synchronized (mutex()) {
            long nowMicros = readMicros();
            if (!canAcquire(nowMicros, timeoutMicros)) {
                return false;
            } else {
                microsToWait = reserveAndGetWaitLength(permits, nowMicros);
            }
        }
        sleepMicrosUninterruptibly(microsToWait);
        return true;
    }


    /**
     * 判断是否 可以 acquire
     *
     * @param nowMicros
     * @param timeoutMicros
     * @return
     */
    private boolean canAcquire(long nowMicros, long timeoutMicros) {
        return queryEarliestAvailable(nowMicros) - timeoutMicros <= nowMicros;
    }

    /**
     * 预约并返回 需要等待的时间
     *
     * @param permits
     * @param nowMicros
     * @return
     */
    final long reserveAndGetWaitLength(int permits, long nowMicros) {
        long momentAvailable = reserveEarliestAvailable(permits, nowMicros);
        return max(momentAvailable - nowMicros, 0);
    }


    /**
     * 获得 最早的 令牌产生时间
     *
     * @param nowMicros
     * @return
     */
    abstract long queryEarliestAvailable(long nowMicros);


    /**
     * 预约令牌，并返回需要等待的时间
     *
     * @param permits
     * @param nowMicros
     * @return
     */
    abstract long reserveEarliestAvailable(int permits, long nowMicros);


    /**
     * 睡眠类 ，负责阻塞等待令牌时的sleep
     */
/*    public static class SleepingStopwatch {

        public Long startTime;

        public SleepingStopwatch() {
            this.startTime = System.nanoTime();
        }

        public SleepingStopwatch(Long startTime) {
            this.startTime = startTime;
        }


        public long readMicros() {
            return (System.nanoTime() - startTime) / 1000;
        }

        public void sleepMicrosUninterruptibly(long micros) {
            if (micros > 0) {
                Uninterruptibles.sleepUninterruptibly(micros, MICROSECONDS);
            }
        }

    }*/
    public abstract long readMicros();

    public abstract void sleepMicrosUninterruptibly(long micros);

    private static void checkPermits(int permits) {
        checkArgument(permits > 0, "Requested permits (%s) must be positive", permits);
    }
}