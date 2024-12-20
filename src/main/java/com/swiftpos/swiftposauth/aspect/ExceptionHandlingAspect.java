package com.swiftpos.swiftposauth.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExceptionHandlingAspect {
    @Pointcut("execution(* com.swiftpos.swiftposauth.service.*.*(..))")
    public void serviceLayer() {}

    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void handleException(Exception ex){
        log.error("EXCEPTION: {}", ex.getMessage());
    }
}
