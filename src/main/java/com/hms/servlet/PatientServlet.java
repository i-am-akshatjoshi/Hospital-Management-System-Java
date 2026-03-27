package com.hms.servlet;

import com.hms.dao.PatientDAO;
import com.hms.model.Patient;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.List;

/**
 * PatientServlet — handles all /api/patients requests
 *
 * GET    /api/patients              → get all patients (JSON array)
 * GET    /api/patients?search=name  → search by name
 * POST   /api/patients              → add new patient (JSON body)
 * PUT    /api/patients?id=1         → update patient (JSON body)
 * DELETE /api/patients?id=1         → delete patient
 */
//@WebServlet("/api/patient")
public class PatientServlet extends HttpServlet {

    private final PatientDAO dao = new PatientDAO();

    // ── GET ─────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();

        try {
            String search = req.getParameter("search");
            List<Patient> list;

            if (search != null && !search.trim().isEmpty()) {
                list = dao.searchByName(search.trim());
            } else {
                list = dao.getAllPatients();
            }

            out.print(patientsToJson(list));

        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    // ── POST (Add) ───────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();

        try {
            String body = readBody(req);
            Patient p = parsePatient(body);

            // Server-side validation
            if (p.getFullName() == null || p.getFullName().isEmpty()) {
                res.setStatus(400);
                out.print("{\"success\":false,\"message\":\"Patient name is required!\"}");
                return;
            }
            if (!p.getPhone().matches("\\d{10}")) {
                res.setStatus(400);
                out.print("{\"success\":false,\"message\":\"Phone must be exactly 10 digits!\"}");
                return;
            }

            boolean added = dao.addPatient(p);
            out.print("{\"success\":" + added + "}");

        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    // ── PUT (Update) ─────────────────────────────────────────
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();

        try {
            int id = Integer.parseInt(req.getParameter("id"));
            String body = readBody(req);
            Patient p = parsePatient(body);

            // Load from DB to get existing ID, then overwrite fields
            Patient existing = dao.getPatientById(id);
            if (existing == null) {
                res.setStatus(404);
                out.print("{\"success\":false,\"message\":\"Patient not found!\"}");
                return;
            }
            existing.setFullName(p.getFullName());
            existing.setGender(p.getGender());
            existing.setDob(p.getDob());
            existing.setPhone(p.getPhone());
            existing.setEmail(p.getEmail());
            existing.setAddress(p.getAddress());
            existing.setBloodGroup(p.getBloodGroup());
            existing.setEmergencyContact(p.getEmergencyContact());

            boolean updated = dao.updatePatient(existing);
            out.print("{\"success\":" + updated + "}");

        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    // ── DELETE ───────────────────────────────────────────────
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();

        try {
            int id = Integer.parseInt(req.getParameter("id"));
            dao.deletePatient(id);
            out.print("{\"success\":true}");
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    // ── Helpers ──────────────────────────────────────────────

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    /** Very simple JSON parser — reads key-value string pairs */
    private Patient parsePatient(String json) {
        Patient p = new Patient();
        p.setFullName(jsonStr(json, "fullName"));
        p.setGender(jsonStr(json, "gender"));
        p.setDob(jsonStr(json, "dob"));
        p.setPhone(jsonStr(json, "phone"));
        p.setEmail(jsonStr(json, "email"));
        p.setAddress(jsonStr(json, "address"));
        p.setBloodGroup(jsonStr(json, "bloodGroup"));
        p.setEmergencyContact(jsonStr(json, "emergencyContact"));
        return p;
    }

    private String patientsToJson(List<Patient> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Patient p = list.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"patientId\":").append(p.getPatientId()).append(",")
              .append("\"fullName\":\"").append(escape(p.getFullName())).append("\",")
              .append("\"gender\":\"").append(escape(p.getGender())).append("\",")
              .append("\"dob\":\"").append(safe(p.getDob())).append("\",")
              .append("\"phone\":\"").append(safe(p.getPhone())).append("\",")
              .append("\"email\":\"").append(safe(p.getEmail())).append("\",")
              .append("\"bloodGroup\":\"").append(safe(p.getBloodGroup())).append("\",")
              .append("\"emergencyContact\":\"").append(safe(p.getEmergencyContact())).append("\"")
              .append("}");
        }
        return sb.append("]").toString();
    }

    /** Extract a string value from a simple flat JSON object */
    private String jsonStr(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx < 0) return "";
        int colon = json.indexOf(':', idx);
        if (colon < 0) return "";
        int q1 = json.indexOf('"', colon + 1);
        if (q1 < 0) return "";
        int q2 = json.indexOf('"', q1 + 1);
        if (q2 < 0) return "";
        return json.substring(q1 + 1, q2);
    }

    private String safe(String s)   { return s == null ? "" : s; }
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}
