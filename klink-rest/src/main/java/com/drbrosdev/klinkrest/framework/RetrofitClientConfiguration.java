package com.drbrosdev.klinkrest.framework;

import com.drbrosdev.klinkrest.gateway.hop.HopService;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

@Configuration
public class RetrofitClientConfiguration {

    @Value("${:}")
    private String hopBaseUrl;

    @Value("${:}")
    private String hopApiKey;

    @Bean
    public OkHttpClient configureClient() {
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
