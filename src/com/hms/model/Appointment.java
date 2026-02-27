package com.hms.model;

public class Appointment {

    private int    apptId, patientId, doctor_Id;
    private String patientName, doctorName, apptDate, apptTime, status, notes;

    // ✅ FIX 1: Added default (no-arg) constructor
    // mapRow() in AppointmentDAO does: new Appointment()
    // then calls setters one by one — this requires a no-arg constructor
    // Without this, you get: "constructor Appointment() is undefined"
    public Appointment() {}

    // Parameterized constructor (kept as-is from your original)
    public Appointment(int apptId, int patientId, int doctor_Id,
                       String patientName, String doctorName,
                       String apptDate, String apptTime,
                       String status, String notes) {
        this.apptId      = apptId;
        this.patientId   = patientId;
        this.doctor_Id   = doctor_Id;
        this.patientName = patientName;
        this.doctorName  = doctorName;
        this.apptDate    = apptDate;
        this.apptTime    = apptTime;
        this.status      = status;
        this.notes       = notes;
    }

    // Getters & Setters — unchanged from your original
    public int    getApptId()           { return apptId; }
    public void   setApptId(int apptId) { this.apptId = apptId; }

    public int    getPatientId()              { return patientId; }
    public void   setPatientId(int patientId) { this.patientId = patientId; }

    public int    getDoctor_Id()               { return doctor_Id; }
    public void   setDoctor_Id(int doctor_Id)  { this.doctor_Id = doctor_Id; }

    public String getPatientName()                   { return patientName; }
    public void   setPatientName(String patientName) { this.patientName = patientName; }

    public String getDoctorName()                  { return doctorName; }
    public void   setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getApptDate()                { return apptDate; }
    public void   setApptDate(String apptDate) { this.apptDate = apptDate; }

    public String getApptTime()                { return apptTime; }
    public void   setApptTime(String apptTime) { this.apptTime = apptTime; }

    public String getStatus()              { return status; }
    public void   setStatus(String status) { this.status = status; }

    public String getNotes()             { return notes; }
    public void   setNotes(String notes) { this.notes = notes; }
}