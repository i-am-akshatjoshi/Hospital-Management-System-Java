package com.hms.model;

public class PatientInsurance {

	private int    insuranceId;
    private int    patientId;
    private String providerName;    
    private String policyNumber;
    private String policyType;      
    private double coverageAmount;  
    private String validFrom;       
    private String validTo;         
    private String status;     
    
    
    public PatientInsurance() {}


	public int getInsuranceId() {
		return insuranceId;
	}


	public void setInsuranceId(int insuranceId) {
		this.insuranceId = insuranceId;
	}


	public int getPatientId() {
		return patientId;
	}


	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}


	public String getProviderName() {
		return providerName;
	}


	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}


	public String getPolicyNumber() {
		return policyNumber;
	}


	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}


	public String getPolicyType() {
		return policyType;
	}


	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}


	public double getCoverageAmount() {
		return coverageAmount;
	}


	public void setCoverageAmount(double coverageAmount) {
		this.coverageAmount = coverageAmount;
	}


	public String getValidFrom() {
		return validFrom;
	}


	public void setValidFrom(String validFrom) {
		this.validFrom = validFrom;
	}


	public String getValidTo() {
		return validTo;
	}


	public void setValidTo(String validTo) {
		this.validTo = validTo;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}
    
    
}
