package tn.itbs.analyse.entities;

// Efficience typique de chaque système d'irrigation (part de l'eau réellement utilisée par la plante)
public enum SystemeIrrigation {

	GOUTTE_A_GOUTTE("goutte-à-goutte", 0.90),
	ASPERSION("aspersion", 0.75),
	GRAVITAIRE("gravitaire", 0.60);

	private final String libelle;
	private final double efficience;

	SystemeIrrigation(String libelle, double efficience) {
		this.libelle = libelle;
		this.efficience = efficience;
	}

	public String getLibelle() {
		return libelle;
	}

	public double getEfficience() {
		return efficience;
	}
}
