package tn.itbs.collecte.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.itbs.collecte.entities.Capteur;

public interface CapteurRepository extends JpaRepository<Capteur, Long> {

	List<Capteur> findByParcelleId(Long parcelleId);
}
