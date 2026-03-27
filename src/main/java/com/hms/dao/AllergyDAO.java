package com.hms.dao;

import com.hms.model.PatientAllergy;
import com.hms.util.DBConnection;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class AllergyDAO {
	public boolean addAllergy(PatientAllergy a) throws SQLException, IOException, ClassNotFoundException {
		String sql = "INSERT INTO patient_allegies"
				   +"(patient_id, allergy_type,allergy_name, severity,reaction)"
				   +"VALUES(?,?,?,?,?)";
		
		PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
		ps.setInt(1, a.getPatientId());
		ps.setString(2, a.getAllergyType());
		ps.setString(3, a.getAllergyName());
		ps.setString(4, a.getSeverity());
		ps.setString(5, a.getReaction());
		
		int rows  = ps.executeUpdate();
		ps.close();
		return rows>0;
	}
	
	public List<PatientAllergy> getByPatient(int patientId)
            throws SQLException, IOException, ClassNotFoundException {

        List<PatientAllergy> list = new ArrayList<>();

        String sql = "SELECT allergy_id, patient_id, allergy_type, allergy_name, severity, reaction "
                   + "FROM patient_allergies WHERE patient_id = ? "
                   + "ORDER BY severity DESC";   // SEVERE first

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, patientId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            PatientAllergy a = new PatientAllergy();
            a.setAllergyId(rs.getInt("allergy_id"));
            a.setPatientId(rs.getInt("patient_id"));
            a.setAllergyType(rs.getString("allergy_type"));
            a.setAllergyName(rs.getString("allergy_name"));
            a.setSeverity(rs.getString("severity"));
            a.setReaction(rs.getString("reaction"));
            list.add(a);
        }

        rs.close();
        ps.close();
        return list;
    }

    public boolean deleteAllergy(int allergyId)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "DELETE FROM patient_allergies WHERE allergy_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, allergyId);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }
	
}
