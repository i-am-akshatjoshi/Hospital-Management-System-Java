package com.hms.servlet;

import com.hms.dao.DoctorDAO;
import com.hms.model.Doctor;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.List;

/**
 * DoctorServlet — handles all /api/doctors requests
 * GET    /api/doctors              → all doctors
 * GET    /api/doctors?search=name  → search by name
 * POST   /api/doctors              → add doctor
 * PUT    /api/doctors?id=1         → update doctor
 * DELETE /api/doctors?id=1         → delete doctor
 */
//@WebServlet("/api/doctors")
public class DoctorServlet extends HttpServlet {

    private final DoctorDAO dao = new DoctorDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        try {
            String search = req.getParameter("search");
            List<Doctor> list = (search != null && !search.trim().isEmpty())
                ? dao.searchByName(search.trim()) : dao.getAllDoctors();
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
            Doctor d = parseDoctor(body);

            if (d.getFullName() == null || d.getFullName().isEmpty()) {
                res.setStatus(400);
                out.print("{\"success\":false,\"message\":\"Doctor name is required!\"}"); return;
            }
            if (!d.getPhone().matches("\\d{10}")) {
                res.setStatus(400);
                out.print("{\"success\":false,\"message\":\"Phone must be 10 digits!\"}"); return;
            }
            if (d.getConsultFee() <= 0) {
                res.setStatus(400);
                out.print("{\"success\":false,\"message\":\"Consult fee must be positive!\"}"); return;
            }
            out.print("{\"success\":" + dao.addDoctor(d) + "}");
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
            Doctor d = dao.getDoctorById(id);
            if (d == null) { res.setStatus(404); out.print("{\"success\":false,\"message\":\"Doctor not found!\"}"); return; }
            String body = readBody(req);
            Doctor upd = parseDoctor(body);
            d.setFullName(upd.getFullName());         d.setGender(upd.getGender());
            d.setDob(upd.getDob());                   d.setPhone(upd.getPhone());
            d.setEmail(upd.getEmail());                d.setAddress(upd.getAddress());
            d.setSpecialization(upd.getSpecialization()); d.setQualification(upd.getQualification());
            d.setConsultFee(upd.getConsultFee());     d.setAvailableDays(upd.getAvailableDays());
            out.print("{\"success\":" + dao.updateDoctor(d) + "}");
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
            dao.deleteDoctor(Integer.parseInt(req.getParameter("id")));
            out.print("{\"success\":true}");
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    private Doctor parseDoctor(String json) {
        Doctor d = new Doctor();
        d.setFullName(jStr(json,"fullName"));       d.setGender(jStr(json,"gender"));
        d.setDob(jStr(json,"dob"));                 d.setPhone(jStr(json,"phone"));
        d.setEmail(jStr(json,"email"));             d.setAddress(jStr(json,"address"));
        d.setSpecialization(jStr(json,"specialization")); d.setQualification(jStr(json,"qualification"));
        d.setAvailableDays(jStr(json,"availableDays"));
        try { d.setConsultFee(Double.parseDouble(jStr(json,"consultFee"))); } catch(Exception e) { d.setConsultFee(0); }
        return d;
    }

    private String toJson(List<Doctor> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Doctor d = list.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"doctorId\":").append(d.getDoctor_Id()).append(",")
              .append("\"fullName\":\"").append(escape(d.getFullName())).append("\",")
              .append("\"gender\":\"").append(safe(d.getGender())).append("\",")
              .append("\"dob\":\"").append(safe(d.getDob())).append("\",")
              .append("\"phone\":\"").append(safe(d.getPhone())).append("\",")
              .append("\"email\":\"").append(safe(d.getEmail())).append("\",")
              .append("\"specialization\":\"").append(safe(d.getSpecialization())).append("\",")
              .append("\"qualification\":\"").append(safe(d.getQualification())).append("\",")
              .append("\"consultFee\":").append(d.getConsultFee()).append(",")
              .append("\"availableDays\":\"").append(safe(d.getAvailableDays())).append("\"")
              .append("}");
        }
        return sb.append("]").toString();
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) { String l; while ((l=br.readLine())!=null) sb.append(l); }
        return sb.toString();
    }
    private String jStr(String json, String key) {
        String s = "\""+key+"\""; int i = json.indexOf(s); if(i<0) return "";
        int c = json.indexOf(':',i); if(c<0) return "";
        int q1 = json.indexOf('"',c+1); if(q1<0) return "";
        int q2 = json.indexOf('"',q1+1); if(q2<0) return "";
        return json.substring(q1+1,q2);
    }
    private String safe(String s)   { return s==null?"":s; }
    private String escape(String s) {
        if(s==null) return "";
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r");
    }
}
