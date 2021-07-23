package Source;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ProfilingResultsForm
{
    private static final String[] COLUMN_PROFILE = { "Message", "Calls", "Time (ns)", "Expected", "Actual" };

    private JTable tableProfile;
    private JLabel labelPercentage;
    private JLabel labelPoints;
    private JPanel mainPanel;
    private NetlabUT unitTest = null;

    public ProfilingResultsForm()
    {
        tableProfile.setModel(new DefaultTableModel(null, COLUMN_PROFILE) {
            @Override /* all cels are not editable */
            public boolean isCellEditable(int row, int column) { return false; }
        });
    }
    public ProfilingResultsForm(NetlabUT unitTest)
    {
        tableProfile.setModel(new DefaultTableModel(null, COLUMN_PROFILE) {
            @Override /* all cels are not editable */
            public boolean isCellEditable(int row, int column) { return false; }
        });

        setProfileResults(unitTest);
    }

    public JPanel getContentPanel() { return mainPanel; }
    public void refresh() { setProfileResults(unitTest); }
    public NetlabUT getUnitTest() { return unitTest; }

    public void setProfileResults(NetlabUT unitTest)
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
            record[0] = profile.message;
            record[1] = "1";
            record[2] = Long.toString(profile.metric.nanoTime);
            record[3] = profile.expected;
            record[4] = profile.metric.returnValue.toString();
            model.addRow(record);
        }
    }
}
