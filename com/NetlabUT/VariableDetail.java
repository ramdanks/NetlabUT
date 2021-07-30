package com.NetlabUT;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class VariableDetail<T>
{
    private JTextArea taToString;
    private JLabel labelIdentifier;
    private JPanel mainPanel;
    private JButton buttonClassDetail;
    private JLabel labelType;
    private JScrollPane paneStringForm;
    private JFormattedTextField formattedTextField1;
    private JTabbedPane tabbedPane1;

    private Class<T> mClass;

    public VariableDetail()
    {
        buttonClassDetail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClassDetail(mClass);
            }
        });
    }

    public void setContent(Class<T> tClass)
    {
        this.mClass = tClass;
        taToString.setText("");
        labelIdentifier.setText(tClass.getName());
        labelType.setText("Class");
    }
    public void setContent(T object)
    {
        this.mClass = (Class<T>) object.getClass();
        labelIdentifier.setText(Profile.getObjectIdentifierString(object));
        if (object instanceof Throwable)
        {
            TitledBorder border = (TitledBorder) paneStringForm.getBorder();
            border.setTitle("Get Message");
            taToString.setText(((Throwable) object).getMessage());
            labelType.setText("Object Throwable");
        }
        else
        {
            taToString.setText(object.toString());
            labelType.setText("Object");
        }
    }
}
