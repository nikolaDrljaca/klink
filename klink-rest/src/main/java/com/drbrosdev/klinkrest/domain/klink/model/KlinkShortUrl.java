package com.drbrosdev.klinkrest.domain.klink.model;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;


@Value
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE) //Hides the constructor to force usage of the Builder.
public class KlinkShortUrl {
    @Nullable
    String fullAccessUrl;

    @Nullable
    String readOnlyUrl;
}
