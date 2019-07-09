package com.wks.test.RemoteExe;

import java.io.*;
import java.lang.reflect.Method;

/**
 * JavaClass执行工具
 *
 * @author zzm
 */
public class JavaClassExecuter {

    public static double i=0;

    /**
     * 执行外部传过来的代表一个Java类的byte数组＜br＞
     * 将输入类的byte数组中代表java.lang.System的CONSTANT_Utf8_info常量修改为劫持后的HackSystem类
     * 执行方法为该类的static main(String[]args)方法,输出结果为该类向System.out/err输出的信息
     *
     * @param classByte 代表一个Java类的byte数组
     * @return执行结果
     */
    public static String execute(byte[] classByte) {
        //HackSystem.clearBuffer();
        ClassModifier cm = new ClassModifier(classByte);
        byte[] modiBytes = cm.modifyUTF8Constant("java/lang/System", "com/wks/test/RemoteExe/HackSystem");
        HotSwapClassLoader loader = new HotSwapClassLoader();
        Class clazz = loader.loadByte(modiBytes);
        try {
            Method method = clazz.getMethod("main", new Class[]{String[].class});
            method.invoke(null, new String[]{null});
        } catch (Throwable e) {
            e.printStackTrace(HackSystem.out);
        }
        return "SUCCESS";//HackSystem.getBufferString();
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        File f = new File("E:\\code\\java_test\\target\\classes\\com\\wks\\test\\RemoteExe\\ss.class");
        System.out.println(f.exists()?"get file":"cannot get file ");
        Long lastModified = f.lastModified();

        while (true) {
            Thread.sleep(1000L);
            boolean changed = f.lastModified() > lastModified;
            if(changed){
                lastModified=f.lastModified();
                i=Math.random();
                System.out.println("now i :"+i);
                InputStream is=new FileInputStream(f);
                byte[]b=new byte[is.available()];
                is.read(b);
                is.close();
                execute(b);
            }
        }
    }
}
