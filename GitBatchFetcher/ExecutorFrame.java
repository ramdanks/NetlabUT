import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.function.Function;

public class ExecutorFrame extends JFrame
{
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;
    private JTable table1;
    private JPanel mainPanel;
    private JLabel labelBatch;
    private JLabel labelRoutine;
    private JLabel labelProgress;

    private static final Color BKG_FAIL = new Color(255, 210, 210);
    private static final Color BKG_FAIL_SELECTED = new Color(255, 175, 175);

    /** first: is error?, second: message */
    private ArrayList<Pair<Boolean, String>>[] commandLogs;
    private boolean[] arrSuccessBatch;

    private static final String ROUTINE_FORMAT    = "Routine: %s";
    private static final String BATCH_FORMAT      = "Batch: %s";
    private static final String PROGRESS_FORMAT   = "[ %d / %d ]";
    private static final String[] COLUMN_TITLE    = { "Repository", "Clone Path", "Commit", "Author", "Date and Time" };
    private static final String[] ROUTINE_LIST    = {
            "Cloning Repository",
            "Get Commit Hash",
            "Checkout Commit",
            "Validation Check"
    };

    @FunctionalInterface
    interface Routine { Object routine(Object arg) throws Throwable; }

    public ExecutorFrame(JFrame owner, Pair<String, String>[] clonePairs, boolean before, int year, int month, int day,
                         int hour, int minute, int second, int nanosecond, ZoneId zoneId)
    {
        setContentPane(mainPanel);
        setVisible(true);
        setLocationRelativeTo(owner);
        setTitle("Fetching Repository");
        setSize(mainPanel.getMinimumSize());
        setMinimumSize(mainPanel.getMinimumSize());

        table1.setModel(new DefaultTableModel(null, COLUMN_TITLE) {
            @Override /* all cells are not editable */
            public boolean isCellEditable(int row, int column) { return false; }
        });

        table1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!arrSuccessBatch[row]) c.setBackground(isSelected ? BKG_FAIL_SELECTED : BKG_FAIL);
                return c;
            }
        });

        Function<Integer, Void> showFetchMessageDialog = (row) -> {
            MessageDialog dialog = new MessageDialog(ExecutorFrame.this, "Fetch Log", false);
            for (Pair<Boolean, String> pair : commandLogs[row])
                dialog.appendln(pair.second);
            dialog.setSize(300,200);
            dialog.setVisible(true);
            return null;
        };

        table1.addMouseListener(new MouseAdapter() {
            @Override // handle double click event
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() != 2) return;
                JTable table = (JTable) me.getSource();
                int row = table.getSelectedRow(); // select a row
                if (row != -1)
                    showFetchMessageDialog.apply(row);
            }
        });

        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("See Logs");
        deleteItem.addActionListener((e) -> {
            int row = table1.getSelectedRow();
            if (row != -1)
                showFetchMessageDialog.apply(row);
        });
        popupMenu.add(deleteItem);
        table1.setComponentPopupMenu(popupMenu);

        progressBar1.setMaximum(clonePairs.length);
        progressBar2.setMaximum(ROUTINE_LIST.length);

        setBatchProgress(0);

        new Thread(() -> {
            final String timestamp = GitCommand.getTimestampFrom(before, year, month, day, hour, minute, second, nanosecond, zoneId);
            final String[] cmdGitHashLatestCommit = GitCommand.getCmdGitHashLatestCommit(timestamp);
            final DefaultTableModel model = (DefaultTableModel) table1.getModel();
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
                final Routine[] routineList = {
                        // git clone
                        (Object ignored) -> {
                            BufferedReader reader = commandLineExecutor(cmdGitClone, null);
                            for (String s = reader.readLine(); s != null; s = reader.readLine())
                            {
                                // right now there's no way we would know if the message is error
                                // for now, let's check the keyword "fatal" returned by "git" command
                                if (s.contains("fatal"))
                                    throw new Throwable(s);
                                commandLogs[idx].add(new Pair<Boolean, String>(false, s));
                            }
                            return null;
                        },
                        // git get hash latest commit
                        (Object ignored) -> {
                            BufferedReader reader = commandLineExecutor(cmdGitHashLatestCommit, path);
                            String hash = reader.readLine();
                            // if there's no such commit, log and remove the clone dir
                            if (hash == null)
                                throw new Throwable("Found No Commit Based on the Given Date!");
                            row[2] = hash;
                            return hash;
                        },
                        // get checkout to commit
                        (Object hash) -> {
                            String[] cmdGitCheckout = GitCommand.getCmdGitCheckoutCommit((String) hash);
                            BufferedReader reader = commandLineExecutor(cmdGitCheckout, path);
                            for (String s = reader.readLine(); s != null; s = reader.readLine())
                                commandLogs[idx].add(new Pair<Boolean, String>(false, s));
                            return hash;
                        },
                        // validation check
                        (Object hash) -> {
                            BufferedReader reader = commandLineExecutor(GitCommand.CMD_GIT_HASH_LATEST_COMMIT, path);
                            String s = reader.readLine();
                            if (!hash.equals(s))
                                throw new Throwable("Fail to checkout commit: " + (String) hash);
                            reader = commandLineExecutor(GitCommand.CMD_GIT_LATEST_COMITTER, path);
                            row[3] = reader.readLine();
                            reader = commandLineExecutor(GitCommand.CMD_GIT_TIME_LATEST_COMMIT, path);
                            row[4] = reader.readLine();
                            return null;
                        }
                };

                try
                {
                    Object args = null;
                    for (int r = 0; r < routineList.length; ++r)
                    {
                        setRoutineLabel(ROUTINE_LIST[r]);
                        args = routineList[r].routine(args);
                        progressBar2.setValue(r+1);
                        if (r == 0) cloningHappens = true;
                    }
                    arrSuccessBatch[i] = true;
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
                    progressBar2.setValue(progressBar2.getMaximum());
                }
                finally
                {
                    setBatchProgress(i+1);
                    model.addRow(row);
                }
            }
        }).start();
    }

    private void setRoutineLabel(String routine)
    {
        String str = String.format(ROUTINE_FORMAT, routine);
        labelRoutine.setText(str);
    }

    private void setBatchLabel(String batch)
    {
        String str = String.format(BATCH_FORMAT, batch);
        labelBatch.setText(str);
    }

    private void setBatchProgress(int i)
    {
        progressBar1.setValue(i);
        labelProgress.setText(String.format(PROGRESS_FORMAT, i, progressBar1.getMaximum()));
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

    private void deleteDirRecursive(Path directory) throws IOException
    {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
