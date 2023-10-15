package org.example;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MedicalRecords {
    private Date date;
    private String diagnosis;
    private String treatment;
    private String doctorID;
    private String prescription;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    private ArrayList<Doctor> authorizedDoctors;




    public MedicalRecords(Date date, String diagnosis, String treatment,String prescription, String doctorID) {
        this.date = date;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.doctorID = doctorID;
        this.prescription = prescription;
        this.authorizedDoctors = new ArrayList<Doctor>();
    }


    public String getDate() {

        String formattedDate = formatter.format(date);
        return formattedDate;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getDoctor() {
        return doctorID;
    }

    public void setDoctor(String doctorID) {
        this.doctorID = doctorID;
    }

    public ArrayList<Doctor> getAuthorizedDoctors() {
        return authorizedDoctors;
    }

    public void setAuthorizedDoctors(ArrayList<Doctor> authorizedDoctors) {
        this.authorizedDoctors = authorizedDoctors;
    }



    @Override
    public String toString() {

        return " MedicalRecord --> "+" Date: " + getDate()+" Diagnosis: " + this.diagnosis + " Treatment: "+ this.treatment + " Prescription: "+ this.prescription +" DoctorID: "+ this.doctorID;
    }
}

