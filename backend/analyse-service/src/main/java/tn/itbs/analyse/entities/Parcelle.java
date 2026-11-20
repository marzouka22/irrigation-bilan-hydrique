package tn.itbs.analyse.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.itbs.analyse.calcul.ParametresParcelle;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Parcelle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Le nom est obligatoire")
	@Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
	private String nom;

	@NotNull(message = "La culture est obligatoire")
	@Enumerated(EnumType.STRING)
	private Culture culture;

	// Facultatif : si absent, le Kc de référence de la culture est utilisé
	@DecimalMin(value = "0.1", message = "Le Kc doit être compris entre 0,1 et 1,5")
	@DecimalMax(value = "1.5", message = "Le Kc doit être compris entre 0,1 et 1,5")
	private Double kc;

	@NotNull(message = "La surface est obligatoire")
	@Positive(message = "La surface doit être positive")
	private Double surfaceHa;

	@NotNull(message = "La latitude est obligatoire")
	@DecimalMin(value = "-90", message = "Latitude invalide")
	@DecimalMax(value = "90", message = "Latitude invalide")
	private Double latitude;

	@NotNull(message = "Le système d'irrigation est obligatoire")
	@Enumerated(EnumType.STRING)
	private SystemeIrrigation systemeIrrigation;

	public ParametresParcelle versParametres() {
		double kcEffectif = kc != null ? kc : culture.getKc();
		return new ParametresParcelle(culture.getLibelle(), kcEffectif, surfaceHa, latitude,
				systemeIrrigation.getLibelle(), systemeIrrigation.getEfficience());
	}
}
