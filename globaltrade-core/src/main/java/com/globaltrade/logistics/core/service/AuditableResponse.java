package com.globaltrade.logistics.core.service;

import jakarta.ejb.Local;

import java.util.UUID;

@Local
public interface AuditableResponse {
    UUID getEntityId();
}
