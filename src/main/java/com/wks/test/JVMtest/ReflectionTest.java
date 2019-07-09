package com.wks.test.JVMtest;

import java.lang.reflect.Method;

public class ReflectionTest {

    private static int count = 0;
    public static void foo() {
        new Exception("test#" + (count++)).printStackTrace();
    }

    public static void main(String[] args) throws Exception {
        Class<?> clz = Class.forName( "com.wks.test.JVMtest.ReflectionTest");
        Method method = clz.getMethod("foo");
        for (int i = 0; i < 20; i++) {
            method.invoke(null);
        }

        System.in.read();
    }
}
