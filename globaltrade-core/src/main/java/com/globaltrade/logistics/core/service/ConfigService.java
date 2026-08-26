package com.globaltrade.logistics.core.service;

import jakarta.ejb.Remote;

@Remote
public interface ConfigService {
    String get(String key);
}
