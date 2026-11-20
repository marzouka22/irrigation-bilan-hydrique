package tn.itbs.analyse.calcul;

// Levée quand les données disponibles ne permettent pas d'appliquer un modèle
public class DonneesInsuffisantesException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DonneesInsuffisantesException(String message) {
		super(message);
	}
}
