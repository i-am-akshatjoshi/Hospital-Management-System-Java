package com.hms.servlet;

import com.hms.dao.VitalSignsDAO;
import com.hms.model.VitalSigns;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.List;

/** VitalsServlet — /api/vitals */
//@WebServlet("/api/vitals")
public class VitalsServlet extends HttpServlet {

    private final VitalSignsDAO dao = new VitalSignsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin","*");
        PrintWriter out = res.getWriter();
        try {
            int patientId = Integer.parseInt(req.getParameter("patientId"));
            List<VitalSigns> list = dao.getByPatient(patientId);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                VitalSigns v = list.get(i);
                if (i > 0) sb.append(",");
                sb.append("{")
                  .append("\"vitalId\":").append(v.getVitalId()).append(",")
                  .append("\"patientId\":").append(v.getPatientId()).append(",")
                  .append("\"apptId\":").append(v.getApptId()).append(",")
                  .append("\"recordedDate\":\"").append(s(String.valueOf(v.getRecordedDate()))).append("\",")
                  .append("\"bloodPressure\":\"").append(s(v.getBloodPressure())).append("\",")
                  .append("\"pulseRate\":").append(v.getPulseRate()).append(",")
                  .append("\"temperature\":").append(v.getTemperature()).append(",")
                  .append("\"oxygenLevel\":").append(v.getOxygenLevel()).append(",")
                  .append("\"weightKg\":").append(v.getWeightKg()).append(",")
                  .append("\"heightCm\":").append(v.getHeightCm()).append(",")
                  .append("\"bmi\":").append(v.getBmi()).append(",")
                  .append("\"notes\":\"").append(s(v.getNotes())).append("\"")
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
            VitalSigns v = new VitalSigns();
            v.setPatientId(ji(body,"patientId"));
            v.setApptId(ji(body,"apptId"));
            v.setBloodPressure(js(body,"bloodPressure"));
            v.setPulseRate(ji(body,"pulseRate"));
            v.setTemperature(jd(body,"temperature"));
            v.setOxygenLevel(jd(body,"oxygenLevel"));
            v.setWeightKg(jd(body,"weightKg"));
            v.setHeightCm(jd(body,"heightCm"));
            v.setBmi(jd(body,"bmi"));
            v.setNotes(js(body,"notes"));
            out.print("{\"success\":" + dao.addVitals(v) + "}");
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
            dao.deleteVitals(Integer.parseInt(req.getParameter("id")));
            out.print("{\"success\":true}");
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private String rb(HttpServletRequest req) throws IOException {
        StringBuilder sb=new StringBuilder();try(BufferedReader br=req.getReader()){String l;while((l=br.readLine())!=null)sb.append(l);}return sb.toString();
    }
    private String js(String j,String k){String s="\""+k+"\"";int i=j.indexOf(s);if(i<0)return"";int c=j.indexOf(':',i);int q1=j.indexOf('"',c+1);int q2=j.indexOf('"',q1+1);return(q1<0||q2<0)?"":j.substring(q1+1,q2);}
    private int ji(String j,String k){String s="\""+k+"\"";int i=j.indexOf(s);if(i<0)return 0;int c=j.indexOf(':',i)+1;while(c<j.length()&&Character.isWhitespace(j.charAt(c)))c++;int e=c;while(e<j.length()&&Character.isDigit(j.charAt(e)))e++;try{return Integer.parseInt(j.substring(c,e));}catch(Exception ex){return 0;}}
    private double jd(String j,String k){String s="\""+k+"\"";int i=j.indexOf(s);if(i<0)return 0;int c=j.indexOf(':',i)+1;while(c<j.length()&&Character.isWhitespace(j.charAt(c)))c++;int e=c;while(e<j.length()&&(Character.isDigit(j.charAt(e))||j.charAt(e)=='.'))e++;try{return Double.parseDouble(j.substring(c,e));}catch(Exception ex){return 0;}}
    private String s(String v){return v==null?"":v.replace("\"","\\\"");}
}
