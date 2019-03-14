package com.wks.test.limiter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.Uninterruptibles;

import static java.util.concurrent.TimeUnit.MICROSECONDS;

public class TestLimiter {
    public static void main(String[] args) throws InterruptedException {
        MySmoothRateLimiter rateLimiter = (MySmoothRateLimiter) MyRateLimiter.create(50);

        Long t1=System.nanoTime();
        JSONObject o1 = (JSONObject) JSON.toJSON(rateLimiter);
        System.out.println("序列化后" + o1);

        MySmoothRateLimiter limiter = rateLimiter;// JSON.parseObject(o1.toString(), MySmoothRateLimiter.class);

        System.out.println("next:" + limiter.getNextFreeTicketMicros());
        System.out.println("past:" + limiter.readMicros());
        for (int i = 0; i < 100; i++) {
            boolean getOrNot = limiter.tryAcquire();

            if (!getOrNot) {
                System.out.println("没得到");
                Thread.sleep(1000L);
            } else {
                System.out.println("得到");
            }

        }

    }


}
