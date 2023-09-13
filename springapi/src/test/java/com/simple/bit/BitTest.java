package com.simple.bit;

import org.junit.Test;

/**
 * 测试位运算
 */
public class BitTest {

    @Test
    public void test1(){
        //0101
        //0011
        //----
        //0001  ==> 1
        System.out.println(Integer.toBinaryString(5 & 3) +"==>"+(5 & 3));

        //0101
        //0011
        //----
        //0111  ==> 7
        System.out.println(Integer.toBinaryString(5 | 3) +"==>"+(5 | 3));

        //0101
        //0011
        //----
        //0110  ==>
        System.out.println(Integer.toBinaryString(5 ^ 3) +"==>"+(5 ^ 3));

        //0101
        //----
        //1111111111111010  ==>
        //1111111111111001  ==>
        //10110  ==>
        System.out.println(Integer.toBinaryString(~5) +"==>"+(~5));

        //0001000
        //0000111

        // 001100
        // 001011

        System.out.println(0xff & 0);


        int flags = 5;



        flags &= ~(1 << 2);

        // 0101
        System.out.println(Integer.toBinaryString(flags));
    }
}
