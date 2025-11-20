package com.drbrosdev.klinkrest.shorturl.internal;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Component
@RequiredArgsConstructor
public class GenerateShortUrl {

    public void execute(
            UUID klinkId,
            String readKey,
            @Nullable String writeKey) {

    }
}
