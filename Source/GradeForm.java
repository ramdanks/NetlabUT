package Source;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;

public class GradeForm extends JFrame
{
    private JPanel mainPanel;
    private JComboBox cbPackage;
    private JLabel labelDate;
    private JLabel labelScore;
    private JLabel labelTestCount;
    private JLabel labelSuccessCount;
    private JLabel labelTitle;
    private JTabbedPane tabUnitTest;
    private JButton btnAllTest;
    private JButton btnSelectedTest;
    private JComboBox cbUnitTest;

    private ProfilingResultsForm[] formProfileResults;

    public GradeForm(String title, NetlabUT[] unitList)
    {
        setTitle("Unit Test Grading");

        setSize(600, 400);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        labelTitle.setText(title);
        labelDate.setText(new Date().toString());

        formProfileResults = new ProfilingResultsForm[unitList.length];
        for (int i = 0; i < unitList.length; i++)
        {
            formProfileResults[i] = new ProfilingResultsForm(unitList[i]);
            tabUnitTest.addTab(unitList[i].getTestName(), formProfileResults[i].getContentPanel());
            cbUnitTest.addItem(unitList[i].getTestName());
        }

        btnAllTest.addActionListener(this::onRunAllTest);
        btnSelectedTest.addActionListener(this::onRunSelectedTest);
    }

    private void onRunSelectedTest(ActionEvent evt)
    {
        int i = cbUnitTest.getSelectedIndex();
        if (i == -1) return;
        formProfileResults[i].getUnitTest().run();
        formProfileResults[i].refresh();
        refreshTestCount();
    }

    private void onRunAllTest(ActionEvent evt)
    {
        for (int i = 0; i < formProfileResults.length; i++)
        {
            formProfileResults[i].getUnitTest().run();
            formProfileResults[i].refresh();
        }
        refreshTestCount();
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
        labelTestCount.setText(String.format("%d", totalTestCount));
        labelSuccessCount.setText(String.format("%d", totalSuccessCount));
        labelScore.setText(String.format("%.2f %%", 100.0 * totalSuccessCount / totalTestCount));
    }

    public void onContextMenuAbout(ActionEvent evt) {

    }


}