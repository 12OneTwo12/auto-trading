package com.jeongil.autotrading.service.trading;

import com.jeongil.autotrading.common.properties.BinanceProperties;
import com.jeongil.autotrading.dto.AccountInfoDto;
import com.jeongil.autotrading.dto.BuySellVolume;
import com.jeongil.autotrading.dto.LongOrShot;
import com.jeongil.autotrading.service.binance.BinanceService;
import com.jeongil.autotrading.utils.GlobalStatus;
import com.jeongil.autotrading.utils.SenderUtils;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class AutoTradingServiceImpl implements AutoTradingService {

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

            if (longOrShot.isNeedToBuy() && accountInfoDto.getAvailableBalance().compareTo(BigDecimal.ZERO) > 0) binanceService.buyIt(longOrShot, accountInfoDto);
        }
    }

    private boolean isNeedToSell(AccountInfoDto accountInfoDto) {
        List<BuySellVolume> buySellVolumes = binanceService.getBuySellVolume("1");
        int volumeListSize = buySellVolumes.size();

        Double totalBuyVolume = 0D;
        Double totalSellVolume = 0D;

        for (BuySellVolume buySellVolume : buySellVolumes) {
            totalBuyVolume += Double.valueOf(buySellVolume.getBuyVol());
            totalSellVolume += Double.valueOf(buySellVolume.getSellVol());
        }

        Double avgBuyVolume = 0D;
        Double avgSellVolume = 0D;

        if (volumeListSize > 0){
            avgBuyVolume = totalBuyVolume / volumeListSize;
            avgSellVolume = totalSellVolume / volumeListSize;
        }

        boolean isLong = accountInfoDto.getIsLong();

        Double standardVolume = isLong ? avgSellVolume : avgBuyVolume;
        Double compareVolume = isLong ? avgBuyVolume : avgSellVolume;

        boolean buySellVolumeNeedToSell = standardVolume >= compareVolume * 3.5;

        Double profitPercent = 20.0 + GlobalStatus.chargePercent;
        Double lossPercent = -4.7 + GlobalStatus.chargePercent;
        Double rate = accountInfoDto.getRate().doubleValue();

        return buySellVolumeNeedToSell || (profitPercent <= rate || lossPercent >= rate);
    }

    private LongOrShot longOrShotAndTheseINeedToBuy() {
        List<BuySellVolume> buySellVolumes = binanceService.getBuySellVolume("1");
        int volumeListSize = buySellVolumes.size();

        Double totalBuyVolume = 0D;
        Double totalSellVolume = 0D;

        for (BuySellVolume buySellVolume : buySellVolumes) {
            totalBuyVolume += Double.valueOf(buySellVolume.getBuyVol());
            totalSellVolume += Double.valueOf(buySellVolume.getSellVol());
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

        boolean isNeedToBuy = standardVolume <= compareVolume * 8;

        return new LongOrShot(isLong, isNeedToBuy);
    }
}
