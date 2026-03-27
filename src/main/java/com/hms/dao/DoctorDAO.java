package com.hms.dao;

import com.hms.model.Doctor;
import com.hms.util.DBConnection;

import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//@WebServlet("/api/doctors")
public class DoctorDAO {

    // ══════════════════════════════════════════════════
    //  ADD DOCTOR
    //  Step 1: Insert into persons table → get person_id
    //  Step 2: Insert into doctors table using that person_id
    // ══════════════════════════════════════════════════
    public boolean addDoctor(Doctor d)
            throws SQLException, IOException, ClassNotFoundException {

        Connection con = DBConnection.getConnection();

        // Step 1 — insert into persons
        String sql1 = "INSERT INTO persons (full_name, gender, dob, phone, email, address, person_type) "
                + "VALUES (?, ?, TO_DATE(?, 'DD-MM-YYYY'), ?, ?, ?, 'DOCTOR')";

        PreparedStatement ps1 = con.prepareStatement(sql1, new String[]{"PERSON_ID"});
        ps1.setString(1, d.getFullName());
        ps1.setString(2, d.getGender());
        ps1.setString(3, d.getDob());
        ps1.setString(4, d.getPhone());
        ps1.setString(5, d.getEmail());
        ps1.setString(6, d.getAddress());
        ps1.executeUpdate();

        // Get the auto-generated person_id
        ResultSet rs = ps1.getGeneratedKeys();
        int personId = 0;
        if (rs.next())
            personId = rs.getInt(1);
        rs.close();

        // Step 2 — insert into doctors
        String sql2 = "INSERT INTO doctors (person_id, specialization, qualification, consult_fee, available_days) "
                    + "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps2 = con.prepareStatement(sql2);
        ps2.setInt(1, personId);
        ps2.setString(2, d.getSpecialization());
        ps2.setString(3, d.getQualification());
        ps2.setDouble(4, d.getConsultFee());
        ps2.setString(5, d.getAvailableDays());
        int rows = ps2.executeUpdate();

        ps1.close();
        ps2.close();
        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  GET ALL DOCTORS
    //  JOIN doctors + persons to get full doctor info
    // ══════════════════════════════════════════════════
    public List<Doctor> getAllDoctors()
            throws SQLException, IOException, ClassNotFoundException {

        List<Doctor> list = new ArrayList<>();

        String sql = "SELECT d.doctor_id, pe.person_id, pe.full_name, pe.gender, "
                   + "TO_CHAR(pe.dob, 'YYYY-MM-DD') AS dob, pe.phone, pe.email, pe.address, "
                   + "d.specialization, d.qualification, d.consult_fee, d.available_days "
                   + "FROM doctors d "
                   + "JOIN persons pe ON d.person_id = pe.person_id "
                   + "ORDER BY d.doctor_id";

        Statement stmt = DBConnection.getConnection().createStatement();
        ResultSet rs   = stmt.executeQuery(sql);

        while (rs.next()) {
            Doctor d = new Doctor();
            d.setDoctor_Id(rs.getInt("doctor_id"));
            d.setPersonId(rs.getInt("person_id"));
            d.setFullName(rs.getString("full_name"));
            d.setGender(rs.getString("gender"));
            d.setDob(rs.getString("dob"));
            d.setPhone(rs.getString("phone"));
            d.setEmail(rs.getString("email"));
            d.setAddress(rs.getString("address"));
            d.setSpecialization(rs.getString("specialization"));
            d.setQualification(rs.getString("qualification"));
            d.setConsultFee(rs.getDouble("consult_fee"));
            d.setAvailableDays(rs.getString("available_days"));
            list.add(d);
        }

        rs.close();
        stmt.close();
        return list;
    }

    // ══════════════════════════════════════════════════
    //  GET DOCTOR BY ID
    //  Used to pre-fill the form when editing
    // ══════════════════════════════════════════════════
    public Doctor getDoctorById(int doctorId)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "SELECT d.doctor_id, pe.person_id, pe.full_name, pe.gender, "
                   + "TO_CHAR(pe.dob, 'YYYY-MM-DD') AS dob, pe.phone, pe.email, pe.address, "
                   + "d.specialization, d.qualification, d.consult_fee, d.available_days "
                   + "FROM doctors d "
                   + "JOIN persons pe ON d.person_id = pe.person_id "
                   + "WHERE d.doctor_id = ?";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, doctorId);
        ResultSet rs = ps.executeQuery();

        Doctor d = null;
        if (rs.next()) {
            d = new Doctor();
            d.setDoctor_Id(rs.getInt("doctor_id"));
            d.setPersonId(rs.getInt("person_id"));
            d.setFullName(rs.getString("full_name"));
            d.setGender(rs.getString("gender"));
            d.setDob(rs.getString("dob"));
            d.setPhone(rs.getString("phone"));
            d.setEmail(rs.getString("email"));
            d.setAddress(rs.getString("address"));
            d.setSpecialization(rs.getString("specialization"));
            d.setQualification(rs.getString("qualification"));
            d.setConsultFee(rs.getDouble("consult_fee"));
            d.setAvailableDays(rs.getString("available_days"));
        }

        rs.close();
        ps.close();
        return d;
    }

    // ══════════════════════════════════════════════════
    //  UPDATE DOCTOR
    //  Update both persons and doctors tables
    // ══════════════════════════════════════════════════
    public boolean updateDoctor(Doctor d)
            throws SQLException, IOException, ClassNotFoundException {

        Connection con = DBConnection.getConnection();

        // Update persons table
        String sql1 = "UPDATE persons SET full_name=?, gender=?, phone=?, email=?, address=? "
                    + "WHERE person_id=?";
        PreparedStatement ps1 = con.prepareStatement(sql1);
        ps1.setString(1, d.getFullName());
        ps1.setString(2, d.getGender());
        ps1.setString(3, d.getPhone());
        ps1.setString(4, d.getEmail());
        ps1.setString(5, d.getAddress());
        ps1.setInt(6, d.getPersonId());
        ps1.executeUpdate();

        // Update doctors table
        String sql2 = "UPDATE doctors SET specialization=?, qualification=?, "
                    + "consult_fee=?, available_days=? "
                    + "WHERE doctor_id=?";
        PreparedStatement ps2 = con.prepareStatement(sql2);
        ps2.setString(1, d.getSpecialization());
        ps2.setString(2, d.getQualification());
        ps2.setDouble(3, d.getConsultFee());
        ps2.setString(4, d.getAvailableDays());
        ps2.setInt(5, d.getDoctor_Id());
        int rows = ps2.executeUpdate();

        ps1.close();
        ps2.close();
        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  DELETE DOCTOR
    //  Delete from persons → cascades to doctors table
    //  (because of ON DELETE CASCADE in schema)
    // ══════════════════════════════════════════════════
    public boolean deleteDoctor(int doctorId)
            throws SQLException, IOException, ClassNotFoundException {

        // First get the person_id linked to this doctor
        String getSQL = "SELECT person_id FROM doctors WHERE doctor_id = ?";
        PreparedStatement psGet = DBConnection.getConnection().prepareStatement(getSQL);
        psGet.setInt(1, doctorId);
        ResultSet rs = psGet.executeQuery();

        int personId = 0;
        if (rs.next())
            personId = rs.getInt("person_id");
        rs.close();
        psGet.close();

        // Delete from persons — CASCADE will delete from doctors too
        String delSQL = "DELETE FROM persons WHERE person_id = ?";
        PreparedStatement psDel = DBConnection.getConnection().prepareStatement(delSQL);
        psDel.setInt(1, personId);
        int rows = psDel.executeUpdate();
        psDel.close();

        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  SEARCH DOCTOR BY NAME
    //  Used to filter doctors in the table
    // ══════════════════════════════════════════════════
    public List<Doctor> searchByName(String name)
            throws SQLException, IOException, ClassNotFoundException {

        List<Doctor> list = new ArrayList<>();

        String sql = "SELECT d.doctor_id, pe.person_id, pe.full_name, pe.gender, "
                   + "TO_CHAR(pe.dob, 'YYYY-MM-DD') AS dob, pe.phone, pe.email, pe.address, "
                   + "d.specialization, d.qualification, d.consult_fee, d.available_days "
                   + "FROM doctors d "
                   + "JOIN persons pe ON d.person_id = pe.person_id "
                   + "WHERE UPPER(pe.full_name) LIKE ?";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, "%" + name.toUpperCase() + "%");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Doctor d = new Doctor();
            d.setDoctor_Id(rs.getInt("doctor_id"));
            d.setPersonId(rs.getInt("person_id"));
            d.setFullName(rs.getString("full_name"));
            d.setGender(rs.getString("gender"));
            d.setDob(rs.getString("dob"));
            d.setPhone(rs.getString("phone"));
            d.setEmail(rs.getString("email"));
            d.setAddress(rs.getString("address"));
            d.setSpecialization(rs.getString("specialization"));
            d.setQualification(rs.getString("qualification"));
            d.setConsultFee(rs.getDouble("consult_fee"));
            d.setAvailableDays(rs.getString("available_days"));
            list.add(d);
        }

        rs.close();
        ps.close();
        return list;
    }
}