package com.swiftpos.swiftposauth.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Pointcut("execution(* com.swiftpos.swiftposauth.service.*.*(..))")
    public void serviceMethods() {

    }

    @Pointcut("execution(* com.swiftpos.swiftposauth.controller.*.*(..))")
    public void controllerMethods() {

    }

    @Around("serviceMethods() || controllerMethods()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        log.info("Executing method: {}", methodName);
        log.info("Arguments: {}", Arrays.toString(joinPoint.getArgs()));

        long startTime = System.currentTimeMillis();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            log.error("Exception in executing method: {} - {}", methodName, e.getMessage());
            throw e;
        }

        long endTime = System.currentTimeMillis();

        log.info("Method executed: {}", methodName);
        log.info("Execution time: {} ms", endTime - startTime);
        log.info("Return value: {}", result);

        return result;
    }
}
