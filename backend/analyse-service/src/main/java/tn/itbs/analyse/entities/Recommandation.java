package tn.itbs.analyse.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Recommandation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long parcelleId;

	// Volume à apporter sur toute la parcelle (m3)
	private Double quantiteEau;

	// Dose brute (mm) correspondante
	private Double doseMm;

	@Column(length = 2000)
	private String justification;

	private LocalDateTime dateCalcul;

	private Long modeleId;

	private String versionModele;

	@Enumerated(EnumType.STRING)
	private Fiabilite fiabilite;
}
