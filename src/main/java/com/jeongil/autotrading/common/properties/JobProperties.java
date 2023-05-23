package com.jeongil.autotrading.common.properties;

import com.jeongil.autotrading.common.properties.factory.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
@PropertySource(value = "classpath:config/properties/job-properties.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties("job")
@Component
@Primary
@Data
public class JobProperties {

    private String name;
    private String cron;
}
