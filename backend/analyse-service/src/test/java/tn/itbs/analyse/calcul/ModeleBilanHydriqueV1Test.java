package tn.itbs.analyse.calcul;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ModeleBilanHydriqueV1Test {

	private final ModeleBilanHydriqueV1 modele = new ModeleBilanHydriqueV1();
	private final ParametresParcelle olivier = new ParametresParcelle("Olivier", 0.70, 2.0, 36.7, "goutte-à-goutte", 0.90);

	@Test
	void calcul_formuleSimple() {
		// ETc = 0,7 x 6 = 4,2 ; dose = 4,2 - 1 = 3,2 mm ; 3,2 x 2 ha x 10 = 64 m3
		ResultatCalcul r = modele.calculer(olivier, new DonneesClimatiques(6.0, 1.0, null, null, null));

		assertThat(r.etc()).isEqualTo(4.2);
		assertThat(r.doseNetteMm()).isEqualTo(3.2);
		assertThat(r.doseBruteMm()).isEqualTo(3.2); // v1 ignore l'efficience
		assertThat(r.quantiteEauM3()).isEqualTo(64.0);
		assertThat(r.justification()).contains("BH 1.0", "Olivier", "64,00 m3");
	}

	@Test
	void calcul_pluieSuffisante_doseNulle() {
		ResultatCalcul r = modele.calculer(olivier, new DonneesClimatiques(6.0, 10.0, null, null, null));

		assertThat(r.doseNetteMm()).isZero();
		assertThat(r.quantiteEauM3()).isZero();
	}

	@Test
	void calcul_sansPluie_supposeZeroEtLeSignale() {
		ResultatCalcul r = modele.calculer(olivier, new DonneesClimatiques(6.0, null, null, null, null));

		assertThat(r.doseNetteMm()).isEqualTo(4.2);
		assertThat(r.justification()).contains("aucune mesure de pluie");
	}

	@Test
	void calcul_sansEt0_leveDonneesInsuffisantes() {
		assertThatThrownBy(() -> modele.calculer(olivier, new DonneesClimatiques(null, 1.0, 20.0, 30.0, null)))
				.isInstanceOf(DonneesInsuffisantesException.class)
				.hasMessageContaining("ET0");
	}
}
