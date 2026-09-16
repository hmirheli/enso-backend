package com.enso.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "zarinpal")
public class ZarinpalConfig {

    private String merchantId;
    private String requestUrl;
    private String verifyUrl;
    private String paymentUrl;
    private String callbackUrl;
}
