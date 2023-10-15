package org.example;

import org.example.client.FabricClient;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.shim.ChaincodeStub;

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

public class Admin {

    private String userID;

    private static HashSet<String> usedIDslist = new HashSet<>();
    private ArrayList<Patient> patientList = new ArrayList<>();
    private ArrayList<Doctor> doctorsList = new ArrayList<>();


    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    private MedicalChaincode medicalChaincode;

    private Channel channel;
    private HFClient client;
    private ChaincodeStub stub;
    private FabricClient fabricClient;
    public Admin(MedicalChaincode medicalChaincode, Channel channel, HFClient client ) {
        this.medicalChaincode= medicalChaincode;
        this.channel =channel;
        this.client =client;

        fabricClient = new FabricClient();

    }


    public String generateId(String firstName, String lastName, String role){

        String prefix = "";

        if (role.equals("patient")) {
            prefix = "PA";
        } else if (role.equals("doctor")) {
            prefix = "DO";
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        Random random = new Random();
        int suffix = random.nextInt(10000);
        userID = prefix + firstName.charAt(0) + lastName.charAt(0) + suffix;
        while (usedIDslist.contains(userID)) {
            suffix = random.nextInt(10000);
            userID = prefix + firstName.charAt(0) + lastName.charAt(0) + suffix;
        }
        usedIDslist.add(userID);
        return userID;

    }
    public Patient addPatient(String firstName, String lastName, String dateString, String gender, String address, String number){

        updatePatientListFromLedger();

        Date dob = null;

        try {
            dob = formatter.parse(dateString);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        String patientID = generateId(firstName,lastName,"patient");
        Patient patient = new Patient(this,patientID, firstName, lastName,dob, gender, address, number);
        patientList.add(patient);
        storePatientListInLedger(patient);
        return patient;
    }



    public Doctor addDoctor( String firstName, String lastName, String dateString, String gender, String address, String number, String qualification){

        updateDoctorListFromLedger();

        Date dob = null;

        try {
            dob = formatter.parse(dateString);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String doctorID = generateId(firstName,lastName,"doctor");
        Doctor doctor = new Doctor(this,doctorID,firstName, lastName,dob,gender,address,number,qualification);
        doctorsList.add(doctor);

        storeDoctorListInLedger(doctor);
        return doctor;
    }



    public void printPatientList(){


        for (Patient patient : patientList) {
            System.out.println(patient.getPatientID() + ": " + patient.getFirstName() + " " + patient.getLastName() + " "+ patient.getDob());
        }
    }

    public void printDoctorList(){


        for (Doctor doctor : doctorsList) {
            System.out.println(doctor.getDoctorID() + ": " + doctor.getFirstName() + " " + doctor.getLastName() + " "+ doctor.getDob());
        }
    }

    public ArrayList<Patient> getPatientList() {

        updatePatientListFromLedger();
        return patientList;
    }

    public ArrayList<Doctor> getDoctorList() {
        updateDoctorListFromLedger();
        return doctorsList;
    }

    public Patient getPatientById(String patientId) {
        for (Patient patient : patientList) {
            if (patient.getPatientID().equals(patientId)) {
                return patient;
            }
        }
        return null;
    }

    public ArrayList<MedicalRecords> getPatientMedicalRecords(String patientID) {
        Patient patient = getPatientById(patientID);
        if (patient != null) {
            return patient.getMedicalRecord();
        }
        return null;
    }

    public void storePatientMedicalRecordsInLedger(String patientID) {
        Patient patient = getPatientById(patientID);
        if (patient != null) {
            fabricClient.invokeStoreMedicalRecords(client,channel,patient.getPatientID());
            //medicalChaincode.storeMedicalRecords(stub, patient.getPatientID());
        } else {
            JOptionPane.showMessageDialog(null,"Patient not found!");
        }
    }

    public void updatePatientMedicalRecordsFromLedger(String patientID) {
        Patient patient = getPatientById(patientID);
        if (patient != null) {
            ArrayList<MedicalRecords> medicalRecords = fabricClient.invokeGetMedicalRecords(client,channel,patientID);
            if (medicalRecords != null) {
                patient.setMedicalRecord(medicalRecords);
            } else {
                JOptionPane.showMessageDialog(null, "No medical records found for the patient!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Patient not found!");
        }
    }
    public void setPatientList(ArrayList<Patient> patientList) {
        this.patientList = patientList;
    }

    public void setDoctorList(ArrayList<Doctor> doctorsList) {
        this.doctorsList = doctorsList;
    }

    public MedicalChaincode getMedicalChaincode() {
        return medicalChaincode;
    }

    public void storePatientListInLedger(Patient patient) {
        fabricClient.invokeStorePatientList(client,channel,patient);
    }

    public void storeDoctorListInLedger(Doctor doctor) {
        fabricClient.invokeStoreDoctorList(client,channel,doctor);
    }

    public ChaincodeStub getStub() {
        return stub;
    }

    public void setStub(ChaincodeStub stub) {
        this.stub = stub;
    }
    public void updatePatientListFromLedger() {

        fabricClient.invokeGetPatientList(client,channel);
        //medicalChaincode.getPatientList(stub);
    }

    public void updateDoctorListFromLedger() {
        fabricClient.invokeGetDoctorList(client,channel);
        //medicalChaincode.getDoctorList(stub);
    }
}