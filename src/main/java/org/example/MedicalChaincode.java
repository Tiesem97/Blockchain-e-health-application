package org.example;

import com.owlike.genson.Context;
import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ResponseUtils;

import java.util.ArrayList;
import java.util.List;

@Contract
@Default
public class MedicalChaincode extends ChaincodeBase {

    private final Genson genson = new Genson();
    private Admin admin;
    private Channel channel;
    private HFClient client;


    public MedicalChaincode() {

        admin = new Admin(this,channel,client);


    }

    @Transaction
    public String initLedger(ChaincodeStub stub) {



        return "Ledger Initialized";
    }

    @Transaction
    public void storePatientList(ChaincodeStub stub) {
        String patientListJson = genson.serialize(admin.getPatientList());
        stub.putStringState("PATIENT_LIST", patientListJson);
    }


    @Transaction
    public void storeDoctorList(ChaincodeStub stub) {
        String doctorListJson = genson.serialize(admin.getDoctorList());
        stub.putStringState("DOCTOR_LIST", doctorListJson);
    }

    @Transaction
    public String getPatientList(ChaincodeStub stub) {
        String patientListJson = stub.getStringState("PATIENT_LIST");
        ArrayList<Patient> patientList = genson.deserialize(patientListJson, new GenericType<ArrayList<Patient>>() {});
        admin.setPatientList(patientList);
        return patientListJson;
    }

    @Transaction
    public String getDoctorList(ChaincodeStub stub) {
        String doctorListJson = stub.getStringState("DOCTOR_LIST");
        ArrayList<Doctor> doctorList = genson.deserialize(doctorListJson, new GenericType<ArrayList<Doctor>>() {});
        admin.setDoctorList(doctorList);
        return doctorListJson;
    }

    @Transaction
    public String storeMedicalRecords(ChaincodeStub stub, String patientID) {
        String patientKey = "PATIENT_" + patientID;
        String patientJson = stub.getStringState(patientKey);

        if (patientJson.isEmpty()) {
            return "Patient not found";
        }
        ArrayList<MedicalRecords> medicalRecords = admin.getPatientMedicalRecords(patientID);
        String medicalRecordsJson = genson.serialize(medicalRecords);

        stub.putStringState(patientKey, medicalRecordsJson);

        return "Medical records added";
    }


//********************************************************************************************************************

    @Transaction
    public ArrayList<MedicalRecords> getMedicalRecordsFromLedger(ChaincodeStub stub, String patientID) {
        String patientKey = "PATIENT_" + patientID;
        String medicalRecordsJson = stub.getStringState(patientKey);

        if (medicalRecordsJson.isEmpty()) {
            return null;
        }

        ArrayList<MedicalRecords> medicalRecords = genson.deserialize(medicalRecordsJson, new GenericType<ArrayList<MedicalRecords>>() {});
        return medicalRecords;
    }


    private String queryByObjectType(ChaincodeStub stub, String objectType) {
        String queryString = "{\"selector\":{\"_id\":{\"$regex\":\"^" + objectType + "\"}}}";
        return stub.getQueryResult(queryString).toString();
    }

    @Override
    public Response init(ChaincodeStub stub) {
        return ResponseUtils.newSuccessResponse(initLedger(stub));
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        String function = stub.getFunction();
        String[] args = stub.getParameters().toArray(new String[0]);
        admin.setStub(stub);

        switch (function) {
            case "initLedger":
                return ResponseUtils.newSuccessResponse(initLedger(stub));
            case "storePatientList":
                Patient patient = genson.deserialize(args[0], Patient.class);
                admin.getPatientList().add(patient);
                storePatientList(stub);
                return ResponseUtils.newSuccessResponse("Patient stored");
            case "storeDoctorList":
                Doctor doctor = genson.deserialize(args[0], Doctor.class);
                admin.getDoctorList().add(doctor);
                storeDoctorList(stub);
                return ResponseUtils.newSuccessResponse("Doctor stored");
            case "storeMedicalRecords":
                return ResponseUtils.newSuccessResponse(storeMedicalRecords(stub, args[0]));
            case "getPatientList":
                return ResponseUtils.newSuccessResponse(getPatientList(stub));
            case "getDoctorList":
                return ResponseUtils.newSuccessResponse(getDoctorList(stub));
            case "getMedicalRecords":
                ArrayList<MedicalRecords> medicalRecords = getMedicalRecordsFromLedger(stub, args[0]);
                if (medicalRecords != null) {
                    String medicalRecordsJson = genson.serialize(medicalRecords);
                    return ResponseUtils.newSuccessResponse(medicalRecordsJson);
                } else {
                    return ResponseUtils.newErrorResponse("Medical records not found for patient ID: " + args[0]);
                }
            default:
                return ResponseUtils.newErrorResponse("Invalid function name: " + function);
        }
    }
}
