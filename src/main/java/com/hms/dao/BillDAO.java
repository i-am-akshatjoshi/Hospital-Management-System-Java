package com.hms.dao;

import com.hms.model.Bill;
import com.hms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    private static final String SELECT_BASE =
        "SELECT b.bill_id, b.appt_id, b.patient_id, "
      + "pe.full_name AS patient_name, "
      + "b.consult_charge, b.medicine_charge, b.test_charge, "
      + "b.total_amount, "
      + "TO_CHAR(b.bill_date, 'DD-MM-YYYY') AS bill_date, "
      + "b.payment_status "
      + "FROM BILLING b "
      + "LEFT JOIN PATIENTS pa ON b.patient_id = pa.patient_id "
      + "LEFT JOIN PERSONS  pe ON pa.person_id  = pe.person_id ";

    // ══════════════════════════════════════════════════
    //  GENERATE BILL
    // ══════════════════════════════════════════════════
    public boolean generateBill(Bill b) throws Exception {
        String sql = "INSERT INTO BILLING "
                   + "(bill_id, appt_id, patient_id, consult_charge, medicine_charge, test_charge, total_amount, bill_date, payment_status) "
                   + "VALUES (billing_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, SYSDATE, 'PENDING')";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, b.getApptId());
        ps.setInt(2, b.getPatientId());
        ps.setDouble(3, b.getConsultCharnge());
        ps.setDouble(4, b.getMedicineCharge());
        ps.setDouble(5, b.getTestCharge());
        ps.setDouble(6, b.getConsultCharnge() + b.getMedicineCharge() + b.getTestCharge());
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  GET ALL BILLS
    // ══════════════════════════════════════════════════
    public List<Bill> getAllBills() throws Exception {
        List<Bill> list = new ArrayList<>();
        Statement stmt = DBConnection.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(SELECT_BASE + "ORDER BY b.bill_date DESC");
        while (rs.next()) list.add(mapRow(rs));
        rs.close(); stmt.close();
        return list;
    }

    // ══════════════════════════════════════════════════
    //  GET BILL BY BILL ID
    // ══════════════════════════════════════════════════
    public Bill getBillById(int billId) throws Exception {
        String sql = SELECT_BASE + "WHERE b.bill_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, billId);
        ResultSet rs = ps.executeQuery();
        Bill bill = null;
        if (rs.next()) bill = mapRow(rs);
        rs.close(); ps.close();
        return bill;
    }

    // ══════════════════════════════════════════════════
    //  GET BILL BY APPOINTMENT ID
    // ══════════════════════════════════════════════════
    public Bill getBillByApptId(int apptId) throws Exception {
        String sql = SELECT_BASE + "WHERE b.appt_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, apptId);
        ResultSet rs = ps.executeQuery();
        Bill bill = null;
        if (rs.next()) bill = mapRow(rs);
        rs.close(); ps.close();
        return bill;
    }

    // ══════════════════════════════════════════════════
    //  GET PENDING BILLS
    // ══════════════════════════════════════════════════
    public List<Bill> getPendingBills() throws Exception {
        List<Bill> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE b.payment_status = 'PENDING' ORDER BY b.bill_date";
        Statement stmt = DBConnection.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) list.add(mapRow(rs));
        rs.close(); stmt.close();
        return list;
    }

    // ══════════════════════════════════════════════════
    //  MARK AS PAID
    // ══════════════════════════════════════════════════
    public boolean markAsPaid(int billId) throws Exception {
        String sql = "UPDATE BILLING SET payment_status = 'PAID' WHERE bill_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, billId);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  DELETE BILL
    // ══════════════════════════════════════════════════
    public boolean deleteBill(int billId) throws Exception {
        String sql = "DELETE FROM BILLING WHERE bill_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, billId);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  GET TOTAL REVENUE
    // ══════════════════════════════════════════════════
    public double getTotalRevenue() throws Exception {
        String sql = "SELECT NVL(SUM(total_amount), 0) AS total "
                   + "FROM BILLING WHERE payment_status = 'PAID'";
        Statement stmt = DBConnection.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        double total = 0;
        if (rs.next()) total = rs.getDouble("total");
        rs.close(); stmt.close();
        return total;
    }

    // ══════════════════════════════════════════════════
    //  PRIVATE HELPER — mapRow()
    // ══════════════════════════════════════════════════
    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("bill_id"));
        b.setApptId(rs.getInt("appt_id"));
        b.setPatientId(rs.getInt("patient_id"));
        b.setPatientName(rs.getString("patient_name"));
        b.setConsultCharnge(rs.getDouble("consult_charge"));
        b.setMedicineCharge(rs.getDouble("medicine_charge"));
        b.setTestCharge(rs.getDouble("test_charge"));
        b.setTotalAmount(rs.getDouble("total_amount"));
        b.setBillDate(rs.getString("bill_date"));
        b.setPaymentStatus(rs.getString("payment_status"));
        return b;
    }
}