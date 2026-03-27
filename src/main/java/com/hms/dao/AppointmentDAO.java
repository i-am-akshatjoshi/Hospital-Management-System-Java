package com.hms.dao;

import com.hms.model.Appointment;
import com.hms.util.DBConnection;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    private static final String SELECT_BASE =
        "SELECT a.appt_id, a.patient_id, a.doctor_id, "
      + "pe_p.full_name AS patient_name, "
      + "pe_d.full_name AS doctor_name, "
      + "TO_CHAR(a.appt_date, 'YYYY-MM-DD') AS appt_date, "
      + "a.appt_time, a.status, a.notes "
      + "FROM appointments a "
      + "JOIN patients  pa   ON a.patient_id = pa.patient_id "
      + "JOIN persons   pe_p ON pa.person_id  = pe_p.person_id "
      + "JOIN doctors   d    ON a.doctor_id   = d.doctor_id "
      + "JOIN persons   pe_d ON d.person_id   = pe_d.person_id ";

    // ══════════════════════════════════════════════════
    //  BOOK APPOINTMENT
    // ══════════════════════════════════════════════════
    public boolean bookAppointment(Appointment a)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "INSERT INTO appointments (patient_id, doctor_id, appt_date, appt_time, status, notes) "
                   + "VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, 'SCHEDULED', ?)";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, a.getPatientId());
        ps.setInt(2, a.getDoctor_Id());     // ✅ matches Appointment model: getDoctor_Id()
        ps.setString(3, a.getApptDate());
        ps.setString(4, a.getApptTime());
        ps.setString(5, a.getNotes());
        int rows = ps.executeUpdate();

        ps.close();
        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  GET ALL APPOINTMENTS
    // ══════════════════════════════════════════════════
    public List<Appointment> getAllAppointments()
            throws SQLException, IOException, ClassNotFoundException {

        List<Appointment> list = new ArrayList<>();
        Statement stmt = DBConnection.getConnection().createStatement();
        ResultSet rs   = stmt.executeQuery(SELECT_BASE + "ORDER BY a.appt_date DESC");
        while (rs.next()) list.add(mapRow(rs));
        rs.close(); stmt.close();
        return list;
    }

    // ══════════════════════════════════════════════════
    //  GET APPOINTMENTS BY PATIENT ID
    // ══════════════════════════════════════════════════
    public List<Appointment> getAppointmentsByPatient(int patientId)
            throws SQLException, IOException, ClassNotFoundException {

        List<Appointment> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE a.patient_id = ? ORDER BY a.appt_date DESC";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, patientId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapRow(rs));
        rs.close(); ps.close();
        return list;
    }

    // ══════════════════════════════════════════════════
    //  GET APPOINTMENTS BY DOCTOR ID
    // ══════════════════════════════════════════════════
    public List<Appointment> getAppointmentsByDoctor(int doctorId)
            throws SQLException, IOException, ClassNotFoundException {

        List<Appointment> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE a.doctor_id = ? ORDER BY a.appt_date DESC";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, doctorId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapRow(rs));
        rs.close(); ps.close();
        return list;
    }

    // ══════════════════════════════════════════════════
    //  GET TODAY'S APPOINTMENTS
    // ══════════════════════════════════════════════════
    public List<Appointment> getTodaysAppointments()
            throws SQLException, IOException, ClassNotFoundException {

        List<Appointment> list = new ArrayList<>();
        String sql = SELECT_BASE
                   + "WHERE TRUNC(a.appt_date) = TRUNC(SYSDATE) ORDER BY a.appt_time";
        Statement stmt = DBConnection.getConnection().createStatement();
        ResultSet rs   = stmt.executeQuery(sql);
        while (rs.next()) list.add(mapRow(rs));
        rs.close(); stmt.close();
        return list;
    }

    // ══════════════════════════════════════════════════
    //  GET APPOINTMENT BY ID
    // ══════════════════════════════════════════════════
    public Appointment getAppointmentById(int apptId)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = SELECT_BASE + "WHERE a.appt_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, apptId);
        ResultSet rs = ps.executeQuery();
        Appointment a = null;
        if (rs.next()) a = mapRow(rs);
        rs.close(); ps.close();
        return a;
    }

    // ══════════════════════════════════════════════════
    //  UPDATE STATUS  (SCHEDULED / COMPLETED / CANCELLED)
    // ══════════════════════════════════════════════════
    public boolean updateStatus(int apptId, String status)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "UPDATE appointments SET status = ? WHERE appt_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, status);
        ps.setInt(2, apptId);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  CANCEL APPOINTMENT
    // ══════════════════════════════════════════════════
    public boolean cancelAppointment(int apptId)
            throws SQLException, IOException, ClassNotFoundException {
        return updateStatus(apptId, "CANCELLED");
    }

    // ══════════════════════════════════════════════════
    //  DELETE APPOINTMENT
    // ══════════════════════════════════════════════════
    public boolean deleteAppointment(int apptId)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "DELETE FROM appointments WHERE appt_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, apptId);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  PRIVATE HELPER — mapRow()
    //  Converts one ResultSet row → Appointment object
    //  Uses new Appointment() then setters
    //  ✅ This requires the no-arg constructor in Appointment.java
    // ══════════════════════════════════════════════════
    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();           // needs no-arg constructor
        a.setApptId(rs.getInt("appt_id"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDoctor_Id(rs.getInt("doctor_id"));      // ✅ matches model: setDoctor_Id()
        a.setPatientName(rs.getString("patient_name"));
        a.setDoctorName(rs.getString("doctor_name")); // ✅ fixed: getString() not sgetString()
        a.setApptDate(rs.getString("appt_date"));
        a.setApptTime(rs.getString("appt_time"));
        a.setStatus(rs.getString("status"));
        a.setNotes(rs.getString("notes"));
        return a;
    }
}