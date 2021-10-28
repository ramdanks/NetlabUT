package com.NetlabUT;

import javax.swing.*;

import org.reflections.Reflections;

import com.Reflector.ReflectTest;
import com.Reflector.ReflectTester;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

/** Class to display profile test from {@link com.NetlabUT.UnitTest} into a GUI
 * @author Ramadhan Kalih Sewu
 * @version 1.2
 */
public class WindowProfiler extends JFrame
{
    private JPanel mainPanel;
    private JLabel labelDate;
    private JLabel labelPoints;
    private JLabel labelPercentage;
    private JLabel labelTitle;
    private JTabbedPane tabUnitTest;
    private JButton btnAllTest;
    private JButton btnSelectedTest;
    private JComboBox<String> cbUnitTest;
    private ArrayList<ProfilingResultsForm> formProfileResults;

    private int totalSuccessCount = 0;
    private int totalTestCount = 0;

    public WindowProfiler(Class<?> sourceClass, String title) 
    		throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException,
    		InvocationTargetException, InstantiationException, NoSuchMethodException, SecurityException
    {
        setTitle("Unit Test Grading");
        setSize(mainPanel.getPreferredSize());
        setMinimumSize(mainPanel.getMinimumSize());
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        labelTitle.setText(title);
        labelDate.setText(new Date().toString());

        btnAllTest.addActionListener(this::onRunAllTest);
        btnSelectedTest.addActionListener(this::onRunSelectedTest);
        
        String packageName = sourceClass.getPackageName();
        
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(ReflectTester.class);
        for (Class<?> ut : classes)
        {
        	ReflectTester tester = ut.getAnnotation(ReflectTester.class);
        	Class<?> clazz = Class.forName(packageName + "." + tester.value());
        	Set<Method> methods = new Reflections(clazz).getMethodsAnnotatedWith(ReflectTest.class);
        	Object instance = clazz.getDeclaredConstructor().newInstance();
        	for (Method m : methods)
        	{
        		m.invoke(instance);
        	}
        }
    }

    public void add(UnitTest... unitTests)
    {
        if (formProfileResults == null)
            formProfileResults = new ArrayList<>(unitTests.length);
        for (int i = 0; i < unitTests.length; i++)
        {
            ProfilingResultsForm form = new ProfilingResultsForm(unitTests[i]);
            formProfileResults.add(form);
            tabUnitTest.addTab(unitTests[i].getTestName(), form.getContentPane());
            cbUnitTest.addItem(unitTests[i].getTestName());

            runUnitTest(i);
            refreshUnitTest(i);
        }
        refreshTestCount();
    }

    public int getTotalSuccessCount() { return totalSuccessCount; }

    public int getTotalTestCount() { return totalTestCount; }

    private void runUnitTest(int index)
    {
        UnitTest ut = formProfileResults.get(index).getUnitTest();
        new UnitTestRunner(this, ut).setVisible(true);
    }
    private void refreshUnitTest(int index)
    {
        UnitTest unitTest = formProfileResults.get(index).getUnitTest();
        // refresh profile results panel
        formProfileResults.get(index).refresh();
        // set tooltip
        int failCount = unitTest.getTestCount() - unitTest.getSuccessCount();
        String tooltip = String.format("%s | Fail: %d", unitTest.getTestName(), failCount);
        tabUnitTest.setToolTipTextAt(index, tooltip);
        // set tab background color, if found any error, it will show red
        Color background = unitTest.getSuccessCount() == unitTest.getTestCount() ? Style.CORRECT_COLOR : Style.WRONG_COLOR;
        setTabColor(index, Color.BLACK, background);
        // set tab title
        String tabTitle = failCount == 0 ? unitTest.getTestName() : String.format("%s (%d)", unitTest.getTestName(), failCount);
        tabUnitTest.setTitleAt(index, tabTitle);
    }

    private void onRunSelectedTest(ActionEvent evt)
    {
        int index = cbUnitTest.getSelectedIndex();
        if (index == -1) return;
        runUnitTest(index);
        refreshUnitTest(index);
        refreshTestCount();
        labelDate.setText(new Date().toString());
    }

    private void onRunAllTest(ActionEvent evt)
    {
        for (int i = 0; i < formProfileResults.size(); i++)
        {
            runUnitTest(i);
            refreshUnitTest(i);
        }
        refreshTestCount();
        labelDate.setText(new Date().toString());
    }

    private void setTabColor(int index, Color foreground, Color background)
    {
        tabUnitTest.setForegroundAt(index, foreground);
        tabUnitTest.setBackgroundAt(index, background);
    }

    private void refreshTestCount()
    {
        totalTestCount = 0;
        totalSuccessCount = 0;
        for (ProfilingResultsForm form : formProfileResults)
        {
            UnitTest unitTest = form.getUnitTest();
            totalTestCount += unitTest.getTestCount();
            totalSuccessCount += unitTest.getSuccessCount();
        }
        labelPoints.setText(String.format("%d out of %d", totalSuccessCount, totalTestCount));
        labelPercentage.setText(String.format("%.2f %%", 100.0 * totalSuccessCount / totalTestCount));
    }

    public void onContextMenuAbout(ActionEvent evt) {

    }

}