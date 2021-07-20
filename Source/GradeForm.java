package Source;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

public class GradeForm extends JFrame
{
    private JPanel mainPanel;
    private JComboBox cbPackage;
    private JTable tableProfile;
    private JLabel labelDate;
    private JLabel labelScore;
    private JLabel labelTestCount;
    private JLabel labelSuccessCount;
    private JLabel labelTitle;
    private JTabbedPane tabUnitTest;
    private JLabel labelSuccessRate;
    private JButton btnAllTest;
    private JButton btnSelectedTest;
    private JComboBox cbUnitTest;

    private String[] colProfile = { "Message", "Calls", "Time (ns)", "Memory (bytes)", "Expected", "Actual" };

    private NetlabUT[] unitList;

    public GradeForm(String title, NetlabUT[] unitList)
    {
        setTitle("Unit Test Grading");

        setSize(400, 300);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        labelTitle.setText(title);

        Date date = new Date();
        labelDate.setText(date.toString());

        tableProfile.setModel(new DefaultTableModel(null, colProfile) {
            /* all cels are not editable */ @Override
            public boolean isCellEditable(int row, int column) { return false; }
        });

        this.unitList = unitList;
        for (int i = 0; i < unitList.length; i++)
        {
            if (unitList[i].getTestCount() != 0)
                addGrade(unitList[i]);
            cbUnitTest.addItem(unitList[i].getTestName());
        }

        btnAllTest.addActionListener(this::onRunAllTest);
        btnSelectedTest.addActionListener(this::onRunSelectedTest);
    }

    private void onRunSelectedTest(ActionEvent evt)
    {
        int idx = cbUnitTest.getSelectedIndex();
        if (idx == -1) return;
        unitList[idx].run();
        addGrade(unitList[idx]);
    }

    private void onRunAllTest(ActionEvent evt)
    {
        for (int i = 0; i < unitList.length; i++)
        {
            unitList[i].run();
            addGrade(unitList[i]);
        }
    }

    private void addGrade(NetlabUT unit)
    {
        String title = unit.getTestName();
        ArrayList<Profile> profileList = unit.getTestProfile();

        tabUnitTest.setTitleAt(0, unit.getTestName());
        labelSuccessRate.setText( unit.getSuccessCount() + " out of " + unit.getTestCount() );

        DefaultTableModel model = (DefaultTableModel) tableProfile.getModel();
        String[] record = new String[6];
        for (Profile profile : profileList) {
            record[0] = profile.message;
            record[1] = "1";
            record[2] = Long.toString(profile.metric.nanoTime);
            record[3] = Long.toString(profile.metric.bytesUsed);
            record[4] = profile.expected;
            record[5] = profile.metric.returnValue.toString();
            model.addRow(record);
        }

        labelTestCount.setText(String.valueOf(unit.getTestCount()));
        labelSuccessCount.setText(String.valueOf(unit.getSuccessCount()));
        labelScore.setText(String.valueOf(unit.getSuccessCount() / unit.getTestCount() * 100));
    }

    public void onContextMenuAbout(ActionEvent evt) {

    }


}