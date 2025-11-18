package com.drbrosdev.klinkrest.gateway.hop;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HopService {

    @POST("/l")
    Call<Hop> createHop(@Body CreateHopPayload payload);

    default Call<Hop> createHop(String url) {
        return createHop(CreateHopPayload.builder()
                .url(url)
                .build());
    }
}
