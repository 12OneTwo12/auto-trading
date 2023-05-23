package com.jeongil.autotrading.common.properties;

import com.jeongil.autotrading.common.properties.factory.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
@PropertySource(value = "classpath:config/properties/binance-properties.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "binance")
@Component
@Data
public class BinanceProperties {

    private String key;
    private String defaultUrl;
}
