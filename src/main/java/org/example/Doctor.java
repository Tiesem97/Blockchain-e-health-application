package org.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Doctor {
    private String doctorID;
    private String firstName;
    private String lastName;
    private Date dob;
    private String gender;
    private String address;
    private String number;
    private String qualification;
    private int ratingCount;
    private double totalRating;
    private Admin admin;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    public Doctor(Admin admin, String doctorID, String firstName, String lastName, Date dob, String gender, String address, String number, String qualification) {
        this.admin =admin;
        this.doctorID = doctorID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.number = number;
        this.qualification = qualification;
    }

    public Doctor(){
        this(null,"N/A","N/A","N/A",new java.util.Date(System.currentTimeMillis()),"N/A","N/A","N/A","N/A");

    }



    public void createMedicalRecord(Patient patient, String dateString, String diagnosis, String treatment,String prescription, String doctorID) {

        Date date = null;

        try {
            date = formatter.parse(dateString);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        MedicalRecords record = new MedicalRecords(date, diagnosis, treatment, prescription,doctorID);
        patient.addMedicalRecord(record, this);
    }

    public void updateRating(double newRating) {
        totalRating += newRating;
        ratingCount++;
    }
    public double getAverageRating() {
        return ratingCount > 0 ? totalRating / ratingCount : 0;
    }

    public void viewMedicalRecords(Patient patient) {
        patient.viewMedicalRecords(this);
    }


    public boolean hasAccessToPatient(Patient patient) {
        return patient.getAuthorizedDoctorsList().contains(this);
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {

        String formattedDob = formatter.format(dob);
        return formattedDob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
}
