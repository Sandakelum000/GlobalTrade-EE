package com.globaltrade.logistics.ejb.timer;

import com.globaltrade.logistics.core.dto.customs.CustomDocumentMonitorRecord;
import com.globaltrade.logistics.core.service.CustomDocumentService;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.Timer;
import jakarta.inject.Inject;

import java.util.List;

@Singleton
@Startup
public class CustomsDeadlineTimer {
    @Inject
    private CustomDocumentService customDocumentService;

    @Schedule(hour = "*",minute = "0",second = "0",persistent = false)
    public void checkCustomDocuments(Timer timer) {
        List<CustomDocumentMonitorRecord> warnings = customDocumentService.findShipmentWithMissingDocuments();
        for (CustomDocumentMonitorRecord warning : warnings) {
            System.out.println("CUSTOMS WARNING: Shipment " + warning.shipNumber()
                    + " is missing documents: " + warning.missingDocuments());
        }
    }
}
