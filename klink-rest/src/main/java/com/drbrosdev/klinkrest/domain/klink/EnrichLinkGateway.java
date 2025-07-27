package com.drbrosdev.klinkrest.domain.klink;

import com.drbrosdev.klinkrest.domain.klink.model.EnrichLinkJob;

public interface EnrichLinkGateway {

    void submit(EnrichLinkJob job);

}
