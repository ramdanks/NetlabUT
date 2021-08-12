package com.NetlabUT;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.function.Function;

/** show a brief {@link com.NetlabUT.Profile} in form of table for {@link com.NetlabUT.UnitTest}
 * @author Ramadhan Kalih Sewu
 * @version 1.1
 */
final class ProfilingResultsForm
{
    private static final String[] COLUMN_PROFILE = { "Assumption", "Message", "Time (ns)", "Reference", "Actual" };

    private UnitTest unitTest;

    private JTable tableProfile;
    private JLabel labelPercentage;
    private JLabel labelPoints;
    private JPanel mainPanel;
    private JCheckBox checkboxMessage;
    private JCheckBox checkboxToString;

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
            record[0] = Status.toString(profile.getReferenceStatus());
            record[1] = profile.getMessage();
            record[2] = Long.toString(profile.getProfileTime());
            record[3] = profile.getReferenceString();
            record[4] = Profile.getObjectIdentifierString(profile.getActual());
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

        checkboxMessage.addActionListener(this::onHideMessage);
        checkboxToString.addActionListener(this::onObjectToString);

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

    private void onObjectToString(ActionEvent e)
    {
        final ArrayList<Profile<Object>> profileList = unitTest.getTestProfile();
        DefaultTableModel model = (DefaultTableModel) tableProfile.getModel();

        Function<Object, String> cellString = checkboxToString.isSelected() ?
                Object::toString :
                Profile::getObjectIdentifierString;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                for (int row = 0; row < profileList.size(); ++row)
                {
                    Profile<Object> profile = profileList.get(row);
                    Object reference = profile.getReference();
                    Object actual = profile.getActual();
                    if (reference != null) model.setValueAt(cellString.apply(reference), row, 3);
                    if (actual != null) model.setValueAt(cellString.apply(actual), row, 4);
                }
                return null;
            }
        }.execute();
    }

    private void onHideMessage(ActionEvent e)
    {
        int minWidth = 100;
        int maxWidth = Integer.MAX_VALUE;
        if (checkboxMessage.isSelected())
        {
            minWidth = 0;
            maxWidth = 0;
        }
        tableProfile.getColumnModel().getColumn(1).setMinWidth(minWidth);
        tableProfile.getColumnModel().getColumn(1).setMaxWidth(maxWidth);
        tableProfile.getColumnModel().getColumn(1).setWidth(minWidth);
        tableProfile.getColumnModel().getColumn(1).setMinWidth(minWidth);
    }
}
