package com.hms.servlet;

import com.hms.dao.MedicalHistoryDAO;
import com.hms.model.MedicalHistory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.List;

/**
 * MedHistoryServlet — /api/medhistory
 * GET  ?patientId=1   → get history for patient
 * POST                → add record
 * DELETE ?id=1        → delete record
 */
//@WebServlet("/api/medhistory")
public class MedHistoryServlet extends HttpServlet {

    private final MedicalHistoryDAO dao = new MedicalHistoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin","*");
        PrintWriter out = res.getWriter();
        try {
            int patientId = Integer.parseInt(req.getParameter("patientId"));
            List<MedicalHistory> list = dao.getByPatient(patientId);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                MedicalHistory h = list.get(i);
                if (i > 0) sb.append(",");
                sb.append("{")
                  .append("\"historyId\":").append(h.getHistoryId()).append(",")
                  .append("\"patientId\":").append(h.getPatientId()).append(",")
                  .append("\"conditionName\":\"").append(s(h.getConditionName())).append("\",")
                  .append("\"diagnosedDate\":\"").append(s(h.getDiagnosedDate())).append("\",")
                  .append("\"treatmentGiven\":\"").append(s(h.getTreatmentGiven())).append("\",")
                  .append("\"isChronic\":\"").append(s(h.getIsChronic())).append("\",")
                  .append("\"notes\":\"").append(s(h.getNotes())).append("\"")
                  .append("}");
            }
            out.print(sb.append("]").toString());
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin","*");
        PrintWriter out = res.getWriter();
        try {
            String body = rb(req);
            MedicalHistory h = new MedicalHistory();
            h.setPatientId(ji(body,"patientId"));
            h.setConditionName(js(body,"conditionName"));
            h.setDiagnosedDate(js(body,"diagnosedDate"));
            h.setTreatmentGiven(js(body,"treatmentGiven"));
            h.setIsChronic(js(body,"isChronic"));
            h.setNotes(js(body,"notes"));
            out.print("{\"success\":" + dao.addHistory(h) + "}");
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin","*");
        PrintWriter out = res.getWriter();
        try {
            dao.deleteHistory(Integer.parseInt(req.getParameter("id")));
            out.print("{\"success\":true}");
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private String rb(HttpServletRequest req) throws IOException {
        StringBuilder sb=new StringBuilder(); try(BufferedReader br=req.getReader()){String l;while((l=br.readLine())!=null)sb.append(l);} return sb.toString();
    }
    private String js(String j,String k){String s="\""+k+"\"";int i=j.indexOf(s);if(i<0)return"";int c=j.indexOf(':',i);int q1=j.indexOf('"',c+1);int q2=j.indexOf('"',q1+1);return(q1<0||q2<0)?"":j.substring(q1+1,q2);}
    private int ji(String j,String k){String s="\""+k+"\"";int i=j.indexOf(s);if(i<0)return 0;int c=j.indexOf(':',i)+1;while(c<j.length()&&Character.isWhitespace(j.charAt(c)))c++;int e=c;while(e<j.length()&&Character.isDigit(j.charAt(e)))e++;try{return Integer.parseInt(j.substring(c,e));}catch(Exception ex){return 0;}}
    private String s(String v){return v==null?"":v.replace("\"","\\\"");}
}
