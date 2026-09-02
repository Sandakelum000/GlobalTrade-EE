package com.globaltrade.logistics.ejb.timer;

import com.globaltrade.logistics.core.dto.customs.CustomDocumentMonitorRecord;
import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.service.AuditLogService;
import com.globaltrade.logistics.core.service.CustomDocumentService;
import jakarta.ejb.*;
import jakarta.inject.Inject;

import java.util.List;

@Singleton
@Startup
public class CustomsDeadlineTimer {
    @Inject
    private CustomDocumentService customDocumentService;
    @EJB
    private AuditLogService  auditLogService;

    @Schedule(dayOfMonth = "2,4,6,8,10,12,14,16,18,20,22,24,26,28,30", hour = "23", minute = "58", second = "0", persistent = false)
    public void checkCustomDocuments(Timer timer) {
        List<CustomDocumentMonitorRecord> warnings = customDocumentService.findShipmentWithMissingDocuments();
        for (CustomDocumentMonitorRecord warning : warnings) {
            auditLogService.log(null, AuditAction.CUSTOM_DEADLINE,
                    "Custom","Custom","CUSTOMS WARNING: Shipment " + warning.shipNumber()
                            + " is missing documents: " + warning.missingDocuments());
        }
    }
}
