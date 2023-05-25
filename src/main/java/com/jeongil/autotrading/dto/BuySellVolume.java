package com.jeongil.autotrading.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuySellVolume {

    private String buySellRatio;
    private String buyVol;
    private String sellVol;
    private Long timestamp;
}
