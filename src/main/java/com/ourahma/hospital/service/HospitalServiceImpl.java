package com.ourahma.hospital.service;

import com.ourahma.hospital.entities.Consultation;
import com.ourahma.hospital.entities.Medcin;
import com.ourahma.hospital.entities.Patient;
import com.ourahma.hospital.entities.RendezVous;
import com.ourahma.hospital.repositories.ConsultationRepository;
import com.ourahma.hospital.repositories.MedcinRepository;
import com.ourahma.hospital.repositories.PatientRepository;
import com.ourahma.hospital.repositories.RendezVousRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class HospitalServiceImpl implements  IHospitalService{

    private PatientRepository patientRepository;
    private MedcinRepository medcinRepository;
    private ConsultationRepository consultationRepository;
    private RendezVousRepository rendezVousRepository;

    public HospitalServiceImpl(PatientRepository patientRepository, MedcinRepository medcinRepository, ConsultationRepository consultationRepository, RendezVousRepository rendezVousRepository) {
        this.patientRepository = patientRepository;
        this.medcinRepository = medcinRepository;
        this.consultationRepository = consultationRepository;
        this.rendezVousRepository = rendezVousRepository;
    }

    @Override
    public Patient savePatient(Patient patient) {
        return this.patientRepository.save(patient);
    }

    @Override
    public Medcin saveMedcin(Medcin medcin) {
        return this.medcinRepository.save(medcin);
    }

    @Override
    public RendezVous saveRDV(RendezVous rendezVous) {
        rendezVous.setId(UUID.randomUUID().toString());
        return this.rendezVousRepository.save(rendezVous);
    }

    @Override
    public Consultation saveConsultation(Consultation consultation) {
        return this.consultationRepository.save(consultation);
    }
}
