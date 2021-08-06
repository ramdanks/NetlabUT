package com.NetlabUT;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/** show a brief {@link com.NetlabUT.Profile} in form of table for {@link com.NetlabUT.UnitTest}
 * @author Ramadhan Kalih Sewu
 * @version 1.0
 */
final class ProfilingResultsForm
{
    private static final String[] COLUMN_PROFILE = { "Assumption", "Message", "Time (ns)", "Reference", "Actual" };

    private JTable tableProfile;
    private JLabel labelPercentage;
    private JLabel labelPoints;
    private JPanel mainPanel;
    private JCheckBox checkboxMessage;
    private UnitTest unitTest = null;

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

        final ArrayList<Profile<Object>> profileList = unitTest.getTestProfile();

        DefaultTableModel model = (DefaultTableModel) tableProfile.getModel();
        model.setRowCount(0);

        String[] record = new String[5];
        for (Profile profile : profileList)
        {
            Metric metric = profile.getMetric();
            record[0] = Status.toString(profile.getReferenceStatus());
            record[1] = profile.getMessage();
            record[2] = Long.toString(profile.getMetric().nanoTime);
            record[3] = profile.getReferenceString();
            record[4] = metric.isThrowing() ?
                    Profile.getObjectIdentifierString(metric.throwable) :
                    Profile.getObjectIdentifierString(metric.returns);
            model.addRow(record);
        }
    }



    private void initTable()
    {
        tableProfile.setModel(new DefaultTableModel(null, COLUMN_PROFILE) {
            @Override /* all cells are not editable */
            public boolean isCellEditable(int row, int column) { return false; }
        });

        tableProfile.getColumnModel().getColumn(0).setMinWidth(100);
        tableProfile.getColumnModel().getColumn(0).setMaxWidth(100);

        checkboxMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkboxMessage.isSelected())
                {
                    tableProfile.getColumnModel().getColumn(1).setMinWidth(0);
                    tableProfile.getColumnModel().getColumn(1).setMaxWidth(0);
                    tableProfile.getColumnModel().getColumn(1).setWidth(0);
                }
                else
                {
                    tableProfile.getColumnModel().getColumn(1).setMinWidth(100);
                    tableProfile.getColumnModel().getColumn(1).setMaxWidth(Integer.MAX_VALUE);
                    tableProfile.getColumnModel().getColumn(1).setWidth(100);
                }
            }
        });

        tableProfile.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                final Profile profile = ProfilingResultsForm.this.unitTest.getTestProfile().get(row);
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 0)               c.setBackground(Style.getAssumptionColor(profile.getReferenceStatus()));
                else if (profile.isCorrect())  c.setBackground(isSelected ? Style.NEUTRAL_FOCUS : Style.NEUTRAL);
                else                           c.setBackground(isSelected ? Style.WRONG_COLOR_FOCUS : Style.WRONG_COLOR);
                return c;
            }
        });

        tableProfile.addMouseListener(new MouseAdapter() {
            @Override // handle double click event
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2)
                {
                    JTable table = (JTable) me.getSource();
                    int row = table.getSelectedRow(); // select a row
                    new ProfileFrame<>(unitTest.getTestProfile().get(row));
                }
            }
        });
    }

    /*
        switch (profile.getReferenceStatus())
        {
            case Status.EQUAL:            setToolTipText("Actual should EQUAL to Reference"); break;
            case Status.NOT_EQUAL:        setToolTipText("Actual should NOT EQUAL to Reference"); break;
            case Status.REFERENCE:        setToolTipText("Actual should REFERENCE Reference"); break;
            case Status.NOT_REFERENCE:    setToolTipText("Actual should NOT REFERENCE Reference"); break;
            case Status.ARRAY_EQUAL:      setToolTipText("Actual should ITERATIVELY EQUAL to Reference"); break;
            case Status.ARRAY_NOT_EQUAL:  setToolTipText("Actual should NOT ITERATIVELY EQUAL to Reference"); break;
            case Status.THROWS:           setToolTipText("Actual should THROWS any"); break;
            case Status.THROWS_TYPE:      setToolTipText("Actual should THROWS an Reference"); break;
        }
     */
}
