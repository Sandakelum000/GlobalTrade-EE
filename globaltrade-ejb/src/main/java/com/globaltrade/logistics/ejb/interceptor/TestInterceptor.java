package com.globaltrade.logistics.ejb.interceptor;

import com.globaltrade.logistics.core.annotation.Test;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.springframework.lang.NonNull;


@Interceptor
@Test
@Priority(Interceptor.Priority.APPLICATION)
public class TestInterceptor {

    @PostConstruct
    public void postConstruct(InvocationContext context) throws Exception {
        System.out.println("TestInterceptor postConstruct");
        context.proceed();
    }

    @AroundInvoke
    public Object m(@NonNull InvocationContext context) throws Exception {
        System.out.println("Before executing EJB method: " + context.getMethod().getName());
        try {
            return context.proceed();
        } finally {
            System.out.println("After executing EJB method.");
        }
    }
}
