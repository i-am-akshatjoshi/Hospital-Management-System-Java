package com.hms.servlet;

import com.hms.dao.AppointmentDAO;
import com.hms.model.Appointment;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.List;

/**
 * AppointmentServlet — /api/appointments
 * GET    ?filter=today   → today's appointments
 * GET    (no param)      → all appointments
 * POST                   → book appointment
 * PUT    ?id=1&action=status  → update status
 * DELETE ?id=1           → delete appointment
 */
//@WebServlet("/api/appointment")
public class AppointmentServlet extends HttpServlet {

    private final AppointmentDAO dao = new AppointmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = res.getWriter();
        try {
            String filter = req.getParameter("filter");
            List<Appointment> list = "today".equals(filter)
                ? dao.getTodaysAppointments() : dao.getAllAppointments();
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
            Appointment a = new Appointment();
            a.setPatientId(jInt(body, "patientId"));
            a.setDoctor_Id(jInt(body, "doctorId"));
            a.setApptDate(jStr(body, "apptDate"));
            a.setApptTime(jStr(body, "apptTime"));
            a.setNotes(jStr(body, "notes"));
            out.print("{\"success\":" + dao.bookAppointment(a) + "}");
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
            String body = readBody(req);
            String status = jStr(body, "status");
            dao.updateStatus(id, status);
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
            dao.deleteAppointment(Integer.parseInt(req.getParameter("id")));
            out.print("{\"success\":true}");
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    private String toJson(List<Appointment> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Appointment a = list.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"apptId\":").append(a.getApptId()).append(",")
              .append("\"patientName\":\"").append(safe(a.getPatientName())).append("\",")
              .append("\"doctorName\":\"").append(safe(a.getDoctorName())).append("\",")
              .append("\"apptDate\":\"").append(safe(a.getApptDate())).append("\",")
              .append("\"apptTime\":\"").append(safe(a.getApptTime())).append("\",")
              .append("\"status\":\"").append(safe(a.getStatus())).append("\",")
              .append("\"notes\":\"").append(safe(a.getNotes())).append("\"")
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
        String s="\""+key+"\""; int i=json.indexOf(s); if(i<0) return "";
        int c=json.indexOf(':',i); if(c<0) return "";
        int q1=json.indexOf('"',c+1); if(q1<0) return "";
        int q2=json.indexOf('"',q1+1); if(q2<0) return "";
        return json.substring(q1+1,q2);
    }
    private int jInt(String json, String key) {
        String s="\""+key+"\""; int i=json.indexOf(s); if(i<0) return 0;
        int c=json.indexOf(':',i); if(c<0) return 0;
        int start=c+1; while(start<json.length()&&Character.isWhitespace(json.charAt(start))) start++;
        int end=start; while(end<json.length()&&(Character.isDigit(json.charAt(end))||json.charAt(end)=='-')) end++;
        try { return Integer.parseInt(json.substring(start,end)); } catch(Exception e) { return 0; }
    }
    private String safe(String s) { return s==null?"":s.replace("\"","\\\""); }
    private String escape(String s) {
        if(s==null) return "";
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r");
    }
}
