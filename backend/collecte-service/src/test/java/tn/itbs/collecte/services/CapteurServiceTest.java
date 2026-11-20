package tn.itbs.collecte.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import tn.itbs.collecte.entities.Capteur;
import tn.itbs.collecte.entities.TypeCapteur;
import tn.itbs.collecte.repositories.CapteurRepository;
import tn.itbs.collecte.repositories.ObservationRepository;

@ExtendWith(MockitoExtension.class)
class CapteurServiceTest {

	@Mock
	private CapteurRepository capteurRepo;
	@Mock
	private ObservationRepository observationRepo;
	@InjectMocks
	private CapteurService capteurService;

	@Test
	void ajouter_ignoreIdFourniEtRetourne201() {
		Capteur c = new Capteur(55L, TypeCapteur.PLUIE, 1L, "Parcelle Nord");
		when(capteurRepo.save(any(Capteur.class))).thenAnswer(inv -> {
			Capteur arg = inv.getArgument(0);
			assertThat(arg.getId()).isNull(); // l'id envoyé par le client est ignoré
			arg.setId(1L);
			return arg;
		});

		ResponseEntity<String> rep = capteurService.ajouter(c);

		assertThat(rep.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(rep.getHeaders().getLocation()).hasToString("/capteurs/1");
	}

	@Test
	void chercherParId_inexistant_leve404() {
		when(capteurRepo.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> capteurService.chercherParId(99L))
				.isInstanceOf(ResponseStatusException.class)
				.satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
						.isEqualTo(HttpStatus.NOT_FOUND));
	}

	@Test
	void modifier_inexistant_leve404() {
		when(capteurRepo.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> capteurService.modifier(99L, new Capteur()))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	void supprimer_avecObservations_leve409EtNeSupprimePas() {
		Capteur c = new Capteur(1L, TypeCapteur.ET0, 1L, "Station météo");
		when(capteurRepo.findById(1L)).thenReturn(Optional.of(c));
		when(observationRepo.existsByCapteurId(1L)).thenReturn(true);

		assertThatThrownBy(() -> capteurService.supprimer(1L))
				.isInstanceOf(ResponseStatusException.class)
				.satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
						.isEqualTo(HttpStatus.CONFLICT));
		verify(capteurRepo, never()).delete(any());
	}

	@Test
	void supprimer_sansObservations_supprime() {
		Capteur c = new Capteur(1L, TypeCapteur.ET0, 1L, "Station météo");
		when(capteurRepo.findById(1L)).thenReturn(Optional.of(c));
		when(observationRepo.existsByCapteurId(1L)).thenReturn(false);

		ResponseEntity<String> rep = capteurService.supprimer(1L);

		assertThat(rep.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(capteurRepo).delete(c);
	}
}
