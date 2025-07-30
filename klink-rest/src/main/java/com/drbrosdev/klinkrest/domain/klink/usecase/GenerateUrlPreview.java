package com.drbrosdev.klinkrest.domain.klink.usecase;

import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.drbrosdev.klinkrest.domain.klink.model.RichKlinkEntryPreview;

import java.util.Optional;

public interface GenerateUrlPreview {

    Optional<RichKlinkEntryPreview> execute(KlinkEntry entry);

}
