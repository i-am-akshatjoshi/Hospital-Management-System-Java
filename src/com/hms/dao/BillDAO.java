package com.hms.dao;

import com.hms.model.Bill;
import com.hms.util.DBConnection;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    private static final String SELECT_BASE =
        "SELECT b.bill_id, b.patient_id, b.appt_id, "
      + "pe.full_name AS patient_name, "
      + "b.consult_charge, b.medicine_charge, b.test_charge, "
      + "(b.consult_charge + b.medicine_charge + b.test_charge) AS total_amount, "
      + "TO_CHAR(b.bill_date, 'YYYY-MM-DD') AS bill_date, "
      + "b.payment_status "
      + "FROM bills b "
      + "JOIN patients pa ON b.patient_id = pa.patient_id "
      + "JOIN persons  pe ON pa.person_id  = pe.person_id ";

    // ══════════════════════════════════════════════════
    //  GENERATE BILL
    // ══════════════════════════════════════════════════
    public boolean generateBill(Bill b)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "INSERT INTO bills (patient_id, appt_id, consult_charge, medicine_charge, test_charge, payment_status) "
                   + "VALUES (?, ?, ?, ?, ?, 'PENDING')";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, b.getPatientId());
        ps.setInt(2, b.getApptId());
        ps.setDouble(3, b.getConsultCharnge());   // ✅ matches model: getConsultCharnge() not getConsultCharge()
        ps.setDouble(4, b.getMedicineCharge());
        ps.setDouble(5, b.getTestCharge());
        int rows = ps.executeUpdate();

        ps.close();
        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  GET ALL BILLS
    // ══════════════════════════════════════════════════
    public List<Bill> getAllBills()
            throws SQLException, IOException, ClassNotFoundException {

        List<Bill> list = new ArrayList<>();
        Statement stmt = DBConnection.getConnection().createStatement();
        ResultSet rs   = stmt.executeQuery(SELECT_BASE + "ORDER BY b.bill_date DESC");
        while (rs.next()) list.add(mapRow(rs));
        rs.close(); stmt.close();
        return list;
    }

    // ══════════════════════════════════════════════════
    //  GET BILL BY BILL ID
    // ══════════════════════════════════════════════════
    public Bill getBillById(int billId)
            throws SQLException, IOException, ClassNotFoundException {

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
    public Bill getBillByApptId(int apptId)
            throws SQLException, IOException, ClassNotFoundException {

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
    //  GET BILLS BY PATIENT ID
    // ══════════════════════════════════════════════════
    public List<Bill> getBillsByPatient(int patientId)
            throws SQLException, IOException, ClassNotFoundException {

        List<Bill> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE b.patient_id = ? ORDER BY b.bill_date DESC";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, patientId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapRow(rs));
        rs.close(); ps.close();
        return list;
    }

    // ══════════════════════════════════════════════════
    //  GET PENDING BILLS
    // ══════════════════════════════════════════════════
    public List<Bill> getPendingBills()
            throws SQLException, IOException, ClassNotFoundException {

        List<Bill> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE b.payment_status = 'PENDING' ORDER BY b.bill_date";
        Statement stmt = DBConnection.getConnection().createStatement();
        ResultSet rs   = stmt.executeQuery(sql);
        while (rs.next()) list.add(mapRow(rs));
        rs.close(); stmt.close();
        return list;
    }

    // ══════════════════════════════════════════════════
    //  MARK AS PAID
    // ══════════════════════════════════════════════════
    public boolean markAsPaid(int billId)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "UPDATE bills SET payment_status = 'PAID' WHERE bill_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, billId);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  UPDATE BILL CHARGES
    // ══════════════════════════════════════════════════
    public boolean updateBill(Bill b)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "UPDATE bills SET consult_charge=?, medicine_charge=?, test_charge=? "
                   + "WHERE bill_id=?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setDouble(1, b.getConsultCharnge());   // ✅ matches model: getConsultCharnge()
        ps.setDouble(2, b.getMedicineCharge());
        ps.setDouble(3, b.getTestCharge());
        ps.setInt(4, b.getBillId());
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  DELETE BILL
    // ══════════════════════════════════════════════════
    public boolean deleteBill(int billId)
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "DELETE FROM bills WHERE bill_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, billId);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    // ══════════════════════════════════════════════════
    //  GET TOTAL REVENUE
    // ══════════════════════════════════════════════════
    public double getTotalRevenue()
            throws SQLException, IOException, ClassNotFoundException {

        String sql = "SELECT NVL(SUM(consult_charge + medicine_charge + test_charge), 0) "
                   + "AS total FROM bills WHERE payment_status = 'PAID'";
        Statement stmt = DBConnection.getConnection().createStatement();
        ResultSet rs   = stmt.executeQuery(sql);
        double total = 0;
        if (rs.next()) total = rs.getDouble("total");
        rs.close(); stmt.close();
        return total;
    }

    // ══════════════════════════════════════════════════
    //  PRIVATE HELPER — mapRow()
    //  Converts one ResultSet row → Bill object
    //  ✅ Uses new Bill() → needs no-arg constructor in Bill.java
    // ══════════════════════════════════════════════════
    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill b = new Bill();                                      // ✅ needs no-arg constructor
        b.setBillId(rs.getInt("bill_id"));
        b.setPatientId(rs.getInt("patient_id"));
        b.setApptId(rs.getInt("appt_id"));
        b.setPatientName(rs.getString("patient_name"));
        b.setConsultCharnge(rs.getDouble("consult_charge"));      // ✅ setConsultCharnge() matches model typo
        b.setMedicineCharge(rs.getDouble("medicine_charge"));
        b.setTestCharge(rs.getDouble("test_charge"));
        b.setTotalAmount(rs.getDouble("total_amount"));           // ✅ needs totalAmount field in Bill.java
        b.setBillDate(rs.getString("bill_date"));
        b.setPaymentStatus(rs.getString("payment_status"));
        return b;
    }
}