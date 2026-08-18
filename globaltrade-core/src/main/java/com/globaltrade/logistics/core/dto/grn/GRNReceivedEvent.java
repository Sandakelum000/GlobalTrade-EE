package com.globaltrade.logistics.core.dto.grn;

import java.io.Serializable;
import java.util.UUID;

public record GRNReceivedEvent(
        UUID grnId,
        String grnNumber,
        UUID vendorId,
        UUID warehouseId
) implements Serializable {
}
