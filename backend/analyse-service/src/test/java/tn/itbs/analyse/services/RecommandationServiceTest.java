package tn.itbs.analyse.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import tn.itbs.analyse.calcul.DonneesClimatiques;
import tn.itbs.analyse.calcul.ModeleBilanHydriqueV1;
import tn.itbs.analyse.calcul.ModeleBilanHydriqueV2;
import tn.itbs.analyse.config.BilanHydriqueCalculateur;
import tn.itbs.analyse.dtos.DemandeCalcul;
import tn.itbs.analyse.dtos.DemandeComparaison;
import tn.itbs.analyse.dtos.ResultatComparaison;
import tn.itbs.analyse.entities.Culture;
import tn.itbs.analyse.entities.Fiabilite;
import tn.itbs.analyse.entities.Modele;
import tn.itbs.analyse.entities.Parcelle;
import tn.itbs.analyse.entities.Recommandation;
import tn.itbs.analyse.entities.SystemeIrrigation;
import tn.itbs.analyse.repositories.RecommandationRepository;

@ExtendWith(MockitoExtension.class)
class RecommandationServiceTest {

	@Mock
	private RecommandationRepository recommandationRepo;
	@Mock
	private ParcelleService parcelleService;
	@Mock
	private ModeleService modeleService;
	@Mock
	private BilanHydriqueCalculateur calculateur;
	@InjectMocks
	private RecommandationService recommandationService;

	private final Parcelle parcelle = new Parcelle(1L, "Oliveraie Menzel Bouzelfa", Culture.OLIVIER, 0.70, 2.0, 36.7,
			SystemeIrrigation.GOUTTE_A_GOUTTE);
	private final Modele v1 = new Modele(1L, "BILAN_HYDRIQUE", "1.0", LocalDate.of(2026, 11, 20), 0.70, "v1", false);
	private final Modele v2 = new Modele(2L, "BILAN_HYDRIQUE", "2.0", LocalDate.of(2026, 11, 20), 0.85, "v2", true);
	private final DonneesClimatiques juillet = new DonneesClimatiques(null, 0.0, 22.0, 33.0, LocalDate.of(2026, 7, 15));

	@Test
	void calculer_sansModele_utiliseLeModeleParDefautEtEnregistre() {
		when(parcelleService.chercherParId(1L)).thenReturn(parcelle);
		when(modeleService.modeleParDefaut()).thenReturn(v2);
		when(calculateur.pourVersion("2.0")).thenReturn(new ModeleBilanHydriqueV2());
		when(recommandationRepo.save(any(Recommandation.class))).thenAnswer(inv -> inv.getArgument(0));

		Recommandation r = recommandationService.calculer(new DemandeCalcul(1L, null, juillet));

		assertThat(r.getParcelleId()).isEqualTo(1L);
		assertThat(r.getModeleId()).isEqualTo(2L);
		assertThat(r.getVersionModele()).isEqualTo("2.0");
		assertThat(r.getFiabilite()).isEqualTo(Fiabilite.NORMALE);
		assertThat(r.getQuantiteEau()).isPositive();
		assertThat(r.getJustification()).contains("Hargreaves");
		assertThat(r.getDateCalcul()).isNotNull();
	}

	@Test
	void calculer_temperaturesIncoherentes_leve400SansEnregistrer() {
		when(parcelleService.chercherParId(1L)).thenReturn(parcelle);
		when(modeleService.modeleParDefaut()).thenReturn(v2);
		DonneesClimatiques incoherentes = new DonneesClimatiques(null, 0.0, 30.0, 20.0, null);

		assertThatThrownBy(() -> recommandationService.calculer(new DemandeCalcul(1L, null, incoherentes)))
				.isInstanceOf(ResponseStatusException.class)
				.satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
						.isEqualTo(HttpStatus.BAD_REQUEST));
		verify(recommandationRepo, never()).save(any());
	}

	@Test
	void comparer_modeleInapplicable_nEmpechePasLesAutres() {
		when(parcelleService.chercherParId(1L)).thenReturn(parcelle);
		when(modeleService.lister()).thenReturn(List.of(v1, v2));
		when(calculateur.pourVersion("1.0")).thenReturn(new ModeleBilanHydriqueV1());
		when(calculateur.pourVersion("2.0")).thenReturn(new ModeleBilanHydriqueV2());

		// Pas d'ET0 mesurée : v1 inapplicable, v2 calcule avec les températures
		List<ResultatComparaison> res = recommandationService.comparer(new DemandeComparaison(1L, juillet));

		assertThat(res).hasSize(2);
		assertThat(res.get(0).version()).isEqualTo("1.0");
		assertThat(res.get(0).resultat()).isNull();
		assertThat(res.get(0).erreur()).contains("ET0");
		assertThat(res.get(1).resultat()).isNotNull();
		assertThat(res.get(1).erreur()).isNull();
		verify(recommandationRepo, never()).save(any()); // la comparaison n'enregistre rien
	}

	@Test
	void parParcelle_parcelleInconnue_propage404() {
		when(parcelleService.chercherParId(99L))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcelle 99 introuvable"));

		assertThatThrownBy(() -> recommandationService.parParcelle(99L))
				.isInstanceOf(ResponseStatusException.class);
	}
}
