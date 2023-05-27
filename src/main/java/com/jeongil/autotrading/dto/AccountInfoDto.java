package com.jeongil.autotrading.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoDto {

    private boolean hasPosition;
    private BigDecimal rate;
    private BigDecimal availableBalance;
    private BigDecimal myPositionPrice;
    private BigDecimal myPositionQuantity;
    private BigDecimal unrealizedProfit;
    private Boolean isLong;
}
