package com.drbrosdev.klinkrest.service;

import com.drbrosdev.klinkrest.model.Klink;
import com.drbrosdev.klinkrest.model.KlinkEntry;
import com.drbrosdev.klinkrest.model.KlinkKey;
import com.drbrosdev.klinkrest.repository.KlinkEntryRepository;
import com.drbrosdev.klinkrest.repository.KlinkKeyRepository;
import com.drbrosdev.klinkrest.repository.KlinkRepository;
import org.openapitools.model.CreateKlinkPayloadApiDto;
import org.openapitools.model.KlinkApiDto;
import org.openapitools.model.KlinkEntryApiDto;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KlinkService {

    private static final Logger LOG = LoggerFactory.getLogger(KlinkService.class);

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final KlinkRepository klinkRepository;
    private final KlinkEntryRepository klinkEntryRepository;
    private final KlinkKeyRepository klinkKeyRepository;


    public KlinkService(KlinkRepository klinkRepository, KlinkEntryRepository klinkEntryRepository, KlinkKeyRepository klinkKeyRepository) {
        this.klinkRepository = klinkRepository;
        this.klinkEntryRepository = klinkEntryRepository;
        this.klinkKeyRepository = klinkKeyRepository;
    }

    @Transactional
    public ResponseEntity<KlinkApiDto> createKlink(CreateKlinkPayloadApiDto createKlinkPayloadApiDto) {

        try {

            Optional<Klink> klinkDB = klinkRepository.findById(createKlinkPayloadApiDto.getId());
            if(klinkDB.isPresent()) {
                LOG.error("Klink is already present with id: {}", createKlinkPayloadApiDto.getId().toString());
                return new ResponseEntity<>(null, HttpStatus.BAD_GATEWAY);
            }

            String readKey = createKlinkKey(8);
            String writeKey = createKlinkKey(8);
            LocalDateTime createdAt = LocalDateTime.now();

            Klink klink = new Klink(createKlinkPayloadApiDto.getId(), createKlinkPayloadApiDto.getName(), null,
                    createdAt, createdAt);

            klinkRepository.save(klink);
            createAndSaveKlinkKey(readKey, writeKey, createKlinkPayloadApiDto.getId());
            createAndSaveKlinkEntry(createKlinkPayloadApiDto.getId(), createKlinkPayloadApiDto.getEntries());

            KlinkApiDto klinkDto = new KlinkApiDto(createKlinkPayloadApiDto.getId(), createKlinkPayloadApiDto.getName(),
                    readKey, writeKey, createKlinkPayloadApiDto.getEntries());

            LOG.info("Created Klink with id: {}", createKlinkPayloadApiDto.getId().toString());
            return new ResponseEntity<>(klinkDto, HttpStatus.CREATED);

        } catch (Exception e) {
            LOG.error("Couldn't create Klink with id: {}", createKlinkPayloadApiDto.getId().toString());
            return new ResponseEntity<>(null, HttpStatus.BAD_GATEWAY);
        }
    }

    @Transactional
    public ResponseEntity<KlinkApiDto> getKlink(UUID id) {

        try {

            Optional<Klink> klink = klinkRepository.findById(id);
            Optional<KlinkKey> klinkKey = klinkKeyRepository.findByKlinkId(id);
            List<KlinkEntry> klinkEntries = klinkEntryRepository.findAllByKlinkId(id);

            if(klink.isPresent() && klinkKey.isPresent()) {
                KlinkApiDto klinkApiDto = getKlinkApiDto(klink.get(), klinkKey.get(), klinkEntries);
                return new ResponseEntity<>(klinkApiDto, HttpStatus.OK);
            } else {
                LOG.error("Klink and/or klinkKey with id {} not found", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOG.error("Error while retrieving Klink with id: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.BAD_GATEWAY);
        }
    }

    @Transactional
    public ResponseEntity<Void> deleteKlink(UUID id) {

        try {

            Optional<Klink> klink = klinkRepository.findById(id);
            Optional<KlinkKey> klinkKey = klinkKeyRepository.findByKlinkId(id);
            List<KlinkEntry> klinkEntries = klinkEntryRepository.findAllByKlinkId(id);

            if(klink.isPresent() && klinkKey.isPresent()) {

                klinkRepository.delete(klink.get());
                klinkKeyRepository.delete(klinkKey.get());
                klinkEntryRepository.deleteAll(klinkEntries);

                return new ResponseEntity<>(null, HttpStatus.OK);
            } else {
                LOG.error("Klink and/or klinkKey with id {} not found", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOG.error("Error while deleting Klink with id: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.BAD_GATEWAY);
        }
    }

    private String createKlinkKey(int length) {

        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(index));
        }
        return builder.toString();

    }

    private void createAndSaveKlinkKey(String readKey, String writeKey, UUID klinkId) {

        KlinkKey klinkKey = new KlinkKey();
        klinkKey.setReadKey(readKey);
        klinkKey.setWriteKey(writeKey);
        klinkKey.setKlinkId(klinkId);

        klinkKeyRepository.save(klinkKey);
    }

    private void createAndSaveKlinkEntry(UUID id, List<KlinkEntryApiDto> entries) {

        for (KlinkEntryApiDto entry : entries) {
            KlinkEntry klinkEntry = new KlinkEntry();
            klinkEntry.setKlinkId(id);
            klinkEntry.setValue(entry.getValue());

            klinkEntryRepository.save(klinkEntry);
        }

    }

    private KlinkApiDto getKlinkApiDto(Klink klink, KlinkKey klinkKey, List<KlinkEntry> klinkEntries) {

        List<KlinkEntryApiDto> klinkEntryApiDtos = new ArrayList<>();
        for (KlinkEntry ke : klinkEntries) {
            KlinkEntryApiDto klinkEntryApiDto = new KlinkEntryApiDto(ke.getValue());
            klinkEntryApiDtos.add(klinkEntryApiDto);
        }

        return new KlinkApiDto(klink.getId(), klink.getName(), klinkKey.getReadKey(),
                klinkKey.getWriteKey(), klinkEntryApiDtos);
    }

}