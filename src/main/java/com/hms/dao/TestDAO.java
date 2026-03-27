package com.hms.dao;

import com.hms.model.Patient;
import java.util.List;

public class TestDAO {

	public static void main(String[] args) throws Exception {

        PatientDAO dao = new PatientDAO();

        // Test 1: Add a patient
        Patient p = new Patient();
        p.setFullName("Priya Sharma");
        p.setGender("Female");
        p.setDob("1998-05-20");
        p.setPhone("9123456789");
        p.setEmail("priya@test.com");
        p.setAddress("Hyderabad");
        p.setBloodGroup("B+");
        p.setEmergencyContact("9000000001");

        boolean added = dao.addPatient(p);
        
        System.out.println("Patient added: " + added);

        // Test 2: Retrieve all patients
        List<Patient> list = dao.getAllPatients();
        System.out.println("Total patients in DB: " + list.size());
        for (Patient pt : list) {
            System.out.println(pt.getPatientId() + " - " + pt.getFullName());
        }
    }
}
