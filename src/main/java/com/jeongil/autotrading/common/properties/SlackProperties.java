package com.jeongil.autotrading.common.properties;

import com.jeongil.autotrading.common.properties.factory.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
@PropertySource(value = "classpath:config/properties/slack-properties.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "slack")
@Component
@Data
public class SlackProperties {

    private String token;
}
