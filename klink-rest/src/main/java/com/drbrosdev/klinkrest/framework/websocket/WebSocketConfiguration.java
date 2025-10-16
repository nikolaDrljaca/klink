package com.drbrosdev.klinkrest.framework.websocket;

import com.drbrosdev.klinkrest.activity.KlinkEventHandler;
import com.drbrosdev.klinkrest.domain.klink.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.klink.usecase.ValidateKlinkAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final KlinkDomainService domainService;

    private final KlinkEventsSessionManager klinkEventsSessionManager;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        var klinkEventHandler = new KlinkEventHandler(
                klinkEventsSessionManager,
                domainService);
        registry.addHandler(klinkEventHandler, "/events/klink/**")
                .addInterceptors(new KlinkEventsSessionValidator(
                        new ValidateKlinkAccess(),
                        domainService))
                .setAllowedOrigins("*");
    }
}
