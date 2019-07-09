package com.wks.test.RemoteExe;

/**
 *为了多次载入执行类而加入的加载器＜br＞
 *把defineClass方法开放出来,只有外部显式调用的时候才会使用到loadByte方法
 *由虚拟机调用时,仍然按照原有的双亲委派规则使用loadClass方法进行类加载
 *
 *@author zzm
 */
public class HotSwapClassLoader extends ClassLoader{
    public HotSwapClassLoader(){
        super(HotSwapClassLoader.class.getClassLoader());
    }
    public Class loadByte(byte[]classByte){
        return defineClass(null,classByte,0,classByte.length);
    }
}



/*

HotSwapClassLoader所做的事情仅仅是公开父类（即java.lang.ClassLoader）中的protected方法defineClass（），
我们将会使用这个方法把提交执行的Java类的byte[]数组转变为Class对象。HotSwapClassLoader中并没有重写loadClass（）或findClass（）方法，
因此如果不算外部手工调用loadByte（）方法的话，这个类加载器的类查找范围与它的父类加载器是完全一致的，在被虚拟机调用时，
它会按照双亲委派模型交给父类加载。构造函数中指定为加载HotSwapClassLoader类的类加载器作为父类加载器，这一步是实现提交的执行代码可以访问服务端引用类库的关键、


真正完成类的加载工作是通过调用 defineClass来实现的；
而启动类的加载过程是通过调用 loadClass来实现的。前者称为一个类的定义加载器（defining loader），后者称为初始加载器（initiating loader）
*/