package com.drbrosdev.klinkrest.framework;

import com.drbrosdev.klinkrest.gateway.hop.HopService;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

@Configuration
@Log4j2
public class RetrofitClientConfiguration {

    @Value("${hop.service.url:}")
    private String hopBaseUrl;

    @Value("${hop.service.key:}")
    private String hopApiKey;

    @Bean
    public OkHttpClient configureClient() {
        log.info(
                "Configuring hop-service client with {} and key {}",
                hopBaseUrl,
                hopApiKey);
        return new OkHttpClient().newBuilder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    var request = chain.request()
                            .newBuilder()
                            .addHeader("X-Api-Key", hopApiKey)
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }

    @Bean
    public HopService configureHopService(OkHttpClient client) {
        var retrofit = new Retrofit.Builder()
                .baseUrl(hopBaseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(HopService.class);
    }
}
