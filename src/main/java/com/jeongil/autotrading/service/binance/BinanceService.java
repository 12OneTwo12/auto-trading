package com.jeongil.autotrading.service.binance;

import com.jeongil.autotrading.dto.AccountInfoDto;
import com.jeongil.autotrading.dto.BuySellVolume;
import com.jeongil.autotrading.dto.LongOrShotAndBuyOrNot;
import com.jeongil.autotrading.dto.TradeHistory;

import java.util.List;

public interface BinanceService {
    void sellIt(AccountInfoDto accountInfoDto);

    void buyIt(LongOrShotAndBuyOrNot longOrShotAndBuyOrNot, AccountInfoDto accountInfoDto);

    List<BuySellVolume> getBuySellVolume(String limit);

    AccountInfoDto getMyAccountPosition();

    String getSignature(String data);

    TradeHistory getLastTradeHistory();
}
