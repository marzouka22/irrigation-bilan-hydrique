package tn.itbs.analyse.calcul;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;

class HargreavesTest {

	// FAO-56, exemple 8 : 20° Sud, 3 septembre (jour 246) => Ra = 32,2 MJ/m²/j
	@Test
	void rayonnementExtraterrestre_exempleFao56() {
		assertThat(Hargreaves.rayonnementExtraterrestre(-20, 246)).isCloseTo(32.2, within(0.1));
	}

	// Ordre de grandeur réaliste pour le Cap Bon en juillet
	@Test
	void et0_capBonEnJuillet_estRealiste() {
		double et0 = Hargreaves.et0(22, 33, 36.7, 196);
		assertThat(et0).isBetween(5.0, 7.0);
	}

	@Test
	void et0_amplitudeNulle_donneZero() {
		assertThat(Hargreaves.et0(20, 20, 36.7, 196)).isZero();
	}
}
