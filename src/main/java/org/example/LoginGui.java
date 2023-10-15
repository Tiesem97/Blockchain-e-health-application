package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGui extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    Admin admin;
    Doctor doctor = new Doctor();
    Patient patient = new Patient();

    private MedicalChaincode medicalChaincode;
    public LoginGui(Admin admin) {
        this.admin= admin;


        setTitle("Login");
        setLayout(new GridLayout(3, 2));

        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");

        usernameField = new JTextField(10);
        passwordField = new JPasswordField(10);

        loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginButtonListener());

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class LoginButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String userID = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (userID.equals("admin") && password.equals("password")) {
                JOptionPane.showMessageDialog(null, "Logged in as admin.");
                // Show the Admin GUI and close the login window
                new AdminGui(admin);
                dispose();
            } else {
                boolean validUser = false;
                boolean isDoctor = false;

                for (Patient p : admin.getPatientList()) {
                    if (p.getPatientID().equals(userID) && password.equals("patient")) {
                        validUser = true;
                        patient = p; // Assign the correct Patient object found in the list
                        break;
                    }
                }

                if (!validUser) {
                    for (Doctor doc : admin.getDoctorList()) {
                        if (doc.getDoctorID().equals(userID) && password.equals("doctor")) {
                            validUser = true;
                            isDoctor = true;
                            doctor = doc; // Assign the correct Doctor object found in the list
                            break;
                        }
                    }
                }

                if (validUser) {
                    if (isDoctor) {
                        JOptionPane.showMessageDialog(null, "Logged in as doctor.");
                        // Show the Doctor GUI and close the login window
                        new DoctorGui(doctor, admin);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Logged in as patient.");
                        // Show the Patient GUI and close the login window
                        new PatientGui(patient, admin.getDoctorList(), admin);
                        dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid user ID or password.");
                }
            }
        }
    }

}
