package com.simple.clazz;

//public class App extends AbstractMy{
public class App extends AbstractMy{

    public static void main(String[] args) {
        System.out.println(new App().my());
    }


    @Override
    public String my() {
        return "acc";
    }

    @Override
    public String my1() {
        return "abb";
    }

    @Override
    public String my2() {
        return "dd";
    }
}
