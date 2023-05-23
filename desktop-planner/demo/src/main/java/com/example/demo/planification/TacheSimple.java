package com.example.demo.planification;

import com.example.demo.enumerations.EtatTache;
import com.example.demo.enumerations.Prio;
import com.example.demo.user.Jour;
import com.example.demo.user.Projet;
import com.example.demo.user.User;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.util.Pair;


import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class TacheSimple extends Tache implements Serializable, Comparable<TacheSimple> {

    public TacheSimple() {
        // Default constructor required for deserialization
    }

    public void changerNom(User user){
        // Create the text input dialog for new name entry
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Renommer la tache");
        inputDialog.setHeaderText("Entrer un nouveau nom pour votre tache");
        inputDialog.setContentText("Le nouveau nom:");

        // Show the text input dialog and get the entered name
        Optional<String> result = inputDialog.showAndWait();
        result.ifPresent(newName -> {
            System.out.println("New name: " + newName);
            // Perform the desired action with the entered new name
            this.setNom(newName);
            user.getPlanning().getTachesaPlanifier().add(this);
        });
    }



    public int compareTo(TacheSimple other) {
        Jour thisJour = this.getJournee();
        Jour otherJour = other.getJournee();

        if (thisJour == null && otherJour == null) {
            return 0;
        } else if (thisJour == null) {
            return -1;
        } else if (otherJour == null) {
            return 1;
        }

        int jourComparison = thisJour.compareTo(otherJour);
        if (jourComparison != 0) {
            return jourComparison;
        } else {
            Creneau thisCreneau = this.getCreneauDeTache();
            Creneau otherCreneau = other.getCreneauDeTache();
            return thisCreneau.compareTo(otherCreneau);
        }
    }


    public void setNbrJourDePeriodicite(int nbrJourDePeriodicite) {
        this.nbrJourDePeriodicite = nbrJourDePeriodicite;
    }

    // ces tachesSimples peuvent être périodiques (planifiées tous les n jours).
    //si n = 0 , la tâche n'est planifiée qu'une fois.
    private int nbrJourDePeriodicite;
    private Creneau creneauDeTache;
    private Jour journee;

    public Creneau getCreneauDeTache() {
        return creneauDeTache;
    }

    public TacheSimple(String nom, Duration duree, Prio priorite, LocalDate deadline, Categorie categorie, int nbrJourDePeriodicite){
        super(nom, priorite, deadline, categorie);
        this.nbrJourDePeriodicite = nbrJourDePeriodicite;
    }
    public TacheSimple(Duration duree, Creneau creneauDeTache, Jour journee){
        super(duree);
        this.creneauDeTache = creneauDeTache;
        this.journee = journee;
        super.getJournees().add(journee);
    }
    public TacheSimple(Duration duree){
        super(duree);
    }

    public void setJournee(Jour journee) {
        this.journee = journee;
    }

    public Jour getJournee() {
        return journee;
    }

    // dans un seul créneau
    public void planifierTache(User user, Pair<Boolean, Projet> projetAjout){
        user.getPlanning().getTachesaPlanifier().add(this);
        if(projetAjout.getKey()){
            projetAjout.getValue().getEnsembleDesTaches().add(this);
        }

        if(nbrJourDePeriodicite>0){
            Jour jour = new Jour(this.journee.getDateDuJour());
            while(jour.comparerDates(jour.getDateDuJour(),user.getPlanning().getDateFin())<=0){
                    System.out.println("Jour de périodicité: " + jour.getDateDuJour().toString());
                    TacheSimple tachePeriodique = (TacheSimple) this;
                    tachePeriodique.setJournee(jour);
                    System.out.println("nom tache périodique: " + tachePeriodique.getNom());
                    System.out.println("date tache périodique: " + tachePeriodique.getJournee().getDateDuJour());
                    user.getPlanning().getTachesaPlanifier().add(tachePeriodique);
                    if(projetAjout.getKey()){
                        projetAjout.getValue().getEnsembleDesTaches().add(tachePeriodique);
                    }

                jour.incrementerJour(nbrJourDePeriodicite);
            }
        }
    }
    void replanifierTache(){

    }
    public void evaluerTache(User user){
        // Create the choice dialog for evaluation selection
        ChoiceDialog<EtatTache> choiceDialog = new ChoiceDialog<>(EtatTache.notRealized, EtatTache.values());
        choiceDialog.setTitle("Evaluation de la tache");
        choiceDialog.setHeaderText("évaluer la tache");
        choiceDialog.setContentText("Choisissez");

        // Show the choice dialog and get the selected evaluation
        Optional<EtatTache> result = choiceDialog.showAndWait();
        result.ifPresent(etatTache -> {
            System.out.println("Selected evaluation: " + etatTache);
            this.setStateDeTache(etatTache);
            user.getPlanning().getTachesaPlanifier().add(this);
            // Perform the desired action with the selected evaluation
        });
    }

    public void setCreneauDeTache(Creneau creneauDeTache) {
        this.creneauDeTache = creneauDeTache;
    }

}
