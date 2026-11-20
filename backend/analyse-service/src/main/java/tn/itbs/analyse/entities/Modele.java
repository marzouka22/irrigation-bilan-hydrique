package tn.itbs.analyse.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modèle de calcul versionné. Chaque version correspond à un algorithme implémenté
 * dans le package calcul. La précision est une valeur déclarée (aucun apprentissage
 * automatique n'est utilisé), documentée dans la description.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Modele {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String type;

	@Column(unique = true, nullable = false)
	private String version;

	// Date de mise en service / calibration du modèle
	private LocalDate dateApprentissage;

	// "precision" est un mot réservé MySQL : nom de colonne explicite
	@Column(name = "precision_modele")
	private Double precision;

	@Column(length = 1000)
	private String description;

	// Modèle utilisé quand aucune version n'est précisée
	private boolean parDefaut;
}
