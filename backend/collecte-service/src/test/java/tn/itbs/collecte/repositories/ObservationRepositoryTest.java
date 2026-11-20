package tn.itbs.collecte.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import tn.itbs.collecte.entities.Observation;

// Test sur une vraie base (H2 en mémoire) : chaque test est annulé (rollback) à la fin
@SpringBootTest
@Transactional
class ObservationRepositoryTest {

	@Autowired
	private ObservationRepository observationRepo;

	@Test
	void findByCapteurIdInAndDateAfter_filtreParCapteursEtDate_trieDuPlusRecent() {
		LocalDateTime maintenant = LocalDateTime.now();
		observationRepo.save(new Observation(null, 1L, 20.0, "°C", maintenant.minusHours(1)));
		observationRepo.save(new Observation(null, 2L, 3.0, "mm", maintenant.minusHours(5)));
		observationRepo.save(new Observation(null, 1L, 18.0, "°C", maintenant.minusHours(48))); // trop ancienne
		observationRepo.save(new Observation(null, 9L, 99.0, "%", maintenant.minusHours(1)));   // autre parcelle

		List<Observation> res = observationRepo.findByCapteurIdInAndDateAfterOrderByDateDesc(
				List.of(1L, 2L), maintenant.minusHours(24));

		assertThat(res).extracting(Observation::getValeur).containsExactly(20.0, 3.0);
	}

	@Test
	void existsByCapteurId() {
		observationRepo.save(new Observation(null, 5L, 1.0, "mm", LocalDateTime.now()));

		assertThat(observationRepo.existsByCapteurId(5L)).isTrue();
		assertThat(observationRepo.existsByCapteurId(6L)).isFalse();
	}
}
