package com.drbrosdev.klinkrest.activity;

import com.drbrosdev.klinkrest.domain.klink.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.klink.usecase.ValidateKlinkAccess;
import com.drbrosdev.klinkrest.framework.SseSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class KlinkSseController {

    private final SseSessionManager sessionManager;

    private final ValidateKlinkAccess validateKlinkAccess;

    private final KlinkDomainService domainService;

    // /api/events/klink/{id}?readKey=QWERASDF
    /*
    @GetMapping("/events/klink/{uuid}")
    public CompletableFuture<SseEmitter> streamKlinkEntryChangeEvents(
            @PathVariable(name = "uuid") String uuid,
            @RequestParam(name = "readKey") String readKey) {
        var klinkId = fromString(uuid);
        return completedFuture(klinkId)
                // make sure a separate thread executes this fetch - allows spring to close db connection
                .thenApplyAsync(domainService::getKeys)
                .thenApply(keys -> validateKlinkAccess.execute(
                        keys,
                        readOnly(readKey)))
                .thenApply((it) -> sessionManager.createSession(klinkId));
    }
     */
}
