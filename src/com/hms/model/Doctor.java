package com.hms.model;

public class Doctor extends Person {

	
	private int doctor_Id;
	private String specialization, qualification, availableDays;
	private double consultFee;
	
	public Doctor() {
		super();
	}
	
	@Override
	public String getRoleInfo() {
		return "Specialization: "+specialization+" | Fee: Rs. "+consultFee;
	}

	public int getDoctor_Id() {
		return doctor_Id;
	}

	public void setDoctor_Id(int doctor_Id) {
		this.doctor_Id = doctor_Id;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getAvailableDays() {
		return availableDays;
	}

	public void setAvailableDays(String availableDays) {
		this.availableDays = availableDays;
	}

	public double getConsultFee() {
		return consultFee;
	}

	public void setConsultFee(double consultFee) {
		this.consultFee = consultFee;
	}
	
	

}
