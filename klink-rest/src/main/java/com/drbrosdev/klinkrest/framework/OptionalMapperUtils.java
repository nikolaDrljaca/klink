package com.drbrosdev.klinkrest.framework;

import java.util.Optional;

public class OptionalMapperUtils {
    public static <T> Optional<T> toOptional(T value) {
        return Optional.ofNullable( value );
    }
}
