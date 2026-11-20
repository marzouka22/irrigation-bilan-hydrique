package tn.itbs.analyse.calcul;

/**
 * Caractéristiques de la parcelle nécessaires au calcul (indépendantes de JPA).
 *
 * @param culture    libellé de la culture (ex. "Olivier")
 * @param kc         coefficient cultural
 * @param surfaceHa  surface en hectares
 * @param latitude   latitude en degrés (positive au nord), utilisée par Hargreaves
 * @param systeme    libellé du système d'irrigation (ex. "goutte-à-goutte")
 * @param efficience efficience du système d'irrigation (0 à 1)
 */
public record ParametresParcelle(
		String culture,
		double kc,
		double surfaceHa,
		double latitude,
		String systeme,
		double efficience
) {
}
