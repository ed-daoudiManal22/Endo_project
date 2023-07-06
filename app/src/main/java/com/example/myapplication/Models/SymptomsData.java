package com.example.myapplication.Models;

public class SymptomsData {
    private String sexdrive;
    private String mood;
    private String symptoms;
    private String vaginalDischarge;
    private String ovulation;

    public SymptomsData() {
        // Empty constructor required for Firestore
    }

    public SymptomsData(String sexdrive, String mood, String symptoms, String vaginalDischarge, String ovulation) {
        this.sexdrive = sexdrive;
        this.mood = mood;
        this.symptoms = symptoms;
        this.vaginalDischarge = vaginalDischarge;
        this.ovulation = ovulation;
    }

    // Add getters and setters as needed
}
