package com.hms.model;

public class Bill {

    private int    billId, patientId, apptId;
    private String patientName, billDate, paymentStatus;
    private double consultCharnge, medicineCharge, testCharge;
    //             ↑ keeping your original spelling "Charnge" 
    //               so all your existing code stays consistent

    // ✅ FIX 1: Added no-arg constructor
    // BillDAO mapRow() does: new Bill() then setters one by one
    // Without this → compile error: "constructor Bill() is undefined"
    public Bill() {}

    // Parameterized constructor — kept exactly as your original
    public Bill(int billId, int patientId, int apptId,
                String patientName, String billDate, String paymentStatus,
                double consultCharnge, double medicineCharge, double testCharge) {
        this.billId         = billId;
        this.patientId      = patientId;
        this.apptId         = apptId;
        this.patientName    = patientName;
        this.billDate       = billDate;
        this.paymentStatus  = paymentStatus;
        this.consultCharnge = consultCharnge;
        this.medicineCharge = medicineCharge;
        this.testCharge     = testCharge;
    }

    // ✅ FIX 2: Added totalAmount field + getter + setter
    // BillDAO mapRow() calls b.setTotalAmount(rs.getDouble("total_amount"))
    // Without this field → compile error: "cannot find symbol setTotalAmount"
    private double totalAmount;

    public double getTotalAmount()             { return totalAmount; }
    public void   setTotalAmount(double total) { this.totalAmount = total; }

    // ── All getters & setters — exactly matching your original ──

    public int    getBillId()               { return billId; }
    public void   setBillId(int billId)     { this.billId = billId; }

    public int    getPatientId()                  { return patientId; }
    public void   setPatientId(int patientId)     { this.patientId = patientId; }

    public int    getApptId()               { return apptId; }
    public void   setApptId(int apptId)     { this.apptId = apptId; }

    public String getPatientName()                      { return patientName; }
    public void   setPatientName(String patientName)    { this.patientName = patientName; }

    public String getBillDate()                   { return billDate; }
    public void   setBillDate(String billDate)    { this.billDate = billDate; }

    public String getPaymentStatus()                        { return paymentStatus; }
    public void   setPaymentStatus(String paymentStatus)    { this.paymentStatus = paymentStatus; }

    // ✅ keeping your spelling "Charnge" — getter/setter match your model exactly
    public double getConsultCharnge()                       { return consultCharnge; }
    public void   setConsultCharnge(double consultCharnge)  { this.consultCharnge = consultCharnge; }

    public double getMedicineCharge()                       { return medicineCharge; }
    public void   setMedicineCharge(double medicineCharge)  { this.medicineCharge = medicineCharge; }

    public double getTestCharge()                     { return testCharge; }
    public void   setTestCharge(double testCharge)    { this.testCharge = testCharge; }
}