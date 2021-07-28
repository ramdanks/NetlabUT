package com.NetlabUT;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ProfileFrame<T> extends JFrame
{
    private JPanel mainPanel;
    private JLabel labelCorrect;
    private JLabel labelStatus;
    private JLabel labelTime;
    private ObjectDetail panelActual;
    private ObjectDetail panelReference;
    private JTabbedPane tabbedPane1;
    private JTextArea textMessage;

    private Profile<T> profile;

    public ProfileFrame(Profile<T> profile)
    {
        this.profile = profile;
        setContentPane(mainPanel);
        Metric<T> metric = profile.getMetric();

        labelCorrect.setText(profile.isCorrect() ? "Correct" : "Wrong");
        labelStatus.setText(profile.getReferenceStatus().toString());
        labelTime.setText(String.format("%d ns", metric.nanoTime));
        textMessage.setText(profile.getMessage());

        panelActual.setObject(metric.returns);
        panelReference.setObject(profile.getReference());

        setSize(getMinimumSize());
        setMinimumSize(getMinimumSize());
        setVisible(true);
    }
}
