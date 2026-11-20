package tn.itbs.analyse.calcul;

import java.time.LocalDate;

/**
 * Données climatiques d'une journée pour une parcelle.
 * Chaque modèle utilise les champs dont il a besoin (ex. ET0 mesurée pour v1, températures pour v2).
 *
 * @param et0  évapotranspiration de référence mesurée (mm/j), peut être null
 * @param pluie cumul de pluie de la journée (mm), null = aucune donnée
 * @param temperatureMin température minimale (°C), peut être null
 * @param temperatureMax température maximale (°C), peut être null
 * @param date  jour concerné (null = aujourd'hui)
 */
public record DonneesClimatiques(
		Double et0,
		Double pluie,
		Double temperatureMin,
		Double temperatureMax,
		LocalDate date
) {
	public LocalDate dateOuAujourdhui() {
		return date != null ? date : LocalDate.now();
	}
}
