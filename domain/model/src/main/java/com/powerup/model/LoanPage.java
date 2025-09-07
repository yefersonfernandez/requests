package com.powerup.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class LoanPage<T> {
    private List<T> content;
    private long totalElements;
    private int page;
    private int size;
}