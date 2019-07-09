package com.wks.test.JVMtest;


import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodHandles.publicLookup;
import static java.lang.invoke.MethodType.methodType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.invoke.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * JSR-292 Method Handle基础用法演示
 *
 * @author zzm
 */
public class MethodHandleTest {
    static class ClassA {
        public void println(String s) {
            System.out.println(s);
        }
    }

/*    public static void main(String[] args) throws Throwable {
        Object obj = System.currentTimeMillis() % 2 == 0 ? System.out : new ClassA();
        *//*无论obj最终是哪个实现类,下面这句都能正确调用到println方法*//*
        Object o=getPrintlnMH(obj).invokeWithArguments("icyfenix");
        System.out.println(o instanceof CallSite);
        System.out.println(getPrintlnMH(obj).invokeExact("icyfenix"));
    }*/

    private static MethodHandle getPrintlnMH(Object reveiver) throws Throwable {
        /*MethodType：代表“方法类型”,包含了方法的返回值(methodType()的第一个参数)和具体参数(methodType()第二个及以后的参数)*/
        MethodType mt = methodType(void.class, String.class);
        /*lookup()方法来自于MethodHandles.lookup,这句的作用是在指定类中查找符合给定的方法名称、方法类型,并且符合调用权限的方法句柄*/
        /*因为这里调用的是一个虚方法,按照Java语言的规则,方法第一个参数是隐式的,代表该方法的接收者,也即是this指向的对象,这个参数以前是放在参数列表中进行传递的,而现在提供了bindTo()方法来完成这件事情*/
        return lookup().findVirtual(reveiver.getClass(), "println", mt).bindTo(reveiver);


        /**
         * getPrintlnMH（）中模拟了invokevirtual指令的执行过程，只不过它的分派逻辑并非固化在Class文件的字节码上，而是通过一个具体方法来实现。而这个方法本身的返回值（MethodHandle对象），可以视为对最终调用方法的一个“引用”
         */

        /**
         * 在某种程度上，invokedynamic指令与MethodHandle机制的作用是一样的，都是为了解决原有4条“invoke*”指令方法分派规则固化在虚拟机之中的问题，
         * 把如何查找目标方法的决定权从虚拟机转嫁到具体用户代码之中，让用户（包含其他语言的设计者）有更高的自由度。而且，它们两者的思路也是可类比的，
         * 可以把它们想象成为了达成同一个目的，一个采用上层Java代码和API来实现，另一个用字节码和Class中其他属性、常量来完成。因此，如果理解了前面的MethodHandle例子，
         * 那么理解invokedynamic指令也并不困难
         */
    }


    public static void main(String[] args) throws Throwable {
/*        MutableCallSite name = new MutableCallSite(methodType(String.class));
        MethodHandle MH_name = name.dynamicInvoker();
        MethodType MT_str1 =    methodType(String.class);
        MethodHandle MH_upcase = MethodHandles.lookup().findVirtual(String.class, "toUpperCase", MT_str1);
        MethodHandle worker1 = MethodHandles.filterReturnValue(MH_name, MH_upcase);
        name.setTarget(MethodHandles.constant(String.class, "Rocky"));
        assertEquals("ROCKY", (String) worker1.invokeExact());
        name.setTarget(MethodHandles.constant(String.class, "Fred"));
        assertEquals("FRED", (String) worker1.invokeExact());
// (mutation can be continued indefinitely)

//        The same call site may be used in several places at once.
        MethodType MT_str2 = methodType(String.class, String.class);
        MethodHandle MH_cat = lookup().findVirtual(String.class,
                "concat", methodType(String.class, String.class));
        MethodHandle MH_dear = MethodHandles.insertArguments(MH_cat, 1, ", dear?");
        MethodHandle worker2 = MethodHandles.filterReturnValue(MH_name, MH_dear);
        assertEquals("Fred, dear?", (String) worker2.invokeExact());
        name.setTarget(MethodHandles.constant(String.class, "Wilma"));
        assertEquals("WILMA", (String) worker1.invokeExact());
        assertEquals("Wilma, dear?", (String) worker2.invokeExact());

        MethodHandle trace = publicLookup().findVirtual(java.io.PrintStream.class,
                "println", methodType(void.class, String.class))
                .bindTo(System.out);
        MethodHandle cat = lookup().findVirtual(String.class,
                "concat", methodType(String.class, String.class));
        //assertEquals("boojum", (String) cat.invokeExact("boo", "jum"));
        MethodHandle catTrace = MethodHandles.foldArguments(cat, trace);*/
// also prints "boo":
        //assertEquals("boojum", (String) catTrace.invokeExact("boo", "jum"));



/*        MethodHandle trace = publicLookup().findVirtual(java.io.PrintStream.class,
                "println", methodType(void.class, String.class))
                .bindTo(System.out);
        MethodHandle cat = lookup().findVirtual(String.class,
                "concat", methodType(String.class, String.class)).bindTo("ss");

        MethodHandle newStr = lookup().findStatic(String.class,
                "valueOf", methodType(String.class, Object.class));
        //assertEquals("boojum", (String) cat.invokeExact("boo", "jum"));

        MethodHandle catTrace = MethodHandles.foldArguments(trace ,cat );

         catTrace.invokeExact((Object) "Str");
        // also prints "boo":
        //assertEquals("boojum", (String) catTrace.invokeExact("boo", "jum"));




        MethodHandle trace1 = publicLookup().findVirtual(java.io.PrintStream.class,
                "println", methodType(void.class, String.class))
                .bindTo(System.out);

        MethodHandle cat1 = lookup().findVirtual(String.class, "concat", methodType(String.class, String.class));

        assertEquals("boojum", (String) cat.invokeExact("boo", "jum"));

        MethodHandle catTrace1 = MethodHandles.foldArguments(cat1, trace1);
        // also prints "boo":
        assertEquals("boojum", (String) catTrace1.invokeExact("boo", "jum"));*/


        MethodHandle combine = publicLookup().findVirtual(MethodHandleTest.class,
                "combine", methodType(String.class, String.class))
                .bindTo(new MethodHandleTest());
        MethodHandle target = lookup().findVirtual(MethodHandleTest.class,
                "target", methodType(void.class, Arrays.asList(String.class,String.class))).bindTo(new MethodHandleTest());

        MethodHandle newStr = lookup().findStatic(String.class,
                "valueOf", methodType(String.class, Object.class));
        //assertEquals("boojum", (String) cat.invokeExact("boo", "jum"));

        MethodHandle catTrace = MethodHandles.foldArguments(target ,combine );

        catTrace.invokeExact( "Str");
        // also prints "boo":
        //assertEquals("boojum", (String) catTrace.invokeExact("boo", "jum"));
    }

    public String combine(String s){
        System.out.println("combine=="+s);
        return s;
    }
    public void target(String s,String b){
        System.out.println(s+b);
    }

}