# Hospital
## Rapport de TP 2
* Nom : OURAHMA.
* Prènom : Maroua.
* Filière : Master en Intelligence artificielle et sciences de données
* Universitè : Facultès des sciences Universitè Moulay Ismaol Meknès.

## **1- Introduction**

Ce rapport présente Hibernate JPA, une implémentation de la spécification Java Persistence API, utilisée pour la gestion des données dans les applications Java. À travers un exemple concret d’un système de gestion hospitalier, il explique comment Hibernate permet de simplifier l’interaction avec la base de données, en assurant le mapping objet-relationnel et la persistance des entités telles que les patients, médecins et rendez-vous.

## **2- Enoncé**

Un hôpital souhaite automatiser son processus de gestion des patients, des rendez-vous et des consultations. Chaque patient peut avoir plusieurs rendez-vous avec différents médecins, chaque médecin peut gérer plusieurs rendez-vous, et chaque rendez-vous peut être associé à une seule consultation. 

1. Modélisation des entités :
    - **Patient** : Une entité représentant un patient, qui peut avoir plusieurs rendez-vous.
    - **Médecin** : Une entité représentant un médecin, qui peut gérer plusieurs rendez-vous.
    - **RendezVous** : Une entité représentant un rendez-vous, lié à un patient et un médecin, et pouvant générer une consultation.
    - **Consultation** : Une entité représentant une consultation médicale, liée à un seul rendez-vous.
2. Utilisation des associations :
    - **OneToMany** : Un patient a plusieurs rendez-vous, et un médecin gère plusieurs rendez-vous.
    - **ManyToOne** : Un rendez-vous est lié à un patient et à un médecin.
    - **OneToOne** : Un rendez-vous est lié à une seule consultation.

## **3- Conception**
Le diagramme de classes présenté ci-dessous a été conçu à l'aide de l'outil **Enterprise Architect**. Il modélise les entités principales du système et leurs relations, en mettant en avant les associations OneToMany, ManyToOne et OneToOne utilisées dans la conception de l'application hospitalière.

![Digramme de classe](screenshots/classe.png)

### Relations entre les entités

- `Patient` → `RendezVous` : Relation **OneToMany**  
  (Un patient peut avoir plusieurs rendez-vous)
  
- `Medecin` → `RendezVous` : Relation **OneToMany**  
  (Un médecin peut gérer plusieurs rendez-vous)
  
- `RendezVous` → `Patient`, `RendezVous` → `Medecin` : Relation **ManyToOne**  
  (Chaque rendez-vous est lié à un seul patient et un seul médecin)
  
- `RendezVous` → `Consultation` : Relation **OneToOne**  
  (Un rendez-vous génère une seule consultation)
## **4- Code source**

### **Entities**:
-   Chaque classe du package `entities` correspond à une table en base de données.
1. #### **La classe `Patient`**:

La classe `Patient` est une entité JPA qui représente la table des patients dans la base de données. Elle contient les informations personnelles d’un patient et sa relation avec d'autres entités comme `RendezVous`.

```java
package com.ourahma.hospital.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private Date dateNaissance;
    private boolean malade;
    private int score;
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private Collection<RendezVous> rendezVous;

}


```
- **Explication des annotations JPA**

- `@Entity` : Indique que cette classe est une entité JPA, donc mappée à une table en base de données.

- `@Data` (Lombok) : Génère automatiquement les getters, setters, `toString`, `equals`, etc.

- `@NoArgsConstructor` (Lombok) : Génère un constructeur sans arguments.

- `@AllArgsConstructor` (Lombok) : Génère un constructeur avec tous les champs.

- `@Id` : Marque le champ `id` comme clé primaire de l’entité.

- `@GeneratedValue(strategy = GenerationType.IDENTITY)` : Indique que la valeur de l’`id` est générée automatiquement par la base de données (auto-incrément).

- `@OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)` :  
  - Définit une relation unidirectionnelle de type *OneToMany* entre `Patient` et `RendezVous`.
  - L'attribut `mappedBy` indique que c’est le champ `patient` dans l’entité `RendezVous` qui gère la relation.
  - `fetch = FetchType.LAZY` signifie que les rendez-vous ne seront chargés que si nécessaire (chargement paresseux).

2. #### **La classe `Medcin`**:

Cette classe représente l'entité médecin dans le système hospitalier. Elle est similaire à `Patient`, avec quelques particularités propres à sa structure et son utilisation.

```java
package com.ourahma.hospital.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Medcin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String email;
    private String specialite;
    @OneToMany(mappedBy = "medcin", fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Collection<RendezVous> rendezVous;

}

```
- **Explication des annotations JPA**:
- `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` :  
  - Indique que ce champ ne doit pas être inclus lors de la sérialisation JSON en réponse (lecture), mais reste accessible en écriture.
  - Utile pour éviter les boucles infinies en cas de relations bidirectionnelles entre entités.


- `@OneToMany(mappedBy = "medcin", fetch = FetchType.LAZY)` :  
  - Relation *un médecin peut avoir plusieurs rendez-vous*.
  - Le champ `mappedBy = "medcin"` indique que la gestion de la relation est du côté de `RendezVous`.
  - Chargement paresseux activé (`fetch = FetchType.LAZY`).

3. #### **La classe `Consultation`**

Cette classe représente une consultation médicale liée à un rendez-vous unique. Elle introduit une nouvelle relation de type `@OneToOne`.
```java
package com.ourahma.hospital.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Consultation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date dateConsultation;
    private String rapport;
    @OneToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private RendezVous rendezVous;
}
```
- **Explication des annotations JPA**:
- `@OneToOne` :  
  - Définit une relation *un-à-un* entre `Consultation` et `RendezVous`.
  - Indique qu'une consultation est associée à un seul rendez-vous, et vice-versa.

- `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` :  
  - Empêche la sérialisation du champ en réponse JSON (lecture), mais permet sa lecture lors de la soumission de données (écriture).
  - Utile ici pour éviter les boucles de sérialisation si la relation avec `RendezVous` est bidirectionnelle.

4. #### **L'énumeration `StatusRDV`**:
L'énumération `StatusRDV` définit les différents états possibles d'un rendez-vous médical dans le système hospitalier.
```java
package com.ourahma.hospital.entities;

public enum StatusRDV {
    PENDING,
    CANCELLED,
    DONE,
}

```
- **Rôles et valeurs**:

- **`PENDING`** : Le rendez-vous est en attente de confirmation ou de traitement.
- **`CANCELLED`** : Le rendez-vous a été annulé par le patient ou le médecin.
- **`DONE`** : Le rendez-vous s'est déroulé et a été effectué avec succès.

Cette énumération est utilisée principalement dans l'entité `RendezVous` pour gérer le statut du rendez-vous de manière claire et typée.

5. #### **La classe `RendezVous`**:
La classe `RendezVous` représente un rendez-vous médical entre un patient et un médecin. Elle utilise plusieurs types de relations (`@ManyToOne`, `@OneToOne`) ainsi qu’une énumération pour modéliser son statut.
```java
package com.ourahma.hospital.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class RendezVous {
    @Id
    private String id;
    private Date date;
    @Enumerated(EnumType.STRING)
    private StatusRDV status;
    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Patient patient;
    @ManyToOne
    private Medcin medcin;
    @OneToOne(mappedBy = "rendezVous")
    private Consultation consultation;

}

```
- `@Enumerated(EnumType.STRING)` :  
  - Indique que l’énumération `StatusRDV` est stockée sous forme de chaîne de caractères dans la base de données.
  - Permet une meilleure lisibilité par rapport à l’option `EnumType.ORDINAL`.

- `@ManyToOne` :  
  - Relation *plusieurs rendez-vous peuvent appartenir à un même patient* et *un même médecin*.
  - Utilisée pour les champs `patient` et `medcin`.

- `@OneToOne(mappedBy = "rendezVous")` :  
  - Relation unidirectionnelle avec l’entité `Consultation`.
  - Le côté propriétaire de cette relation est géré par `Consultation` via son champ `rendezVous`.

- `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` :  
  - Appliqué sur le champ `patient` pour éviter sa sérialisation dans les réponses JSON, tout en permettant sa lecture lors de l’écriture (utile pour éviter les boucles infinies).

### Repositories:

Les **repositories** dans une architecture Spring Data JPA jouent un rôle central dans la gestion de la persistance et de la récupération des données. Ils :

- Abstraient les opérations CRUD (Create, Read, Update, Delete) sur les entités.
- Interagissent avec la base de données via JPA/Hibernate.
- Fournissent une interface simple entre les services métier et la couche de données.
- Permettent de bénéficier de méthodes prédéfinies via `JpaRepository`.

1. #### **ConsultationRepository**:
```java
package com.ourahma.hospital.repositories;

import com.ourahma.hospital.entities.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
}

```
Cette interface hérite de `JpaRepository<Consultation, Long>` :
- Le premier paramètre (`Consultation`) indique l’entité gérée par ce repository.
- Le deuxième paramètre (`Long`) représente le type de l’identifiant de l’entité.

#### Fonctionnalités disponibles automatiquement :
- `findAll()` : Récupère toutes les consultations.
- `findById()` : Trouve une consultation par son identifiant.
- `save()` : Sauvegarde ou met à jour une consultation.
- `delete()` : Supprime une consultation.
- `count()` : Retourne le nombre total de consultations.

#### Méthodes personnalisées
Il est possible d'ajouter des méthodes de requêtes basées sur les noms de champs, par exemple :

```java
List<Consultation> findByDate(Date date);
```
2. #### **MedcinRepository**:
L'interface `MedcinRepository` étend `JpaRepository<Medcin, Long>`, ce qui lui permet de bénéficier automatiquement des méthodes CRUD de Spring Data JPA.
```java
package com.ourahma.hospital.repositories;

import com.ourahma.hospital.entities.Medcin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedcinRepository extends JpaRepository<Medcin, Long> {
    Medcin findByNom(String nom);
}

```
Méthode personnalisée
```java
Medcin findByNom(String nom);
```
- Cette méthode permet de rechercher un médecin par son nom.
- Spring Data JPA génère automatiquement l'implémentation de cette méthode en se basant sur le nom du champ (`nom`).

3. #### **PatientRepository**:
L'interface `PatientRepository` aussi étend `JpaRepository<Patient, Long>`, ce qui lui aussi permet de bénéficier automatiquement des méthodes CRUD standards de Spring Data JPA.

```java
package com.ourahma.hospital.repositories;

import com.ourahma.hospital.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findByNom(String name);

}

```
**La méthode personnalisé :**
```java
Patient findByNom(String name);
```
- Cette méthode permet de rechercher un patient par son nom.
- Spring Data JPA génère automatiquement l’implémentation en se basant sur le champ nom de l’entité Patient.

4. #### **RendezVousRepository**:
L'interface `RendezVousRepository` étend `JpaRepository<RendezVous, String>`, ce qui permet de gérer les opérations CRUD sur l’entité `RendezVous`.
```java
package com.ourahma.hospital.repositories;

import com.ourahma.hospital.entities.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RendezVousRepository extends JpaRepository<RendezVous,String> {
}

```
- Le type d’identifiant utilisé ici est `String`, car les IDs des rendez-vous seront générés sous forme de `UUID` , assurant ainsi une meilleure unicité et sécurité des identifiants.


### Service:
Le package `service` contient les classes et interfaces qui implémentent la **logique métier** de l’application.  
Il sert d’intermédiaire entre les contrôleurs (ou autres couches d’entrée) et les repositories, en gérant les opérations sur les données avant leur persistance ou leur retour au client.

---

1. ## **Interface `IHospitalService`**

Cette interface définit les services principaux du système hospitalier, permettant de sauvegarder différentes entités dans la base de données.

```java
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

```
- **Les méthodes**
    - `savePatient(Patient patient)` : Sauvegarde un nouveau patient.
    - `saveMedcin(Medcin medcin)` : Enregistre un nouveau médecin.
    - `saveRDV(RendezVous rendezVous)` : Crée un nouveau rendez-vous.
    - `saveConsultation(Consultation consultation)` : Persiste une consultation médicale.

2. ## Implémentation du service : `HospitalServiceImpl`

```java
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

```
- ** Méthodes de la classe `HospitalServiceImpl`**

    - `savePatient()` : Sauvegarde un objet `Patient` dans la base via `PatientRepository`.
    - `saveMedcin()` : Enregistre un objet `Medcin` en utilisant `MedcinRepository`.
    - `saveRDV()` : Génère un identifiant unique (`UUID`) avant de sauvegarder le rendez-vous via `RendezVousRepository`.
    - `saveConsultation()` : Persiste une consultation médicale à l’aide de `ConsultationRepository`.

- **Annotations importantes**

    - `@Service` : Indique que cette classe est un composant Spring qui contient de la logique métier.
    - `@Transactional` : Garantit que les opérations sur la base de données s’effectuent dans une transaction (rollback automatique en cas d'erreur).




### Web :
Le package `web` contient les contrôleurs Spring (`@RestController`) qui gèrent les requêtes HTTP entrantes.  
Il constitue la couche de présentation de l’application et permet d’exposer des endpoints REST pour interagir avec les données via des API.

1. **Classe `PatientRestController`**

Cette classe est un contrôleur REST qui expose une API pour récupérer la liste des patients depuis la base de données.
```java
package com.ourahma.hospital.web;

import com.ourahma.hospital.entities.Patient;
import com.ourahma.hospital.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PatientRestController {
    @Autowired
    private PatientRepository patientRepository;
    @GetMapping("/patients")
    public List<Patient> patientList(){
        return patientRepository.findAll();
    }
}

```
**Fonctionnalités**

- `@RestController` : Combine `@Controller` et `@ResponseBody`, ce qui permet de renvoyer directement des objets sérialisés en JSON/XML dans les réponses HTTP.
- `@Autowired` : Injecte automatiquement une instance de `PatientRepository`.
- `@GetMapping("/patients")` : Définit une route GET accessible via l’URL `/patients`.
- `patientList()` : Méthode qui retourne la liste de tous les patients stockés en base de données.

    - Cette classe permet ainsi d’interroger facilement les données des patients à travers une API REST.
## **5- Captures écrans**

Ce code est exécuté dans une fonction lambda passée à `CommandLineRunner` , ce qui signifie qu’il s’exécute automatiquement juste après le démarrage de l’application Spring Boot . Cela en fait une solution pratique pour effectuer des tests ou initialiser des données de démonstration.

1. ### **Ajouter des patients**:
Ce bloc de code permet de tester l’ajout de trois patients dans la base de données via une opération effectuée au démarrage de l’application .
```java
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
```
- Il utilise `Stream.of(...)` pour créer un flux contenant les noms des patients à insérer.  
- La méthode `forEach(...)` applique, pour chaque nom, les opérations suivantes :

    - Création d’un nouvel objet `Patient`.
    - Initialisation des attributs du patient (nom, statut médical, date de naissance, score aléatoire).
    - Appel de la méthode `savePatient()` du service `iHospitalService` pour persister l’entité en base de données.

- **Le résultat :** 

Les patients sont insérés dans la Base de données h2: 

![Insertion des patient](screenshots/inserer_patients.png)

2. ### **Consulter tous les patients**
Ce bloc de code permet de tester la consultation de tous les patients dans la base de données.

```java
// Consulter tous les patients
			List<Patient> patients = patientRepository.findAll();
			patients.forEach(p ->{
				System.out.println("Patient : "+p.getId()+ " ==> "+p.getNom());
			});
```
- La méthode `patientRepository.findAll()` récupère toutes les entités Patient persistantes.
- La méthode `forEach(...)` parcourt chaque objet Patient pour afficher son nom via `p.getNom()`.

- **Le résultat :** 

![find all](screenshots/findall.png)

3. ###  **Consulter un patient**:
Ce bloc de code permet de récupérer un patient spécifique en base de données à partir de son identifiant (ID = 1) , puis d'afficher ses informations dans la console.


```java
// consulter le patient avec id 1
    Patient patient = patientRepository.findById(1L).orElse(null);
    System.out.println("Patient avec id 1 : " + patient.getId() +" ==> "+patient.getNom() );
```

- La méthode `findById(1L)` du patientRepository recherche en base de données le patient dont l’ID est égal à 1.
- `.orElse(null)` indique que si aucun patient n’est trouvé, la variable patient sera affectée à null.
Ensuite, le programme affiche l’ID et le nom du patient récupéré.

- **Le résultat**
    - ![Diagramme de classe](screenshots/patient1id.png)
4. ###  **Chercher des patients**
Ce bloc permet de rechercher un patient dans la base de données en utilisant son nom ("Mohamed"), puis d'afficher ses informations.


```java
// chercher les patients avec le nom égal a Mohamed
Patient patient1 = patientRepository.findByNom("Mohamed");
System.out.println("Patient avec le nom Mohamed : "+patient.getId() +patient.getNom());
```
- La méthode `findByNom("Mohamed")` du patientRepository récupère le patient dont le nom correspond à "Mohamed".
- Ensuite, le programme affiche l’ID et le nom du patient trouvé.

- **Le résultat**:
    - ![photo](screenshots/patientavecnommohamed.png)

5. ###  **Mettre à jour un patient**

```java
// mettre à jour un patient
patient1.setMalade(true);
patient1.setScore(100);
patientRepository.save(patient1);
```
- `setMalade(true)` : indique que le patient est malade.
- `setScore(100)` : attribue un score santé maximal (ou arbitraire selon le contexte métier).
- La méthode `save(...) ` du repository est ensuite appelée pour persister ces modifications dans la base de données.



6. ###  **supprimer un patient**

```java
// supprimer un patient
patientRepository.delete(patient);
// chercher tous les patients
List<Patient> pp = patientRepository.findAll();
System.out.println("Les patients après suppression : ");
pp.forEach(p ->{
    System.out.println("Patient : "+ p.getId() + " ==> " +p.getNom());
});
```
- **Le résultat**:
    - ![](screenshots/supp.png)


7. ###  **Migration vers MySQL**

La migration de l'application vers une base de données MySQL s'effectue simplement en modifiant les propriétés définies dans le fichier `application.properties`. Il suffit de remplacer les paramètres de configuration relatifs à la base H2 par ceux correspondant à MySQL, tels que l’URL de connexion, le nom d’utilisateur, le mot de passe et le dialecte JPA.

```
spring.application.name=hospital
server.port=8086
spring.datasource.url=jdbc:mysql:localhost:3306/hospital_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MariaDBDialect
```
- Après lancer le `WAMP` tous les données sont migrés vers une base de données MySQL:

![](screenshots/phpmyadmin.png)

- Le code est par la suite executé sans problème.

![](screenshots/executionmysql.png)

## **6- Conclusion**

Ce projet a permis d’explorer les fonctionnalités offertes par JPA : ajout, consultation, mise à jour et suppression des données. La migration vers MySQL s’est effectuée facilement en modifiant simplement la configuration dans `application.properties`, démontrant ainsi la flexibilité de Spring Data JPA face au changement de SGBD.

## **7- Auteur**

- **Nom:**  OURAHMA
- **Prénom:** Maroua
- **Courriel:** [Email](mailto:marouaourahma@gmail.com)
- **LinkedIn:** [Linkedin](www.linkedin.com/in/maroua-ourahma)