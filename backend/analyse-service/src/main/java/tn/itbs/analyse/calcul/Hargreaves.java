package tn.itbs.analyse.calcul;

/**
 * Estimation de l'évapotranspiration de référence ET0 par la méthode de Hargreaves
 * (FAO-56, équations 21 à 25 et 52), à partir des seules températures min et max.
 */
public final class Hargreaves {

	// Constante solaire (MJ m-2 min-1)
	private static final double GSC = 0.0820;

	private Hargreaves() {
	}

	/**
	 * Rayonnement extraterrestre Ra (MJ m-2 j-1), FAO-56 équation 21.
	 *
	 * @param latitudeDeg latitude en degrés (positive au nord, négative au sud)
	 * @param jourAnnee   numéro du jour dans l'année (1 à 366)
	 */
	public static double rayonnementExtraterrestre(double latitudeDeg, int jourAnnee) {
		double phi = Math.toRadians(latitudeDeg);
		double dr = 1 + 0.033 * Math.cos(2 * Math.PI * jourAnnee / 365.0);          // distance Terre-Soleil
		double delta = 0.409 * Math.sin(2 * Math.PI * jourAnnee / 365.0 - 1.39);     // déclinaison solaire
		double x = Math.max(-1, Math.min(1, -Math.tan(phi) * Math.tan(delta)));
		double ws = Math.acos(x);                                                      // angle horaire au coucher
		return (24 * 60 / Math.PI) * GSC * dr
				* (ws * Math.sin(phi) * Math.sin(delta) + Math.cos(phi) * Math.cos(delta) * Math.sin(ws));
	}

	/**
	 * ET0 de Hargreaves (mm/j), FAO-56 équation 52 :
	 * ET0 = 0,0023 x (Tmoy + 17,8) x racine(Tmax - Tmin) x 0,408 x Ra
	 */
	public static double et0(double temperatureMin, double temperatureMax, double latitudeDeg, int jourAnnee) {
		double ra = rayonnementExtraterrestre(latitudeDeg, jourAnnee);
		double tMoy = (temperatureMin + temperatureMax) / 2.0;
		return 0.0023 * (tMoy + 17.8) * Math.sqrt(temperatureMax - temperatureMin) * 0.408 * ra;
	}
}
