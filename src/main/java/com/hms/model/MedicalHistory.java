package com.hms.model;

public class MedicalHistory {

	private int historyId;
	private int patientId;
	private String conditionName;
	private String diagnosedDate;
	private String treatmentGiven;
	private String isChronic;
	private String notes;
	
	
	public MedicalHistory() {}
	
	public int getHistoryId() {
		return historyId;
	}
	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}
	public int getPatientId() {
		return patientId;
	}
	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}
	public String getConditionName() {
		return conditionName;
	}
	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}
	public String getDiagnosedDate() {
		return diagnosedDate;
	}
	public void setDiagnosedDate(String diagnosedDate) {
		this.diagnosedDate = diagnosedDate;
	}
	public String getTreatmentGiven() {
		return treatmentGiven;
	}
	public void setTreatmentGiven(String treatmentGiven) {
		this.treatmentGiven = treatmentGiven;
	}
	public String getIsChronic() {
		return isChronic;
	}
	public void setIsChronic(String isChronic) {
		this.isChronic = isChronic;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
}
