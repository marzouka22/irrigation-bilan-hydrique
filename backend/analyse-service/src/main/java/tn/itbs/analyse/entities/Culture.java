package tn.itbs.analyse.entities;

/**
 * Cultures courantes du Cap Bon et coefficient cultural Kc en pleine saison
 * (valeurs de référence FAO-56, tableau 12). Le Kc peut être ajusté par parcelle.
 */
public enum Culture {

	OLIVIER("Olivier", 0.70),
	AGRUMES("Agrumes", 0.65),
	TOMATE("Tomate", 1.15),
	POMME_DE_TERRE("Pomme de terre", 1.15),
	FRAISE("Fraise", 0.85),
	VIGNE("Vigne", 0.70);

	private final String libelle;
	private final double kc;

	Culture(String libelle, double kc) {
		this.libelle = libelle;
		this.kc = kc;
	}

	public String getLibelle() {
		return libelle;
	}

	public double getKc() {
		return kc;
	}
}
