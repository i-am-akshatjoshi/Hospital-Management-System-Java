package com.hms.servlet;

import com.hms.dao.AppointmentDAO;
import com.hms.dao.BillDAO;
import com.hms.model.Appointment;
import com.hms.model.Bill;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.List;

/**
 * BillingServlet — /api/billing
 * GET  (no param)          → all bills
 * GET  ?filter=pending     → pending bills
 * GET  ?action=revenue     → total revenue
 * POST                     → generate bill
 * PUT  ?id=1&action=paid   → mark as paid
 * DELETE ?id=1             → delete bill
 */
//@WebServlet("/api/billing")
public class BillingServlet extends HttpServlet {

    private final BillDAO        billDAO = new BillDAO();
    private final AppointmentDAO apptDAO = new AppointmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        try {
            String action = req.getParameter("action");
            String filter = req.getParameter("filter");

            if ("revenue".equals(action)) {
                double rev = billDAO.getTotalRevenue();
                out.print("{\"revenue\":" + rev + "}");
                return;
            }
            List<Bill> list = "pending".equals(filter)
                ? billDAO.getPendingBills() : billDAO.getAllBills();
            out.print(toJson(list));
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        try {
            String body = readBody(req);
            int apptId = jInt(body, "apptId");

            Appointment a = apptDAO.getAppointmentById(apptId);
            if (a == null) {
                res.setStatus(400);
                out.print("{\"success\":false,\"message\":\"Appointment ID not found!\"}"); return;
            }
            if (billDAO.getBillByApptId(apptId) != null) {
                res.setStatus(400);
                out.print("{\"success\":false,\"message\":\"Bill already exists for this appointment!\"}"); return;
            }

            Bill b = new Bill();
            b.setApptId(apptId);
            b.setPatientId(a.getPatientId());
            b.setConsultCharnge(jDouble(body, "consultCharge"));
            b.setMedicineCharge(jDouble(body, "medicineCharge"));
            b.setTestCharge(jDouble(body, "testCharge"));

            double total = b.getConsultCharnge() + b.getMedicineCharge() + b.getTestCharge();
            boolean added = billDAO.generateBill(b);
            out.print("{\"success\":" + added + ",\"total\":\"" + String.format("%.2f", total) + "\"}");
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            billDAO.markAsPaid(id);
            out.print("{\"success\":true}");
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        try {
            billDAO.deleteBill(Integer.parseInt(req.getParameter("id")));
            out.print("{\"success\":true}");
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    private String toJson(List<Bill> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Bill b = list.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"billId\":").append(b.getBillId()).append(",")
              .append("\"patientName\":\"").append(safe(b.getPatientName())).append("\",")
              .append("\"apptId\":").append(b.getApptId()).append(",")
              .append("\"consultCharge\":").append(b.getConsultCharnge()).append(",")
              .append("\"medicineCharge\":").append(b.getMedicineCharge()).append(",")
              .append("\"testCharge\":").append(b.getTestCharge()).append(",")
              .append("\"totalAmount\":").append(b.getTotalAmount()).append(",")
              .append("\"billDate\":\"").append(safe(b.getBillDate())).append("\",")
              .append("\"paymentStatus\":\"").append(safe(b.getPaymentStatus())).append("\"")
              .append("}");
        }
        return sb.append("]").toString();
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) { String l; while((l=br.readLine())!=null) sb.append(l); }
        return sb.toString();
    }
    private String jStr(String json, String key) {
        String s="\""+key+"\""; int i=json.indexOf(s); if(i<0) return "";
        int c=json.indexOf(':',i); int q1=json.indexOf('"',c+1); int q2=json.indexOf('"',q1+1);
        return (q1<0||q2<0)?"":json.substring(q1+1,q2);
    }
    private int jInt(String json, String key) {
        String s="\""+key+"\""; int i=json.indexOf(s); if(i<0) return 0;
        int c=json.indexOf(':',i)+1;
        while(c<json.length()&&Character.isWhitespace(json.charAt(c))) c++;
        int e=c; while(e<json.length()&&Character.isDigit(json.charAt(e))) e++;
        try { return Integer.parseInt(json.substring(c,e)); } catch(Exception ex) { return 0; }
    }
    private double jDouble(String json, String key) {
        String s="\""+key+"\""; int i=json.indexOf(s); if(i<0) return 0;
        int c=json.indexOf(':',i)+1;
        while(c<json.length()&&Character.isWhitespace(json.charAt(c))) c++;
        int e=c; while(e<json.length()&&(Character.isDigit(json.charAt(e))||json.charAt(e)=='.')) e++;
        try { return Double.parseDouble(json.substring(c,e)); } catch(Exception ex) { return 0; }
    }
    private String safe(String s) { return s==null?"":s.replace("\"","\\\""); }
    private String escape(String s) {
        if(s==null) return "";
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n");
    }
}

//http://localhost:8090/Hospital_Management_System_Web/
