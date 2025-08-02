package com.drbrosdev.klinkrest.framework.websocket;

import com.drbrosdev.klinkrest.activity.KlinkEventHandler;
import com.drbrosdev.klinkrest.domain.klink.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.klink.usecase.ParseKlinkSessionDetails;
import com.drbrosdev.klinkrest.domain.klink.usecase.ValidateKlinkAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final KlinkDomainService domainService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(createHandler(), "/events/klink/**")
                .addInterceptors(new KlinkEventsSessionValidationInterceptor(
                        new ValidateKlinkAccess(),
                        new ParseKlinkSessionDetails(),
                        domainService))
                .setAllowedOrigins("*");
    }

    @Bean
    public KlinkEventHandler createHandler() {
        return new KlinkEventHandler(new WebSocketSessionManager());
    }
}
