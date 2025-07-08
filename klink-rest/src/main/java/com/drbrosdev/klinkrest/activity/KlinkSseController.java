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

import java.util.UUID;

import static com.drbrosdev.klinkrest.domain.klink.model.KlinkKey.readOnly;

@RestController
@RequiredArgsConstructor
public class KlinkSseController {

    private final SseSessionManager sessionManager;

    private final ValidateKlinkAccess validateKlinkAccess;

    private final KlinkDomainService domainService;

    // /api/klink/.../events?readKey=QWERASDF
    @GetMapping("/klink/{uuid}/events")
    public SseEmitter streamKlinkEntryChangeEvents(
            @PathVariable(name = "uuid") String uuid,
            @RequestParam(name = "readKey") String readKey) {
        var klinkId = UUID.fromString(uuid);
        // validate access
        validateKlinkAccess.execute(
                domainService.getKeys(klinkId),
                readOnly(readKey));
        // create session
        return sessionManager.createSession(klinkId);
    }
}
