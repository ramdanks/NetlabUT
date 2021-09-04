package com.Reflector;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableModel;

import com.NetlabUT.WindowProfiler;

/** Run ReflectorUnitTest with a single or multiple packages specified by the user.
 * This class expect your {@link ReflectorUnitTest} to obtained it subject from {@link ClassR#ClassR(String, String)}
 * using a specific package. This will help automate test for a multiple packages.
 * @author Ramadhan Kalih Sewu
 * @version 1.2
 */
public class PackageRunner extends JFrame
{
    private static final String[] TABLE_PACKAGES_COLUMN = { "Packages", "Percentage" };
    private static final Object[] rowBuffer = new Object[2];
    private final Vector<Pair<String, WindowProfiler>> listPackageAndProfiler = new Vector<>(1);
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JTable tablePackages;
    private JLabel labelTotalPackage;
    private JLabel labelAvgPercentage;
    private JComboBox<Pair<Integer, String>> comboBox1;

    /** prepare every packages listed from [Property of "user.dir"]/TestedPackages.txt.
     * and creates an initial window.
     * @param title the title of unit test passed to {@link com.NetlabUT.WindowProfiler} */
    public PackageRunner(String title)
    {
        setContentPane(mainPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1024, 600);
        setMinimumSize(new Dimension(800, 400));

        File file = new File(System.getProperty("user.dir") + "/TestedPackages.txt");
        try
        {
            final DefaultTableModel model = (DefaultTableModel) tablePackages.getModel();
            final Scanner reader = new Scanner(file);
            // so combo box can identify the object uniquely.
            // getSelectedIndex() will return the first occurrences of selected object
            // instead of returning the index of the user currently selected.
            // omg java swing, this is dumb!
            int packageSerial = 0;
            while (reader.hasNextLine())
            {
                String packageName = reader.nextLine();
                WindowProfiler profiler = new WindowProfiler(title);
                profiler.setTitle("Package: " + packageName);
                rowBuffer[0] = packageName;
                rowBuffer[1] = "<none>";
                model.addRow(rowBuffer);
                comboBox1.addItem(new Pair<>(packageSerial++, packageName));
                tabbedPane1.addTab(packageName, profiler.getContentPane());
                listPackageAndProfiler.add(new Pair<>(packageName, profiler));
            }
            labelTotalPackage.setText(String.valueOf(listPackageAndProfiler.size()));
            labelAvgPercentage.setText("<none>");
            reader.close();
        }
        catch (FileNotFoundException ignored)
        {
            JOptionPane.showMessageDialog(
                null,
                "Please create a file: \n" +
                file.getAbsolutePath() +
                "\nSpecify the list of packages you want to run using Reflector Unit Test" +
                "\nThe list should contains the name of existing packages and separated by line!" +
                "\nIf empty, it will run under the scope of your unit test package!",
                "Specify Packages",
                JOptionPane.INFORMATION_MESSAGE
            );
            System.exit(-1);
        }

        comboBox1.addActionListener((e) -> {
            @SuppressWarnings("rawtypes")
            JComboBox source = (JComboBox) e.getSource();
            setCurrentTab(source.getSelectedIndex());
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
                    setCurrentTab(table.getSelectedRow());
                }
            }
        });
    }

    private void setCurrentTab(int index)
    {
        comboBox1.setSelectedIndex(index);
        tabbedPane1.setSelectedIndex(index);
    }

    /** run {@code tester} to all available packages that was previously initiated from the constructor.
     * make sure to refer from the given package {@link MonoPackageTester#obtainPackage(String)}. Also
     * this will create an instance of the {@code tester} as much as the number of packages needs to be tested.
     * make sure to properly to overload {@link MonoPackageTester#newInstance()} so it can distribute
     * the test evenly to all of packages */
    public <T extends ReflectorUnitTest & MonoPackageTester<T>> void add(T tester)
    {
        final DefaultTableModel model = (DefaultTableModel) tablePackages.getModel();
        model.setRowCount(0);
        double totalPercentage = 0.0;
        for (Pair<String, WindowProfiler> pair : listPackageAndProfiler)
        {
            if (tester == null)
            {
                JOptionPane.showMessageDialog(
                    null,
                    "MonoPackageTester should implements newInstance() which function was" +
                    "\nto create a an instance of the class itself. The return value should" +
                    "\nnot be null or referencing another object! This was done so the package" +
                    "\nrunner can then create an instance of your ReflectorUnitTest class" +
                    "\nas much as the number of requested packages needs to be tested",
                    "MonoPackageTester Error!",
                    JOptionPane.ERROR_MESSAGE
                );
                System.exit(-1);
            }
            T cloneTester = tester.newInstance();
            cloneTester.obtainPackage(pair.first);
            pair.second.add(cloneTester);
            // update the score table
            double percentage = 100.0 * pair.second.getTotalSuccessCount() / pair.second.getTotalTestCount();
            rowBuffer[0] = pair.first;
            rowBuffer[1] = String.format("%.2f %%", percentage);
            totalPercentage += percentage;
            model.addRow(rowBuffer);
        }
        String avgPercentage = String.format("%.2f %%", totalPercentage / listPackageAndProfiler.size());
        labelAvgPercentage.setText(avgPercentage);
    }
}