package com.drbrosdev.klinkrest.gateway.hop;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HopService {

    @POST("/")
    Call<Hop> createHop(@Body CreateHopPayload payload);

}
