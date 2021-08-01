package com.NetlabUT;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** a frame that shows a detail of a class including inheritance, declaration, implemented interfaces
 * @author Ramadhan Kalih Sewu
 * @version 1.0
 */
final class ClassDetail extends JFrame
{
    private JLabel labelType;
    private JLabel labelName;
    private JLabel labelPackage;
    private JTabbedPane tabbedPane1;
    private JPanel mainPanel;
    private JList listInheritance;
    private JList listInterface;
    private JList listClasses;
    private JList listFields;
    private JList listMethods;
    private JScrollPane paneFields;

    public ClassDetail(Class<?> tClass)
    {
        setContentPane(mainPanel);
        setMinimumSize(getMinimumSize());
        setSize(getMinimumSize());

        listInheritance.setModel(new DefaultListModel());
        listInterface.setModel(new DefaultListModel());
        listClasses.setModel(new DefaultListModel());
        listFields.setModel(new DefaultListModel());
        listMethods.setModel(new DefaultListModel());

        setClass(tClass);
        setVisible(true);
    }

    public JPanel getMainPanel() { return mainPanel; }
    public void setClass(Class<?> tClass)
    {
        DefaultListModel modelInterface = (DefaultListModel) listInterface.getModel();
        DefaultListModel modelInheritance = (DefaultListModel) listInheritance.getModel();
        DefaultListModel modelClasses = (DefaultListModel) listClasses.getModel();
        DefaultListModel modelFields = (DefaultListModel) listFields.getModel();
        DefaultListModel modelMethods = (DefaultListModel) listMethods.getModel();

        labelType.setText(tClass.isInterface() ? "Interface" : "Class");
        labelName.setText(tClass.getName());
        labelPackage.setText(tClass.getPackageName());

        for (Class c = tClass.getSuperclass(); c != null; c = c.getSuperclass())
            modelInheritance.addElement(c.getName());

        final Class<?>[] interfaces = tClass.getInterfaces();
        for (int i = 0; i < interfaces.length; i++)
            modelInterface.addElement(interfaces[i].getName());

        final Class<?>[] classes = tClass.getDeclaredClasses();
        listClasses.setVisibleRowCount(classes.length);
        for (int i = 0; i < classes.length; i++)
            modelClasses.addElement(classes[i].getName());

        final Field[] fields = tClass.getDeclaredFields();
        listFields.setVisibleRowCount(fields.length);
        for (int i = 0; i < fields.length; i++)
            modelFields.addElement(fields[i]);

        final Method[] methods = tClass.getDeclaredMethods();
        listMethods.setVisibleRowCount(methods.length);
        for (int i = 0; i < methods.length; i++)
            modelMethods.addElement(methods[i]);
    }

}
