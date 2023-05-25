package com.jeongil.autotrading.service.trading;

import com.jeongil.autotrading.common.properties.BinanceProperties;
import com.jeongil.autotrading.dto.AccountInfoDto;
import com.jeongil.autotrading.dto.BuySellVolume;
import com.jeongil.autotrading.dto.LongOrShot;
import com.jeongil.autotrading.service.binance.BinanceService;
import com.jeongil.autotrading.utils.SenderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutoTradingServiceImpl implements AutoTradingService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SenderUtils senderUtils;

    @Autowired
    private BinanceService binanceService;

    @Autowired
    private BinanceProperties binanceProperties;

    @Override
    public void process(){
        AccountInfoDto accountInfoDto = binanceService.getMyAccountPosition();

        if (accountInfoDto.isHasPosition()){
            if (isNeedToSell(accountInfoDto)) binanceService.sellIt(accountInfoDto);
        } else {
            LongOrShot longOrShot = longOrShotAndTheseINeedToBuy();

            if (longOrShot.isNeedToBuy() && accountInfoDto.getAvailableBalance() > 0) binanceService.buyIt(longOrShot, accountInfoDto);
        }
    }

    private boolean isNeedToSell(AccountInfoDto accountInfoDto) {
        Double profitPercent = 3.0;
        Double lossPercent = -1.5;
        Double rate = accountInfoDto.getRate();

        return profitPercent <= rate || lossPercent <= rate;
    }

    private LongOrShot longOrShotAndTheseINeedToBuy() {
        List<BuySellVolume> buySellVolumes = binanceService.getBuySellVolume();
        int volumeListSize = buySellVolumes.size();

        Double totalBuyVolume = 0D;
        Double totalSellVolume = 0D;

        for (BuySellVolume buySellVolume : buySellVolumes) {
            totalBuyVolume += buySellVolume.getBuyVol();
            totalSellVolume += buySellVolume.getSellVol();
        }

        Double avgBuyVolume = 0D;
        Double avgSellVolume = 0D;

        if (volumeListSize > 0){
            avgBuyVolume = totalBuyVolume / volumeListSize;
            avgSellVolume = totalSellVolume / volumeListSize;
        }

        boolean isLong = avgBuyVolume > avgSellVolume;

        Double standardVolume = isLong ? avgBuyVolume : avgSellVolume;
        Double compareVolume = isLong ? avgSellVolume : avgBuyVolume;

        boolean isNeedToBuy = standardVolume >= compareVolume * 1.5;

        return new LongOrShot(isLong, isNeedToBuy);
    }
}
