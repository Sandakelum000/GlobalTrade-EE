package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.entity.common.NumberSequence;
import com.globaltrade.logistics.core.service.NumberSequenceService;
import com.globaltrade.logistics.ejb.repository.NumberSequenceRepository;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

@Stateless
public class NumberSequenceBean implements NumberSequenceService {

    private static final Long DEFAULT_SEQUENCE_NUMBER = 1L;
    public static final int DEFAULT_INCREMENT = 1;

    @Inject
    private NumberSequenceRepository numberSequenceRepository;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String next(String sequenceKey, String prefix, int width) {
        if (sequenceKey == null || sequenceKey.isBlank()) {
            throw new IllegalArgumentException("Sequence key is required...");
        }

        if (prefix == null || prefix.isBlank()) {
            throw new IllegalArgumentException("Prefix is required...");
        }

        if (width <= 0) {
            throw new IllegalArgumentException("Width must be greater than zero");
        }

        NumberSequence sequence = numberSequenceRepository
                .findByKeyForUpdate(sequenceKey)
                .orElseGet(() -> createSequence(sequenceKey, prefix));

        if(!sequence.getPrefix().equals(prefix)){
            throw new IllegalArgumentException("The prefix is not match for this sequence: " + sequenceKey);
        }

        Long currentValue = sequence.getNextValue();
        sequence.setNextValue(currentValue + NumberSequenceBean.DEFAULT_INCREMENT);

        return formatNumber(prefix,currentValue,width);

    }

    private NumberSequence createSequence(String sequenceKey, String prefix) {
        NumberSequence sequence = NumberSequence.builder()
                .sequenceKey(sequenceKey)
                .prefix(prefix)
                .nextValue(NumberSequenceBean.DEFAULT_SEQUENCE_NUMBER)
                .build();

        numberSequenceRepository.save(sequence);
        return sequence;
    }

    private String formatNumber(String prefix, long value, int width) {
        return prefix + "-" + String.format("%0" + width + "d", value);
    }
}
