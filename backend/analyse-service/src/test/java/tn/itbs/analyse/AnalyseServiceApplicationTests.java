package tn.itbs.analyse;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tn.itbs.analyse.repositories.ModeleRepository;

@SpringBootTest
class AnalyseServiceApplicationTests {

	@Autowired
	private ModeleRepository modeleRepo;

	@Test
	void contextLoads_etModelesInitialises() {
		assertThat(modeleRepo.findAll()).extracting("version").containsExactlyInAnyOrder("1.0", "2.0");
		assertThat(modeleRepo.findFirstByParDefautTrue()).get().extracting("version").isEqualTo("2.0");
	}
}
