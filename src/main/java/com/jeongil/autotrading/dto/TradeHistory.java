package com.jeongil.autotrading.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeHistory {

    private Boolean maker;
    private String realizedPnl;
    private String side;
    private String positionSide;
    private String symbol;
    private String price;
}
