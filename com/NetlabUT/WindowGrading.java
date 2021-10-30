package com.NetlabUT;

import javax.swing.*;
import com.NetlabUT.annotations.NetlabTest;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Class to display profile test from {@link com.NetlabUT.UnitTest} into a GUI
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
    private ArrayList<ProfilingResultsForm> formProfileResults;

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

        labelTitle.setText(title);
        labelDate.setText(new Date().toString());

        totalTestCount = 0;
        totalSuccessCount = 0;
        formProfileResults = new ArrayList<>();

        for (Object ut : unitTests)
        {
            ProfilingResultsForm form = new ProfilingResultsForm(ut);
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

        }
        labelPoints.setText(String.format("%d out of %d", totalSuccessCount, totalTestCount));
        labelPercentage.setText(String.format("%.2f %%", 100.0 * totalSuccessCount / totalTestCount));
        labelDate.setText(new Date().toString());
    }
}