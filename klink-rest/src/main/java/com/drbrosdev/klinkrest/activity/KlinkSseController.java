package com.drbrosdev.klinkrest.activity;

import com.drbrosdev.klinkrest.domain.klink.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.klink.usecase.ValidateKlinkAccess;
import com.drbrosdev.klinkrest.framework.SseSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.CompletableFuture;

import static com.drbrosdev.klinkrest.domain.klink.model.KlinkKey.readOnly;
import static java.util.UUID.fromString;
import static java.util.concurrent.CompletableFuture.completedFuture;

@RestController
@RequiredArgsConstructor
public class KlinkSseController {

    private final SseSessionManager sessionManager;

    private final ValidateKlinkAccess validateKlinkAccess;

    private final KlinkDomainService domainService;

    // /api/klink/.../events?readKey=QWERASDF
    @GetMapping("/klink/{uuid}/events")
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
}
