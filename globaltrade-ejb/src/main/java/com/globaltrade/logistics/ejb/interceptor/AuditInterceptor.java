package com.globaltrade.logistics.ejb.interceptor;

import com.globaltrade.logistics.core.annotation.Audited;
import com.globaltrade.logistics.core.dto.grn.AuditableResponse;
import com.globaltrade.logistics.core.entity.security.User;
import com.globaltrade.logistics.core.service.AuditLogService;
import com.globaltrade.logistics.ejb.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.security.enterprise.SecurityContext;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundConstruct;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.springframework.lang.NonNull;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Interceptor
@Audited
@Priority(Interceptor.Priority.APPLICATION)
public class AuditInterceptor {

    private static final Logger LOGGER = Logger.getLogger(AuditInterceptor.class.getName());
    @Inject
    private AuditLogService auditLogService;
    @Inject
    private UserRepository userRepository;
    @Inject
    private SecurityContext securityContext;

    @PostConstruct
    public void init(InvocationContext context) throws Exception {
        Logger.getLogger(AuditInterceptor.class.getName()).log(Level.INFO, "AuditInterceptor init");
        context.proceed();
    }

    @AroundInvoke
    public Object audit(@NonNull InvocationContext context) throws Exception {
        Logger.getLogger(AuditInterceptor.class.getName()).log(Level.INFO,"Interceptor Called");

        Object result = null;
        try {
            result = context.proceed();
            return result;
        } finally {
            if (result instanceof AuditableResponse response) {
                UUID entityId = response.getEntityId();

                Audited audited = context.getMethod().getAnnotation(Audited.class);
                if (audited != null) {
                    String username =
                            securityContext.getCallerPrincipal() != null
                            ? securityContext.getCallerPrincipal().getName()
                            :null;
                    User user = userRepository.findByUsername(username).orElse(null);

                    auditLogService.log(
                            user,
                            audited.action(),
                            audited.entity(),
                            entityId.toString(),
                            "Executed " + context.getMethod().getName()
                    );
                }
            }else{
                LOGGER.log(Level.WARNING, "@Audited annotation missing on method or interface for: {0}",
                        context.getMethod().getName());
            }
        }
    }
}
