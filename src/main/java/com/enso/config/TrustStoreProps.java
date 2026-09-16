package com.enso.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "enso.truststore")
public record TrustStoreProps(String password, String path) {
}
