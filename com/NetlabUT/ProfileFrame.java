package com.NetlabUT;

import javax.swing.*;

public class ProfileFrame<T> extends JFrame
{
    private JPanel mainPanel;
    private JLabel labelCorrect;
    private JLabel labelStatus;
    private JLabel labelTime;
    private VariableDetail panelActual;
    private VariableDetail panelReference;
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

        Status status = profile.getReferenceStatus();
        if (status == Status.THROWS || status == Status.THROWS_TYPE)
        {
            if (metric.isThrowing()) panelActual.setContent(metric.throwable);
            else panelActual.setContent(metric.returns);
            panelReference.setContent(profile.getClassReference());
        }
        else
        {
            panelActual.setContent(metric.isThrowing() ? metric.throwable : metric.returns);
            panelReference.setContent(profile.getReference());
        }

        setSize(getMinimumSize());
        setMinimumSize(getMinimumSize());
        setVisible(true);
    }
}
