package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class DoctorGui extends JFrame {
    private JTable table;
    private JTable tableRec;
    private JScrollPane tableScrollPane;
    private JScrollPane tableScrollPaneRec;
    private Doctor doctor;
    private MedicalChaincode medicalChaincode;
    private Admin admin;
    public DoctorGui(Doctor doctor, Admin admin) {
        this.doctor = doctor;
        this.admin =admin;
        this.medicalChaincode = admin.getMedicalChaincode();


        setTitle("Doctor Dashboard");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        createTopPanel();
        createCenterPanel();

        setLocationRelativeTo(null);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                dispose();
                // Create and display a new LoginGui
                new LoginGui(admin);
            }
        });

    }

    private void createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(100, 149, 237));
        topPanel.setLayout(new FlowLayout());

        JLabel welcomeLabel = new JLabel("Welcome, Dr. " + doctor.getFirstName() + " " + doctor.getLastName() + " (ID: " + doctor.getDoctorID() + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(welcomeLabel);

        add(topPanel, BorderLayout.NORTH);
    }

    private void createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        table = new JTable();
        tableRec =new JTable();

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(100, 149, 237));
        searchPanel.setLayout(new FlowLayout());
        JLabel searchLabel = new JLabel("Enter Patient ID: ");
        JTextField searchField = new JTextField(10);
        JButton searchButton = new JButton("Search");
        JButton allPatientsButton = new JButton("View all patients");

        table.setPreferredScrollableViewportSize(new Dimension(700, 150));
        tableScrollPane = new JScrollPane(table);

        tableRec.setPreferredScrollableViewportSize(new Dimension(700, 150));
        tableScrollPaneRec = new JScrollPane(tableRec);




        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(allPatientsButton);


        JPanel recordsPanel = new JPanel();
        recordsPanel.setBackground(new Color(100, 149, 237));
        recordsPanel.setLayout(new BorderLayout());
        JLabel recordsLabel = new JLabel("Medical Records");
        recordsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        recordsPanel.add(recordsLabel, BorderLayout.NORTH);


        JPanel patientsPanel = new JPanel();
        patientsPanel.setBackground(new Color(100, 149, 237));
        patientsPanel.setLayout(new BorderLayout());
        JLabel patientsLabel = new JLabel("Patients");
        patientsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        patientsPanel.add(patientsLabel, BorderLayout.NORTH);


        DefaultTableModel patientTableModel = new DefaultTableModel();
        patientTableModel.setRowCount(0);
        patientTableModel.addColumn("ID");
        patientTableModel.addColumn("First Name");
        patientTableModel.addColumn("Last Name");
        patientTableModel.addColumn("DOB");
        patientTableModel.addColumn("Gender");
        patientTableModel.addColumn("Address");
        patientTableModel.addColumn("Number");

        table.setModel(patientTableModel);

        DefaultTableModel recTableModel = new DefaultTableModel();
        recTableModel.setRowCount(0);

        recTableModel.addColumn("Date");
        recTableModel.addColumn("Diagnosis");
        recTableModel.addColumn("Treatment");
        recTableModel.addColumn("Prescription");
        recTableModel.addColumn("DoctorID");
        tableRec.setModel(recTableModel);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String patientId = searchField.getText();
                Patient patient = admin.getPatientById(patientId);

                if (patient != null) {
                    // Clear the table
                    ArrayList<String[]> medicalRecordsData = patient.getDecryptedMedicalRecordsData(doctor);

                    patientTableModel.setRowCount(0);
                    recTableModel.setRowCount(0);

                    // Add the patient's information as a new row
                    patientTableModel.addRow(new Object[]{
                            patient.getPatientID(),
                            patient.getFirstName(),
                            patient.getLastName(),
                            patient.getDob(),
                            patient.getGender(),
                            patient.getAddress(),
                            patient.getNumber()
                    });

                    for (String[] recordData : medicalRecordsData) {
                        recTableModel.addRow(recordData);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Patient not found.");
                }
            }
        });

        allPatientsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                patientTableModel.setRowCount(0);
                recTableModel.setRowCount(0);
                ArrayList<Patient> patients = admin.getPatientList();
                for (Patient patient : patients) {
                    patientTableModel.addRow(new Object[]{
                            patient.getPatientID(),
                            patient.getFirstName(),
                            patient.getLastName(),
                            patient.getDob(),
                            patient.getGender(),
                            patient.getAddress(),
                            patient.getNumber()
                    });
                }
            }
        });

        table.setFillsViewportHeight(true);
        patientsPanel.add(tableScrollPane, BorderLayout.CENTER);

        tableRec.setFillsViewportHeight(true);
        recordsPanel.add(tableScrollPaneRec, BorderLayout.CENTER);

        JPanel newRecordPanel = new JPanel();
        newRecordPanel.setBackground(new Color(100, 149, 237));
        newRecordPanel.setLayout(new BorderLayout());

        JLabel newRecordLabel = new JLabel("Add New Medical Record");
        newRecordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        newRecordPanel.add(newRecordLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel patientIdLabel = new JLabel("Patient ID:");
        JTextField patientIdField = new JTextField(10);
        JLabel dateLabel = new JLabel("Date:");
        JTextField dateField = new JTextField(10);
        JLabel diagnosisLabel = new JLabel("Diagnosis:");
        JTextField diagnosisField = new JTextField(10);
        JLabel treatmentLabel = new JLabel("Treatment:");
        JTextField treatmentField = new JTextField(10);
        JLabel prescriptionLabel = new JLabel("Prescription:");
        JTextField prescriptionField = new JTextField(10);

        inputPanel.add(patientIdLabel);
        inputPanel.add(patientIdField);
        inputPanel.add(dateLabel);
        inputPanel.add(dateField);
        inputPanel.add(diagnosisLabel);
        inputPanel.add(diagnosisField);
        inputPanel.add(treatmentLabel);
        inputPanel.add(treatmentField);
        inputPanel.add(prescriptionLabel);
        inputPanel.add(prescriptionField);
        newRecordPanel.add(inputPanel, BorderLayout.CENTER);

        JButton addButton = new JButton("Add Record");
        newRecordPanel.add(addButton, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String patientId = patientIdField.getText();
                String dateString = dateField.getText();
                String diagnosis = diagnosisField.getText();
                String treatment = treatmentField.getText();
                String prescription = prescriptionField.getText();

                if (patientId.isEmpty() || dateString.isEmpty() || diagnosis.isEmpty() || treatment.isEmpty()||prescription.isEmpty()) {
                    throw new IllegalArgumentException("Please fill all the required fields.");
                }

                if (!dateString.matches("\\d{2}-\\d{2}-\\d{4}")) {
                    throw new IllegalArgumentException("Invalid date format. Please enter the date in DD-MM-YYYY format.");
                }

                Patient patient = admin.getPatientById(patientId);

                if (patient != null) {
                    doctor.createMedicalRecord(patient, dateString, diagnosis, treatment,prescription,  doctor.getDoctorID());
                    refreshMedicalRecordsTable(patient);
                } else {
                    JOptionPane.showMessageDialog(null, "Patient not found.");
                }
                refreshMedicalRecordsTable(patient);
            }
        });

        mainPanel.add(searchPanel);
        mainPanel.add(patientsPanel);
        mainPanel.add(newRecordPanel);
        mainPanel.add(recordsPanel);


        centerPanel.add(mainPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

    }

    private void createChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setPreferredSize(new Dimension(300, 400));
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField inputField = new JTextField();
        inputPanel.add(inputField, BorderLayout.CENTER);

        JButton sendMessageButton = new JButton("Send");
        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String patientId = (String) table.getValueAt(selectedRow, 0);
                    Patient patient = admin.getPatientById(patientId);
                    if (patient != null) {
                        String message = inputField.getText();
                        if (!message.trim().isEmpty()) {
                            // Save the message in the patient's conversation with the doctor
                            patient.sendMessage(doctor, message,true);
                            updateChatArea(chatArea, patient);
                            inputField.setText("");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please select a patient.");
                    }
                }
            }
        });
        inputPanel.add(sendMessageButton, BorderLayout.EAST);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String patientId = (String) table.getValueAt(selectedRow, 0);
                    Patient patient = admin.getPatientById(patientId);
                    if (patient != null) {
                        updateChatArea(chatArea, patient);
                    }
                }
            }
        });

        add(chatPanel, BorderLayout.EAST);
    }
    private void updateChatArea(JTextArea chatArea, Patient patient) {
        chatArea.setText("");
        ArrayList<String> conversation = patient.getConversation(doctor);
        if (conversation != null) {
            for (String message : conversation) {
                chatArea.append(message + "\n");
            }
        }
    }
    private void refreshMedicalRecordsTable(Patient patient) {
        DefaultTableModel recTableModel = (DefaultTableModel) tableRec.getModel();
        recTableModel.setRowCount(0);

        ArrayList<String[]> medicalRecordsData = patient.getDecryptedMedicalRecordsData(doctor);
        for (String[] recordData : medicalRecordsData) {
            recTableModel.addRow(recordData);
        }
    }

}
