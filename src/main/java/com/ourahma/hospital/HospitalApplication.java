package com.ourahma.hospital;

import com.ourahma.hospital.entities.Medcin;
import com.ourahma.hospital.entities.Patient;
import com.ourahma.hospital.entities.RendezVous;
import com.ourahma.hospital.entities.StatusRDV;
import com.ourahma.hospital.repositories.MedcinRepository;
import com.ourahma.hospital.repositories.PatientRepository;
import com.ourahma.hospital.repositories.RendezVousRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class HospitalApplication {

	public static void main(String[] args) {
		SpringApplication.run(HospitalApplication.class, args);
	}
	@Bean
	CommandLineRunner start(PatientRepository patientRepository,
							MedcinRepository medcinRepository,
							RendezVousRepository rendezVousRepository) {
		return args -> {
			Stream.of("Mohamed","Hassan","Najat")
					.forEach(name->{
						Patient p = new Patient();
						p.setNom(name);
						p.setMalade(false);
						p.setDateNaissance(new Date());
						patientRepository.save(p);
					});
			Stream.of("aymane","Hanane","Yassmine")
					.forEach(name->{
						Medcin m = new Medcin();
						m.setEmail(name+"@gmail.com");
						m.setNom(name);
						m.setSpecialite(Math.random()>0.5?"Cardio":"Dentitste");
						medcinRepository.save(m);
					});
			Patient patient = patientRepository.findById(1L).orElse(null);
			Patient patient1 = patientRepository.findByNom("Mohamed");

			Medcin medcin = medcinRepository.findByNom("Yassmine");

			RendezVous rendezVous=new RendezVous();
			rendezVous.setDate(new Date());
			rendezVous.setStatus(StatusRDV.PENDING);
			rendezVous.setMedcin(medcin);
			rendezVous.setPatient(patient);
			rendezVousRepository.save(rendezVous);
		};
	}
}
