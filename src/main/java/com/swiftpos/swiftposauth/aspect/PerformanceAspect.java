package com.swiftpos.swiftposauth.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceAspect {

    @Pointcut("execution(* com.swiftpos.swiftposauth.service.*.*(..))")
    public void serviceLayer() {}

    @Around("serviceLayer()")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable{
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        log.info("PERFORMANCE: {} executed in {} ms", joinPoint.getSignature().toShortString(), endTime - startTime);

        return result;
    }
}
