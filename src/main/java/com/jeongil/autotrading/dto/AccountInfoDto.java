package com.jeongil.autotrading.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoDto {

    private boolean hasPosition;
    private Double rate;
    private Double totalWalletBalance;
}
