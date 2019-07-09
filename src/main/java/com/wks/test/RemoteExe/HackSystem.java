package com.wks.test.RemoteExe;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 *为JavaClass劫持java.lang.System提供支持
 *除了out和err外，其余的都直接转发给System处理
 *
 *@author zzm
 */
public class HackSystem{
    public final static InputStream in=System.in;
    public   static PrintStream out;

    static {
        try {
            out = new PrintStream("E:\\code\\java_test\\target\\classes\\com\\wks\\test\\RemoteExe\\logfile");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public final static PrintStream err=out;

    public static void setSecurityManager(final SecurityManager s){
        System.setSecurityManager(s);
    }
    public static SecurityManager getSecurityManager(){
        return System.getSecurityManager();
    }
    public static long currentTimeMillis(){
        return System.currentTimeMillis();
    }
    public static void arraycopy(Object src,int srcPos,Object dest,int destPos,int length){
        System.arraycopy(src,srcPos,dest,destPos,length);
    }
    public static int identityHashCode(Object x){
        return System.identityHashCode(x);
    }
//下面所有的方法都与java.lang.System的名称一样
//实现都是字节转调System的对应方法
//因版面原因，省略了其他方法
}
