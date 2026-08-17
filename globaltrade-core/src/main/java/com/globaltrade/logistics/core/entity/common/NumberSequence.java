package com.globaltrade.logistics.core.entity.common;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Table(
        name = "number_sequences",
        indexes = {
                @Index(name = "idx_number_sequence_key", columnList = "sequence_key", unique = true)
        }
)
@NamedQuery(name = "NumberSequence.findByKey", query = "SELECT n FROM NumberSequence n WHERE n.sequenceKey = :sequenceKey" )
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NumberSequence extends BaseEntity {
    @NotBlank
    @Column(name = "sequence_key", nullable = false, unique = true, length = 50)
    private String sequenceKey;

    @NotBlank
    @Column(name = "prefix", nullable = false, length = 20)
    private String prefix;

    @PositiveOrZero
    @Column(name = "next_value", nullable = false)
    @Builder.Default
    private Long nextValue = 1L;
}
