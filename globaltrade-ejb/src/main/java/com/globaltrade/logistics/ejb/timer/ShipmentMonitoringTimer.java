package com.globaltrade.logistics.ejb.timer;

import com.globaltrade.logistics.core.dto.shipment.ShipmentMonitorRecord;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;
import com.globaltrade.logistics.core.service.ShipmentService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class ShipmentMonitoringTimer {
    private static final Logger LOGGER = Logger.getLogger(ShipmentMonitoringTimer.class.getName());

    @Inject
    private ShipmentService shipmentService;

    @Resource
    private TimerService timerService;


    @PostConstruct
    public void init() {
        TimerConfig timerConfig = new TimerConfig("ShipmentMonitoringTimer", true);
        ScheduleExpression scheduleExpression = new ScheduleExpression();

        scheduleExpression.hour("*");
        scheduleExpression.minute("*/15");
        scheduleExpression.second("0");

        timerService.createCalendarTimer(scheduleExpression, timerConfig);

    }

    @Timeout
    public void monitorShipments() {
        LOGGER.info("Shipment monitoring timer started...");

        try {
            List<ShipmentMonitorRecord> activeShipments = shipmentService.findActiveShipments();
            LocalDateTime now = LocalDateTime.now();

            for (ShipmentMonitorRecord shipment : activeShipments) {
                if (shipment.estimatedDeliveryDate() != null &&
                        now.isAfter(shipment.estimatedDeliveryDate()) &&
                        shipment.status() != ShipmentStatus.DELIVERED) {

                    LOGGER.warning("OVERDUE SHIPMENT: " + shipment.shipmentNumber() +
                            ", Order=" + shipment.orderNumber() + ", Status=" + shipment.status());

                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Shipment monitoring timer failed.", e);
        }
    }
}
