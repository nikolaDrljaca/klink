package com.drbrosdev.klinkrest.domain.klink.usecase;

import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntryRichPreview;

import java.util.Optional;

public interface GenerateUrlPreview {

    Optional<KlinkEntryRichPreview> execute(KlinkEntry entry);

}
