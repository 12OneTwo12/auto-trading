package com.jeongil.autotrading.service.binance;

import com.jeongil.autotrading.dto.AccountInfoDto;
import com.jeongil.autotrading.dto.BuySellVolume;
import com.jeongil.autotrading.dto.LongOrShot;

import java.util.List;

public interface BinanceService {
    void sellIt();

    void buyIt(LongOrShot longOrShot);

    List<BuySellVolume> getBuySellVolume();

    AccountInfoDto getMyAccountPosition();

    String getSignature(String data);
}
