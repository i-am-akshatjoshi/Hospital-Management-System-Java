package com.hms.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hms.model.Patient;
import com.hms.util.DBConnection;

public class PatientDAO {

    // ══════════════════════════════════════════════════
    //  ADD PATIENT
    //  Step 1: Insert into persons → get generated person_id
    //  Step 2: Insert into patients using that person_id
    // ══════════════════════════════════════════════════
    public boolean addPatient(Patient p)
            throws SQLException, IOException, ClassNotFoundException {

        Connection con = DBConnection.getConnection();

        // Step 1 — insert into persons table
        String sql1 = "INSERT INTO persons (full_name, gender, dob, phone, email, address, person_type) "
                    + "VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, 'PATIENT')";

        PreparedStatement ps1 = con.prepareStatement(sql1, new String[]{"PERSON_ID"});
        ps1.setString(1, p.getFullName());       // ✅ getFullName()  — from Person
        ps1.setString(2, p.getGender());         // ✅ getGender()    — from Person
        ps1.setString(3, p.getDob());            // ✅ getDob()       — from Person
        ps1.setString(4, p.getPhone());          // ✅ getPhone()     — from Person
        ps1.setString(5, p.getEmail());          // ✅ getEmail()     — from Person
        ps1.setString(6, p.getAddress());        // ✅ getAddress()   — from Person
        ps1.executeUpdate();

        // Get auto-generated person_id from Oracle
        ResultSet rs = ps1.getGeneratedKeys();
        int personId = 0;
        if (rs.next())
            personId = rs.getInt(1);
        rs.close();

        // Step 2 — insert into patients table
        String sql2 = "INSERT INTO patients (person_id, blood_group, emergency_contact) "
                    + "VALUES (?, ?, ?)";

        PreparedStatement ps2 = con.prepareStatement(sql2);
        ps2.setInt(1, personId);
        ps2.setString(2, p.getBloodGroup());        // ✅ getBloodGroup()      — from Patient
        ps2.setString(3, p.getEmergencyContact());  // ✅ getEmergencyContact()— from Patient
        int rows = ps2.executeUpdate();

        ps1.close();
        ps2.close();
        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  GET ALL PATIENTS
    //  JOIN patients + persons to get full info
    // ══════════════════════════════════════════════════
    public List<Patient> getAllPatients()
            throws SQLException, IOException, ClassNotFoundException {

        List<Patient> list = new ArrayList<>();

        String sql = "SELECT pa.patient_id, pe.person_id, pe.full_name, pe.gender, "
                   + "TO_CHAR(pe.dob, 'YYYY-MM-DD') AS dob, pe.phone, pe.email, pe.address, "
                   + "pa.blood_group, pa.emergency_contact "
                   + "FROM patients pa "
                   + "JOIN persons pe ON pa.person_id = pe.person_id "
                   + "ORDER BY pa.patient_id";

        Statement stmt = DBConnection.getConnection().createStatement();
        ResultSet rs   = stmt.executeQuery(sql);

        while (rs.next()) {
            Patient p = new Patient();                              // ✅ needs no-arg constructor
            p.setPatientId(rs.getInt("patient_id"));               // Patient field
            p.setPersonId(rs.getInt("person_id"));                 // ✅ setPersonId() — from Person
            p.setFullName(rs.getString("full_name"));              // ✅ setFullName()  — from Person
            p.setGender(rs.getString("gender"));                   // ✅ setGender()    — from Person
            p.setDob(rs.getString("dob"));                         // ✅ setDob()       — from Person
            p.setPhone(rs.getString("phone"));                     // ✅ setPhone()     — from Person
            p.setEmail(rs.getString("email"));                     // ✅ setEmail()     — from Person
            p.setAddress(rs.getString("address"));                 // ✅ setAddress()   — from Person
            p.setBloodGroup(rs.getString("blood_group"));          // Patient field
            p.setEmergencyContact(rs.getString("emergency_contact")); // Patient field
            list.add(p);
        }

        rs.close();
        stmt.close();
        return list;
    }

    // ══════════════════════════════════════════════════
    //  GET PATIENT BY ID
    //  Used to pre-fill the edit form when row is clicked
    // ══════════════════════════════════════════════════
    public Patient getPatientById(int patientId)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "SELECT pa.patient_id, pe.person_id, pe.full_name, pe.gender, "
                   + "TO_CHAR(pe.dob, 'YYYY-MM-DD') AS dob, pe.phone, pe.email, pe.address, "
                   + "pa.blood_group, pa.emergency_contact "
                   + "FROM patients pa "
                   + "JOIN persons pe ON pa.person_id = pe.person_id "
                   + "WHERE pa.patient_id = ?";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, patientId);
        ResultSet rs = ps.executeQuery();

        Patient p = null;
        if (rs.next()) {
            p = new Patient();
            p.setPatientId(rs.getInt("patient_id"));
            p.setPersonId(rs.getInt("person_id"));
            p.setFullName(rs.getString("full_name"));
            p.setGender(rs.getString("gender"));
            p.setDob(rs.getString("dob"));
            p.setPhone(rs.getString("phone"));
            p.setEmail(rs.getString("email"));
            p.setAddress(rs.getString("address"));
            p.setBloodGroup(rs.getString("blood_group"));
            p.setEmergencyContact(rs.getString("emergency_contact"));
        }

        rs.close();
        ps.close();
        return p;
    }

    // ══════════════════════════════════════════════════
    //  UPDATE PATIENT
    //  Update both persons and patients tables
    // ══════════════════════════════════════════════════
    public boolean updatePatient(Patient p)
            throws SQLException, IOException, ClassNotFoundException {

        Connection con = DBConnection.getConnection();

        // Update persons table using person_id
        String sql1 = "UPDATE persons SET full_name=?, gender=?, dob=TO_DATE(?,'YYYY-MM-DD'), "
                    + "phone=?, email=?, address=? "
                    + "WHERE person_id=?";

        PreparedStatement ps1 = con.prepareStatement(sql1);
        ps1.setString(1, p.getFullName());
        ps1.setString(2, p.getGender());
        ps1.setString(3, p.getDob());
        ps1.setString(4, p.getPhone());
        ps1.setString(5, p.getEmail());
        ps1.setString(6, p.getAddress());
        ps1.setInt(7, p.getPersonId());           // ✅ getPersonId() — from Person
        ps1.executeUpdate();

        // Update patients table using patient_id
        String sql2 = "UPDATE patients SET blood_group=?, emergency_contact=? "
                    + "WHERE patient_id=?";

        PreparedStatement ps2 = con.prepareStatement(sql2);
        ps2.setString(1, p.getBloodGroup());
        ps2.setString(2, p.getEmergencyContact());
        ps2.setInt(3, p.getPatientId());
        int rows = ps2.executeUpdate();

        ps1.close();
        ps2.close();
        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  DELETE PATIENT
    //  Get person_id first → delete from persons
    //  ON DELETE CASCADE removes patient row too
    // ══════════════════════════════════════════════════
    public boolean deletePatient(int patientId)
            throws SQLException, IOException, ClassNotFoundException {

        // Step 1: get person_id linked to this patient
        String getSQL = "SELECT person_id FROM patients WHERE patient_id = ?";
        PreparedStatement psGet = DBConnection.getConnection().prepareStatement(getSQL);
        psGet.setInt(1, patientId);
        ResultSet rs = psGet.executeQuery();

        int personId = 0;
        if (rs.next())
            personId = rs.getInt("person_id");
        rs.close();
        psGet.close();

        // Step 2: delete from persons → CASCADE deletes from patients too
        String delSQL = "DELETE FROM persons WHERE person_id = ?";
        PreparedStatement psDel = DBConnection.getConnection().prepareStatement(delSQL);
        psDel.setInt(1, personId);
        int rows = psDel.executeUpdate();
        psDel.close();

        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  SEARCH PATIENT BY NAME
    //  LIKE query — partial name match
    // ══════════════════════════════════════════════════
    public List<Patient> searchByName(String name)
            throws SQLException, IOException, ClassNotFoundException {

        List<Patient> list = new ArrayList<>();

        String sql = "SELECT pa.patient_id, pe.person_id, pe.full_name, pe.gender, "
                   + "TO_CHAR(pe.dob, 'YYYY-MM-DD') AS dob, pe.phone, pe.email, pe.address, "
                   + "pa.blood_group, pa.emergency_contact "
                   + "FROM patients pa "
                   + "JOIN persons pe ON pa.person_id = pe.person_id "
                   + "WHERE UPPER(pe.full_name) LIKE ?";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, "%" + name.toUpperCase() + "%");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Patient p = new Patient();
            p.setPatientId(rs.getInt("patient_id"));
            p.setPersonId(rs.getInt("person_id"));
            p.setFullName(rs.getString("full_name"));
            p.setGender(rs.getString("gender"));
            p.setDob(rs.getString("dob"));
            p.setPhone(rs.getString("phone"));
            p.setEmail(rs.getString("email"));
            p.setAddress(rs.getString("address"));
            p.setBloodGroup(rs.getString("blood_group"));
            p.setEmergencyContact(rs.getString("emergency_contact"));
            list.add(p);
        }

        rs.close();
        ps.close();
        return list;
    }
}