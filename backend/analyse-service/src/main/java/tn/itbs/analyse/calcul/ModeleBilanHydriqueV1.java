package tn.itbs.analyse.calcul;

import java.util.Locale;

/**
 * Modèle BH v1.0 (simple) :
 * dose = max(0, Kc x ET0 mesurée - pluie)
 * Hypothèses : toute la pluie est utile, pas de pertes du système d'irrigation.
 */
public class ModeleBilanHydriqueV1 implements ModeleCalcul {

	public static final String VERSION = "1.0";

	@Override
	public String version() {
		return VERSION;
	}

	@Override
	public ResultatCalcul calculer(ParametresParcelle p, DonneesClimatiques d) {
		if (d.et0() == null) {
			throw new DonneesInsuffisantesException(
					"Le modèle BH " + VERSION + " nécessite une ET0 mesurée (capteur de type ET0)");
		}
		boolean pluieConnue = d.pluie() != null;
		double pluie = pluieConnue ? d.pluie() : 0.0;

		double etc = p.kc() * d.et0();
		double doseNette = Math.max(0, etc - pluie);
		double doseBrute = doseNette; // v1 : pas de prise en compte des pertes
		double m3 = ModeleCalcul.mmEnM3(doseBrute, p.surfaceHa());

		String justification = String.format(Locale.FRANCE,
				"Modèle BH %s (simple). Culture : %s (Kc = %.2f). "
				+ "ET0 mesurée = %.2f mm/j, donc ETc = %.2f x %.2f = %.2f mm. "
				+ "Pluie = %.2f mm%s, considérée entièrement utile. "
				+ "Dose = max(0 ; %.2f - %.2f) = %.2f mm. "
				+ "Surface %.2f ha : %.2f mm x %.2f ha x 10 = %.2f m3.",
				VERSION, p.culture(), p.kc(),
				d.et0(), p.kc(), d.et0(), etc,
				pluie, pluieConnue ? "" : " (aucune mesure de pluie : 0 mm supposé)",
				etc, pluie, doseNette,
				p.surfaceHa(), doseBrute, p.surfaceHa(), m3);

		return new ResultatCalcul(VERSION,
				ModeleCalcul.arrondi(d.et0()), ModeleCalcul.arrondi(etc), ModeleCalcul.arrondi(pluie),
				ModeleCalcul.arrondi(doseNette), ModeleCalcul.arrondi(doseBrute), ModeleCalcul.arrondi(m3),
				justification);
	}
}
