package com.ourahma.hospital.entities;

import java.util.Collection;

public class Medcin {
    private Long id;
    private String nom;
    private String email;
    private String specialite;
    private Collection<RendezVous> rendezVous;

}
