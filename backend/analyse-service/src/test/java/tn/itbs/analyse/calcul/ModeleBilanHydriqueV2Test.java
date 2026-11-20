package tn.itbs.analyse.calcul;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ModeleBilanHydriqueV2Test {

	private final ModeleBilanHydriqueV2 modele = new ModeleBilanHydriqueV2();
	private final ParametresParcelle olivier = new ParametresParcelle("Olivier", 0.70, 2.0, 36.7, "goutte-à-goutte", 0.90);
	private final LocalDate juillet = LocalDate.of(2026, 7, 15);

	@Test
	void pluieEfficace_regleDuSeuil() {
		assertThat(ModeleBilanHydriqueV2.pluieEfficace(3.0)).isZero();      // < 5 mm : évaporée
		assertThat(ModeleBilanHydriqueV2.pluieEfficace(10.0)).isEqualTo(8.0); // 80 %
	}

	@Test
	void calcul_utiliseHargreavesEtEfficience() {
		ResultatCalcul r = modele.calculer(olivier, new DonneesClimatiques(null, 0.0, 22.0, 33.0, juillet));

		double et0Attendue = Hargreaves.et0(22, 33, 36.7, juillet.getDayOfYear());
		assertThat(r.et0()).isCloseTo(et0Attendue, within(0.01));
		assertThat(r.doseNetteMm()).isCloseTo(0.70 * et0Attendue, within(0.01));
		assertThat(r.doseBruteMm()).isCloseTo(0.70 * et0Attendue / 0.90, within(0.01)); // pertes du goutte-à-goutte
		assertThat(r.quantiteEauM3()).isCloseTo(r.doseBruteMm() * 2.0 * 10, within(0.1));
		assertThat(r.justification()).contains("Hargreaves", "goutte-à-goutte");
	}

	@Test
	void calcul_faiblePluieIgnoree_donneMemeDoseQueSansPluie() {
		ResultatCalcul sansPluie = modele.calculer(olivier, new DonneesClimatiques(null, 0.0, 22.0, 33.0, juillet));
		ResultatCalcul petitePluie = modele.calculer(olivier, new DonneesClimatiques(null, 3.0, 22.0, 33.0, juillet));

		assertThat(petitePluie.doseNetteMm()).isEqualTo(sansPluie.doseNetteMm());
	}

	@Test
	void calcul_sansTemperatures_leveDonneesInsuffisantes() {
		assertThatThrownBy(() -> modele.calculer(olivier, new DonneesClimatiques(6.0, 1.0, null, null, juillet)))
				.isInstanceOf(DonneesInsuffisantesException.class)
				.hasMessageContaining("températures");
	}
}
