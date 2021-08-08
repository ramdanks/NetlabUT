package com.NetlabUT;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** a frame that shows a detail of a class including inheritance, declaration, implemented interfaces
 * @author Ramadhan Kalih Sewu
 * @version 1.0
 */
public class ClassDetail extends JFrame
{
    private final Class<?> mClass;

    private JLabel labelType;
    private JLabel labelName;
    private JLabel labelPackage;
    private JPanel mainPanel;
    private JList<String> listInheritance;
    private JList<String> listInterface;
    private JList<String> listClasses;
    private JList<String> listFields;
    private JList<String> listMethods;

    /** expect not null class */
    public ClassDetail(Class<?> tClass)
    {
        this.mClass = tClass;

        setTitle("Class Detail - " + tClass.getName());
        setContentPane(mainPanel);
        setMinimumSize(getMinimumSize());
        setSize(getMinimumSize());

        listInheritance.setModel(new DefaultListModel<String>());
        listInterface.setModel(new DefaultListModel<String>());
        listClasses.setModel(new DefaultListModel<String>());
        listFields.setModel(new DefaultListModel<String>());
        listMethods.setModel(new DefaultListModel<String>());

        setClass(tClass);
        setVisible(true);
    }

    public JPanel getMainPanel() { return mainPanel; }
    public void setClass(Class<?> tClass)
    {
        labelType.setText(tClass.isInterface() ? "Interface" : "Class");
        labelName.setText(tClass.getName());
        labelPackage.setText(tClass.getPackageName());

        DefaultListModel<String> modelInterface   = (DefaultListModel<String>) listInterface.getModel();
        DefaultListModel<String> modelInheritance = (DefaultListModel<String>) listInheritance.getModel();
        DefaultListModel<String> modelClasses     = (DefaultListModel<String>) listClasses.getModel();
        DefaultListModel<String> modelFields      = (DefaultListModel<String>) listFields.getModel();
        DefaultListModel<String> modelMethods     = (DefaultListModel<String>) listMethods.getModel();

        int inheritanceLength = 0;
        for (Class c = tClass.getSuperclass(); c != null; c = c.getSuperclass(), inheritanceLength++)
            modelInheritance.addElement(c.getName());
        listInheritance.setVisibleRowCount(inheritanceLength);

        final Class<?>[] interfaces = tClass.getInterfaces();
        listInterface.setVisibleRowCount(interfaces.length);
        for (int i = 0; i < interfaces.length; i++)
            modelInterface.addElement(interfaces[i].getName());

        final Class<?>[] classes = tClass.getDeclaredClasses();
        listClasses.setVisibleRowCount(classes.length);
        for (int i = 0; i < classes.length; i++)
            modelClasses.addElement(classes[i].getName());

        final Field[] fields = tClass.getDeclaredFields();
        listFields.setVisibleRowCount(fields.length);
        for (int i = 0; i < fields.length; i++)
            modelFields.addElement(fields[i].toString());

        final Method[] methods = tClass.getDeclaredMethods();
        listMethods.setVisibleRowCount(methods.length);
        for (int i = 0; i < methods.length; i++)
            modelMethods.addElement(methods[i].toString());
    }

}
