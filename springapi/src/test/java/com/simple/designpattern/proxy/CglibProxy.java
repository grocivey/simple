package com.simple.designpattern.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * cglib动态代理测试
 */
public class CglibProxy {
    static class Game{
        public void playLOL(String user){
            System.out.println(user + ":打LOL");
        }
        public void playCF(String user){
            System.out.println(user + ":打CF");
        }
    }

    static class GameProxy implements MethodInterceptor{
        Game game;
        public GameProxy(Game game){
            this.game = game;
        }

        @Override
        public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            System.out.println("before : " + method.getName() + " : " + methodProxy.getSuperName());
//            Object re = method.invoke(game, args);
            Object re = methodProxy.invoke(game, args);
            System.out.println("after : " + method.getName() + " : " + methodProxy.getSuperName());
            return re;
        }
        public Game getProxyInstance(){
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(Game.class);
            enhancer.setCallback(this);
            return (Game) enhancer.create();
        }


    }

    public static void main(String[] args) {
        Game game = new Game();
        Game proxyGame = new GameProxy(game).getProxyInstance();
        proxyGame.playCF("gzy");
        proxyGame.playLOL("张雨");
    }





}
