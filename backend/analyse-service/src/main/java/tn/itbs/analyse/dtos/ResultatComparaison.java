package tn.itbs.analyse.dtos;

import tn.itbs.analyse.calcul.ResultatCalcul;

/**
 * Résultat d'un modèle dans une comparaison : soit un résultat, soit la raison
 * pour laquelle le modèle n'a pas pu être appliqué (données insuffisantes).
 */
public record ResultatComparaison(
		Long modeleId,
		String version,
		Double precision,
		ResultatCalcul resultat,
		String erreur
) {
}
