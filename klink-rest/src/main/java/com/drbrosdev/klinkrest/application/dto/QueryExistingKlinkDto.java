package com.drbrosdev.klinkrest.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) //Hides the constructor to force usage of the Builder.
public class QueryExistingKlinkDto {
    List<QueryExistingKlinkItemDto> klinks;
}
