package tn.itbs.analyse.config;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import tn.itbs.analyse.calcul.ModeleBilanHydriqueV1;
import tn.itbs.analyse.calcul.ModeleBilanHydriqueV2;
import tn.itbs.analyse.entities.Modele;
import tn.itbs.analyse.repositories.ModeleRepository;

/**
 * Enregistre au démarrage les modèles correspondant aux algorithmes implémentés
 * (uniquement s'ils n'existent pas encore en base).
 */
@Component
public class ModelesInitializer implements CommandLineRunner {

	public static final String TYPE = "BILAN_HYDRIQUE";

	@Autowired
	private ModeleRepository modeleRepo;

	@Override
	public void run(String... args) {
		enregistrerSiAbsent(ModeleBilanHydriqueV1.VERSION, 0.70, false,
				"Bilan simple : dose = max(0 ; Kc x ET0 mesurée - pluie). Toute la pluie est jugée utile, "
				+ "les pertes du système d'irrigation sont ignorées. Précision déclarée (non apprise).");
		enregistrerSiAbsent(ModeleBilanHydriqueV2.VERSION, 0.85, true,
				"Bilan affiné : ET0 de Hargreaves (FAO-56) à partir de Tmin/Tmax et de la latitude, "
				+ "pluie efficace (0 si < 5 mm, sinon 80 %), dose brute corrigée par l'efficience du système. "
				+ "Précision déclarée (non apprise).");
	}

	private void enregistrerSiAbsent(String version, double precision, boolean parDefaut, String description) {
		if (modeleRepo.findByVersion(version).isEmpty()) {
			modeleRepo.save(new Modele(null, TYPE, version, LocalDate.of(2026, 11, 20), precision, description, parDefaut));
		}
	}
}
