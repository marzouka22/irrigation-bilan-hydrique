package tn.itbs.collecte.entities;

/**
 * Types de capteurs utiles au bilan hydrique.
 * Chaque type fixe son unité et la plage de valeurs physiquement acceptables.
 */
public enum TypeCapteur {

	TEMPERATURE("°C", -30, 60),
	PLUIE("mm", 0, 500),
	ET0("mm/j", 0, 20),          // évapotranspiration de référence (station météo)
	HUMIDITE_SOL("%", 0, 100);

	private final String unite;
	private final double min;
	private final double max;

	TypeCapteur(String unite, double min, double max) {
		this.unite = unite;
		this.min = min;
		this.max = max;
	}

	public String getUnite() {
		return unite;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public boolean valeurValide(double valeur) {
		return valeur >= min && valeur <= max;
	}
}
