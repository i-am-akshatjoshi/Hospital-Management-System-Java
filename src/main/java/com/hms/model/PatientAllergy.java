package com.hms.model;

public class PatientAllergy {

	private int allergyId;
	private int    patientId;
    private String allergyType;   
    private String allergyName;   
    private String severity;      
    private String reaction;  
    
    public PatientAllergy() {}

	public int getAllergyId() {
		return allergyId;
	}

	public void setAllergyId(int allergyId) {
		this.allergyId = allergyId;
	}

	public int getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	public String getAllergyType() {
		return allergyType;
	}

	public void setAllergyType(String allergyType) {
		this.allergyType = allergyType;
	}

	public String getAllergyName() {
		return allergyName;
	}

	public void setAllergyName(String allergyName) {
		this.allergyName = allergyName;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getReaction() {
		return reaction;
	}

	public void setReaction(String reaction) {
		this.reaction = reaction;
	}
    
}
