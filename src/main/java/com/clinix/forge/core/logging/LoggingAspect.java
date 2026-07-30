package com.clinix.forge.core.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * The omnipresent scribe.
 * <p>
 * This aspect silently observes the execution of controller and service logic,
 * recording the duration, input parameters, and outcome of every major invocation.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // Pointcut targeting classes annotated with @RestController or @Service
    @Around("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Service *)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();

        long start = System.currentTimeMillis();

        if (log.isDebugEnabled()) {
            log.debug("Entering method [{}.{}] with arguments: {}",
                    className, methodName, serializeAndMask(joinPoint.getArgs()));
        } else {
            log.trace("Entering method [{}.{}]", className, methodName);
        }

        try {
            // Allow the actual business logic to execute
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - start;

            if (log.isDebugEnabled()) {
                log.debug("Exiting method [{}.{}] in {}ms returning: {}",
                        className, methodName, duration, maskSensitive(result));
            } else {
                log.trace("Exiting method [{}.{}] in {}ms", className, methodName, duration);
            }

            return result;

        } catch (Exception e) {
            // Record the failure before passing the burden back to the caller
            log.error("Exception in method [{}.{}]: {}",
                    className, methodName, e.getMessage());
            throw e;
        }
    }

    private String serializeAndMask(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            sb.append(maskSensitive(args[i]));
            if (i < args.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String maskSensitive(Object obj) {
        if (obj == null) {
            return "null";
        }
        String str = obj.toString();
        // Regex to identify and mask passwords, secrets, tokens, credentials, and jwts
        return str.replaceAll("(?i)(password|secret|token|credentials|jwt)\\s*[:=]\\s*['\"]?[^,'\"\\s}]+['\"]?", "$1=*****");
    }
}