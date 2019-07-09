package com.wks.test.RemoteExe;

public class B extends A {
    static {
        System.out.println("B init");
    }
    public B() {
        System.out.println("B Instance");
    }

    public static void main(String[] args) {

    }

    public static final String HELLOWORD = "hello word";
}
