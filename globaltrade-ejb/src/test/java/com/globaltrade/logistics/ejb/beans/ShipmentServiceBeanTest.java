package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.shipment.ShipmentTrackingRequest;
import com.globaltrade.logistics.core.entity.order.shipment.Shipment;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;
import com.globaltrade.logistics.core.entity.order.shipment.tracking.TrackingStatus;
import com.globaltrade.logistics.core.exception.ShipmentTrackingException;
import com.globaltrade.logistics.ejb.repository.ShipmentRepository;
import com.globaltrade.logistics.ejb.repository.ShipmentTrackingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShipmentServiceBeanTest {
    @InjectMocks
    private ShipmentServiceBean shipmentService;
    @Mock
    private ShipmentRepository shipmentRepository;
    @Mock
    private ShipmentTrackingRepository shipmentTrackingRepository;

    private UUID shipmentId;
    private Shipment shipment;

    @BeforeEach
    void setup(){
        shipmentId = UUID.randomUUID();
        shipment = mock(Shipment.class);
    }

    @Test
    void UpdateShipmentTracking_shouldUpdateToProcessing_whenLabelCreated(){
        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));

        when(shipment.getStatus()).thenReturn(ShipmentStatus.PENDING);

        ShipmentTrackingRequest request = new ShipmentTrackingRequest(
                TrackingStatus.LABEL_CREATED,
                "Label created",
                "warehouse"
        );

        shipmentService.updateShipmentTracking(shipmentId, request);

        verify(shipmentRepository).findById(shipmentId);
        verify(shipmentTrackingRepository).save(any());
        verify(shipment).setStatus(ShipmentStatus.PROCESSING);
        verify(shipment,never()).setDeliveredAt(any());
    }

    @Test
    void updateShipmentTracking_shouldThrowException_whenRequestIsNull() {

        ShipmentTrackingException exception =
                assertThrows(
                        ShipmentTrackingException.class,
                        () -> shipmentService.updateShipmentTracking(shipmentId, null)
                );
        assertEquals(
                "Tracking request cannot be null",
                exception.getMessage());
        verifyNoInteractions(shipmentRepository);
    }


    @Test
    void updateShipmentTracking_shouldThrowException_whenLocationMissing() {
        ShipmentTrackingRequest request =
                new ShipmentTrackingRequest(TrackingStatus.LABEL_CREATED, "Label created", "");

        ShipmentTrackingException exception =
                assertThrows(
                        ShipmentTrackingException.class,
                        () -> shipmentService.updateShipmentTracking(
                                shipmentId,
                                request
                        )
                );

        assertEquals(
                "Tracking location is required",
                exception.getMessage()
        );
        verifyNoInteractions(shipmentRepository);
    }


    @Test
    void updateShipmentTracking_shouldThrowException_whenShipmentNotFound() {

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.empty());

        ShipmentTrackingRequest request =
                new ShipmentTrackingRequest(
                        TrackingStatus.LABEL_CREATED,
                        "Label created",
                        "Warehouse"
                );

        ShipmentTrackingException exception =
                assertThrows(
                        ShipmentTrackingException.class,
                        () -> shipmentService.updateShipmentTracking(
                                shipmentId,
                                request
                        )
                );

        assertTrue(exception.getMessage().contains("Shipment not found"));
    }


    @Test
    void updateShipmentTracking_shouldRejectCancelledShipment() {

        when(shipmentRepository.findById(shipmentId))
                .thenReturn(Optional.of(shipment));

        when(shipment.getStatus())
                .thenReturn(ShipmentStatus.CANCELLED);

        ShipmentTrackingRequest request =
                new ShipmentTrackingRequest(
                        TrackingStatus.LABEL_CREATED,
                        "Label created",
                        "Warehouse"
                );

        ShipmentTrackingException exception =
                assertThrows(
                        ShipmentTrackingException.class,
                        () -> shipmentService.updateShipmentTracking(
                                shipmentId,
                                request
                        )
                );

        assertEquals("Cannot update tracking for a cancelled shipment", exception.getMessage());
        verifyNoInteractions(shipmentTrackingRepository);
    }


    @Test
    void updateShipmentTracking_shouldRejectDeliveredShipment() {

        when(shipmentRepository.findById(shipmentId))
                .thenReturn(Optional.of(shipment));

        when(shipment.getStatus())
                .thenReturn(ShipmentStatus.DELIVERED);

        ShipmentTrackingRequest request =
                new ShipmentTrackingRequest(
                        TrackingStatus.LABEL_CREATED,
                        "Label created",
                        "Warehouse"
                );

        ShipmentTrackingException exception =
                assertThrows(
                        ShipmentTrackingException.class,
                        () -> shipmentService.updateShipmentTracking(
                                shipmentId,
                                request
                        )
                );

        assertEquals(
                "Cannot update tracking after delivery",
                exception.getMessage()
        );
    }
}
