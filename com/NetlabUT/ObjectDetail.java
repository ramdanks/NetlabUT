package com.NetlabUT;

import javax.swing.*;

public class ObjectDetail extends JFrame
{
    private JPanel mainPanel;
    private JTextArea textString;
    private JLabel labelClass;

    public ObjectDetail(Object object)
    {
        labelClass.setText(object.getClass().getName());
        textString.setText(object.toString());

        setTitle(Profile.getObjectIdentifierString(object));
        setContentPane(mainPanel);
        setMinimumSize(mainPanel.getMinimumSize());
        setSize(mainPanel.getMinimumSize());
        setVisible(true);
    }
}
