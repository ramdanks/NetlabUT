package com.NetlabUT;

import javax.swing.*;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Class to display profile test from {@link Assumptions} into a GUI
 * @author Ramadhan Kalih Sewu
 * @version 1.2
 */
public class WindowGrading extends JFrame
{
    private JPanel mainPanel;
    private JLabel labelDate;
    private JLabel labelPoints;
    private JLabel labelPercentage;
    private JLabel labelTitle;
    private JTabbedPane tabUnitTest;
    private JComboBox<String> cbUnitTest;
    private ArrayList<UnitTestResults> formProfileResults;

    private int totalSuccessCount = 0;
    private int totalTestCount = 0;

    public WindowGrading(String title, List<?> unitTests)
    		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException
    {
        setTitle("Unit Test Grading");
        setSize(mainPanel.getPreferredSize());
        setMinimumSize(mainPanel.getMinimumSize());
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cbUnitTest.addActionListener((e) -> {
            int tabCount = tabUnitTest.getTabCount();
            int idx = cbUnitTest.getSelectedIndex();
            if (idx != -1 && idx < tabCount)
                tabUnitTest.setSelectedIndex(idx);
        });

        tabUnitTest.addChangeListener((e) -> {
            int itemCount = cbUnitTest.getItemCount();
            int idx = tabUnitTest.getSelectedIndex();
            if (idx != -1 && idx < itemCount)
                cbUnitTest.setSelectedIndex(idx);
        });

        labelTitle.setText(title);
        labelDate.setText(new Date().toString());

        totalTestCount = 0;
        totalSuccessCount = 0;
        formProfileResults = new ArrayList<>();

        for (Object ut : unitTests)
        {
            UnitTestResults form = new UnitTestResults(ut);
            String utName = ut.getClass().getSimpleName();

            formProfileResults.add(form);
            totalTestCount += form.getTestTotalCount();
            totalSuccessCount += form.getTestSuccessCount();

            int failCount   = form.getTestTotalCount() - form.getTestSuccessCount();
            String tooltip  = String.format("%s | Fail: %d", utName, failCount);
            String tabTitle = failCount == 0 ? utName : String.format("%s (%d)", utName, failCount);

            int idx = tabUnitTest.getTabCount();
            tabUnitTest.addTab(tabTitle, form.getContentPane());
            tabUnitTest.setToolTipTextAt(idx, tooltip);
            // set tab background color, if found any error, it will show red
            tabUnitTest.setForegroundAt(idx,  Color.BLACK);
            tabUnitTest.setBackgroundAt(idx, failCount == 0 ? Style.CORRECT_COLOR : Style.WRONG_COLOR);
            // add unit test to combobox
            cbUnitTest.addItem(utName);
        }

        labelPoints.setText(String.format("%d out of %d", totalSuccessCount, totalTestCount));
        labelPercentage.setText(String.format("%.2f %%", 100.0 * totalSuccessCount / totalTestCount));
        labelDate.setText(new Date().toString());
    }
}