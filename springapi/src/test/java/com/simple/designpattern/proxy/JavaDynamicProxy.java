package com.simple.designpattern.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JavaDynamicProxy {

    static class TargetImp implements Target{

        @Override
        public void printName(){
            System.out.println("gzy");
        }
    }

     interface Target{
         void printName();
    }


    static class TargetProxy implements InvocationHandler{
        private final Target target;
        TargetProxy(Target target){
            this.target = target;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("那个我准备打印你的名字了");
            Object re = method.invoke(target, args);
            System.out.println("ok，我打印好了");
            return re;
        }

        Target getProxyInstance(){
            return (Target)(Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(),this));
        }
    }

    public static void main(String[] args) {
        Target target = new TargetImp();
        Target proxyTarget = new TargetProxy(target).getProxyInstance();
        proxyTarget.printName();
    }

}
