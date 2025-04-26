package com.ourahma.hospital.repositories;

import com.ourahma.hospital.entities.Medcin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedcinRepository extends JpaRepository<Medcin, Long> {
}
