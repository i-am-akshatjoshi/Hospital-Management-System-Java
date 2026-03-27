package com.hms.dao;

import com.hms.model.VitalSigns;
import com.hms.util.DBConnection;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class VitalSignsDAO {

	
	public boolean addVitals(VitalSigns v)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "INSERT INTO vital_signs "
                   + "(patient_id, appt_id, recorded_date, blood_pressure, pulse_rate, "
                   + "temperature, oxygen_level, weight_kg, height_cm, bmi, notes) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1,    v.getPatientId());
        ps.setInt(2,    v.getApptId());
        ps.setString(3, v.getRecordedDate());
        ps.setString(4, v.getBloodPressure());
        ps.setInt(5,    v.getPulseRate());
        ps.setDouble(6, v.getTemperature());
        ps.setDouble(7, v.getOxygenLevel());
        ps.setDouble(8, v.getWeightKg());
        ps.setDouble(9, v.getHeightCm());
        ps.setDouble(10, v.getBmi());
        ps.setString(11, v.getNotes());

        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    // ── GET ALL vital sign records for one patient ───────────
    public List<VitalSigns> getByPatient(int patientId)
            throws SQLException, IOException, ClassNotFoundException {

        List<VitalSigns> list = new ArrayList<>();

        String sql = "SELECT vital_id, patient_id, appt_id, recorded_date, blood_pressure, "
                   + "pulse_rate, temperature, oxygen_level, weight_kg, height_cm, bmi, notes "
                   + "FROM vital_signs WHERE patient_id = ? "
                   + "ORDER BY recorded_date DESC";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, patientId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            VitalSigns v = new VitalSigns();
            v.setVitalId(rs.getInt("vital_id"));
            v.setPatientId(rs.getInt("patient_id"));
            v.setApptId(rs.getInt("appt_id"));
            v.setRecordedDate(rs.getString("recorded_date"));
            v.setBloodPressure(rs.getString("blood_pressure"));
            v.setPulseRate(rs.getInt("pulse_rate"));
            v.setTemperature(rs.getDouble("temperature"));
            v.setOxygenLevel(rs.getDouble("oxygen_level"));
            v.setWeightKg(rs.getDouble("weight_kg"));
            v.setHeightCm(rs.getDouble("height_cm"));
            v.setBmi(rs.getDouble("bmi"));
            v.setNotes(rs.getString("notes"));
            list.add(v);
        }

        rs.close();
        ps.close();
        return list;
    }

    // ── DELETE one vital signs record ────────────────────────
    public boolean deleteVitals(int vitalId)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "DELETE FROM vital_signs WHERE vital_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, vitalId);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }
}
