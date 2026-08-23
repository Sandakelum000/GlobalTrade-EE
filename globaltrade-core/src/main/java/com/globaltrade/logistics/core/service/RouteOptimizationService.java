package com.globaltrade.logistics.core.service;

import jakarta.ejb.Local;

@Local
public interface RouteOptimizationService {
    void optimizeRoutes();
}
