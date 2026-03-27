package com.hms.dao;

import com.hms.model.PatientInsurance;
import com.hms.util.DBConnection;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class InsuranceDAO {

	public boolean addInsurance(PatientInsurance ins)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "INSERT INTO patient_insurance "
                   + "(patient_id, provider_name, policy_number, policy_type, "
                   + "coverage_amount, valid_from, valid_to, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1,    ins.getPatientId());
        ps.setString(2, ins.getProviderName());
        ps.setString(3, ins.getPolicyNumber());
        ps.setString(4, ins.getPolicyType());
        ps.setDouble(5, ins.getCoverageAmount());
        ps.setString(6, ins.getValidFrom());
        ps.setString(7, ins.getValidTo());
        ps.setString(8, ins.getStatus());

        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    // ── UPDATE insurance status (e.g. ACTIVE → EXPIRED) ─────
    public boolean updateStatus(int insuranceId, String status)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "UPDATE patient_insurance SET status = ? WHERE insurance_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, status);
        ps.setInt(2, insuranceId);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    // ── GET ALL insurance records for one patient ────────────
    public List<PatientInsurance> getByPatient(int patientId)
            throws SQLException, IOException, ClassNotFoundException {

        List<PatientInsurance> list = new ArrayList<>();

        String sql = "SELECT insurance_id, patient_id, provider_name, policy_number, "
                   + "policy_type, coverage_amount, valid_from, valid_to, status "
                   + "FROM patient_insurance WHERE patient_id = ?";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, patientId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            PatientInsurance ins = new PatientInsurance();
            ins.setInsuranceId(rs.getInt("insurance_id"));
            ins.setPatientId(rs.getInt("patient_id"));
            ins.setProviderName(rs.getString("provider_name"));
            ins.setPolicyNumber(rs.getString("policy_number"));
            ins.setPolicyType(rs.getString("policy_type"));
            ins.setCoverageAmount(rs.getDouble("coverage_amount"));
            ins.setValidFrom(rs.getString("valid_from"));
            ins.setValidTo(rs.getString("valid_to"));
            ins.setStatus(rs.getString("status"));
            list.add(ins);
        }

        rs.close();
        ps.close();
        return list;
    }

    // ── DELETE one insurance record ──────────────────────────
    public boolean deleteInsurance(int insuranceId)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "DELETE FROM patient_insurance WHERE insurance_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, insuranceId);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }
}
