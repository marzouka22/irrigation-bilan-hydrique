package tn.itbs.analyse.calcul;

// Un algorithme de calcul de bilan hydrique, identifié par sa version
public interface ModeleCalcul {

	String version();

	ResultatCalcul calculer(ParametresParcelle parcelle, DonneesClimatiques donnees);

	// 1 mm d'eau sur 1 ha = 10 m3
	static double mmEnM3(double mm, double surfaceHa) {
		return mm * surfaceHa * 10.0;
	}

	static double arrondi(double v) {
		return Math.round(v * 100.0) / 100.0;
	}
}
