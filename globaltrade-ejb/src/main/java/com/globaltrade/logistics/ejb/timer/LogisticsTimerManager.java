package com.globaltrade.logistics.ejb.timer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;

//@Singleton
//@Startup
public class LogisticsTimerManager {

    @Resource
    private TimerService timerService;

    @PostConstruct
    public void initializeTimers() {
        createInventoryTimer();
        createShipmentTimer();
        createVendorPerformanceTimer();
        createRouteOptimizationTimer();
    }

    private void createInventoryTimer() {
        TimerConfig config = new TimerConfig("Inventory Monitoring", false);

        timerService.createIntervalTimer(
                0,
                60 * 60 * 1000, // every 1 hour
                config
        );
    }

    private void createShipmentTimer() {
        TimerConfig config =
                new TimerConfig("Shipment Monitoring", false);

        timerService.createIntervalTimer(
                0,
                30 * 60 * 1000, // every 30 minutes
                config
        );
    }

    private void createVendorPerformanceTimer() {
        TimerConfig config =
                new TimerConfig("Vendor Performance", false);

        timerService.createIntervalTimer(
                0,
                24 * 60 * 60 * 1000, // every 24 hours
                config
        );
    }

    private void createRouteOptimizationTimer() {
        TimerConfig config =
                new TimerConfig("Route Optimization", false);

        timerService.createIntervalTimer(
                0,
                6 * 60 * 60 * 1000, // every 6 hours
                config
        );
    }
}
