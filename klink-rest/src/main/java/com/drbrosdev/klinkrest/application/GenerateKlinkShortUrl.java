package com.drbrosdev.klinkrest.application;

import com.drbrosdev.klinkrest.domain.klink.KlinkDomainService;
import com.drbrosdev.klinkrest.gateway.hop.HopService;
import com.drbrosdev.klinkrest.utils.UseCase;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;

import java.util.UUID;

@UseCase
@AllArgsConstructor
public class GenerateKlinkShortUrl {

    private final HopService hopService;

    private final KlinkDomainService klinkDomainService;

    public void execute(
            UUID klinkId,
            String readKey,
            @Nullable String writeKey) {
        /*
        1 Look for existing shortUrl in db
        if found and non-null: unwrap and based on access level return appropriate short url (from db entry)
        if NOT found:
            create a share link (look at logic on the client)
            pass it to hop service
            on success store into db and return result of service call
            on failure log and return share link (the not short one obv)
         */
    }

}
