package com.NetlabUT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

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
    private JComboBox cbUnitTest;

    private ProfilingResultsForm[] formProfileResults;

    public WindowProfiler(String title, NetlabUT[] unitList)
    {
        setTitle("Unit Test Grading");
        setVisible(true);

        setSize(600, 400);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        labelTitle.setText(title);
        labelDate.setText(new Date().toString());

        formProfileResults = new ProfilingResultsForm[unitList.length];
        for (int i = 0; i < unitList.length; i++)
        {
            if (unitList[i].getTestCount() == 0)
                unitList[i].run();
            formProfileResults[i] = new ProfilingResultsForm(unitList[i]);
            tabUnitTest.addTab(unitList[i].getTestName(), formProfileResults[i].getContentPanel());
            cbUnitTest.addItem(unitList[i].getTestName());

            // set tab background color, if found any error, it will show red
            Color background = unitList[i].getSuccessCount() == unitList[i].getTestCount() ?
                    new Color(200, 255, 200) : new Color(255, 200, 200);
            setTabColor(i, Color.BLACK, background);
        }
        refreshTestCount();

        btnAllTest.addActionListener(this::onRunAllTest);
        btnSelectedTest.addActionListener(this::onRunSelectedTest);
    }

    private void onRunSelectedTest(ActionEvent evt)
    {
        int i = cbUnitTest.getSelectedIndex();
        if (i == -1) return;
        NetlabUT unitTest = formProfileResults[i].getUnitTest();
        unitTest.run();
        // set tab background color, if found any error, it will show red
        Color background = unitTest.getSuccessCount() == unitTest.getTestCount() ?
                new Color(200, 255, 200) : new Color(255, 200, 200);
        setTabColor(i, Color.BLACK, background);
        // refresh display
        formProfileResults[i].refresh();
        refreshTestCount();
        labelDate.setText(new Date().toString());
    }

    private void setTabColor(int index, Color foreground, Color background)
    {
        tabUnitTest.setForegroundAt(index, foreground);
        tabUnitTest.setBackgroundAt(index, background);
    }

    private void onRunAllTest(ActionEvent evt)
    {
        for (int i = 0; i < formProfileResults.length; i++)
        {
            // run unit test
            NetlabUT unitTest = formProfileResults[i].getUnitTest();
            unitTest.run();
            // refresh panel
            formProfileResults[i].refresh();
            // set tab background color, if found any error, it will show red
            Color background = unitTest.getSuccessCount() == unitTest.getTestCount() ?
                    new Color(200, 255, 200) : new Color(255, 200, 200);
            setTabColor(i, Color.BLACK, background);
        }
        refreshTestCount();
        labelDate.setText(new Date().toString());
    }

    private void refreshTestCount()
    {
        int totalTestCount = 0;
        int totalSuccessCount = 0;
        for (int i = 0; i < formProfileResults.length; i++)
        {
            NetlabUT unitTest = formProfileResults[i].getUnitTest();
            totalTestCount += unitTest.getTestCount();
            totalSuccessCount += unitTest.getSuccessCount();
        }
        labelPoints.setText(String.format("%d out of %d", totalSuccessCount, totalTestCount));
        labelPercentage.setText(String.format("%.2f %%", 100.0 * totalSuccessCount / totalTestCount));
    }

    public void onContextMenuAbout(ActionEvent evt) {

    }


}