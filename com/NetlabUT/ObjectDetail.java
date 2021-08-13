package com.NetlabUT;

import javax.swing.*;

public class ObjectDetail extends JFrame
{
    private final Object mObject;

    private JPanel mainPanel;
    private JTextArea textString;
    private JLabel labelClass;

    public ObjectDetail(Object object)
    {
        this.mObject = object;

        labelClass.setText(object.getClass().getName());
        textString.setText(object.toString());

        setTitle("Object - " + StringFormatter.idString(object));
        setContentPane(mainPanel);
        setMinimumSize(mainPanel.getMinimumSize());
        setSize(mainPanel.getMinimumSize());
        setVisible(true);
    }
}
