package com.wks.test.limiter;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.Uninterruptibles;

import static java.util.concurrent.TimeUnit.MICROSECONDS;

public class TestLimiter {
    public static void main(String[] args) throws InterruptedException {
        MySmoothRateLimiter rateLimiter = (MySmoothRateLimiter)MyRateLimiter.create(50);

        Object o1 = JSON.toJSON(rateLimiter);
        System.out.println(o1);


        for (int i = 0; i < 10; i++) {
            boolean getOrNot = rateLimiter.tryAcquire();
            if (!getOrNot) {
                //System.out.println("没得到");
                Thread.sleep(1000L);
            } else {
                //System.out.println("得到");
                //System.out.println("剩余："+ rateLimiter.getStoredPermits());
            }
        }


        Object o2 = JSON.toJSON(rateLimiter);
        //System.out.println(o2);


        long past =
                new MyRateLimiter.SleepingStopwatch() {
                    final Stopwatch stopwatch = Stopwatch.createStarted();

                    {
                        Long t1=System.nanoTime();
                        final Stopwatch stopwatch = Stopwatch.createStarted();
                        //System.out.println("new对象 消耗"+ (System.nanoTime()-t1 )/1000  );
                    }

                    @Override
                    protected long readMicros() {
                        return stopwatch.elapsed(MICROSECONDS);
                    }

                    @Override
                    protected void sleepMicrosUninterruptibly(long micros) {
                        if (micros > 0) {
                            Uninterruptibles.sleepUninterruptibly(micros, MICROSECONDS);
                        }
                    }
                }.readMicros();
         //System.out.println(past);
     }


}
