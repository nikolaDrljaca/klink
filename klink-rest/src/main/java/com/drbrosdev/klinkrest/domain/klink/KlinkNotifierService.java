package com.drbrosdev.klinkrest.domain.klink;

import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntryChangeNotification;

import java.util.function.Consumer;

public interface KlinkNotifierService {

    Runnable createKlinkEntryChangeHandler(Consumer<KlinkEntryChangeNotification> consumer);

}
