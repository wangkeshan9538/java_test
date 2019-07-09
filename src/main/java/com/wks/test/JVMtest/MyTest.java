package com.wks.test.JVMtest;

import java.io.IOException;


interface MyInterface {
    public void testMe();
}


abstract class A {
    public void printMe() {
        System.out.println("I love vim");
    }

    public abstract void sayHello();
}

class B extends A implements MyInterface {
    @Override
    public void sayHello() {
        System.out.println("hello, i am child B");
    }

    @Override
    public void testMe() {
        System.out.println("test me");
    }
}

public class MyTest {
    public static void main(String[] args) throws IOException {
        A obj = new B();
        System.in.read();
        System.out.println(obj);
    }
}
