package com.hms.model;

public class Patient extends Person {

    private int    patientId;
    private String bloodGroup, emergencyContact;

    // ✅ No-arg constructor — required by PatientDAO mapRow()
    // Calls super() which calls Person() no-arg constructor
    public Patient() {
        super();
    }

    // ✅ FIX 1: Added parameterized constructor
    // Useful when you want to create a Patient object in one line
    public Patient(int patientId, int personId, String fullName, String gender,
                   String dob, String phone, String email, String address,
                   String bloodGroup, String emergencyContact) {
        super(personId, fullName, gender, dob, phone, email, address, "PATIENT");
        this.patientId        = patientId;
        this.bloodGroup       = bloodGroup;
        this.emergencyContact = emergencyContact;
    }

    // ✅ getRoleInfo() — implements abstract method from Person
    @Override
    public String getRoleInfo() {
        return "Blood Group: " + bloodGroup
             + " | Emergency: " + emergencyContact;
    }

    // ── Getters & Setters ────────────────────────────────
    public int    getPatientId()                    { return patientId; }
    public void   setPatientId(int patientId)       { this.patientId = patientId; }

    public String getBloodGroup()                   { return bloodGroup; }
    public void   setBloodGroup(String bloodGroup)  { this.bloodGroup = bloodGroup; }

    public String getEmergencyContact()                         { return emergencyContact; }
    public void   setEmergencyContact(String emergencyContact)  { this.emergencyContact = emergencyContact; }
}