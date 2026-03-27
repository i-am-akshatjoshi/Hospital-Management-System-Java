package com.hms.dao;

import com.hms.model.MedicalHistory;
import com.hms.util.DBConnection;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class MedicalHistoryDAO {
	public boolean addHistory(MedicalHistory h)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "INSERT INTO patient_medical_history "
                   + "(patient_id, condition_name, diagnosed_date, treatment_given, is_chronic, notes) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1,    h.getPatientId());
        ps.setString(2, h.getConditionName());
        ps.setString(3, h.getDiagnosedDate());
        ps.setString(4, h.getTreatmentGiven());
        ps.setString(5, h.getIsChronic());
        ps.setString(6, h.getNotes());

        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    public List<MedicalHistory> getByPatient(int patientId)
            throws SQLException, IOException, ClassNotFoundException {

        List<MedicalHistory> list = new ArrayList<>();

        String sql = "SELECT history_id, patient_id, condition_name, diagnosed_date, "
                   + "treatment_given, is_chronic, notes "
                   + "FROM patient_medical_history "
                   + "WHERE patient_id = ? "
                   + "ORDER BY diagnosed_date DESC";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, patientId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            MedicalHistory h = new MedicalHistory();       // no-arg constructor
            h.setHistoryId(rs.getInt("history_id"));
            h.setPatientId(rs.getInt("patient_id"));
            h.setConditionName(rs.getString("condition_name"));
            h.setDiagnosedDate(rs.getString("diagnosed_date"));
            h.setTreatmentGiven(rs.getString("treatment_given"));
            h.setIsChronic(rs.getString("is_chronic"));
            h.setNotes(rs.getString("notes"));
            list.add(h);
        }

        rs.close();
        ps.close();
        return list;
    }

    public boolean deleteHistory(int historyId)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "DELETE FROM patient_medical_history WHERE history_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, historyId);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }
}

