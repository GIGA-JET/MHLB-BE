package com.gigajet.mhlb.global.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
//@Aspect
//@Component
public class TimeCheckAop {

//    @Around("execution(public * com.gigajet.mhlb.domain.*.controller.*.*(..))")
    public synchronized Object execute(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch(joinPoint.getTarget().getClass().getName());
        stopWatch.start();

        try {
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            System.out.println(stopWatch.getId());
            System.out.println(stopWatch.getTotalTimeMillis());
        }

    }

}
