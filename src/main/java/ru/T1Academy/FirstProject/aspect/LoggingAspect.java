package ru.T1Academy.FirstProject.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import ru.T1Academy.FirstProject.exception.LoggingAspectException;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect
{
    @Before(value = "@within(ru.T1Academy.FirstProject.aspect.annotation.LoggingBefore)")
    public void loggingBefore(JoinPoint joinPoint)
    {
        log.info("Метод: {}, запустился с аргументами: {}.",
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterThrowing(
            value = "@annotation(ru.T1Academy.FirstProject.aspect.annotation.LoggingAfterThrowing)"
            , throwing = "exception")
    public void loggingAfterThrowing(JoinPoint joinPoint, Exception exception)
    {
        log.error("Метод: {}, бросил ошибку: {}.",
                joinPoint.getSignature().getName(),
                exception.getMessage());
    }

    @AfterReturning(
            value = "@annotation(ru.T1Academy.FirstProject.aspect.annotation.LoggingAfterReturning)"
            , returning = "result")
    public void loggingAfterReturning(JoinPoint joinPoint, Object result)
    {
        log.info("Метод: {}, вернул: {}.",
                joinPoint.getSignature().getName(),
                result != null ? result.toString() : "null");
    }

    @Around(value = "@annotation(ru.T1Academy.FirstProject.aspect.annotation.LoggingAround)")
    public Object loggingAround(ProceedingJoinPoint joinPoint)
    {
        Object result;

        long startTime = System.currentTimeMillis();
        try
        {
            result = joinPoint.proceed();
        }
        catch (Throwable e)
        {
            long endTime = System.currentTimeMillis();
            log.info("Метод: {}, сломался за {} _ms.",
                    joinPoint.getSignature().getName(),
                    (endTime - startTime));

            throw new LoggingAspectException("LoggingAspect получил ошибку во время выполнения метода: " + joinPoint.getSignature().getName());
        }
        long endTime = System.currentTimeMillis();
        log.info("Метод: {}, выполнился за {} _ms.",
                joinPoint.getSignature().getName(),
                (endTime - startTime));

        return result;
    }
}














