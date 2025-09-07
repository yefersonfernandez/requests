package com.powerup.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("loan_state")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanStateEntity {
    @Id
    private Long id;
    private String name;
    private String description;
}
