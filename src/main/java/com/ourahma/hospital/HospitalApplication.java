package com.ourahma.hospital;

import com.ourahma.hospital.entities.*;
import com.ourahma.hospital.repositories.ConsultationRepository;
import com.ourahma.hospital.repositories.MedcinRepository;
import com.ourahma.hospital.repositories.PatientRepository;
import com.ourahma.hospital.repositories.RendezVousRepository;
import com.ourahma.hospital.service.IHospitalService;


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
	CommandLineRunner start(IHospitalService iHospitalService,
							PatientRepository patientRepository,
							RendezVousRepository rendezVousRepository,
							MedcinRepository medcinRepository) {
		return args -> {
			// Tester la fontionnalité d'ajout des patients
			Stream.of("Mohamed","Hassan","Najat")
					.forEach(name->{
						Patient p = new Patient();
						p.setNom(name);
						p.setMalade(false);
						p.setDateNaissance(new Date());
						p.setScore((int)(Math.random()*100));
						iHospitalService.savePatient(p);
					});
			// Tester la fontionnalité d'ajout des medcins
			Stream.of("aymane","Hanane","Yassmine")
					.forEach(name->{
						Medcin m = new Medcin();
						m.setEmail(name+"@gmail.com");
						m.setNom(name);
						m.setSpecialite(Math.random()>0.5?"Cardio":"Dentitste");
						iHospitalService.saveMedcin(m);
					});
			// Consulter tous les patients
			List<Patient> patients = patientRepository.findAll();
			patients.forEach(p ->{
				System.out.println("Patient : "+ p.getId() + " ==> " +p.getNom());
			});
			// consulter un patient
			// consulter le patient avec id 1
			Patient patient = patientRepository.findById(2L).orElse(null);
			System.out.println("Patient avec id 1 : " + patient.getId() +" ==> "+ patient.getNom() );
			
			// chercher les patients avec le nom égal a Mohamed
			Patient patient1 = patientRepository.findByNom("Mohamed");
			System.out.println("Patient avec le nom Mohamed : "+patient.getId() +" ==> "+ patient.getNom());

			// mettre à jour un patient
			patient1.setMalade(true);
			patient1.setScore(100);
			patientRepository.save(patient1);

			// supprimer un patient
			patientRepository.delete(patient);
			// chercher tous les patients
			List<Patient> pp = patientRepository.findAll();
			System.out.println("Les patients après suppression : ");
			pp.forEach(p ->{
				System.out.println("Patient : "+ p.getId() + " ==> " +p.getNom());
			});

			// chercher un medcin avec le nom égal a Yassmine
			Medcin medcin = medcinRepository.findByNom("Yassmine");

			System.out.println("Medcin avec le nom Yassmine : "+medcin.getNom());

			// ajouter un rendez-vous
			RendezVous rendezVous=new RendezVous();
			rendezVous.setDate(new Date());
			rendezVous.setStatus(StatusRDV.PENDING);
			rendezVous.setMedcin(medcin);
			Patient patient2 = patientRepository.findById(2L).orElse(null);
			rendezVous.setPatient(patient2);
			// sauvegarder le rendez-vous
			RendezVous savedRDV = iHospitalService.saveRDV(rendezVous);
			System.out.println("L'ID de rendez vous sauvegaurdé "+savedRDV.getId());

			
			// chercher le premier rendez vous
			RendezVous rendezVous1=rendezVousRepository.findAll().get(0);
			// Créer la consulation à partir du rendez-vous
			Consultation consultation=new Consultation();
			consultation.setDateConsultation(rendezVous1.getDate());
			consultation.setRendezVous(rendezVous1);
			consultation.setRapport("Rapport de la consultation .....");
			// sauvegarder la consultation
			iHospitalService.saveConsultation(consultation);

		};
	}
}
