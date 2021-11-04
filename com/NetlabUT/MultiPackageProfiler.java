package com.NetlabUT;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableModel;

/** Run ReflectorUnitTest with a single or multiple packages specified by the user.
 * This class expect your {@link ReflectorUnitTest} to obtained it subject from {@link Instantiator#Instantiator(String, String)}
 * using a specific package. This will help automate test for a multiple packages.
 * @author Ramadhan Kalih Sewu
 * @version 1.3
 */
public class MultiPackageProfiler extends JFrame
{
    private static final String[] TABLE_PACKAGES_COLUMN = { "Packages", "Percentage" };
    private static final Object[] rowBuffer = new Object[2];

    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JTable tablePackages;
    private JLabel labelTotalPackage;
    private JLabel labelAvgPercentage;
    private JComboBox<Pair<Integer, String>> comboBox1;

    /** prepare every packages listed from [Property of "user.dir"]/TestedPackages.txt.
     * and creates an initial window.
     * @param title the title of unit test passed to {@link WindowGrading} */
    public MultiPackageProfiler(String title, List<?> unitTests)
            throws InvocationTargetException, IllegalAccessException
    {
        setContentPane(mainPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1024, 600);
        setMinimumSize(new Dimension(800, 400));
        setTitle(title);

        Consumer<Integer> setCurrentTab = (i) -> {
            comboBox1.setSelectedIndex(i);
            tabbedPane1.setSelectedIndex(i);
        };

        comboBox1.addActionListener((e) -> {
            @SuppressWarnings("rawtypes")
            JComboBox source = (JComboBox) e.getSource();
            setCurrentTab.accept(source.getSelectedIndex());
        });

        comboBox1.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                @SuppressWarnings("unchecked")
                Pair<Integer, String> pair = (Pair<Integer, String>) value;
                setText(pair.second);
                return this;
            }
        });

        tabbedPane1.addChangeListener((e) -> {
            JTabbedPane source = (JTabbedPane) e.getSource();
            comboBox1.setSelectedIndex(source.getSelectedIndex());
        });

        tablePackages.setModel(new DefaultTableModel(null, TABLE_PACKAGES_COLUMN) {
            @Override /* all cells are not editable */
            public boolean isCellEditable(int row, int column) { return false; }
        });

        tablePackages.addMouseListener(new MouseAdapter() {
            @Override // handle double click event
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    JTable table = (JTable) me.getSource();
                    setCurrentTab.accept(table.getSelectedRow());
                }
            }
        });

        WindowGrading profiler = new WindowGrading(title, unitTests);
        profiler.setVisible(true);
    }
}