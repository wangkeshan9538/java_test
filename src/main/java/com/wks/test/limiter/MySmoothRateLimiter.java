package com.wks.test.limiter;

import com.google.common.math.LongMath;
import com.google.common.util.concurrent.Uninterruptibles;
import lombok.Data;

import static java.lang.Math.min;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;


@Data
public class MySmoothRateLimiter extends MyRateLimiter {


    /**
     * 允许的 突发 时间长度
     */
    double maxBurstSeconds;

    /**
     * 当前已存的 令牌数量
     */
    double storedPermits;

    /**
     * 最大允许 存储 的令牌数量
     */
    double maxPermits;

    /**
     * 使用频率算出的 令牌产生间隔
     */
    double stableIntervalMicros;

    /**
     * 下次产生 令牌的 时间
     */
    private long nextFreeTicketMicros = 0L; // could be either in the past or future


    public Long startTime;


    public MySmoothRateLimiter() {
        this.startTime=System.nanoTime();
    }

    public MySmoothRateLimiter( double maxBurstSeconds) {
        super();
        this.maxBurstSeconds = maxBurstSeconds;
        this.startTime=System.nanoTime();
    }


    /**
     * 设置 rate
     *
     * @param permitsPerSecond
     * @param nowMicros
     */
    @Override
    final void doSetRate(double permitsPerSecond, long nowMicros) {
        resync(nowMicros);

        double stableIntervalMicros = SECONDS.toMicros(1L) / permitsPerSecond;
        this.stableIntervalMicros = stableIntervalMicros;
        doSetRate(permitsPerSecond, stableIntervalMicros);
    }


    /**
     * @param permitsPerSecond
     * @param stableIntervalMicros
     */
    void doSetRate(double permitsPerSecond, double stableIntervalMicros) {
        double oldMaxPermits = this.maxPermits;
        maxPermits = maxBurstSeconds * permitsPerSecond;
        if (oldMaxPermits == Double.POSITIVE_INFINITY) {
            // if we don't special-case this, we would get storedPermits == NaN, below
            storedPermits = maxPermits;
        } else {
            storedPermits =
                    (oldMaxPermits == 0.0)
                            ? 0.0 // initial state
                            : storedPermits * maxPermits / oldMaxPermits;
        }
    }

    @Override
    final double doGetRate() {
        return SECONDS.toMicros(1L) / stableIntervalMicros;
    }

    @Override
    final long queryEarliestAvailable(long nowMicros) {
        return nextFreeTicketMicros;
    }

    @Override
    final long reserveEarliestAvailable(int requiredPermits, long nowMicros) {
        resync(nowMicros);
        long returnValue = nextFreeTicketMicros;
        double storedPermitsToSpend = min(requiredPermits, this.storedPermits);
        double freshPermits = requiredPermits - storedPermitsToSpend;
        long waitMicros = (long) (freshPermits * stableIntervalMicros);

        this.nextFreeTicketMicros = LongMath.saturatedAdd(nextFreeTicketMicros, waitMicros);
        this.storedPermits -= storedPermitsToSpend;
        return returnValue;
    }

    /**
     * Returns the number of microseconds during cool down that we have to wait to get a new permit.
     */
    double coolDownIntervalMicros() {
        return stableIntervalMicros;
    }

    /**
     * Updates storedPermits nextFreeTicketMicros  based on the current time.
     * 同步 令牌桶的信息
     */
    void resync(long nowMicros) {
        // if nextFreeTicket is in the past, resync to now
        if (nowMicros > nextFreeTicketMicros) {
            //算出这段间隔 内产生了多少新的令牌
            double newPermits = (nowMicros - nextFreeTicketMicros) / coolDownIntervalMicros();
            //更新存储令牌数
            storedPermits = min(maxPermits, storedPermits + newPermits);
            //
            nextFreeTicketMicros = nowMicros;
        }
    }

    @Override
    public void sleepMicrosUninterruptibly(long micros) {
        if (micros > 0) {
            Uninterruptibles.sleepUninterruptibly(micros, MICROSECONDS);
        }
    }
    @Override
    public long readMicros() {
        return (System.nanoTime() - startTime) / 1000;
    }
}