package com.example.checkoutCase.aspects;

import com.example.checkoutCase.entity.ItemRequest.ItemRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component

public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @AfterReturning("execution(* com.example.checkoutCase.controller.ItemController.*(com.example.checkoutCase.entity.ItemRequest.ItemRequest))")
    public void log_request(JoinPoint joinPoint) {
        ItemRequest request = (ItemRequest) joinPoint.getArgs()[0];

        logger.info("Request received:" + request);
    }

    @Before("execution(* com.example.checkoutCase.service.ItemService.*(com.example.checkoutCase.entity.ItemRequest.ItemRequest))")
    public void log_method(JoinPoint joinPoint) {
        ItemRequest request = (ItemRequest) joinPoint.getArgs()[0];
        String methodName = joinPoint.getSignature().getName();

        logger.info("Method '" + methodName + "' is called for item ID: " + request.getItemId());
    }

    @Before("execution(* com.example.checkoutCase.service.ItemService.*Cart(..))")
    public void log_methods_with_no_args(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();

        logger.info("Method '" + methodName + "' is called");
    }

    @AfterThrowing(value = "execution(* com.example.checkoutCase.service.ItemService.*(com.example.checkoutCase.entity.ItemRequest.ItemRequest))", throwing = "ex")
    public void log_errors(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().getName();
        ItemRequest request = (ItemRequest) joinPoint.getArgs()[0];

        logger.error("Error when executing " + methodName + " method for object with id " + request.getItemId() + ". Error message: " + ex.getMessage());
    }

    @AfterReturning(value = "execution(* com.example.checkoutCase.service.ItemService.*(com.example.checkoutCase.entity.ItemRequest.ItemRequest))")
    public void log_success(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        ItemRequest request = (ItemRequest) joinPoint.getArgs()[0];

        logger.error(methodName + " is successfully executed for object with id " + request.getItemId());
    }
}