package tn.itbs.analyse.dtos;

import jakarta.validation.constraints.NotNull;
import tn.itbs.analyse.calcul.DonneesClimatiques;

public record DemandeComparaison(
		@NotNull(message = "L'identifiant de la parcelle est obligatoire") Long parcelleId,
		@NotNull(message = "Les données climatiques sont obligatoires") DonneesClimatiques donnees
) {
}
