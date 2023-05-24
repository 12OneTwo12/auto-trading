package com.jeongil.autotrading.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuySellVolume {

    private Double buySellRatio;
    private Double buyVol;
    private Double sellVol;
    private String timestamp;
}
