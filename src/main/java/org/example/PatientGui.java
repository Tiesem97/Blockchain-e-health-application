package org.example;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;


public class PatientGui extends JFrame {

    private JTable recordsTable;
    private DefaultTableModel recordsTableModel;
    private JList<Doctor> doctorsList;
    private DefaultListModel<Doctor> doctorsListModel;
    private JButton grantAccessButton;
    private JButton revokeAccessButton;


    private JPanel chatPanel;
    private DefaultListModel<String> chatListModel;
    private JList<String> chatList;
    private JScrollPane chatScrollPane;
    private JButton sendMessageButton;
    private JButton endConversationButton;

    private Admin admin;
    private JTextArea chatArea;
    private JTextField messageInput;
    private JButton sendButton;

    public PatientGui(Patient patient, ArrayList<Doctor> allDoctors, Admin admin) {
        this.admin =admin;


        setTitle("Patient: " + patient.getFirstName() + " " + patient.getLastName());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1300, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());

        createTopPanel(patient, mainPanel);



        // Records table
        recordsTableModel = new DefaultTableModel(new Object[]{"Date", "Diagnosis", "Treatment", "Doctor ID"}, 0);
        recordsTable = new JTable(recordsTableModel);
        JScrollPane recordsScrollPane = new JScrollPane(recordsTable);
        mainPanel.add(recordsScrollPane, BorderLayout.CENTER);


        // Doctors list
        doctorsListModel = new DefaultListModel<>();
        for (Doctor doctor : allDoctors) {
            doctorsListModel.addElement(doctor);
        }
        doctorsList = new JList<>(doctorsListModel);
        doctorsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Doctor doctor = (Doctor) value;
                String doctorAccess = doctor.hasAccessToPatient(patient) ? "Access granted" : "Access not granted";
                label.setText(doctor.getDoctorID() + " - " + doctor.getFirstName() + " " + doctor.getLastName() + " - " + doctor.getQualification() + " - " + doctorAccess +" - Rating:"+String.format("%.2f",doctor.getAverageRating()));
                return label;
            }
        });
        JScrollPane doctorsScrollPane = new JScrollPane(doctorsList);
        doctorsScrollPane.setPreferredSize(new Dimension(600, 0));
        mainPanel.add(doctorsScrollPane, BorderLayout.EAST);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1));
        grantAccessButton = new JButton("Grant Access");
        revokeAccessButton = new JButton("Revoke Access");
        buttonsPanel.add(grantAccessButton);
        buttonsPanel.add(revokeAccessButton);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        updateRecordsTable(patient);

        grantAccessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Doctor selectedDoctor = doctorsList.getSelectedValue();
                if (selectedDoctor != null) {
                    patient.authorizeDoctor(selectedDoctor);
                    updateRecordsTable(patient);
                    refreshDoctorList(allDoctors, patient);
                }
            }
        });

        revokeAccessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Doctor selectedDoctor = doctorsList.getSelectedValue();
                if (selectedDoctor != null) {
                    patient.revokeAuthorization(selectedDoctor);
                    updateRecordsTable(patient);
                    refreshDoctorList(allDoctors, patient);
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                // Dispose the PatientGui
                PatientGui.this.dispose();
                // Create and display a new LoginGui
                new LoginGui(admin);
            }
        });
        createChatPanel(patient, mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void createChatPanel(Patient patient, JPanel mainPanel) {
        chatPanel = new JPanel(new BorderLayout());
        chatPanel.setPreferredSize(new Dimension(300, 0)); // Set a preferred size for the chat panel

        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);
        chatScrollPane = new JScrollPane(chatList);
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

        // Modify the layout of chatControlsPanel
        JPanel chatControlsPanel = new JPanel();
        chatControlsPanel.setLayout(new BoxLayout(chatControlsPanel, BoxLayout.Y_AXIS));
        JPanel messageInputPanel = new JPanel(new FlowLayout());
        JPanel buttonsPanel = new JPanel(new FlowLayout());

        messageInput = new JTextField(20);
        messageInputPanel.add(messageInput);

        sendMessageButton = new JButton("Send Message");
        endConversationButton = new JButton("End Conversation");
        buttonsPanel.add(sendMessageButton);
        buttonsPanel.add(endConversationButton);

        chatControlsPanel.add(messageInputPanel);
        chatControlsPanel.add(buttonsPanel);

        chatPanel.add(chatControlsPanel, BorderLayout.SOUTH);

        mainPanel.add(chatPanel, BorderLayout.WEST);

        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(patient.getAuthorizedDoctorsList().contains(doctorsList.getSelectedValue())){
                    Doctor selectedDoctor = doctorsList.getSelectedValue();
                    if (selectedDoctor != null) {
                        String message = messageInput.getText();
                        if (!message.isEmpty()) {
                            // Save the message in the patient's conversation with the doctor
                            patient.sendMessage(selectedDoctor, message,false);
                            chatListModel.addElement(patient.getFirstName()+ ": "+ message);
                            messageInput.setText("");
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"Authorize Doctor to start conversation!");
                }

            }
        });

        endConversationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Doctor selectedDoctor = doctorsList.getSelectedValue();
                if (selectedDoctor != null) {
                    patient.endConversation(selectedDoctor);
                    chatListModel.clear();
                    JOptionPane.showMessageDialog(null,"End of conversation! The chat will be cleared");
                    RatingGui ratingGui = new RatingGui(patient,selectedDoctor);
                }
            }
        });

        doctorsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Doctor selectedDoctor = doctorsList.getSelectedValue();
                    if (selectedDoctor != null) {
                        ArrayList<String> conversation = patient.getConversation(selectedDoctor);
                        chatListModel.clear();
                        for (String message : conversation) {
                            chatListModel.addElement(patient.getFirstName()+ ": "+ message);
                        }
                    } else {
                        chatListModel.clear(); // Clear the chat list if no doctor is selected
                    }
                }
            }
        });
    }
    private void createTopPanel(Patient patient, JPanel mainPanel) {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(100, 149, 237));
        topPanel.setLayout(new FlowLayout());

        JLabel welcomeLabel = new JLabel("Welcome, " + patient.getFirstName() + " " + patient.getLastName() + " (ID: " + patient.getPatientID() + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(welcomeLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
    }

    private void updateRecordsTable(Patient patient) {
        ArrayList<MedicalRecords> recordsData = patient.viewMedicalRecords(patient);
        recordsTableModel.setRowCount(0);

        for (MedicalRecords recordData : recordsData) {
            recordsTableModel.addRow(new Object[]{
                    recordData.getDate(),
                    recordData.getDiagnosis(),
                    recordData.getTreatment(),
                    recordData.getPrescription(),
                    recordData.getDoctor()
            });
        }
    }

    private void refreshDoctorList(ArrayList<Doctor> allDoctors, Patient patient) {
        doctorsListModel.clear();
        for (Doctor doctor : allDoctors) {
            doctorsListModel.addElement(doctor);
        }
    }


}
