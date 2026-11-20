package tn.itbs.analyse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.itbs.analyse.entities.Parcelle;

public interface ParcelleRepository extends JpaRepository<Parcelle, Long> {
}
