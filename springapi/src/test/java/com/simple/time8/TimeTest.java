package com.simple.time8;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * java8 time 测试
 */
public class TimeTest {
    @Test
    public void t1(){
        String string = "2023-09-09 11:11:11";
        //字符串转时间
        LocalDateTime auditTime1 = LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(string));
        LocalDateTime auditTime2 = LocalDateTime.parse(string,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(auditTime1);
        System.out.println(auditTime2);
        //时间转字符串
        String format = LocalDateTime.now().minusSeconds(3028664).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(format);

    }
}
