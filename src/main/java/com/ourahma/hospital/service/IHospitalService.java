package com.ourahma.hospital.service;

import com.ourahma.hospital.entities.Consultation;
import com.ourahma.hospital.entities.Medcin;
import com.ourahma.hospital.entities.Patient;
import com.ourahma.hospital.entities.RendezVous;

public interface IHospitalService {
    public Patient savePatient(Patient patient);
    public Medcin saveMedcin(Medcin medcin);
    public RendezVous saveRDV(RendezVous rendezVous);
    public Consultation saveConsultation(Consultation consultation);
}
