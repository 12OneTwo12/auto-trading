package com.jeongil.autotrading.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetailInfoDto {

    private Boolean canTrade;
    private List<Asset> assets;
    private List<Position> positions;
}
