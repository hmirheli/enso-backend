package com.enso.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.security.KeyStore;

@Configuration
@EnableConfigurationProperties({TrustStoreProps.class})
public class RestClientConfig {

    @Bean
    @Primary
    public RestClient restClient(TrustStoreProps props) throws Exception {
        KeyStore trustStore = KeyStore.getInstance("PKCS12");

        try (InputStream inputStream = new ClassPathResource(props.path()).getInputStream()) {
            trustStore.load(inputStream, props.password().toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());

        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");

        sslContext.init(null,tmf.getTrustManagers(),null);

        HttpClient httpClient = HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();

        JdkClientHttpRequestFactory requestFactory =new JdkClientHttpRequestFactory(httpClient);

        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    @Bean("kavenegarRestClient")
    public RestClient kavenegarRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("https://api.kavenegar.com")
                .build();
    }
}
