package com.drbrosdev.klinkrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KlinkRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(KlinkRestApplication.class, args);
    }

}
