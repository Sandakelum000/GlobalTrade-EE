package com.globaltrade.logistics.core.dto.customs;

import com.globaltrade.logistics.core.entity.order.shipment.CustomsDocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CustomsDocumentRegistrationRequest(
        @NotNull
        UUID shipmentId,

        @NotNull
        CustomsDocumentType documentType,

        @Size(max = 40)
        String documentNumber,

        @NotBlank
        @Size(max = 255)
        String fileName,

        @NotBlank
        @Size(max = 500)
        String filePath
) {
}
