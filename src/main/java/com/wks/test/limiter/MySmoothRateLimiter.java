package com.wks.test.limiter;

import com.google.common.math.LongMath;

import static java.lang.Math.min;
import static java.util.concurrent.TimeUnit.SECONDS;

public   class MySmoothRateLimiter extends MyRateLimiter{

    double maxBurstSeconds;



    /** The currently stored permits. */
    double storedPermits;

    /** The maximum number of stored permits. */
    double maxPermits;

    /**
     * The interval between two unit requests, at our stable rate. E.g., a stable rate of 5 permits
     * per second has a stable interval of 200ms.
     */
    double stableIntervalMicros;

    /**
     * The time when the next request (no matter its size) will be granted. After granting a request,
     * this is pushed further in the future. Large requests push this further than small requests.
     */
    private long nextFreeTicketMicros = 0L; // could be either in the past or future

    public MySmoothRateLimiter(SleepingStopwatch stopwatch, double maxBurstSeconds) {
        super(stopwatch);
        this.maxBurstSeconds=maxBurstSeconds;
    }

    @Override
    final void doSetRate(double permitsPerSecond, long nowMicros) {
        resync(nowMicros);
        double stableIntervalMicros = SECONDS.toMicros(1L) / permitsPerSecond;
        this.stableIntervalMicros = stableIntervalMicros;
        doSetRate(permitsPerSecond, stableIntervalMicros);
    }


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
    double coolDownIntervalMicros(){
        return stableIntervalMicros;
    }

    /** Updates {@code storedPermits} and {@code nextFreeTicketMicros} based on the current time. */
    void resync(long nowMicros) {
        // if nextFreeTicket is in the past, resync to now
        if (nowMicros > nextFreeTicketMicros) {
            double newPermits = (nowMicros - nextFreeTicketMicros) / coolDownIntervalMicros();
            storedPermits = min(maxPermits, storedPermits + newPermits);
            nextFreeTicketMicros = nowMicros;
        }
    }


    /**
     * get set
     *
     *
     */


    public double getMaxBurstSeconds() {
        return maxBurstSeconds;
    }

    public void setMaxBurstSeconds(double maxBurstSeconds) {
        this.maxBurstSeconds = maxBurstSeconds;
    }

    public double getStoredPermits() {
        return storedPermits;
    }

    public void setStoredPermits(double storedPermits) {
        this.storedPermits = storedPermits;
    }

    public double getMaxPermits() {
        return maxPermits;
    }

    public void setMaxPermits(double maxPermits) {
        this.maxPermits = maxPermits;
    }

    public double getStableIntervalMicros() {
        return stableIntervalMicros;
    }

    public void setStableIntervalMicros(double stableIntervalMicros) {
        this.stableIntervalMicros = stableIntervalMicros;
    }

    public long getNextFreeTicketMicros() {
        return nextFreeTicketMicros;
    }

    public void setNextFreeTicketMicros(long nextFreeTicketMicros) {
        this.nextFreeTicketMicros = nextFreeTicketMicros;
    }
}