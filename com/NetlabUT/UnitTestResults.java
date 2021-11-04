package com.NetlabUT;

import com.NetlabUT.annotations.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * @author Ramadhan Kalih Sewu
 * @version 1.3
 */
final class UnitTestResults extends JFrame implements AssumptionListener
{
    private static final String[] COLUMN_PROFILE = { "Assumption", "Message", "Time (ns)", "Reference", "Actual" };
    private static final String[] RECORD_BUFFER  = new String[5];

    private final ArrayList<AssumeMetric> metrics = new ArrayList<>();
    private int testSuccessCount = 0;
    private int testTotalCount   = 0;

    private JTable tableProfile;
    private JLabel labelPercentage;
    private JLabel labelPoints;
    private JPanel mainPanel;
    private JCheckBox checkboxMessage;
    private JCheckBox checkboxToString;

    public UnitTestResults(Object unitTest)
            throws InvocationTargetException, IllegalAccessException
    {
        setContentPane(mainPanel);

        tableProfile.setModel(new DefaultTableModel(null, COLUMN_PROFILE) {
            @Override /* all cells are not editable */
            public boolean isCellEditable(int row, int column) { return false; }
        });

        tableProfile.getColumnModel().getColumn(0).setMinWidth(120);
        tableProfile.getColumnModel().getColumn(0).setMaxWidth(120);

        checkboxMessage.addActionListener(this::onHideMessage);
        checkboxToString.addActionListener(this::onObjectToString);

        tableProfile.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                AssumeMetric metric = UnitTestResults.this.metrics.get(row);
                Component c   = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if      (column == 0)    c.setBackground(Style.getAssumptionColor(metric.status));
                else if (metric.correct) c.setBackground(isSelected ? Style.NEUTRAL_FOCUS : Style.NEUTRAL);
                else                     c.setBackground(isSelected ? Style.WRONG_COLOR_FOCUS : Style.WRONG_COLOR);
                return c;
            }
        });

        tableProfile.addMouseListener(new MouseAdapter() {
            @Override // handle double click event
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2)
                {
                    JTable table = (JTable) me.getSource();
                    int row = table.getSelectedRow();
                    new AssumeMetricWindow(metrics.get(row));
                }
            }
        });

        Method[] methods  = unitTest.getClass().getDeclaredMethods();
        Assumptions.listener = this;
        for (Method m : methods)
        {
            if (m.isAnnotationPresent(Test.class))
            {
                Test testAnnotation = m.getAnnotation(Test.class);
                m.setAccessible(true);
                m.invoke(unitTest);
            }
        }
        Assumptions.listener  = null;

        double percentage = 100.0 * testSuccessCount / testTotalCount;
        labelPercentage.setText(String.format("%.2f %%", percentage));
        labelPoints.setText(String.format("%d out of %d", testSuccessCount, testTotalCount));
    }

    public int getTestSuccessCount() { return testSuccessCount; }
    public int getTestTotalCount() { return testTotalCount; }

    private void onObjectToString(ActionEvent e)
    {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                DefaultTableModel model = (DefaultTableModel) tableProfile.getModel();
                Function<Object, String> translator = checkboxToString.isSelected() ?
                        StringFormatter::toString : StringFormatter::idString;
                for (int row = 0; row < metrics.size(); ++row)
                {
                    AssumeMetric metric    = metrics.get(row);
                    if (metric.reference != null) model.setValueAt(translator.apply(metric.reference), row, 3);
                    if (metric.actual != null)    model.setValueAt(translator.apply(metric.actual), row, 4);
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

    @Override
    public void listen(AssumeMetric metric)
    {
        if (metric.correct)
            testSuccessCount += 1;
        testTotalCount += 1;

        Function<Object, String> translator = checkboxToString.isSelected() ?
                StringFormatter::toString : StringFormatter::idString;

        RECORD_BUFFER[0] = metric.status.toString();
        RECORD_BUFFER[1] = metric.message;
        RECORD_BUFFER[2] = Long.toString(metric.nanoTime);
        RECORD_BUFFER[3] = translator.apply(metric.reference);
        RECORD_BUFFER[4] = translator.apply(metric.actual);

        metrics.add(metric);

        DefaultTableModel model = (DefaultTableModel) tableProfile.getModel();
        model.addRow(RECORD_BUFFER);
    }
}
