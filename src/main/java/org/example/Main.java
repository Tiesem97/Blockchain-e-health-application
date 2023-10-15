package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;
import java.io.FileInputStream;

public class Main {
    public static void main(String[] args) {


        HFClient client = createHFClient();
        Channel channel = createChannel(client);


        MedicalChaincode medicalChaincode = new MedicalChaincode();
        Admin admin = new Admin(medicalChaincode,channel,client);

        LoginGui loginGui = new LoginGui(admin);
    }

    private static HFClient createHFClient() {

        try {
            // Load network configuration from a YAML or JSON file
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            NetworkConfig networkConfig = objectMapper.readValue(new FileInputStream("/home/tiesem/hyperledger-fabric/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/connection-org1.yaml"), NetworkConfig.class);
            // Set up the Hyperledger Fabric client
            HFClient client = HFClient.createNewInstance();
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            client.setUserContext(networkConfig.getPeerAdmin("Org1MSP")); // Replace Org1MSP with the appropriate organization MSP ID

            return client;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Channel createChannel(HFClient client) {
        try {
            // Load network configuration from a YAML or JSON file
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            NetworkConfig networkConfig = objectMapper.readValue(new FileInputStream("/home/tiesem/hyperledger-fabric/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/connection-org1.yaml"), NetworkConfig.class);

            // Get the channel from the network configuration
            Channel channel = client.loadChannelFromConfig("medicalchannel", networkConfig);

            // Add an event listener for block events
            channel.registerBlockListener(blockEvent -> System.out.println("Received block event: " + blockEvent));

            // Initialize the channel
            channel.initialize();

            return channel;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}