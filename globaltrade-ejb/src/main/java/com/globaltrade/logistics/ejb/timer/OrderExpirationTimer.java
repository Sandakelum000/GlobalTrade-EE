package com.globaltrade.logistics.ejb.timer;

import com.globaltrade.logistics.core.service.AdminOrderService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.*;

import java.util.logging.Logger;

@Singleton
@Startup
public class OrderExpirationTimer {
    private static final Logger LOGGER = Logger.getLogger(OrderExpirationTimer.class.getName());

    @EJB
    private AdminOrderService adminOrderService;
    @Resource
    private TimerService timerService;

    @PostConstruct
    public void init() {
        TimerConfig timerConfig = new TimerConfig("OrderExpirationTimer",true);
        ScheduleExpression schedule = new ScheduleExpression();

        schedule.hour("23");
        schedule.minute("50");
        schedule.second("0");

        timerService.createCalendarTimer(schedule,timerConfig);

        LOGGER.info("OrderExpirationTimer has scheduled...");
    }

    @Timeout
    public void expireUnpaidOrders() {
        LOGGER.info("OrderExpirationTimer has been started...");
        try{
            adminOrderService.cancelExpiredUnpaidOrders();
        } catch (Exception e) {
            LOGGER.warning("OrderExpirationTimer failed to start..."+e.getMessage());
        }
    }
}
