package com.jeongil.autotrading.service.binance;

import com.jeongil.autotrading.dto.AccountInfoDto;
import com.jeongil.autotrading.dto.BuySellVolume;
import com.jeongil.autotrading.dto.LongOrShot;
import com.jeongil.autotrading.dto.TradeHistory;

import java.util.List;

public interface BinanceService {
    void sellIt(AccountInfoDto accountInfoDto);

    void buyIt(LongOrShot longOrShot, AccountInfoDto accountInfoDto);

    List<BuySellVolume> getBuySellVolume();

    AccountInfoDto getMyAccountPosition();

    String getSignature(String data);

    TradeHistory getLastTradeHistory();
}
