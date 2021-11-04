package com.NetlabUT;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

//import org.apache.commons.csv.*;

/** Class to display profile test from {@link com.NetlabUT.UnitTest} into a GUI
 * @author Ramadhan Kalih Sewu
 * @version 1.3
 */
@Deprecated
public class WindowProfiler extends JFrame
{
    private JPanel mainPanel;
    private JLabel labelDate;
    private JLabel labelPoints;
    private JLabel labelPercentage;
    private JLabel labelTitle;
    private JTabbedPane tabUnitTest;
    private JButton btnAllTest;
    private JComboBox<Object> cbUnitTest;
    private JButton exportAsCSVButton;

    private boolean handleEvent = true;
    private int totalSuccessCount = 0;
    private int totalTestCount = 0;
    private final ArrayList<ProfilingResultsForm> formProfileResults = new ArrayList<>(10);

    public WindowProfiler(String title)
    {
        setTitle("Unit Test Grading");
        setSize(mainPanel.getPreferredSize());
        setMinimumSize(mainPanel.getMinimumSize());
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        labelTitle.setText(title);
        labelDate.setText(new Date().toString());

        cbUnitTest.addActionListener((e) -> {
            if (!handleEvent) return;
            @SuppressWarnings("rawtypes")
            JComboBox source = (JComboBox) e.getSource();
            tabUnitTest.setSelectedIndex(source.getSelectedIndex());
        });

        tabUnitTest.addChangeListener((e) -> {
            if (!handleEvent) return;
            JTabbedPane pane = (JTabbedPane) e.getSource();
            cbUnitTest.setSelectedIndex(pane.getSelectedIndex());
        });

        btnAllTest.addActionListener(this::onRunAllTest);
        //exportAsCSVButton.addActionListener(this::onExportAsCSV);
    }

    public void add(UnitTest... unitTests)
    {
        handleEvent = false;
        for (final UnitTest unitTest : unitTests)
        {
            ProfilingResultsForm form = new ProfilingResultsForm(unitTest);
            formProfileResults.add(form);
            cbUnitTest.addItem(makeUnique(unitTest.getTestName()));
            tabUnitTest.addTab(unitTest.getTestName(), form.getContentPane());
        }
        handleEvent = true;
    }

    private Object makeUnique(final String string) {
        return new Object() {
            @Override
            public String toString() { return string; }
        };
    }

    public void run() { onRunAllTest(null); }
    public int getTotalSuccessCount() { return totalSuccessCount; }
    public int getTotalTestCount() { return totalTestCount; }

    private void onRunAllTest(ActionEvent event)
    {
        totalTestCount = 0;
        totalSuccessCount = 0;
        for (int i = 0; i < formProfileResults.size(); ++i)
        {
            ProfilingResultsForm form = formProfileResults.get(i);
            UnitTest unitTest = form.getUnitTest();
            // run and refresh profile results panel
            unitTest.run();
            form.refresh();
            // calculate test result
            final int testCount = unitTest.getTestCount();
            final int successCount = unitTest.getSuccessCount();
            totalTestCount += testCount;
            totalSuccessCount += successCount;
            // set tooltip
            final int failCount = testCount - successCount;
            final String tooltip = String.format("Fail: %d", failCount);
            tabUnitTest.setToolTipTextAt(i, tooltip);
            // set tab title
            final String tabTitle = failCount == 0 ? unitTest.getTestName() : String.format("%s (%d)", unitTest.getTestName(), failCount);
            tabUnitTest.setTitleAt(i, tabTitle);
        }
        labelPoints.setText(String.format("%d out of %d", totalSuccessCount, totalTestCount));
        labelPercentage.setText(String.format("%.2f %%", 100.0 * totalSuccessCount / totalTestCount));
        labelDate.setText(new Date().toString());
    }

    /*
    private void onExportAsCSV(ActionEvent event)
    {
        try
        {
            // specify file and path to save
            final JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Comma Separated Values (.csv)", "csv"));
            int modal = fc.showSaveDialog(this);
            if (modal != JFileChooser.APPROVE_OPTION) return;
            String filename = fc.getSelectedFile().toString();
            if (!filename .endsWith(".csv")) filename += ".csv";
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            // write header using apache common csv
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            csvPrinter.printRecord(this.getTitle());
            csvPrinter.printRecord("Profile Summary:", labelTitle.getText());
            csvPrinter.printRecord("Total Percentage:", labelPercentage.getText());
            csvPrinter.printRecord("Total Points:", labelPoints.getText());
            csvPrinter.printRecord("Generated:", labelDate.getText());
            csvPrinter.println();
            // print results of every unit tests
            for (ProfilingResultsForm form : formProfileResults)
            {
                UnitTest unitTest = form.getUnitTest();
                csvPrinter.printRecord(unitTest.getTestName());
                final int sucessCount = unitTest.getSuccessCount();
                final int testCount = unitTest.getTestCount();
                csvPrinter.printRecord("Percentage:", String.format("%.2f %%", 100.0 * sucessCount / testCount));
                csvPrinter.printRecord("Points:", String.format("%d out of %d", sucessCount, testCount));
                csvPrinter.printRecord("[Correctness]", "[Reference Status]", "[Message]", "[Reference]", "[Actual]");
                ArrayList<Profile<Object>> profiles = unitTest.getTestProfile();
                for (Profile<Object> profile : profiles)
                {
                    csvPrinter.printRecord(
                            profile.isCorrect() ? "correct" : "wrong",
                            Status.toString(profile.getReferenceStatus()),
                            profile.getMessage(),
                            profile.getReferenceString(Object::toString),
                            profile.getActualString(Object::toString)
                    );
                }
                csvPrinter.println();
            }
            csvPrinter.flush();
        }
        catch (Throwable throwable)
        {
            JOptionPane.showMessageDialog(
                null,
                throwable.toString(),
                "Export as CSV",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

     */
}