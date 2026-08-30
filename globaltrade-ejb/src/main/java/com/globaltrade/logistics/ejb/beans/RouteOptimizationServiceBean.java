package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.entity.common.Country;
import com.globaltrade.logistics.core.entity.order.shipment.Shipment;
import com.globaltrade.logistics.core.service.RouteOptimizationService;
import com.globaltrade.logistics.ejb.repository.ShipmentRepository;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class RouteOptimizationServiceBean implements RouteOptimizationService {

    @Inject
    private ShipmentRepository shipmentRepository;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void optimizeRoutes() {
        List<Shipment> activeShipments = shipmentRepository.findActiveShipments();

        for (Shipment shipment : activeShipments) {
            if (shipment.getOriginAddress() == null || shipment.getDestinationAddress() == null) {
                continue;
            }
            BigDecimal distance = calculateDistance(shipment);
            BigDecimal estimatedHours = calculateEstimatedHours(shipment, distance);
            Integer riskScore = calculateRiskScore(shipment);
            String routeName = generateRouteNames(shipment);

            shipment.setRouteEstimatedHours(estimatedHours);
            shipment.setRouteDistanceKm(distance);
            shipment.setRouteRiskScore(riskScore);
            shipment.setRouteName(routeName);

            shipment.setEstimatedDeliveryDate(LocalDateTime.now().plusMinutes(
                    estimatedHours.multiply(BigDecimal.valueOf(60)).longValue()
            ));
        }
    }

    private BigDecimal calculateDistance(Shipment shipment) {
        if (isOriginAndDestinationSameCountry(shipment)) {
            return BigDecimal.valueOf(250.0);
        }
        return BigDecimal.valueOf(3500.0);
    }

    private Integer calculateRiskScore(Shipment shipment) {
        int risk = 10;

        if (isOriginAndDestinationSameCountry(shipment)) {
            risk += 25;
        }
        if (shipment.getPriority() >= 8) risk += 10;

        return Math.min(risk, 100);
    }

    public boolean isOriginAndDestinationSameCountry(Shipment shipment) {
        Country originCountry = shipment.getOriginAddress().getCountry();
        Country destinationCountry = shipment.getDestinationAddress().getCountry();

        return originCountry.getId().equals(destinationCountry.getId());
    }

    private BigDecimal calculateEstimatedHours(Shipment shipment, BigDecimal distance) {
        BigDecimal averageSpeed;
        if (shipment.getPriority() >= 8) {
            averageSpeed = BigDecimal.valueOf(70);
        } else if (shipment.getPriority() >= 5) {
            averageSpeed = BigDecimal.valueOf(80);
        } else {
            averageSpeed = BigDecimal.valueOf(50);
        }
        return distance.divide(averageSpeed, 2, RoundingMode.HALF_UP); // kalaya = dura / vegaya

    }



    private String generateRouteNames(Shipment shipment) {
        return shipment.getOriginAddress().getCountry().getName() + " -> " + shipment.getDestinationAddress().getCountry().getName();
    }
}
