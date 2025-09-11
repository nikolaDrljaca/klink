package com.drbrosdev.klinkrest.domain.klink;

import com.drbrosdev.klinkrest.domain.klink.model.EnrichKlinkEntryJob;

public interface EnrichKlinkEntryGateway {

    void submit(EnrichKlinkEntryJob job);

}
