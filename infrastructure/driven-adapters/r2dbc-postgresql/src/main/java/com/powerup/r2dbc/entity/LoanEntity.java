package com.powerup.r2dbc.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("loans")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanEntity {
    @Id
    @Column("loan_id")
    private Long id;
    private BigDecimal amount;
    private Integer term;
    private String email;
    private Long idLoanState;
    private Long idLoanType;
}
