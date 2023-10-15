package org.example;

import org.hyperledger.fabric.shim.ChaincodeStub;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
public class AdminGui extends JFrame {

    private JTextField firstNameField, lastNameField, dobField, genderField, addressField, numberField, qualificationField;
    private JComboBox<String> roleComboBox;

    private JTable table;
    private JScrollPane tableScrollPane;
    private JButton showPatientsButton, showDoctorsButton;
    private Admin admin;
    public AdminGui(Admin admin) {

        this.admin = admin;
        setTitle("Admin");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                // Dispose the AdminGui
                dispose();
                // Create and display a new LoginGui
                new LoginGui(admin);
            }
        });

        JPanel formPanel = createFormPanel();
        JPanel listPanel = createListPanel();

        add(formPanel, BorderLayout.CENTER);
        add(listPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null); // Open the frame in the middle of the screen
        setVisible(true);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(9, 2));

        panel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        panel.add(firstNameField);

        panel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        panel.add(lastNameField);

        panel.add(new JLabel("DOB (dd-MM-yyyy):"));
        dobField = new JTextField();
        panel.add(dobField);

        panel.add(new JLabel("Gender:"));
        genderField = new JTextField();
        panel.add(genderField);

        panel.add(new JLabel("Address:"));
        addressField = new JTextField();
        panel.add(addressField);

        panel.add(new JLabel("Number:"));
        numberField = new JTextField();
        panel.add(numberField);

        panel.add(new JLabel("Role:"));
        roleComboBox = new JComboBox<>(new String[]{"doctor", "patient"});
        panel.add(roleComboBox);

        panel.add(new JLabel("Qualification (doctor only):"));
        qualificationField = new JTextField();
        panel.add(qualificationField);

        JButton addButton = new JButton("Add User");
        addButton.addActionListener(new AddButtonListener());
        panel.add(addButton);

        return panel;
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Initialize table with an empty model
        table = new JTable(new DefaultTableModel());
        table.setPreferredScrollableViewportSize(new Dimension(700, 150));
        table.setFillsViewportHeight(true);

        tableScrollPane = new JScrollPane(table);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        showPatientsButton = new JButton("Show Patients");
        showPatientsButton.addActionListener(new ShowPatientsButtonListener());
        buttonPanel.add(showPatientsButton);

        showDoctorsButton = new JButton("Show Doctors");
        showDoctorsButton.addActionListener(new ShowDoctorsButtonListener());
        buttonPanel.add(showDoctorsButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private class AddButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String dob = dobField.getText();
            String gender = genderField.getText();
            String address = addressField.getText();
            String number = numberField.getText();
            String qualification = qualificationField.getText();
            String role = (String) roleComboBox.getSelectedItem();

            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
            lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);



            if (role.equals("doctor")) {
                if (firstName.isEmpty() || lastName.isEmpty() || dob.isEmpty() || gender.isEmpty() || address.isEmpty() || number.isEmpty() || qualification.isEmpty()) {
                    JOptionPane.showMessageDialog(null,"Please fill all the required fields.");
                    throw new IllegalArgumentException("Please fill all the required fields.");
                }
                if (!dob.matches("\\d{2}-\\d{2}-\\d{4}")) {
                    JOptionPane.showMessageDialog(null, "Invalid date format. Please enter the date in DD-MM-YYYY format.");
                    throw new IllegalArgumentException("Invalid date format. Please enter the date in DD-MM-YYYY format.");
                }
                admin.addDoctor(firstName, lastName, dob, gender, address, number, qualification);
                showDoctorsButton.doClick();
            } else if (role.equals("patient")) {
                if (firstName.isEmpty() || lastName.isEmpty() || dob.isEmpty() || gender.isEmpty() || address.isEmpty() || number.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all the required fields.");
                    throw new IllegalArgumentException("Please fill all the required fields.");
                }
                if (!dob.matches("\\d{2}-\\d{2}-\\d{4}")) {
                    JOptionPane.showMessageDialog(null, "Invalid date format. Please enter the date in DD-MM-YYYY format.");
                    throw new IllegalArgumentException("Invalid date format. Please enter the date in DD-MM-YYYY format.");
                }
                admin.addPatient(firstName, lastName, dob, gender, address, number);
                showPatientsButton.doClick();
            }

        }
    }

    private class ShowPatientsButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<Patient> patients = admin.getPatientList();

            DefaultTableModel patientTableModel = new DefaultTableModel();

            patientTableModel.setRowCount(0);

            patientTableModel.addColumn("ID");
            patientTableModel.addColumn("First Name");
            patientTableModel.addColumn("Last Name");
            patientTableModel.addColumn("DOB");
            patientTableModel.addColumn("Gender");
            patientTableModel.addColumn("Address");
            patientTableModel.addColumn("Number");

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
            table.setModel(patientTableModel);
        }
    }

    private class ShowDoctorsButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultTableModel doctorTableModel = new DefaultTableModel();
            doctorTableModel.addColumn("ID");
            doctorTableModel.addColumn("First Name");
            doctorTableModel.addColumn("Last Name");
            doctorTableModel.addColumn("DOB");
            doctorTableModel.addColumn("Gender");
            doctorTableModel.addColumn("Address");
            doctorTableModel.addColumn("Number");
            doctorTableModel.addColumn("Qualification");

            for (Doctor doctor : admin.getDoctorList()) {
                doctorTableModel.addRow(new Object[]{
                        doctor.getDoctorID(),
                        doctor.getFirstName(),
                        doctor.getLastName(),
                        doctor.getDob(),
                        doctor.getGender(),
                        doctor.getAddress(),
                        doctor.getNumber(),
                        doctor.getQualification()
                });
            }
            table.setModel(doctorTableModel);
        }
    }


}

