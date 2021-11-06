package com.NetlabUT;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PackageWindowGrading extends JFrame
{
    private JPanel mainPanel;
    private JTable table1;
    private JButton gradingWindowButton;
    private JScrollPane scrollPane;
    private JLabel labelTitle;
    private ArrayList<WindowGrading> windowGradings;
    private Map<String, List<Object>> mapPackageUT;

    public PackageWindowGrading(String title, Map<String, List<Object>> mapPackageUT)
    {
        super("Window Grading - " + title);
        this.mapPackageUT = mapPackageUT;

        labelTitle.setText(" " + title);
        setContentPane(mainPanel);
        setMinimumSize(new Dimension(600, 400));

        List<Object> listUT = (List<Object>) mapPackageUT.values().toArray()[0];
        Set<String> setPackage = mapPackageUT.keySet();

        int rowSize = setPackage.size();
        int colSize = 2 + listUT.size();

        String[] colTitle = new String[colSize];
        colTitle[0] = "Package";
        colTitle[colSize - 1] = "Final Score";
        for (int i = 0; i < listUT.size(); ++i)
            colTitle[i + 1] = listUT.get(i).getClass().getSimpleName();

        DefaultTableModel tableModel = new DefaultTableModel(colTitle, rowSize) {
            public boolean isCellEditable(int row, int column) { return false; }};

        table1 = new JTable(tableModel) {
            protected JTableHeader createDefaultTableHeader() {
                return new GroupableTableHeader(columnModel);
        }};
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table1.setMinimumSize(new Dimension(colSize * 120, -1));

        TableColumnModel cm = table1.getColumnModel();
        GroupableTableHeader header = (GroupableTableHeader) table1.getTableHeader();

        // unit test column header grouping
        ColumnGroup colGroupUT = new ColumnGroup("Unit Test Score");
        for (int i = 0; i < listUT.size(); ++i)
            colGroupUT.add(cm.getColumn(i + 1));

        // set value for each packages in a row
        AtomicInteger idx = new AtomicInteger();
        setPackage.forEach(pkg -> table1.setValueAt(pkg, idx.getAndIncrement(), 0));

        header.addColumnGroup(colGroupUT);
        scrollPane.setViewportView(table1);
    }

    public void runTester()
    {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                String formatScore = "%d / %d (%.2f %%)";
                AtomicInteger idx = new AtomicInteger();
                mapPackageUT.forEach((pkg, listUT) -> {
                    int idxRow = idx.getAndIncrement();
                    int totalSuccessCount = 0;
                    int totalTestCount    = 0;
                    for (int idxCol = 0; idxCol < listUT.size(); ++idxCol)
                    {
                        int successCount  = 0;
                        int testCount     = 0;
                        totalSuccessCount += successCount;
                        totalTestCount    += testCount;
                        double percentage = 100.0 * successCount / testCount;
                        String utScore    = String.format(formatScore, successCount, testCount, percentage);
                        table1.setValueAt(utScore, idxRow, 1 + idxCol); // because of first column header is package
                    }
                    double percentage = 100.0 * totalSuccessCount / totalTestCount;
                    String utScore    = String.format(formatScore, totalSuccessCount, totalTestCount, percentage);
                    table1.setValueAt(utScore, idxRow, table1.getColumnCount() - 1);
                });
                return null;
            }
        }.execute();
    }
}
