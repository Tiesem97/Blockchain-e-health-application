package org.example.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import org.example.Doctor;
import org.example.MedicalRecords;
import org.example.Patient;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;

public class FabricClient {
    public static Genson genson = new Genson();

    private static final String CHAINCODE_NAME = "SecureMedicalRec";
    private static final TransactionRequest.Type CHAINCODE_LANG = TransactionRequest.Type.JAVA;

    public static void main(String[] args) throws Exception {


        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());//read network configuration file
        NetworkConfig networkConfig = objectMapper.readValue(new FileInputStream("/home/tiesem/hyperledger-fabric/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/connection-org1.yaml"), NetworkConfig.class);

        // Set up the Hyperledger Fabric client
        HFClient client = HFClient.createNewInstance(); //used to interact with the blockchain
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(networkConfig.getPeerAdmin("Org1MSP")); // Replace Org1MSP with the appropriate organization MSP ID

        // Get the channel from the network configuration
        Channel channel = client.loadChannelFromConfig("medicalchannel", networkConfig);

        // Add an event listener for block events
        channel.registerBlockListener(blockEvent -> System.out.println("Received block event: " + blockEvent));

        // Initialize the channel
        channel.initialize();

        // Invoke the chaincode functions
        invokeInitLedger(client, channel);
        invokeStorePatientList(client, channel, new Patient());
        invokeStoreDoctorList(client, channel, new Doctor());
        invokeStoreMedicalRecords(client, channel, "patient_id_example");
        invokeGetPatientList(client, channel);
        invokeGetDoctorList(client, channel);
        invokeGetMedicalRecords(client, channel, "patient_id_example");

        // Close the channel and shut down the client
        channel.shutdown(true);
    }

    private static void invokeInitLedger(HFClient client, Channel channel) {
        invokeChaincode(client, channel, "initLedger");
    }

    public static void invokeStorePatientList(HFClient client, Channel channel, Patient patient) {
        String patientJson = genson.serialize(patient);
        invokeChaincode(client, channel, "storePatientList", patientJson);
    }

    public static void invokeStoreDoctorList(HFClient client, Channel channel, Doctor doctor) {
        String doctorJson = genson.serialize(doctor);
        invokeChaincode(client, channel, "storeDoctorList", doctorJson);
    }

    public static void invokeStoreMedicalRecords(HFClient client, Channel channel, String patientID) {
        invokeChaincode(client, channel, "storeMedicalRecords", patientID);
    }

    public static void invokeGetPatientList(HFClient client, Channel channel) {
        invokeChaincode(client, channel, "getPatientList");
    }

    public static void invokeGetDoctorList(HFClient client, Channel channel) {
        invokeChaincode(client, channel, "getDoctorList");
    }

    public static ArrayList<MedicalRecords> invokeGetMedicalRecords(HFClient client, Channel channel, String patientID) {
        String response = invokeChaincode(client, channel, "getMedicalRecords", patientID);
        if (response != null && !response.isEmpty()) {
            return genson.deserialize(response, new GenericType<ArrayList<MedicalRecords>>() {});
        }
        return null;
    }

    private static String invokeChaincode(HFClient client, Channel channel, String functionName, String... args) {
        try {
            TransactionProposalRequest request = client.newTransactionProposalRequest();
            request.setChaincodeName(CHAINCODE_NAME);
            request.setFcn(functionName);
            request.setArgs(args);
            request.setChaincodeLanguage(CHAINCODE_LANG);

            Collection<ProposalResponse> responses = channel.sendTransactionProposal(request);
            for (ProposalResponse response : responses) {
                if (response.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                    System.out.println("Transaction proposal for function '" + functionName + "' was successful: " + response.getMessage());
                } else {
                    System.out.println("Transaction proposal for function '" + functionName + "' failed: " + response.getMessage());
                }
            }
            channel.sendTransaction(responses);

            // Extract and return the response payload
            return responses.iterator().next().getProposalResponse().getResponse().getPayload().toStringUtf8();
        } catch (ProposalException | InvalidArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}
