package com.GitFetcher;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;

public class Fetcher extends JFrame
{
    private JPanel mainPanel;
    private JTextArea textVersion;
    private JTable table1;
    private JTextPane textLog;
    private JButton addButton;
    private JTextArea textRepoInput;
    private JButton removeButton;
    private JComboBox<String> cbYear;
    private JComboBox<String> cbMonth;
    private JComboBox<String> cbDay;
    private JComboBox<String> cbRegion;
    private JComboBox<String> cbHour;
    private JComboBox<String> cbMinute;
    private JButton fetchLatestCommitButton;
    private JComboBox<String> cbCondition;
    private JTabbedPane tabbedPane1;
    private JTextField textField1;
    private JButton saveTableButton;

    private static final int AT_TIME     = 0;
    private static final int BEFORE_TIME = 1;
    private static final int AFTER_TIME  = 2;

    private static String[] COLUMN_TITLE = { "URL", "Clone Path" };

    public static void main(String[] args) { new Fetcher(); }

    public Fetcher()
    {
        setMinimumSize(mainPanel.getMinimumSize());
        setSize(mainPanel.getMinimumSize());
        setContentPane(mainPanel);
        setTitle("Git Fetcher - Get Latest Commit by Time");
        setVisible(true);

        fillComboBoxYear(2000, 2050);
        fillComboBoxMonth(1, 12);
        fillComboBoxDay(2000, 1);
        fillComboBoxHour();
        fillComboBoxMinute();
        fillComboBoxTimezone();
        fillComboBoxCondition();

        textLog.setBackground(new Color(40,40,40));

        cbMonth.addActionListener(this::onMonthOrYearSelected);
        cbYear.addActionListener(this::onMonthOrYearSelected);

        table1.setModel(new DefaultTableModel(null, COLUMN_TITLE) {
            @Override /* all cells are not editable */
            public boolean isCellEditable(int row, int column) { return true; }
        });
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.setRowCount(10);

        try {
            Runtime run = Runtime.getRuntime();
            Process pr = run.exec("git --version");
            pr.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = "";
            while ((line=buf.readLine()) != null) {
                textVersion.setText(line);
            }
        } catch (Throwable t) {
            System.err.println(t);
        }

        table1.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                removeButton.setEnabled(true);
            }
        });

        addButton.addActionListener(this::onAddButton);
        removeButton.addActionListener(this::onRemoveButton);
        fetchLatestCommitButton.addActionListener(this::onFetchLatestCommitButton);
    }

    private void onFetchLatestCommitButton(ActionEvent evt) {
        int cnt = table1.getRowCount();
        if (cnt == 0)
        {
            JOptionPane.showMessageDialog(null, "You should have at least 1 repository in the Table!");
            return;
        }
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        tabbedPane1.setSelectedIndex(1);
        for (int i = 0; i < cnt; ++i)
        {
            String link = (String) model.getValueAt(i, 0);
            String clonePath = (String) model.getValueAt(i, 1);
            if (link == null) continue;
            if (clonePath == null)
            {
                String[] cmd = {"git", "clone", link};
                getCommitRoutine(cmd);
            }
            else
            {
                String[] cmd = {"git", "clone", link, clonePath};
                getCommitRoutine(cmd);
            }
        }
    }

    private void onAddButton(ActionEvent evt)
    {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.addRow((Object[]) null);
    }

    private void onRemoveButton(ActionEvent evt)
    {
        int[] indices = table1.getSelectedRows();
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        for (int i = 0; i < indices.length; ++i)
            model.removeRow(indices[i]-i);
        if (table1.getSelectedRow() == -1)
            removeButton.setEnabled(false);
    }

    private void getCommitRoutine(String[] command)
    {
        StyledDocument doc = textLog.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, Color.WHITE);
        try
        {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            final InputStream is = process.getInputStream();
            BufferedReader isreader = new BufferedReader(new InputStreamReader(is));
            String s = null;
            while ((s = isreader.readLine()) != null)
            {
                doc.insertString(doc.getLength(), s, attr);
                doc.insertString(doc.getLength(), "\n", attr);
            }
        }
        catch (Throwable t)
        {
            JOptionPane.showMessageDialog(null, t.toString());
        }
    }

    private void onMonthOrYearSelected(ActionEvent e)
    {
        int year = Integer.parseInt((String) cbYear.getSelectedItem());
        int month = cbMonth.getSelectedIndex() + 1;
        fillComboBoxDay(year, month);
    }

    private void fillComboBoxYear(int min, int max)
    {
        cbYear.removeAllItems();
        for (int i = min; i <= max; ++i)
            cbYear.addItem(String.valueOf(i));
    }

    private void fillComboBoxMonth(int min, int max)
    {
        cbMonth.removeAllItems();
        for (int i = min; i <= max; ++i)
            cbMonth.addItem(monthString(i));
    }

    private void fillComboBoxDay(int year, int month)
    {
        cbDay.removeAllItems();
        LocalDate date = LocalDate.of(year, month, 1);
        int last = date.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        for (int i = 1; i <= last; ++i)
            cbDay.addItem(String.valueOf(i));
    }

    private void fillComboBoxHour()
    {
        cbHour.removeAllItems();
        for (int i = 0; i < 24; ++i)
            cbHour.addItem(String.format("%02d", i));
    }

    private void fillComboBoxMinute()
    {
        cbMinute.removeAllItems();
        for (int i = 0; i < 60; ++i)
            cbMinute.addItem(String.format("%02d", i));
    }

    private void fillComboBoxTimezone()
    {
        final ZoneId zoneId = ZoneId.systemDefault();
        final Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
        for (String s : availableZoneIds)
            cbRegion.addItem(s);
        cbRegion.setSelectedItem(zoneId.toString());
    }

    private void fillComboBoxCondition()
    {
        // make sure in order because we read the combo box index
        cbCondition.addItem("At Time");
        cbCondition.addItem("Before Time");
        cbCondition.addItem("After Time");
    }

    private String monthString(int value)
    {
        switch (value)
        {
            case 1: return "January";
            case 2: return "February";
            case 3: return "March";
            case 4: return "April";
            case 5: return "May";
            case 6: return "June";
            case 7: return "July";
            case 8: return "August";
            case 9: return "September";
            case 10: return "October";
            case 11: return "November";
            case 12: return "December";
        }
        return "";
    }
}
