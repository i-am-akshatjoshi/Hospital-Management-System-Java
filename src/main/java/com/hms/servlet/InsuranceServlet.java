package com.hms.servlet;

import com.hms.dao.InsuranceDAO;
import com.hms.model.PatientInsurance;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.List;

/** InsuranceServlet — /api/insurance */

//@WebServlet("/api/insurance")
public class InsuranceServlet extends HttpServlet {

    private final InsuranceDAO dao = new InsuranceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin","*");
        PrintWriter out = res.getWriter();
        try {
            int patientId = Integer.parseInt(req.getParameter("patientId"));
            List<PatientInsurance> list = dao.getByPatient(patientId);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                PatientInsurance ins = list.get(i);
                if (i > 0) sb.append(",");
                sb.append("{")
                  .append("\"insuranceId\":").append(ins.getInsuranceId()).append(",")
                  .append("\"patientId\":").append(ins.getPatientId()).append(",")
                  .append("\"providerName\":\"").append(s(ins.getProviderName())).append("\",")
                  .append("\"policyNumber\":\"").append(s(ins.getPolicyNumber())).append("\",")
                  .append("\"coverageAmount\":").append(ins.getCoverageAmount()).append(",")
                  .append("\"validFrom\":\"").append(s(ins.getValidFrom())).append("\",")
                  .append("\"validTo\":\"").append(s(ins.getValidTo())).append("\",")
                  .append("\"policyType\":\"").append(s(ins.getPolicyType())).append("\",")
                  .append("\"status\":\"").append(s(ins.getStatus())).append("\"")
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
            PatientInsurance ins = new PatientInsurance();
            ins.setPatientId(ji(body,"patientId"));
            ins.setProviderName(js(body,"providerName"));
            ins.setPolicyNumber(js(body,"policyNumber"));
            ins.setCoverageAmount(jd(body,"coverageAmount"));
            ins.setValidFrom(js(body,"validFrom"));
            ins.setValidTo(js(body,"validTo"));
            ins.setPolicyType(js(body,"policyType"));
            ins.setStatus(js(body,"status"));
            out.print("{\"success\":" + dao.addInsurance(ins) + "}");
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin","*");
        PrintWriter out = res.getWriter();
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            dao.updateStatus(id, "EXPIRED");
            out.print("{\"success\":true}");
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
            dao.deleteInsurance(Integer.parseInt(req.getParameter("id")));
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
