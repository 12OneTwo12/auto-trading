package com.jeongil.autotrading.service.binance;

import com.jeongil.autotrading.common.exception.EncryptException;
import com.jeongil.autotrading.common.properties.BinanceProperties;
import com.jeongil.autotrading.dto.AccountInfoDto;
import com.jeongil.autotrading.dto.BuySellVolume;
import com.jeongil.autotrading.dto.LongOrShot;
import com.jeongil.autotrading.utils.SenderUtils;
import org.apache.commons.codec.binary.Hex;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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
        logger.info("account request start");

        String timeStamp = Long.toString(System.currentTimeMillis());

        String queryString = "?timestamp=" + timeStamp;

        String signature = getSignature(queryString);

        queryString += "&signature=" + signature;

        String url = binanceProperties.getDefaultUrl() + binanceProperties.getGetAccountInfoUrl() + queryString;

        logger.info("before send get");

        JSONObject jsonObject = senderUtils.sendGet(HttpMethod.GET,url, new JSONObject());

        logger.info("after send get");

        logger.info("account request"+jsonObject.toString());

        return null;
    }

    @Override
    public void sellIt() {

    }

    @Override
    public void buyIt(LongOrShot longOrShot) {

    }

    @Override
    public List<BuySellVolume> getBuySellVolume() {
        return null;
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
