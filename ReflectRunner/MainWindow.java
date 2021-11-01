import com.NetlabUT.NetlabTestApp;
import com.NetlabUT.annotations.NetlabTest;

import javax.swing.*;
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
import java.nio.file.SimpleFileVisitor;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class MainWindow extends JFrame
{
    private static final String[] TABLE_GIT_REMOTE = { "Identifier", "Remote URL" };
    private static final String[] SELECTION_FETCH  = { "Latest Commit", "After Date and Time", "Before Date and Time" };
    private static final String[] COLUMN_REPORT    = { "Subject", "Git", "Search", "Compile", "Test", "Score" };
    private static final String[] COLUMN_UNIT_TEST = { "Unit Test File", "Status" };

    private JPanel mainPanel;
    private JButton btnStart;
    private JButton btnExit;
    private JLabel labelClonePath;
    private JLabel labelGitVersion;
    private JButton btnPickDate;
    private JComboBox<String> cbFetch;
    private JSpinner sbMinute;
    private JSpinner spHour;
    private JComboBox<String> cbTimezone;
    private JButton btnAdd;
    private JButton btnRemove;
    private JButton btnClear;
    private JPanel panelFiles;
    private JPanel panelGitRemote;
    private JList listFiles;
    private JTable tableGitRemote;
    private JButton clearButton;
    private JButton removeButton;
    private JButton addButton;
    private JTabbedPane tabbedPane1;
    private JCheckBox fromFilesCheckBox;
    private JCheckBox fromGitRemoteCheckBox;
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;
    private JTable tableReport;
    private JButton openRepoInExplorerButton;
    private JButton consoleLogButton;
    private JButton unitTestGradingButton;
    private JButton btnClearMain;
    private JButton btnRemoveMain;
    private JButton addUnitTestButton;
    private JButton addScanDirectoryButton;
    private JTextField textField1;
    private JTable tableUnitTest;
    private JPanel panelMain;

    public static void main(String[] args)
    {
        new MainWindow().setVisible(true);
    }

    public MainWindow()
    {
        setContentPane(mainPanel);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String workingDir = System.getProperty("user.dir");
        String cloneDir   = "clone";
        File clonePath    = new File(workingDir, cloneDir);

        labelClonePath.setText(clonePath.getAbsolutePath());
        tableGitRemote.setModel(new DefaultTableModel(null, TABLE_GIT_REMOTE));
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

        panelMain.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                autoCatchWithDialog((o) -> {
                    evt.acceptDrop(DnDConstants.ACTION_REFERENCE);
                    List<File> list = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File f : list)
                    {
                        Files.walk(f.toPath())
                                .filter(Files::isRegularFile)
                                .forEach(MainWindow.this::processUnitTestFile);
                    }
                    return null;
                });
            }
        });

        // fetch selection
        for (String s : SELECTION_FETCH)
            cbFetch.addItem(s);

        // find git
        try
        {
            BufferedReader reader = commandLineExecutor(GitCommand.CMD_GIT_VERSION, null);
            String line = reader.readLine();
            labelGitVersion.setText(line);
            if (line == null)
                throw new Throwable("Can't found Git in your system!");
        }
        catch (Throwable t)
        {
            JOptionPane.showMessageDialog(null, t.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        // fill timezone combo box and set to system timezone
        String systemZoneId = ZoneId.systemDefault().toString();
        String systemOffset = getZoneIdOffset(systemZoneId);
        Set<String> offsets = getAllZoneIdOffset();
        for (String s : offsets)
        {
            cbTimezone.addItem("UTC" + s);
            if (s.equals(systemOffset))
                cbTimezone.setSelectedIndex(cbTimezone.getItemCount() - 1);
        }
        addButton.addActionListener(this::onAddButton);
        removeButton.addActionListener(createTableClearListener(tableGitRemote));
        clearButton.addActionListener(createTableRemoveListener(tableGitRemote));
        btnStart.addActionListener(this::onStartButton);
        btnClearMain.addActionListener(createTableClearListener(tableUnitTest));
        btnRemoveMain.addActionListener(createTableRemoveListener(tableUnitTest));
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
        // do it in another thread
        // we should revive the button upon completion
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground()
            {
                DefaultTableModel model = (DefaultTableModel) tableGitRemote.getModel();
                // check if table contain a blankspace
                boolean removeRowsWithAnyBlankColumn = false;
                for (int i = 0; i < model.getRowCount(); ++i)
                {
                    if (model.getValueAt(i, 0) == null || model.getValueAt(i, 1) == null)
                    {
                        final int modal = JOptionPane.showConfirmDialog(
                            MainWindow.this,
                            "Cannot process with incomplete information!\n" +
                            "You cannot left an empty column in the table!\n" +
                            "Do you want to Remove all rows containing any blank column?",
                            "Incomplete Information",
                            JOptionPane.YES_NO_OPTION
                        );
                        if (modal != 0) return null;
                        removeRowsWithAnyBlankColumn = true;
                        break;
                    }
                }
                // remove rows containing blank column
                if (removeRowsWithAnyBlankColumn)
                {
                    for (int i = 0; i < model.getRowCount(); ++i)
                    {
                        if (model.getValueAt(i, 0) == null || model.getValueAt(i, 1) == null)
                        {
                            model.removeRow(i);
                            --i;
                        }
                    }
                }
                // check if there's data left to process
                // (because there's possibility that we remove some row before)
                if (model.getRowCount() == 0)
                {
                    JOptionPane.showMessageDialog(null, "You should have at least 1 repository in the Table!");
                    return null;
                }
                // clone location should be unique
                // check by identifier
                HashSet<String> cloneSet = new HashSet<>(model.getRowCount());
                for (int i = 0; i < model.getRowCount(); ++i)
                {
                    String id = (String) model.getValueAt(i, 0);
                    if (!cloneSet.add(id))
                    {
                        JOptionPane.showMessageDialog(
                            MainWindow.this,
                            "Duplicate identifier detected!\n" +
                            "Please resolve this issue before continuing\n" +
                            "Identifier: " + id,
                            "Conflict Identifier",
                            JOptionPane.ERROR_MESSAGE
                        );
                        return null;
                    }
                }
                // do git command
                /*
                final String timestamp = GitCommand.getTimestampFrom(
                );
                final String[] cmdGitHashLatestCommit = GitCommand.getCmdGitHashLatestCommit(timestamp);
                final DefaultTableModel model = (DefaultTableModel) tableReport.getModel();
                String[] row = new String[COLUMN_TITLE.length];
                commandLogs = new ArrayList[clonePairs.length];
                arrSuccessBatch = new boolean[clonePairs.length];
                for (int i = 0; i < clonePairs.length; ++i)
                {
                    final String url = clonePairs[i].first;
                    final String path = clonePairs[i].second;
                    setBatchLabel(url);
                    row[0] = url;
                    row[1] = path;
                    commandLogs[i] = new ArrayList<>();
                    String[] cmdGitClone = GitCommand.cmdGitClone(url, path);
                    final int idx = i;
                    boolean cloningHappens = false;
                    try
                    {
                        // git clone
                        BufferedReader reader = commandLineExecutor(cmdGitClone, null);
                        for (String s = reader.readLine(); s != null; s = reader.readLine())
                        {
                            // right now there's no way we would know if the message is error
                            // for now, let's check the keyword "fatal" returned by "git" command
                            if (s.contains("fatal"))
                                throw new Throwable(s);
                            commandLogs[idx].add(new Pair<Boolean, String>(false, s));
                        }
                        // git get hash latest commit
                        reader = commandLineExecutor(cmdGitHashLatestCommit, path);
                        String hash = reader.readLine();
                        // if there's no such commit, log and remove the clone dir
                        if (hash == null)
                            throw new Throwable("Found No Commit Based on the Given Date!");
                        row[2] = hash;
                        // get checkout to commit
                        String[] cmdGitCheckout = GitCommand.getCmdGitCheckoutCommit((String) hash);
                        reader = commandLineExecutor(cmdGitCheckout, path);
                        for (String s = reader.readLine(); s != null; s = reader.readLine())
                            commandLogs[idx].add(new Pair<Boolean, String>(false, s));
                        // validation check
                        reader = commandLineExecutor(GitCommand.CMD_GIT_HASH_LATEST_COMMIT, path);
                        String s = reader.readLine();
                        if (!hash.equals(s))
                            throw new Throwable("Fail to checkout commit: " + (String) hash);
                        reader = commandLineExecutor(GitCommand.CMD_GIT_LATEST_COMITTER, path);
                        row[3] = reader.readLine();
                        reader = commandLineExecutor(GitCommand.CMD_GIT_TIME_LATEST_COMMIT, path);
                        row[4] = reader.readLine();
                    }
                    catch (Throwable t)
                    {
                        arrSuccessBatch[i] = false;
                        setRoutineLabel("Backtracking");
                        commandLogs[idx].add(new Pair<Boolean, String>(true, t.getMessage()));
                        // remove cloning directory on unsuccessful attempt
                        if (cloningHappens)
                        {
                            commandLogs[idx].add(new Pair<Boolean, String>(true, "Deleting " + clonePairs[i].second));
                            try { deleteDirRecursive(Paths.get(clonePairs[i].second)); }
                            catch (IOException e) { commandLogs[idx].add(new Pair<Boolean, String>(true, e.getMessage())); }
                        }
                    }
                    finally
                    {
                        setBatchProgress(i+1);
                        model.addRow(row);
                    }
                */
                return null;
            }
        }.execute();
    }

    /** must be .java or .class file */
    private void processUnitTestFile(Path path)
    {
        boolean needCompilation = false;
        String filepath         = path.toString();
        int idxExt              = filepath.lastIndexOf(".");
        CharSequence ext        = filepath.subSequence(idxExt, filepath.length());

        if (ext.equals(".java"))
            needCompilation = true;
        else if (!ext.equals(".class"))
            return;

        DefaultTableModel model = (DefaultTableModel) tableUnitTest.getModel();
        int idx = model.getRowCount();
        model.addRow(new Object[]{filepath, "Verifying"});

        if (needCompilation)
        {
            File fileClass = new File(filepath.subSequence(0, idxExt) + ".class");
            if (fileClass.exists())
            {
                try
                {
                    if(!fileClass.delete())
                        throw new SecurityException();
                }
                catch (SecurityException e)
                {
                    model.setValueAt("Error Delete Existing .class", idx, 1);
                    return;
                }
            }
            // compile the .java code
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null)
            {
                model.setValueAt("System Java Compiler not Found", idx, 1);
                return;
            }
            int returnCompiler = compiler.run(null, null, null, filepath);
            if (returnCompiler != 0)
            {
                model.setValueAt("Fail Compiling, Return Code: " + returnCompiler, idx, 1);
                return;
            };
            filepath = fileClass.getAbsolutePath();
        }

        try
        {
            byte[] bytes = Files.readAllBytes(new File(filepath).toPath());
            Class<?> ut  = new NetlabTestApp.ByteClassLoader().defineClass(bytes);
            if (!ut.isAnnotationPresent(NetlabTest.class))
            {
                model.setValueAt("Not a NetlabTest", idx, 1);
                return;
            }
            model.setValueAt("Verified", idx, 1);
        }
        catch (IOException e)
        {
            model.setValueAt("Fail Reading Class, IOException", idx, 1);
        }
    }

    private void onAddButton(ActionEvent evt)
    {
        DefaultTableModel model = (DefaultTableModel) tableGitRemote.getModel();
        model.addRow((Object[]) null);
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
