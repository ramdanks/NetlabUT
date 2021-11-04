import com.NetlabUT.NetlabTestApp;
import com.NetlabUT.annotations.NetlabReflectTest;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class MainWindow extends JFrame
{
    private static final String[] COLUMN_REPORT    = { "Subject", "Git", "Search", "Compile", "Test", "Score" };
    private static final String[] COLUMN_UNIT_TEST = { "Unit Test File", "Status" };
    private static final String[] COLUMN_SUBJECT_FILES = { "Path", "Hint" };

    private JPanel mainPanel;
    private JButton btnStart;
    private JButton btnExit;
    private JButton btnAddDirFiles;
    private JButton btnRemoveFiles;
    private JButton btnClearFiles;
    private JPanel panelFiles;
    private JTabbedPane tabbedPane1;
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;
    private JTable tableReport;
    private JButton openRepoInExplorerButton;
    private JButton consoleLogButton;
    private JButton unitTestGradingButton;
    private JButton btnClearMain;
    private JButton btnRemoveMain;
    private JButton addButton1;
    private JButton scanFromDirectoryButton;
    private JTextField tfTitle;
    private JTable tableUnitTest;
    private JPanel panelMain;
    private JRadioButton onlyIncludeClassRadioButton;
    private JRadioButton onlyIncludeJavaRadioButton;
    private JRadioButton prioritizeJavaRadioButton;
    private JRadioButton prioritizeClassRadioButton;
    private JCheckBox useHintCheckBox;
    private JTable tableFiles;

    public static void main(String[] args)
    {
        new MainWindow().setVisible(true);
    }

    public MainWindow()
    {
        setContentPane(mainPanel);
        setMinimumSize(new Dimension(700, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ButtonGroup group = new ButtonGroup();
        group.add(onlyIncludeClassRadioButton);
        group.add(onlyIncludeJavaRadioButton);
        group.add(prioritizeJavaRadioButton);
        group.add(prioritizeClassRadioButton);
        onlyIncludeJavaRadioButton.setSelected(true);

        useHintCheckBox.setToolTipText(
            "<html>" +
            "Look for specific directory that may contained in the $Path directory tree." + "<br>" +
            "If the hint directory doesn't exists anywhere in the tree, no subject to be tested" +
            "</html>"
        );

        tableFiles.setToolTipText("Path to directory that contain subject files needed for the unit test");

        Consumer<Path> unitTestFileListener = path -> {
            String filepath  = path.toString();
            int idxExt       = filepath.lastIndexOf(".");
            CharSequence ext = filepath.subSequence(idxExt, filepath.length());
            if (ext.equals(".java") || ext.equals(".class"))
            {
                DefaultTableModel model = (DefaultTableModel) tableUnitTest.getModel();
                model.addRow(new Object[]{filepath, "Non-Verified"});
            }
        };

        tableReport.setModel(new DefaultTableModel(null, COLUMN_REPORT){
            @Override /* all cells are not editable */
            public boolean isCellEditable(int row, int column) { return false; }
        });
        tableUnitTest.setModel(new DefaultTableModel(null, COLUMN_UNIT_TEST){
            @Override /* all cells are not editable */
            public boolean isCellEditable(int row, int column) { return false; }
        });
        tableUnitTest.getColumnModel().getColumn(1).setMinWidth(200);
        tableUnitTest.getColumnModel().getColumn(1).setMaxWidth(200);

        tableFiles.setModel(new DefaultTableModel(null, COLUMN_SUBJECT_FILES){
            @Override /* all cells are not editable */
            public boolean isCellEditable(int row, int column) { return column == 1; }
        });

        panelMain.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                autoCatchWithDialog(o -> {
                    evt.acceptDrop(DnDConstants.ACTION_REFERENCE);
                    List<File> list = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File f : list)
                    {
                        Files.walk(f.toPath())
                            .filter(Files::isRegularFile)
                            .forEach(unitTestFileListener);
                    }
                    return null;
                });
            }
        });

        panelFiles.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                autoCatchWithDialog(o -> {
                    evt.acceptDrop(DnDConstants.ACTION_REFERENCE);
                    List<File> list = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File f : list)
                    {
                        if (!f.isDirectory()) continue;
                        DefaultTableModel model = (DefaultTableModel) tableFiles.getModel();
                        model.addRow(new Object[]{f, null});
                    }
                    return null;
                });
            }
        });

        btnStart.addActionListener(this::onStartButton);
        btnExit.addActionListener(actionEvent -> super.dispose());
        btnClearMain.addActionListener(createTableClearListener(tableUnitTest));
        btnRemoveMain.addActionListener(createTableRemoveListener(tableUnitTest));
        btnRemoveFiles.addActionListener(createTableRemoveListener(tableFiles));
        btnClearFiles.addActionListener(createTableClearListener(tableFiles));
        btnAddDirFiles.addActionListener(actionEvent -> {
            JFileChooser f = new JFileChooser();
            f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int ret = f.showOpenDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION)
            {
                DefaultTableModel model = (DefaultTableModel) tableFiles.getModel();
                model.addRow(new Object[]{f.getSelectedFile(), null});
            }
        });
        scanFromDirectoryButton.addActionListener(actionEvent -> {
            JFileChooser f = new JFileChooser();
            f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int ret = f.showOpenDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION)
            {
                autoCatchWithDialog(o -> {
                    Files.walk(f.getSelectedFile().toPath())
                        .filter(Files::isRegularFile)
                        .forEach(unitTestFileListener);
                    return null;
                });
            }
        });
        addButton1.addActionListener(actionEvent -> {
            JFileChooser f = new JFileChooser();
            f.setFileSelectionMode(JFileChooser.FILES_ONLY);
            f.setAcceptAllFileFilterUsed(false);
            f.addChoosableFileFilter(new FileNameExtensionFilter("Java Source Code", "java"));
            f.addChoosableFileFilter(new FileNameExtensionFilter("Java Byte Code", "class"));
            int ret = f.showOpenDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION)
                unitTestFileListener.accept(f.getSelectedFile().toPath());
        });
        useHintCheckBox.addActionListener(actionEvent -> {
            JCheckBox cb = (JCheckBox) actionEvent.getSource();
            int minWidth = 100;
            int maxWidth = Integer.MAX_VALUE;
            if (!cb.isSelected())
            {
                minWidth = 0;
                maxWidth = 0;
            }
            tableFiles.getColumnModel().getColumn(1).setMinWidth(minWidth);
            tableFiles.getColumnModel().getColumn(1).setMaxWidth(maxWidth);
            tableFiles.getColumnModel().getColumn(1).setWidth(minWidth);
            tableFiles.getColumnModel().getColumn(1).setMinWidth(minWidth);
        });

        useHintCheckBox.setSelected(true);
    }

    private static Set<String> getAllZoneIdOffset()
    {
        Set<String> result = new HashSet<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        for (String zoneId : ZoneId.getAvailableZoneIds())
            result.add(getZoneIdOffset(zoneId));
        return result;
    }

    private static String getZoneIdOffset(String zoneId)
    {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId id = ZoneId.of(zoneId);
        ZonedDateTime zonedDateTime = localDateTime.atZone(id);
        ZoneOffset zoneOffset = zonedDateTime.getOffset();
        return zoneOffset.getId().replaceAll("Z", "+00:00");
    }

    private BufferedReader commandLineExecutor(String[] args, String workingDirectory) throws IOException
    {
        ProcessBuilder builder = new ProcessBuilder(args);
        builder.redirectErrorStream(true);
        if (workingDirectory != null)
            builder.directory(new File(workingDirectory));
        Process process = builder.start();
        final InputStream is = process.getInputStream();
        return new BufferedReader(new InputStreamReader(is));
    }

    private void onStartButton(ActionEvent evt)
    {
        btnStart.setEnabled(false);
        tableFiles.setEnabled(false);
        tableUnitTest.setEnabled(false);
        // do it in another thread
        // we should revive the button upon completion
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground()
            {
                // error handling
                if (tableUnitTest.getModel().getRowCount() == 0)
                {
                    tabbedPane1.setSelectedIndex(0);
                    JOptionPane.showMessageDialog(
                        MainWindow.this,
                        "Please specify unit test to run!",
                        "Empty Unit Test",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    btnStart.setEnabled(true);
                    return null;
                }
                else if (tableFiles.getModel().getRowCount() == 0)
                {
                    tabbedPane1.setSelectedIndex(1);
                    JOptionPane.showMessageDialog(
                        MainWindow.this,
                        "Please specify subject directory path!",
                        "Empty Subject",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    btnStart.setEnabled(true);
                    return null;
                }
                tabbedPane1.setSelectedIndex(2);
                DefaultTableModel model = (DefaultTableModel) tableFiles.getModel();
                // check if use hint is check but no hint given
                boolean nullHint = false;
                for (int i = 0; i < model.getRowCount(); ++i)
                {
                    // path directory is null
                    if (model.getValueAt(i, 0) == null)
                    {
                        final int modal = JOptionPane.showConfirmDialog(
                            MainWindow.this,
                            "Path directory cannot be empty!\n" +
                            "Do you want to remove all rows with blank Path?",
                            "Empty Subject",
                            JOptionPane.YES_NO_OPTION
                        );
                        if (modal != 0) return null;
                        break;
                    }
                    // hint is null
                    else if (model.getValueAt(i, 1) == null)
                    {
                        nullHint = true;
                    }
                }
                if (nullHint)
                {
                    tabbedPane1.setSelectedIndex(1);
                    int ret = JOptionPane.showConfirmDialog(
                        MainWindow.this,
                        "Path with empty directory hint expect to directly contain\n" +
                        "the files necessary to be the subject of unit test.\n" +
                        "Confirm this behaviour and proceed to run the test?",
                        "Contain Empty Hint",
                        JOptionPane.OK_CANCEL_OPTION
                    );
                    if (ret != 0)
                    {
                        btnStart.setEnabled(true);
                        return null;
                    }
                }
                // provide all the test classes
                List<Class<? extends NetlabReflectTest>> tests = new ArrayList<>();
                // verify unit test file
                // expect table to only contain .java and .class
                model = (DefaultTableModel) tableUnitTest.getModel();
                int utCount = model.getRowCount();
                for (int i = 0; i < utCount; ++i)
                {
                    String filepath   = (String) model.getValueAt(i, 0);
                    int idxExt        = filepath.lastIndexOf(".");
                    CharSequence ext  = filepath.subSequence(idxExt, filepath.length());
                    // compile before reading the class
                    if (ext.equals(".java"))
                    {
                        File fileClass = new File(filepath.subSequence(0, idxExt) + ".class");
                        if (fileClass.exists() && !fileClass.delete())
                        {
                            model.setValueAt("Error Delete Existing .class", i, 1);
                            continue;
                        }
                        // compile the .java code
                        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                        if (compiler == null)
                        {
                            model.setValueAt("System Java Compiler not Found", i, 1);
                            continue;
                        }
                        int returnCompiler = compiler.run(null, null, null, filepath);
                        if (returnCompiler != 0)
                        {
                            model.setValueAt("Fail Compiling, Return Code: " + returnCompiler, i, 1);
                            continue;
                        };
                        filepath = fileClass.getAbsolutePath();
                    }
                    // load the class
                    try
                    {
                        byte[] bytes = Files.readAllBytes(new File(filepath).toPath());
                        Class<?> ut  = new NetlabTestApp.ByteClassLoader().defineClass(bytes);
                        if (!ut.isAnnotationPresent(NetlabReflectTest.class))
                        {
                            model.setValueAt("Not a NetlabTest", i, 1);
                            continue;
                        }
                        tests.add((Class<? extends NetlabReflectTest>) ut);
                        model.setValueAt("Verified", i, 1);
                    }
                    catch (IOException e)
                    {
                        model.setValueAt("Fail Reading Class, IOException", i, 1);
                    }
                }

                model = (DefaultTableModel) tableFiles.getModel();
                int subjectDirCount = model.getRowCount();
                List<File> subjectDirs = new ArrayList<>(subjectDirCount);
                for (int i = 0; i < subjectDirCount; ++i)
                {
                    String rootDir = (String) model.getValueAt(i, 0);
                    String hintDir = (String) model.getValueAt(i, 1);
                    if (!useHintCheckBox.isSelected() || hintDir == null || hintDir.isBlank())
                    {
                        subjectDirs.add(new File(rootDir));
                        continue;
                    }
                    autoCatchWithDialog(o -> {
                        Files.walk(new File(rootDir).toPath())
                            .filter(Files::isDirectory)
                            .forEach(path -> {
                                if (path.getFileName().equals(hintDir))
                                    subjectDirs.add(path.toFile());
                            });
                        return null;
                    });
                }

                int subjectHook = -1;
                if (onlyIncludeClassRadioButton.isSelected()) subjectHook = NetlabTestApp.SUBJECT_CLASS_ONLY;
                else if (onlyIncludeJavaRadioButton.isSelected()) subjectHook = NetlabTestApp.SUBJECT_JAVA_ONLY;
                else if (prioritizeClassRadioButton.isSelected()) subjectHook = NetlabTestApp.SUBJECT_PRIORITIZE_CLASS;
                else if (prioritizeJavaRadioButton.isSelected()) subjectHook = NetlabTestApp.SUBJECT_PRIORITIZE_JAVA;

                NetlabTestApp.runReflect(
                    (Class<? extends NetlabReflectTest>[]) tests.toArray(),
                    tfTitle.getText(),
                    (File[]) subjectDirs.toArray(),
                    subjectHook
                );

                btnStart.setEnabled(true);
                tableFiles.setEnabled(true);
                tableUnitTest.setEnabled(true);
                return null;
            }
        }.execute();
    }

    private ActionListener createTableRemoveListener(JTable table)
    {
        return event -> {
            int[] indices = table.getSelectedRows();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int i = 0; i < indices.length; ++i)
                model.removeRow(indices[i]-i);
        };
    }

    private ActionListener createTableClearListener(JTable table)
    {
        return event -> {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            while (model.getRowCount() != 0)
                model.removeRow(0);
        };
    }

    private <R> R autoCatchWithDialog(Executable<Void,R,Throwable> executable)
    {
        try { return executable.run(null); }
        catch (Throwable throwable)
        {
            JOptionPane.showMessageDialog(
                this,
                throwable,
                "Exception",
                JOptionPane.ERROR_MESSAGE
            );
        }
        return null;
    }
}
