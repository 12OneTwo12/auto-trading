package com.jeongil.autotrading.service.binance;

import com.jeongil.autotrading.common.exception.EncryptException;
import com.jeongil.autotrading.common.exception.RequestOrderException;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class BinanceServiceImpl implements BinanceService{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BinanceProperties binanceProperties;

    @Autowired
    private SenderUtils senderUtils;

    @Override
    public AccountInfoDto getMyAccountPosition() {
        String timeStamp = Long.toString(System.currentTimeMillis());

        String queryString = "timestamp=" + timeStamp;

        String signature = getSignature(queryString);

        queryString += "&signature=" + signature;

        String url = binanceProperties.getDefaultUrl() + binanceProperties.getGetAccountInfoUrl() + "?" + queryString;

        AccountDetailInfoDto accountDetailInfoDto = senderUtils.sendGet(HttpMethod.GET, url, new AccountDetailInfoDto());

        boolean hasPosition = false;
        BigDecimal percentageDifference = BigDecimal.ZERO;
        BigDecimal myPositionPrice = BigDecimal.ZERO;
        BigDecimal myPositionQuantity = BigDecimal.ZERO;
        BigDecimal unrealizedProfit = BigDecimal.ZERO;
        Boolean isLong = false;

        for (Position position : accountDetailInfoDto.getPositions()) {
            if (position.getSymbol().equals(binanceProperties.getSymbol()) && position.getEntryPrice().compareTo(BigDecimal.ZERO) > 0)  {
                hasPosition = true;

                if (position.getUnrealizedProfit().compareTo(BigDecimal.ZERO) == 0) continue;

                BigDecimal positionAmt = position.getPositionAmt();

                if (positionAmt.signum() == -1) { // 숫자가 음수인 경우
                    positionAmt = positionAmt.multiply(BigDecimal.valueOf(-1)) ; // 부호를 반전하여 양수로 변환
                }

                unrealizedProfit = position.getUnrealizedProfit();
                BigDecimal decimal = unrealizedProfit.divide(position.getEntryPrice().multiply(positionAmt), 2);
                percentageDifference = decimal.multiply(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(position.getLeverage()));

                myPositionQuantity = position.getPositionAmt();
                isLong = position.getPositionSide().equals("LONG") ? true : false;
            }
        }

        BigDecimal availableBalance = BigDecimal.ZERO;

        for (Asset asset : accountDetailInfoDto.getAssets()){
            if ("BTC".equals(asset.getAsset())) availableBalance = asset.getAvailableBalance();
        }
        
        return new AccountInfoDto(hasPosition, percentageDifference, availableBalance, myPositionPrice, myPositionQuantity, unrealizedProfit, isLong);
    }

    @Override
    public void sellIt(AccountInfoDto accountInfoDto) {
        String side = accountInfoDto.getIsLong() ? "SELL" : "BUY";
        String type = "MARKET";
        String timeStamp = Long.toString(System.currentTimeMillis());
        String positionSide = accountInfoDto.getIsLong() ? "LONG" : "SHORT";

        String quantity = accountInfoDto.getMyPositionQuantity().toString();

        if (accountInfoDto.getMyPositionQuantity().signum() == -1) { // 숫자가 음수인 경우
            quantity = accountInfoDto.getMyPositionQuantity().negate().toString(); // 부호를 반전하여 양수로 변환
        }

        if (quantity.length() > 5) quantity = quantity.substring(0, 5);

        String queryString = "side=" + side;
        queryString += "&type=" + type;
        queryString += "&positionSide=" + positionSide;
        queryString += "&quantity=" + quantity;
        queryString += "&symbol=" + binanceProperties.getSymbol();
        queryString += "&timestamp=" + timeStamp;

        String sig = getSignature(queryString);
        queryString += "&signature=" + sig;

        String uri = binanceProperties.getDefaultUrl() + binanceProperties.getOrderUrl() + "?" + queryString;

        OrderResponseDto responseDto = senderUtils.sendGet(HttpMethod.POST, uri, new OrderResponseDto());

        if (!"NEW".equals(responseDto.getStatus())) {
            senderUtils.sendSlack(senderUtils.getErrorMessage("RequestOrderException", "Order Response가 비정상적입니다."));
            throw RequestOrderException.ofError("Order Response가 비정상적입니다.");
        }

        TradeHistory tradeHistory = getLastTradeHistory();

        String winOrLose = accountInfoDto.getRate().compareTo(BigDecimal.ZERO) > 0 ? "익절" : "손절";
        String rate = accountInfoDto.getRate().compareTo(BigDecimal.ZERO) > 0 ? accountInfoDto.getRate().toString().substring(0, 4) : accountInfoDto.getRate().toString().substring(0, 5);
        
        String message = "*" + winOrLose + "*" + " - [ 포지션 종료 - " + " 수익률 : `" + rate + "%`" + " 손익 금액 : " + accountInfoDto.getUnrealizedProfit() + " position side : " + tradeHistory.getPositionSide() + " ]";

        senderUtils.sendSlack(message);
    }

    @Override
    public void buyIt(LongOrShot longOrShot, AccountInfoDto accountInfoDto) {
        String side = longOrShot.isLong() ? "BUY" : "SELL";
        String type = "MARKET";
        String positionSide = longOrShot.isLong() ? "LONG" : "SHORT";
        String timeStamp = Long.toString(System.currentTimeMillis());
        Integer setLeverage = 20;
        String quantity = accountInfoDto.getAvailableBalance().multiply(BigDecimal.valueOf(setLeverage)).toString();

        if (quantity.length() > 5) quantity = quantity.substring(0, 5);

        String queryString = "side=" + side;
        queryString += "&symbol=" + binanceProperties.getSymbol();
        queryString += "&quantity=" + quantity;
        queryString += "&type=" + type;
        queryString += "&positionSide=" + positionSide;
        queryString += "&timestamp=" + timeStamp;

        String sig = getSignature(queryString);
        queryString += "&signature=" + sig;

        String uri = binanceProperties.getDefaultUrl() + binanceProperties.getOrderUrl() + "?" + queryString;

        OrderResponseDto responseDto = senderUtils.sendGet(HttpMethod.POST, uri, new OrderResponseDto());

        if (!"NEW".equals(responseDto.getStatus())) {
            senderUtils.sendSlack(senderUtils.getErrorMessage("RequestOrderException", "Order Response가 비정상적입니다."));
            throw RequestOrderException.ofError("Order Response가 비정상적입니다.");
        }

        String leverageQueryString = "symbol=" + binanceProperties.getSymbol();
        leverageQueryString += "&leverage=" + setLeverage;
        leverageQueryString += "&timestamp=" + timeStamp;

        String leverSig = getSignature(leverageQueryString);
        leverageQueryString += "&signature=" + leverSig;

        String setLeverageUrl = binanceProperties.getDefaultUrl() + binanceProperties.getLeverageUrl() + "?" + leverageQueryString;

        LeverageResponseDto leverageResponseDto = senderUtils.sendGet(HttpMethod.POST, setLeverageUrl, new LeverageResponseDto());

        TradeHistory tradeHistory = getLastTradeHistory();

        String message = "[ 구매 완료 - " + "position side : " + positionSide + " 포지션 수량 : " + quantity + " ]";

        senderUtils.sendSlack(message);

        if (!setLeverage.equals(leverageResponseDto.getLeverage())){
            senderUtils.sendSlack(senderUtils.getErrorMessage("RequestOrderException", "Set Leverage Response가 비정상적입니다."));
            throw RequestOrderException.ofError("Set Leverage Response가 비정상적입니다.");
        }
    }

    @Override
    public List<BuySellVolume> getBuySellVolume(String limit) {
        String queryString = "symbol=" + binanceProperties.getSymbol();
        queryString += "&period=" + "5m";
        queryString += "&limit=" + limit;

        String url = binanceProperties.getDefaultUrl() + binanceProperties.getTakerLongShotRatioUrl() + "?" + queryString;

        List<BuySellVolume> buySellVolumes = senderUtils.sendList(HttpMethod.GET, url, new BuySellVolume());

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
            senderUtils.sendSlack(senderUtils.getErrorMessage("EncryptException","sha256 도중 문제 발생"));
            throw new EncryptException("sha256 도중 문제 발생");
        }
    }

    @Override
    public TradeHistory getLastTradeHistory() {
        String timeStamp = Long.toString(System.currentTimeMillis());

        String queryString = "timestamp=" + timeStamp;

        queryString += "&limit=" + 1;

        String signature = getSignature(queryString);

        queryString += "&signature=" + signature;

        String url = binanceProperties.getDefaultUrl() + binanceProperties.getUserTradesUrl() + "?" + queryString;

        List<TradeHistory> tradeHistory = senderUtils.sendList(HttpMethod.GET, url, new TradeHistory());

        return tradeHistory.get(0);
    }
}
