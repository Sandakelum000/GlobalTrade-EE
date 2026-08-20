package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.annotation.Audited;
import com.globaltrade.logistics.core.dto.grn.GRNRegistrationRequest;
import com.globaltrade.logistics.core.dto.grn.GRNRegistrationResponse;
import com.globaltrade.logistics.core.entity.audit.AuditAction;
import jakarta.ejb.Local;

import java.util.UUID;

@Local
public interface GRNService {
    GRNRegistrationResponse createGRN(GRNRegistrationRequest request);
    GRNRegistrationResponse cancelGRN(UUID grnId);
}
