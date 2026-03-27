package com.hms.servlet;

import com.hms.dao.AllergyDAO;
import com.hms.model.PatientAllergy;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.List;

/** AllergyServlet — /api/allergies */
//@WebServlet("/api/allergies")
public class AllergyServlet extends HttpServlet {

    private final AllergyDAO dao = new AllergyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin","*");
        PrintWriter out = res.getWriter();
        try {
            int patientId = Integer.parseInt(req.getParameter("patientId"));
            List<PatientAllergy> list = dao.getByPatient(patientId);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                PatientAllergy a = list.get(i);
                if (i > 0) sb.append(",");
                sb.append("{")
                  .append("\"allergyId\":").append(a.getAllergyId()).append(",")
                  .append("\"patientId\":").append(a.getPatientId()).append(",")
                  .append("\"allergyType\":\"").append(s(a.getAllergyType())).append("\",")
                  .append("\"allergyName\":\"").append(s(a.getAllergyName())).append("\",")
                  .append("\"severity\":\"").append(s(a.getSeverity())).append("\",")
                  .append("\"reaction\":\"").append(s(a.getReaction())).append("\"")
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
            PatientAllergy a = new PatientAllergy();
            a.setPatientId(ji(body,"patientId"));
            a.setAllergyType(js(body,"allergyType"));
            a.setAllergyName(js(body,"allergyName"));
            a.setSeverity(js(body,"severity"));
            a.setReaction(js(body,"reaction"));
            out.print("{\"success\":" + dao.addAllergy(a) + "}");
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
            dao.deleteAllergy(Integer.parseInt(req.getParameter("id")));
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
    private String s(String v){return v==null?"":v.replace("\"","\\\"");}
}
