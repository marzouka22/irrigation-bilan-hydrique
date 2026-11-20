package tn.itbs.collecte.dtos;

import java.time.LocalDateTime;

import tn.itbs.collecte.entities.Capteur;
import tn.itbs.collecte.entities.Observation;
import tn.itbs.collecte.entities.TypeCapteur;

// Observation + informations de son capteur (type, parcelle), utile pour le calcul du bilan hydrique
public record ObservationDetails(
		Long id,
		Long capteurId,
		Long parcelleId,
		TypeCapteur type,
		Double valeur,
		String unite,
		LocalDateTime date
) {
	public static ObservationDetails of(Observation o, Capteur c) {
		return new ObservationDetails(
				o.getId(),
				o.getCapteurId(),
				c.getParcelleId(),
				c.getType(),
				o.getValeur(),
				o.getUnite(),
				o.getDate());
	}
}
