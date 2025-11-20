package com.drbrosdev.klinkrest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class KlinkRestApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void analyzeModulithStructure() {
		ApplicationModules modules = ApplicationModules.of(KlinkRestApplication.class);
		modules.forEach(System.out::println);
	}

}
