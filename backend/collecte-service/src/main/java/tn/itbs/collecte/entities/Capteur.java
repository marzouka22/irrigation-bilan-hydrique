package tn.itbs.collecte.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Capteur {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Le type du capteur est obligatoire")
	@Enumerated(EnumType.STRING)
	private TypeCapteur type;

	// Identifiant de la parcelle (aucune relation JPA : la parcelle n'appartient pas à ce service)
	@NotNull(message = "L'identifiant de la parcelle est obligatoire")
	@Positive(message = "L'identifiant de la parcelle doit être positif")
	private Long parcelleId;

	@NotBlank(message = "La localisation est obligatoire")
	@Size(max = 150, message = "La localisation ne doit pas dépasser 150 caractères")
	private String localisation;
}
