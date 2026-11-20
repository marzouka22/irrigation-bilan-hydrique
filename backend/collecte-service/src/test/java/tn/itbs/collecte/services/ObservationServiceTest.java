package tn.itbs.collecte.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import tn.itbs.collecte.dtos.ObservationDetails;
import tn.itbs.collecte.entities.Capteur;
import tn.itbs.collecte.entities.Observation;
import tn.itbs.collecte.entities.TypeCapteur;
import tn.itbs.collecte.repositories.CapteurRepository;
import tn.itbs.collecte.repositories.ObservationRepository;

@ExtendWith(MockitoExtension.class)
class ObservationServiceTest {

	@Mock
	private ObservationRepository observationRepo;
	@Mock
	private CapteurRepository capteurRepo;
	@Mock
	private CapteurService capteurService;
	@InjectMocks
	private ObservationService observationService;

	@Test
	void ajouter_capteurInconnu_propage404() {
		when(capteurService.chercherParId(99L))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Capteur 99 introuvable"));

		assertThatThrownBy(() -> observationService.ajouter(new Observation(null, 99L, 3.0, null, null)))
				.isInstanceOf(ResponseStatusException.class);
		verify(observationRepo, never()).save(any());
	}

	@Test
	void ajouter_valeurHorsPlage_leve400() {
		when(capteurService.chercherParId(1L)).thenReturn(new Capteur(1L, TypeCapteur.PLUIE, 1L, "Nord"));

		assertThatThrownBy(() -> observationService.ajouter(new Observation(null, 1L, -2.0, null, null)))
				.isInstanceOf(ResponseStatusException.class)
				.satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
						.isEqualTo(HttpStatus.BAD_REQUEST));
		verify(observationRepo, never()).save(any());
	}

	@Test
	void ajouter_remplitUniteDuTypeEtDateCourante() {
		when(capteurService.chercherParId(1L)).thenReturn(new Capteur(1L, TypeCapteur.ET0, 1L, "Station"));
		when(observationRepo.save(any(Observation.class))).thenAnswer(inv -> {
			Observation o = inv.getArgument(0);
			o.setId(10L);
			return o;
		});
		Observation o = new Observation(null, 1L, 5.4, "km", null); // unité fausse envoyée par le client

		var rep = observationService.ajouter(o);

		assertThat(rep.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(o.getUnite()).isEqualTo("mm/j");
		assertThat(o.getDate()).isNotNull().isBeforeOrEqualTo(LocalDateTime.now());
	}

	@Test
	void dernieresParParcelle_sansCapteur_retourneListeVide() {
		when(capteurRepo.findByParcelleId(7L)).thenReturn(List.of());

		assertThat(observationService.dernieresParParcelle(7L, 24)).isEmpty();
		verifyNoInteractions(observationRepo);
	}

	@Test
	void dernieresParParcelle_heuresInvalides_leve400() {
		assertThatThrownBy(() -> observationService.dernieresParParcelle(1L, 0))
				.isInstanceOf(ResponseStatusException.class);
		assertThatThrownBy(() -> observationService.dernieresParParcelle(1L, ObservationService.HEURES_MAX + 1))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	void dernieresParParcelle_ajouteTypeEtParcelleDuCapteur() {
		Capteur pluie = new Capteur(3L, TypeCapteur.PLUIE, 1L, "Nord");
		LocalDateTime d = LocalDateTime.now().minusHours(2);
		when(capteurRepo.findByParcelleId(1L)).thenReturn(List.of(pluie));
		when(observationRepo.findByCapteurIdInAndDateAfterOrderByDateDesc(anyCollection(), any(LocalDateTime.class)))
				.thenReturn(List.of(new Observation(20L, 3L, 4.5, "mm", d)));

		List<ObservationDetails> res = observationService.dernieresParParcelle(1L, 24);

		assertThat(res).hasSize(1);
		assertThat(res.get(0).type()).isEqualTo(TypeCapteur.PLUIE);
		assertThat(res.get(0).parcelleId()).isEqualTo(1L);
		assertThat(res.get(0).valeur()).isEqualTo(4.5);
	}
}
