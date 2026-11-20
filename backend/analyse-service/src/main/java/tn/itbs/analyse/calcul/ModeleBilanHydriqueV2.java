package tn.itbs.analyse.calcul;

import java.util.Locale;

/**
 * Modèle BH v2.0 (affiné) :
 *  - ET0 estimée par Hargreaves à partir de Tmin / Tmax et de la latitude de la parcelle ;
 *  - pluie efficace : une pluie inférieure à 5 mm est considérée comme évaporée (0 mm utile),
 *    au-delà 80 % de la pluie est retenue ;
 *  - dose brute = dose nette / efficience du système d'irrigation.
 */
public class ModeleBilanHydriqueV2 implements ModeleCalcul {

	public static final String VERSION = "2.0";
	public static final double SEUIL_PLUIE_MM = 5.0;
	public static final double TAUX_PLUIE_EFFICACE = 0.8;

	@Override
	public String version() {
		return VERSION;
	}

	public static double pluieEfficace(double pluie) {
		return pluie < SEUIL_PLUIE_MM ? 0.0 : TAUX_PLUIE_EFFICACE * pluie;
	}

	@Override
	public ResultatCalcul calculer(ParametresParcelle p, DonneesClimatiques d) {
		if (d.temperatureMin() == null || d.temperatureMax() == null) {
			throw new DonneesInsuffisantesException(
					"Le modèle BH " + VERSION + " nécessite les températures minimale et maximale (capteur TEMPERATURE)");
		}
		if (d.temperatureMax() < d.temperatureMin()) {
			throw new DonneesInsuffisantesException("Température maximale inférieure à la température minimale");
		}
		int jour = d.dateOuAujourdhui().getDayOfYear();
		double ra = Hargreaves.rayonnementExtraterrestre(p.latitude(), jour);
		double et0 = Hargreaves.et0(d.temperatureMin(), d.temperatureMax(), p.latitude(), jour);

		boolean pluieConnue = d.pluie() != null;
		double pluie = pluieConnue ? d.pluie() : 0.0;
		double pluieEff = pluieEfficace(pluie);

		double etc = p.kc() * et0;
		double doseNette = Math.max(0, etc - pluieEff);
		double doseBrute = doseNette / p.efficience();
		double m3 = ModeleCalcul.mmEnM3(doseBrute, p.surfaceHa());

		String regle = pluie < SEUIL_PLUIE_MM
				? String.format(Locale.FRANCE, "inférieure à %.0f mm, donc considérée comme évaporée : 0,00 mm utile", SEUIL_PLUIE_MM)
				: String.format(Locale.FRANCE, "efficace à %.0f %% : %.2f mm utiles", TAUX_PLUIE_EFFICACE * 100, pluieEff);

		String justification = String.format(Locale.FRANCE,
				"Modèle BH %s (Hargreaves). Culture : %s (Kc = %.2f). "
				+ "Jour %d, latitude %.2f° : Ra = %.2f MJ/m²/j. "
				+ "Tmin = %.1f °C, Tmax = %.1f °C : ET0 Hargreaves = %.2f mm/j, donc ETc = %.2f x %.2f = %.2f mm. "
				+ "Pluie = %.2f mm%s, %s. "
				+ "Dose nette = max(0 ; %.2f - %.2f) = %.2f mm. "
				+ "Efficience %s = %.0f %% : dose brute = %.2f / %.2f = %.2f mm. "
				+ "Surface %.2f ha : %.2f mm x %.2f ha x 10 = %.2f m3.",
				VERSION, p.culture(), p.kc(),
				jour, p.latitude(), ra,
				d.temperatureMin(), d.temperatureMax(), et0, p.kc(), et0, etc,
				pluie, pluieConnue ? "" : " (aucune mesure de pluie : 0 mm supposé)", regle,
				etc, pluieEff, doseNette,
				p.systeme(), p.efficience() * 100, doseNette, p.efficience(), doseBrute,
				p.surfaceHa(), doseBrute, p.surfaceHa(), m3);

		return new ResultatCalcul(VERSION,
				ModeleCalcul.arrondi(et0), ModeleCalcul.arrondi(etc), ModeleCalcul.arrondi(pluieEff),
				ModeleCalcul.arrondi(doseNette), ModeleCalcul.arrondi(doseBrute), ModeleCalcul.arrondi(m3),
				justification);
	}
}
