package org.example;

import org.hyperledger.fabric.shim.ChaincodeStub;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.*;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Patient{
    private String patientID;
    private String firstName;
    private String lastName;
    private Date dob;
    private String gender;
    private String address;
    private String number;

    private Admin admin;
    private ArrayList<Chat> chatHistory;
    private ArrayList<MedicalRecords> medicalRecord;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    private ArrayList<Doctor> authorizedDoctorsList;
    private Map<Doctor, ArrayList<String>> conversations;
    private SecretKey key;
    private MedicalChaincode medicalChaincode;

    public Patient(Admin admin, String patientID, String firstName, String lastName, Date dob, String gender, String address, String number) {

        this.admin = admin;
        this.patientID = patientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.number = number;
        this.medicalRecord = new ArrayList<MedicalRecords>();
        this.authorizedDoctorsList = new ArrayList<Doctor>();
        conversations = new HashMap<>();

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            this.key = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
    public Patient(){
        this(null,"N/A","N/A","N/A",new java.util.Date(System.currentTimeMillis()),"N/A","N/A","N/A");
    }


    public void addMedicalRecord(MedicalRecords record, Doctor doctor) {
        admin.updatePatientMedicalRecordsFromLedger(getPatientID());

        if (authorizedDoctorsList.contains(doctor)){

            String encryptDiagnosis = Encryption.encrypt(record.getDiagnosis(), this.key);
            String encryptTreatment = Encryption.encrypt(record.getTreatment(), this.key);
            String encryptPrescription = Encryption.encrypt(record.getPrescription(), this.key);


            record.setDiagnosis(encryptDiagnosis);
            record.setTreatment(encryptTreatment);
            record.setPrescription(encryptPrescription);

            medicalRecord.add(record);
            JOptionPane.showMessageDialog(null, "Medical record added successfully.");

            admin.storePatientMedicalRecordsInLedger(getPatientID());
        }else {
            System.out.println("Doctor not authorized to add medical record");
            JOptionPane.showMessageDialog(null, "Doctor not authorized to add medical records.");
        }
    }

    public void rateDoctor(Doctor doctor, double rating) {
        if (rating >= 0 && rating <= 5) {
            doctor.updateRating(rating);
        } else {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
    }
    public void sendMessage(Doctor doctor, String message, boolean isDoctorSender) {
        if (!conversations.containsKey(doctor)) {
            conversations.put(doctor, new ArrayList<>());
        }
        if (isDoctorSender) {
            conversations.get(doctor).add("Doctor: " + message);
        } else {
            conversations.get(doctor).add("Patient: " + message);
        }
    }
    public void endConversation(Doctor doctor) {
        if (conversations.containsKey(doctor)) {
            conversations.remove(doctor);
        }
    }
    public ArrayList<String> getConversation(Doctor doctor) {
        return conversations.getOrDefault(doctor, new ArrayList<>());
    }
    public void authorizeDoctor(Doctor doctor) {


        this.authorizedDoctorsList.add(doctor);
    }

    public void revokeAuthorization(Doctor doctor) {
        this.authorizedDoctorsList.remove(doctor);
    }

    public void viewMedicalRecords(Doctor doctor) {
        admin.updatePatientMedicalRecordsFromLedger(getPatientID());
        if (authorizedDoctorsList.contains(doctor)) {
            for (MedicalRecords record : medicalRecord) {
                System.out.println(record.toString());
            }
        } else {
            System.out.println("Doctor not authorized to view medical records.");
            JOptionPane.showMessageDialog(null, "Doctor not authorized to view medical records.");
        }
    }


    public ArrayList<MedicalRecords>  viewMedicalRecords(Patient patient){
        admin.updatePatientMedicalRecordsFromLedger(getPatientID());

        ArrayList<MedicalRecords> patientRecords = new ArrayList<>();

        for (MedicalRecords record : medicalRecord) {
            String decryptedDiagnosis = Encryption.decrypt(record.getDiagnosis(), this.key);
            String decryptedTreatment = Encryption.decrypt(record.getTreatment(), this.key);
            String decryptedPrescription = Encryption.decrypt(record.getPrescription(), this.key);


            Date date = null;

            try {
                date = formatter.parse(record.getDate());

            } catch (ParseException e) {
                e.printStackTrace();
            }

            MedicalRecords decryptedRecord = new MedicalRecords(date, decryptedDiagnosis, decryptedTreatment,decryptedPrescription, record.getDoctor());
            patientRecords.add(decryptedRecord);
            System.out.println(patientID + decryptedRecord.toString());
        }
        return patientRecords;
    }
    public ArrayList<String[]> getDecryptedMedicalRecordsData(Doctor doctor) {
        admin.updatePatientMedicalRecordsFromLedger(getPatientID());

        ArrayList<String[]> decryptedRecordsData = new ArrayList<>();

        for (MedicalRecords record : medicalRecord) {

            String decryptedDiagnosis = null;
            String decryptedTreatment = null;
            String decryptedPrescription = null;
            String[] recordData= null;

            if (authorizedDoctorsList.contains(doctor)){
                decryptedDiagnosis = Encryption.decrypt(record.getDiagnosis(), this.key);
                decryptedTreatment = Encryption.decrypt(record.getTreatment(), this.key);
                decryptedPrescription = Encryption.decrypt(record.getPrescription(), this.key);
                recordData = new String[]{
                        record.getDate(),
                        decryptedDiagnosis,
                        decryptedTreatment,
                        decryptedPrescription,
                        record.getDoctor()
                };

            }else {
                //JOptionPane.showMessageDialog(null, "Doctor not authorized to view medical records.");
                recordData = new String[]{
                        record.getDate(),
                        record.getDiagnosis(),
                        record.getTreatment(),
                        record.getPrescription(),
                        record.getDoctor()
                };
            }
            // Create a new String array with the decrypted data

            decryptedRecordsData.add(recordData);
        }
        return decryptedRecordsData;
    }


    public void viewEncryptedMedicalRecords(){
        admin.updatePatientMedicalRecordsFromLedger(getPatientID());
        for (MedicalRecords record : medicalRecord) {
            System.out.println(patientID + record.toString());
        }
    }
    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
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


    public ArrayList<MedicalRecords> getMedicalRecord() {

        return medicalRecord;
    }

    public void setMedicalRecord(ArrayList<MedicalRecords> medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public ArrayList<Doctor> getAuthorizedDoctorsList() {
        return authorizedDoctorsList;
    }

    public void setAuthorizedDoctorsList(ArrayList<Doctor> authorizedDoctorsList) {
        this.authorizedDoctorsList = authorizedDoctorsList;
    }


}
