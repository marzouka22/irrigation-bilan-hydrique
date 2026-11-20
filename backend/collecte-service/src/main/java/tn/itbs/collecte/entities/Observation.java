package tn.itbs.collecte.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Observation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "L'identifiant du capteur est obligatoire")
	private Long capteurId;

	@NotNull(message = "La valeur est obligatoire")
	private Double valeur;

	// Remplie automatiquement à partir du type du capteur
	private String unite;

	// Facultative à la création : la date courante est utilisée si elle est absente
	@PastOrPresent(message = "La date de l'observation ne peut pas être dans le futur")
	@Column(name = "date_observation")
	private LocalDateTime date;
}
