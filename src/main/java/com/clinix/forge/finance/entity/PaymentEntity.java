package com.clinix.forge.finance.entity;

import com.clinix.forge.core.entity.BaseEntity;
import com.clinix.forge.treatment.entity.TreatmentEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity mapping representing payments associated with treatments.
 */
@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payment_treatment", columnList = "treatment_id")
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipt_id", nullable = false)
    private ReciptEntity recipt;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treatment_id", unique = true, nullable = false)
    private TreatmentEntity treatment;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PaymentMethod method;

    @Column(nullable = false, length = 255)
    @Builder.Default
    private String reference = "";
}
