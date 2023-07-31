package com.simple.test;

import com.simple.api.service.BaseStatService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringTest {
    @Autowired
    BaseStatService service;
    @Test
    public void test1(){
        System.out.println(service.list());
    }
}
