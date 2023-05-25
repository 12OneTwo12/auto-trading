package com.jeongil.autotrading.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Position {

    private String symbol;
    private Integer leverage;
    private Double entryPrice;
    private Double markPrice;
    private Double unrealizedProfit;
    private String positionSide;
}
