package com.simple.api.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


/**
 * aop
 *
 * @author gzy
 */
@Slf4j
@Component
@Aspect
public class RedisAspect {

    @Around("execution(* com.simple.api.controller.RedisLockController.*(..))")
    public Object interceptMyMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("around 前");
        Object r = joinPoint.proceed();
        log.info("around 后");
        return r;
    }


}
