package com.hms.model;

public class VitalSigns {

	private int    vitalId;
    private int    patientId;
    private int    apptId;
    private String recordedDate;    
    private String bloodPressure;   
    private int    pulseRate;       
    private double temperature;     
    private double oxygenLevel;     
    private double weightKg;
    private double heightCm;
    private double bmi;             
    private String notes;
    
    public VitalSigns() {}

	public int getVitalId() {
		return vitalId;
	}

	public void setVitalId(int vitalId) {
		this.vitalId = vitalId;
	}

	public int getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	public int getApptId() {
		return apptId;
	}

	public void setApptId(int apptId) {
		this.apptId = apptId;
	}

	public String getRecordedDate() {
		return recordedDate;
	}

	public void setRecordedDate(String recordedDate) {
		this.recordedDate = recordedDate;
	}

	public String getBloodPressure() {
		return bloodPressure;
	}

	public void setBloodPressure(String bloodPressure) {
		this.bloodPressure = bloodPressure;
	}

	public int getPulseRate() {
		return pulseRate;
	}

	public void setPulseRate(int pulseRate) {
		this.pulseRate = pulseRate;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getOxygenLevel() {
		return oxygenLevel;
	}

	public void setOxygenLevel(double oxygenLevel) {
		this.oxygenLevel = oxygenLevel;
	}

	public double getWeightKg() {
		return weightKg;
	}

	public void setWeightKg(double weightKg) {
		this.weightKg = weightKg;
	}

	public double getHeightCm() {
		return heightCm;
	}

	public void setHeightCm(double heightCm) {
		this.heightCm = heightCm;
	}

	public double getBmi() {
		return bmi;
	}

	public void setBmi(double bmi) {
		this.bmi = bmi;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
    
    
}
