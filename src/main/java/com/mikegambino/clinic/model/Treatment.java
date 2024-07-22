package com.mikegambino.clinic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;
    private String title;
    private String description;
    private BigDecimal price;
    @Column(name = "beneficiary_discount")
    private int beneficiaryDiscount;
    private int discount;

    public boolean hasDiscount(boolean beneficiary) {
        return discount > 0 || (beneficiary && beneficiaryDiscount > 0);
    }

    public int getMaxDiscount(boolean beneficiary) {
        if (beneficiary) return Math.max(beneficiaryDiscount, discount);
        else return discount;
    }

    public BigDecimal getPriceWithMaxDiscount(boolean beneficiary) {
        BigDecimal x = BigDecimal.valueOf((100 - getMaxDiscount(beneficiary)) / 100d);
        return price.multiply(x);
    }
}
