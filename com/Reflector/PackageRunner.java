package com.Reflector;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.NetlabUT.UnitTest;
import com.NetlabUT.WindowProfiler;

/** Run ReflectorUnitTest with a single or multiple packages specified by the user.
 * This class expect your {@link ReflectorUnitTest} to obtained it subject from {@link ClassR#ClassR(String, String)}
 * using a specific package. This will help automate test for a multiple packages.
 * @author Ramadhan Kalih Sewu
 * @version 1.2
 */
public class PackageRunner<T extends ReflectorUnitTest & MonoPackageTester> extends JFrame
{
    private static final String[] TABLE_PACKAGES_COLUMN = { "Packages", "Percentage" };
    private static final Object[] rowBuffer = new Object[2];
    private ArrayList<String> packages = new ArrayList<>(1);
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JTable tablePackages;
    private JTable table2;

    /** run {@code unitTests} for every packages that was
     * listed from [Property of "user.dir"]/TestedPackages.txt.
     * Refer to your ReflectorUnitTest Classes to run the test
     * from the given package name {@link MonoPackageTester#obtainPackage(String)}.
     * @param title the title of unit test passed to {@link com.NetlabUT.WindowProfiler}
     * @param unitTests list of reflector unit test for each package to run */
    public PackageRunner(String title, T... unitTests)
    {
        setContentPane(mainPanel);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(800, 600);

        tablePackages.setModel(new DefaultTableModel(null, TABLE_PACKAGES_COLUMN) {
            @Override /* all cells are not editable */
            public boolean isCellEditable(int row, int column) { return false; }
        });

        tablePackages.addMouseListener(new MouseAdapter() {
            @Override // handle double click event
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2)
                {
                    JTable table = (JTable) me.getSource();
                    tabbedPane1.setSelectedIndex(table.getSelectedRow());
                }
            }
        });

        File file = new File(System.getProperty("user.dir") + "/TestedPackages.txt");
        try
        {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine())
                packages.add(reader.nextLine());
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
        }

        new Thread(() -> {
            for (String packageName : packages)
            {
                UnitTest[] currentUnitTests = new UnitTest[unitTests.length];
                for (int i = 0; i < unitTests.length; ++i)
                {
                    T tester = unitTests[i].newInstance();
                    tester.obtainPackage(packageName);
                    currentUnitTests[i] = tester;
                }
                WindowProfiler wnd = new WindowProfiler(title, currentUnitTests);
                appendPackage(packageName, wnd);
            }
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }).start();
    }

    private void appendPackage(String packageName, WindowProfiler windowProfiler)
    {
        tabbedPane1.addTab(packageName, windowProfiler.getContentPane());
        windowProfiler.setTitle("Package: " + packageName);

        DefaultTableModel model = (DefaultTableModel) tablePackages.getModel();
        rowBuffer[0] = packageName;
        rowBuffer[1] = 100.0 * windowProfiler.getTotalSuccessCount() / windowProfiler.getTotalSuccessCount();
        model.addRow(rowBuffer);
    }
}