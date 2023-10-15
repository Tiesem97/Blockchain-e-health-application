package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RatingGui extends JFrame {
    private Patient patient;
    private Doctor doctor;

    public RatingGui(Patient patient, Doctor doctor) {
        this.patient = patient;
        this.doctor = doctor;

        setTitle("Rate Doctor");
        setSize(200, 100);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel ratingPanel = new JPanel();
        ratingPanel.setLayout(new BoxLayout(ratingPanel, BoxLayout.Y_AXIS));

        JLabel rateLabel = new JLabel("Rate your doctor (" + doctor.getDoctorID() + "):");
        ratingPanel.add(rateLabel);

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0.0, 0.0, 5.0, 0.1);
        JSpinner ratingSpinner = new JSpinner(spinnerModel);
        ratingPanel.add(ratingSpinner);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double rating = (double) ratingSpinner.getValue();
                patient.rateDoctor(doctor, rating);
                JOptionPane.showMessageDialog(null, "Thank you for your feedback!");
                RatingGui.this.dispose();
            }
        });
        ratingPanel.add(submitButton);

        add(ratingPanel, BorderLayout.CENTER);
    }
}
