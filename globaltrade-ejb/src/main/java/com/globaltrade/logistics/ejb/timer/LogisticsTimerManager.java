package com.globaltrade.logistics.ejb.timer;

import com.globaltrade.logistics.core.service.RouteOptimizationService;
import com.globaltrade.logistics.core.service.VendorPerformanceService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.inject.Inject;

import java.io.Serializable;

@Stateless
public class LogisticsTimerManager {
    private static final String TASK_VENDOR_PERFORMANCE = "VENDOR_PERFORMANCE";
    private static final String TASK_ROUTE_OPTIMIZATION = "ROUTE_OPTIMIZATION";

    @Resource
    private TimerService timerService;
    @Inject
    private VendorPerformanceService vendorPerformanceService;
    @Inject
    private RouteOptimizationService routeOptimizationService;

    @PostConstruct
    public void init() {
        if (!timerExists(TASK_ROUTE_OPTIMIZATION)) {
            createRouteOptimizationTimer();
        }
        if (!timerExists(TASK_VENDOR_PERFORMANCE)) {
            createVendorPerformanceTimer();
        }
    }

    @Timeout
    public void timeout(Timer timer) {
        Serializable info = timer.getInfo();
        if (!(info instanceof String task)) {
            return;
        }
        switch (task) {
            case TASK_VENDOR_PERFORMANCE -> vendorPerformanceService.evaluatePerformance();

            case TASK_ROUTE_OPTIMIZATION -> routeOptimizationService.optimizeRoutes();

            default -> System.out.println("Unknown task: " + task);
        }
    }

    public void createVendorPerformanceTimer() {
        ScheduleExpression schedule = new ScheduleExpression();

        schedule.hour("*");
        schedule.minute("*/10");
        schedule.second("0");

        createTimer(TASK_VENDOR_PERFORMANCE, schedule);
    }

    public void createRouteOptimizationTimer() {
        ScheduleExpression schedule = new ScheduleExpression();
        schedule.hour("*");
        schedule.minute("*/10");
        schedule.second("0");

        createTimer(TASK_ROUTE_OPTIMIZATION, schedule);
    }

    private boolean timerExists(String taskName) {
        for (Timer timer : timerService.getAllTimers()) {
            if (taskName.equals(timer.getInfo())) {
                return true;
            }
        }
        return false;
    }

    private void createTimer(String taskName, ScheduleExpression schedule) {
        TimerConfig config = new TimerConfig();
        config.setInfo(taskName);
        config.setPersistent(true);

        timerService.createCalendarTimer(schedule, config);
    }
}
