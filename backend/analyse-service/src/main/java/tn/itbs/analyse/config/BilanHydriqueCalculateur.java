package tn.itbs.analyse.config;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import tn.itbs.analyse.calcul.ModeleBilanHydriqueV1;
import tn.itbs.analyse.calcul.ModeleBilanHydriqueV2;
import tn.itbs.analyse.calcul.ModeleCalcul;

// Registre des algorithmes disponibles, retrouvés par leur numéro de version
@Component
public class BilanHydriqueCalculateur {

	private final Map<String, ModeleCalcul> modeles = List.of(
			new ModeleBilanHydriqueV1(),
			new ModeleBilanHydriqueV2())
			.stream()
			.collect(Collectors.toMap(ModeleCalcul::version, Function.identity()));

	public ModeleCalcul pourVersion(String version) {
		ModeleCalcul m = modeles.get(version);
		if (m == null) {
			throw new IllegalStateException("Aucun algorithme implémenté pour la version " + version);
		}
		return m;
	}
}
