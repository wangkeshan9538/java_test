package com.wks.test.JVMtest;

import java.util.ArrayList;
import java.util.List;

public class TestB {


    public  int i;

    public void handleI() {
        i++;
        List<String> s=new ArrayList<String>();
        s.get(0).length();
        List<Long> ss=new ArrayList<Long>();
    }

    public void print(List<String> list)  { }

    class BBBB<T>{

    }

    public String main() {
        String s = "s";
        String b = "b";
        try {
            return s;
        } finally {
            return b;
        }
    }
}
