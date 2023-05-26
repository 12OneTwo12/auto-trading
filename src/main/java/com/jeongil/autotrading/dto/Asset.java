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
public class Asset {

    private String asset;
    private BigDecimal walletBalance;
    private BigDecimal availableBalance;
}
