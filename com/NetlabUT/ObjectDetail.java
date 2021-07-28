package com.NetlabUT;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjectDetail
{
    private JTextArea taToString;
    private JLabel labelIdentifier;
    private JPanel mainPanel;
    private JButton buttonClassDetail;
    private JButton button1;
    private JFormattedTextField formattedTextField1;
    private JTabbedPane tabbedPane1;

    private Object object;

    public  ObjectDetail()
    {
        buttonClassDetail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClassDetail(object.getClass());
            }
        });
    }
    public void setObject(Object object)
    {
        this.object = object;
        taToString.setText(object.toString());
        labelIdentifier.setText(Profile.getObjectIdentifierString(object));
    }
}
