package tn.itbs.analyse.dtos;

import jakarta.validation.constraints.NotNull;
import tn.itbs.analyse.calcul.DonneesClimatiques;

/**
 * Demande de calcul d'une recommandation.
 * modeleId est facultatif : le modèle par défaut est alors utilisé.
 * (À l'étape 3, les données climatiques seront récupérées automatiquement depuis Collecte.)
 */
public record DemandeCalcul(
		@NotNull(message = "L'identifiant de la parcelle est obligatoire") Long parcelleId,
		Long modeleId,
		@NotNull(message = "Les données climatiques sont obligatoires") DonneesClimatiques donnees
) {
}
