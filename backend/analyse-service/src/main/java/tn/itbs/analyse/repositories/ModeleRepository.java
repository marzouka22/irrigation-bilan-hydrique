package tn.itbs.analyse.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.itbs.analyse.entities.Modele;

public interface ModeleRepository extends JpaRepository<Modele, Long> {

	Optional<Modele> findByVersion(String version);

	Optional<Modele> findFirstByParDefautTrue();

	List<Modele> findAllByOrderByVersionAsc();
}
