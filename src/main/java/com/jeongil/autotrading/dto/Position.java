package com.jeongil.autotrading.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Position {

    private String symbol;
    private Integer leverage;
    private BigDecimal entryPrice;
    private BigDecimal unrealizedProfit;
    private String positionSide;
    private BigDecimal positionAmt;
}
