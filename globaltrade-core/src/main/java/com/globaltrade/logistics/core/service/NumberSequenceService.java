package com.globaltrade.logistics.core.service;

import jakarta.ejb.Local;

@Local
public interface NumberSequenceService {
    String next(String sequenceKey, String prefix, int width);
}
