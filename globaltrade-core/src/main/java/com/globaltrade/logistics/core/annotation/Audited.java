package com.globaltrade.logistics.core.annotation;

import com.globaltrade.logistics.core.entity.audit.AuditAction;
import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Audited {
    AuditAction action() default AuditAction.VIEW;
    String entity() default "";
}
