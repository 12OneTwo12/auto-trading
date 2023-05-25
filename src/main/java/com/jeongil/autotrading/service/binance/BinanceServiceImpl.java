package com.jeongil.autotrading.service.binance;

import com.jeongil.autotrading.common.exception.EncryptException;
import com.jeongil.autotrading.common.properties.BinanceProperties;
import com.jeongil.autotrading.dto.*;
import com.jeongil.autotrading.utils.SenderUtils;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.List;

@Service
public class BinanceServiceImpl implements BinanceService{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BinanceProperties binanceProperties;

    @Autowired
    private SenderUtils senderUtils;

    private String symbol = "BTSUSDT";

    @Override
    public AccountInfoDto getMyAccountPosition() {
        String timeStamp = Long.toString(System.currentTimeMillis());

        String queryString = "timestamp=" + timeStamp;

        String signature = getSignature(queryString);

        queryString += "&signature=" + signature;

        String url = binanceProperties.getDefaultUrl() + binanceProperties.getGetAccountInfoUrl() + "?" + queryString;

        AccountDetailInfoDto accountDetailInfoDto = senderUtils.sendGet(HttpMethod.GET, url, new AccountDetailInfoDto());

        boolean hasPosition = false;
        double percentageDifference = 0;

        for (Position position : accountDetailInfoDto.getPositions()) {
            if (position.getSymbol().equals(symbol) && position.getEntryPrice() > 0)  {
                hasPosition = true;

                double difference = position.getMarkPrice() - position.getEntryPrice();

                if (difference == 0) continue;

                percentageDifference = (difference / position.getEntryPrice()) * 100;
            }
        }

        double availableBalance = 0;

        for (Asset asset : accountDetailInfoDto.getAssets()){
            if ("BTC".equals(asset.getAsset())) availableBalance = asset.getAvailableBalance();
        }

        return new AccountInfoDto(hasPosition, percentageDifference, availableBalance);
    }

    @Override
    public void sellIt() {

    }

    @Override
    public void buyIt(LongOrShot longOrShot) {

    }

    @Override
    public List<BuySellVolume> getBuySellVolume() {
        String queryString = "symbol=" + symbol;
        queryString += "&period=" + "5m";
        queryString += "&limit=" + "3";

        String url = binanceProperties.getDefaultUrl() + binanceProperties.getTakerLongShotRatioUrl() + "?" + queryString;

        List<BuySellVolume> buySellVolumeEx = new ArrayList<>();

        List<BuySellVolume> buySellVolumes = senderUtils.sendGet(HttpMethod.GET, url, buySellVolumeEx);

        return buySellVolumes;
    }

    @Override
    public String getSignature(String data) {
        String algorithms = "HmacSHA256";

        try {
            //1. SecretKeySpec 클래스를 사용한 키 생성
            SecretKeySpec secretKey = new SecretKeySpec(binanceProperties.getSecretKey().getBytes(), algorithms);

            //2. 지정된  MAC 알고리즘을 구현하는 Mac 객체를 작성합니다.
            Mac hashes = Mac.getInstance(algorithms);

            //3. 키를 사용해 이 Mac 객체를 초기화
            hashes.init(secretKey);

            //3. 암호화 하려는 데이터의 바이트의 배열을 처리해 MAC 조작을 종료
            byte[] hash = hashes.doFinal(data.getBytes());

            //4. Hex Encode to String
            return Hex.encodeHexString(hash);
        } catch (Exception e){
            throw new EncryptException("sha256 도중 문제 발생");
        }
    }
}
