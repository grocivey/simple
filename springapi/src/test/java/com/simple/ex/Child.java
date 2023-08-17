package com.simple.ex;

public class Child extends Parent{

    public Child() {
        super();
        i = 1;

        System.out.println("parent" + i);

    }

    public Child(int ii) {
        i = 9;
        System.out.println("parent" + i);

    }



    public void p(){
        System.out.println(i);
    }

    public static void main(String[] args) {
        new Child(8).p();
    }
}
