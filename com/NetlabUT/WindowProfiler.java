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

            refreshUnitTest(i);
        }
        refreshTestCount();

        btnAllTest.addActionListener(this::onRunAllTest);
        btnSelectedTest.addActionListener(this::onRunSelectedTest);
    }

    private void runUnitTest(int index) { formProfileResults[index].getUnitTest().run(); }
    private void refreshUnitTest(int index)
    {
        NetlabUT unitTest = formProfileResults[index].getUnitTest();
        // refresh profile results panael
        formProfileResults[index].refresh();
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
        runUnitTest(index);
        refreshUnitTest(index);
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
            runUnitTest(i);
            refreshUnitTest(i);
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