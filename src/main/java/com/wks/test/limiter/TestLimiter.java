package com.wks.test.limiter;

import com.alibaba.fastjson.JSON;

public class TestLimiter {

    public static void main(String[] args) throws InterruptedException {
        MyRateLimiter rateLimiter = MyRateLimiter.create(50);
         for(int i=0;i<10;i++){
            boolean getOrNot=rateLimiter.tryAcquire();
            if (!getOrNot){
                System.out.println("没得到");
                Thread.sleep(1000L);
            }else{
                System.out.println("得到");
            }
        }


        Object o=JSON.toJSON(rateLimiter);
        System.out.println(o);
    }


}
