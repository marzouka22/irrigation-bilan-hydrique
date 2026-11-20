package tn.itbs.analyse.calcul;

/**
 * Résultat d'un calcul de bilan hydrique (toutes les valeurs sont arrondies à 2 décimales).
 *
 * @param versionModele  version du modèle utilisé
 * @param et0            ET0 retenue (mm/j)
 * @param etc            besoin de la culture ETc = Kc x ET0 (mm)
 * @param pluieEfficace  pluie réellement utile à la culture (mm)
 * @param doseNetteMm    eau à apporter à la plante (mm)
 * @param doseBruteMm    eau à sortir du système d'irrigation (mm)
 * @param quantiteEauM3  volume total pour la parcelle (m3)
 * @param justification  explication détaillée du calcul
 */
public record ResultatCalcul(
		String versionModele,
		double et0,
		double etc,
		double pluieEfficace,
		double doseNetteMm,
		double doseBruteMm,
		double quantiteEauM3,
		String justification
) {
}
