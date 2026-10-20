package com.enso.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kavenegar")
public class KavenegarConfig {
    private String apiKey;
    private String otpTemplate;
}
