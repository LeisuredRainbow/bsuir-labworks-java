package by.bsuir.labworks.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceExecutionTimeLoggingAspect {
  private static final Logger LOG
      = LoggerFactory.getLogger(ServiceExecutionTimeLoggingAspect.class);

  @Around("execution(* by.bsuir.labworks.service..*(..))")
  public Object logServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.nanoTime();
    try {
      return joinPoint.proceed();
    } finally {
      double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
      if (LOG.isInfoEnabled()) {
        LOG.info("{} executed in {} ms",
            joinPoint.getSignature().toShortString(), String.format("%.3f", elapsedMs));
      }
    }
  }
}