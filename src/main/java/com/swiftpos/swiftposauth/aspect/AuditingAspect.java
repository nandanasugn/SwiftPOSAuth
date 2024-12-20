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
public class AuditingAspect {

    @Pointcut("execution(* com.swiftpos.swiftposauth.service.IAuthService.register(..))")
    public void userRegistration() {}

    @Pointcut("execution(* com.swiftpos.swiftposauth.service.IAuthService.login(..))")
    public void userLogin() {}

    @Pointcut("execution(* com.swiftpos.swiftposauth.service.IAuthService.logout(..))")
    public void userLogout() {}

    @Pointcut("execution(* com.swiftpos.swiftposauth.service.IAuthService.refreshToken(..))")
    public void userRefreshToken() {}

    @Around("userRegistration() || userLogin() || userLogout() || userRefreshToken()")
    public Object auditUserAction(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodName = proceedingJoinPoint.getSignature().getName();
        String className = proceedingJoinPoint.getTarget().getClass().getSimpleName();

        log.info("AUDIT: Starting {} action in class {}.", methodName, className);

        try {
            Object result = proceedingJoinPoint.proceed();
            log.info("AUDIT: Successfully completed {} action in class {}.", methodName, className);
            return result;
        } catch (Throwable throwable) {
            log.error("AUDIT: Failed to complete {} action in class {}.", methodName, className, throwable);
            throw throwable;
        }
    }
}
