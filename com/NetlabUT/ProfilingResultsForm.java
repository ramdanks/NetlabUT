package com.NetlabUT;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ProfilingResultsForm
{
    private static final String[] COLUMN_PROFILE = { "Message", "Time (ns)", "Expected", "Actual" };

    private JTable tableProfile;
    private JLabel labelPercentage;
    private JLabel labelPoints;
    private JPanel mainPanel;
    private UnitTest unitTest = null;

    public ProfilingResultsForm()
    {
        initTable();;
    }
    public ProfilingResultsForm(UnitTest unitTest)
    {
        initTable();
        setProfileResults(unitTest);
    }

    public JPanel getContentPanel() { return mainPanel; }
    public void refresh() { setProfileResults(unitTest); }
    public UnitTest getUnitTest() { return unitTest; }
    public void setProfileResults(UnitTest unitTest)
    {
        this.unitTest = unitTest;

        final int count = unitTest.getTestCount();
        final int success = unitTest.getSuccessCount();
        final double percentage = 100.0 * success / count;

        labelPercentage.setText(String.format("%.2f %%", percentage));
        labelPoints.setText(String.format("%d out of %d", success, count));

        final ArrayList<Profile> profileList = unitTest.getTestProfile();

        DefaultTableModel model = (DefaultTableModel) tableProfile.getModel();
        model.setRowCount(0);

        String[] record = new String[5];
        for (Profile profile : profileList)
        {
            Metric metric = profile.getMetric();
            record[0] = profile.getMessage();
            record[1] = Long.toString(profile.getMetric().nanoTime);
            record[2] = profile.getExpected();
            record[3] = metric.isThrowing() ? metric.throwable.toString() : Profile.toString(metric.returns);
            model.addRow(record);
        }
    }

    private void initTable()
    {
        tableProfile.setModel(new DefaultTableModel(null, COLUMN_PROFILE) {
            @Override /* all cels are not editable */
            public boolean isCellEditable(int row, int column) { return false; }
        });

        tableProfile.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                final Profile profile = ProfilingResultsForm.this.unitTest.getTestProfile().get(row);
                c.setBackground(profile.isCorrect() ? Color.WHITE : Style.WRONG_COLOR);
                return c;
            }
        });
    }
}
