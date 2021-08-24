import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.json.simple.*;

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
    private JComboBox<String> cbZone;
    private JComboBox<String> cbHour;
    private JComboBox<String> cbMinute;
    private JButton fetchLatestCommitButton;
    private JComboBox<String> cbCondition;
    private JTabbedPane tabbedPane1;
    private JTextField textField1;

    private static final int BEFORE_TIME = 0;
    private static final int AFTER_TIME  = 1;

    private static final String[] COLUMN_TITLE            = {"Repo URL", "Clone Path"};
    private static final String JSON_KEY_CONTENT          = "content";
    private static final String JSON_KEY_DATE_YEAR        = "year";
    private static final String JSON_KEY_DATE_MONTH       = "month";
    private static final String JSON_KEY_DATE_DAY         = "day";
    private static final String JSON_KEY_TIME_HOUR        = "hour";
    private static final String JSON_KEY_TIME_MINUTE      = "min";
    private static final String JSON_KEY_TIME_ZONE        = "zone";
    private static final String JSON_KEY_REPO_URL         = "repo_url";
    private static final String JSON_KEY_CLONE_PATH       = "clone_path";
    private static final String JSON_KEY_FILE_TYPE        = "file_type";
    private static final String JSON_KEY_FILE_TYPE_TABLE  = "table";
    private static final String JSON_KEY_FILE_TYPE_DATE   = "date";

    public static void main(String[] args) { new Fetcher(); }

    public Fetcher()
    {
        setMinimumSize(mainPanel.getMinimumSize());
        setSize(mainPanel.getMinimumSize());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        setTitle("Git Fetcher - Get Latest Commit by Time");
        setVisible(true);
        setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try
                {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles)
                    {
                        JSONObject obj = (JSONObject) JSONValue.parse(new FileReader(file));
                        parseDateTimeJSON(obj);
                        parseTableJSON(obj);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });

        JMenuBar mb = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenu menuImport = new JMenu("Import");
        menuImport.add("Table").addActionListener(this::onImportTable);
        menuImport.add("DateTime").addActionListener(this::onImportDateTime);
        JMenu menuExport = new JMenu("Export");
        menuExport.add("Table").addActionListener(this::onExportTable);
        menuExport.add("DateTime").addActionListener(this::onExportDateTime);
        menuFile.add(menuImport);
        menuFile.add(menuExport);
        menuFile.addSeparator();
        menuFile.add("Exit").addActionListener(this::onExit);
        mb.add(menuFile);

        JMenu menuHelp = new JMenu("Help");
        menuHelp.add("About").addActionListener(this::onAbout);
        mb.add(menuHelp);
        setJMenuBar(mb);

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
        table1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
                return c;
            }
        });

        try
        {
            BufferedReader reader = commandLineExecutor(GitCommand.CMD_GIT_VERSION, null);
            String line = reader.readLine();
            textVersion.setText(line);
            if (line == null)
                throw new Throwable("Can't found Git in your system!");
        }
        catch (Throwable t)
        {
            JOptionPane.showMessageDialog(null, t.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);;
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

    private Object AutoCatchWithDialog(Executable executable)
    {
        try { return executable.execute(); }
        catch (Throwable throwable)
        {
            JOptionPane.showMessageDialog(
                this,
                throwable.getMessage(),
                "Exception",
                JOptionPane.ERROR_MESSAGE
            );
        }
        return null;
    }

    private void onFetchLatestCommitButton(ActionEvent evt)
    {
        fetchLatestCommitButton.setEnabled(false);
        // do it in another thread
        // we should revive the button upon completion
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground()
            {
                Executable exec = () -> {

                    DefaultTableModel model = (DefaultTableModel) table1.getModel();

                    StyledDocument doc = textLog.getStyledDocument();
                    SimpleAttributeSet attrGood = new SimpleAttributeSet();
                    SimpleAttributeSet attrBad = new SimpleAttributeSet();
                    StyleConstants.setForeground(attrGood, new Color(220, 220, 200));
                    StyleConstants.setForeground(attrBad, new Color(255, 120, 120));

                    // check if table contain a blankspace
                    boolean removeRowsWithAnyBlankColumn = false;
                    for (int i = 0; i < model.getRowCount(); ++i)
                    {
                        if (model.getValueAt(i, 0) == null || model.getValueAt(i, 1) == null)
                        {
                            final int modal = JOptionPane.showConfirmDialog(
                                    Fetcher.this,
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
                    HashSet<String> cloneSet = new HashSet<>(model.getRowCount());
                    for (int i = 0; i < model.getRowCount(); ++i)
                    {
                        String path = (String) model.getValueAt(i, 1);
                        if (!cloneSet.add(path))
                        {
                            JOptionPane.showMessageDialog(
                                    Fetcher.this,
                                    "Cannot clone repo to the same path!\n" +
                                    "Please resolve this issue by using a unique path.\n" +
                                    "Duplicate Path: " + path,
                                    "Conflict Path",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            return null;
                        }
                    }

                    // prepare args to git command
                    int year       = Integer.parseInt((String) cbYear.getSelectedItem());
                    int month      = cbMonth.getSelectedIndex() + 1;
                    int day        = Integer.parseInt((String) cbDay.getSelectedItem());
                    int hour       = Integer.parseInt((String) cbHour.getSelectedItem());
                    int minute     = Integer.parseInt((String) cbMinute.getSelectedItem());
                    int second     = 0;
                    int nanosecond = 0;
                    ZoneId zoneId  = ZoneId.of((String) cbZone.getSelectedItem());

                    boolean fromBefore = false;
                    if (cbCondition.getSelectedIndex() == BEFORE_TIME)
                        fromBefore = true;

                    String timestamp = GitCommand.getTimestampFrom(fromBefore, year, month, day, hour, minute, second, nanosecond, zoneId);
                    String[] cmdGitHashLatestCommit = GitCommand.getCmdGitHashLatestCommit(timestamp);

                    // focus tab on console log because we want to process fetching
                    tabbedPane1.setSelectedIndex(1);
                    for (int i = 0; i < model.getRowCount(); ++i)
                    {
                        String repoLink  = (String) model.getValueAt(i, 0);
                        String clonePath = (String) model.getValueAt(i, 1);

                        String[] cmdGitClone = GitCommand.cmdGitClone(repoLink, clonePath);
                        boolean cloningHappens = false;
                        try
                        {
                            // string buffer for reading buffered reader from IO
                            String s = null;
                            // do git clone
                            BufferedReader reader = commandLineExecutor(cmdGitClone, null);
                            while ((s = reader.readLine()) != null)
                            {
                                // right now there's no way we would know if the message is error
                                // for now, let's check the keyword "fatal" returned by "git" command
                                if (s.contains("fatal"))
                                    throw new Throwable(s);
                                doc.insertString(doc.getLength(), s, attrGood);
                                doc.insertString(doc.getLength(), "\n", attrGood);
                            }
                            // set to true after cloning completed successfully
                            cloningHappens = true;
                            // get commit hash (git log) from specified date and time
                            reader = commandLineExecutor(cmdGitHashLatestCommit, clonePath);
                            String hash = reader.readLine();
                            // if there's no such commit, log and remove the clone dir
                            if (hash == null)
                                throw new Throwable("Found 0 Commit!");
                            // checkout to the specified commit
                            String[] cmdGitCheckout = GitCommand.getCmdGitCheckoutCommit(hash);
                            reader = commandLineExecutor(cmdGitCheckout, clonePath);
                            while ((s = reader.readLine()) != null)
                            {
                                doc.insertString(doc.getLength(), s, attrGood);
                                doc.insertString(doc.getLength(), "\n", attrGood);
                            }
                            // cross check
                            reader = commandLineExecutor(GitCommand.getCmdGitHashLatestCommit(), clonePath);
                            s = reader.readLine();
                            if (!hash.equals(s))
                                throw new Throwable("Fail to checkout commit: " + hash);
                        }
                        catch (Throwable t)
                        {
                            doc.insertString(doc.getLength(), t.getMessage(), attrBad);
                            doc.insertString(doc.getLength(), "\n", attrBad);
                            // remove cloning directory on unsuccessful attempt
                            if (cloningHappens)
                                deleteDirRecursive(Paths.get(clonePath));
                        }
                    }
                    return null;
                };
                try { exec.execute(); }
                catch (Throwable ignored) {}
                fetchLatestCommitButton.setEnabled(true);
                return null;
            }
        }.execute();
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

    private void onExit(ActionEvent evt)
    {
        dispatchEvent(new WindowEvent(Fetcher.this, WindowEvent.WINDOW_CLOSING));
    }

    private void onAbout(ActionEvent evt)
    {

    }

    private void onImportDateTime(ActionEvent evt)
    {
        AutoCatchWithDialog(() ->
        {
            final JFileChooser fc = new JFileChooser();
            int modal = fc.showOpenDialog(this);
            if (modal == JFileChooser.APPROVE_OPTION)
            {
                JSONObject obj = (JSONObject) JSONValue.parse(new FileReader(fc.getSelectedFile()));
                parseDateTimeJSON(obj);
            }
            return null;
        });
    }

    private void onExportDateTime(ActionEvent evt)
    {
        AutoCatchWithDialog(() ->
        {
            final JFileChooser fc = new JFileChooser();
            int modal = fc.showSaveDialog(this);
            if (modal == JFileChooser.APPROVE_OPTION)
            {
                JSONObject object = new JSONObject();
                object.put(JSON_KEY_FILE_TYPE, JSON_KEY_FILE_TYPE_DATE);
                object.put(JSON_KEY_DATE_YEAR, cbYear.getSelectedItem());
                object.put(JSON_KEY_DATE_MONTH, cbMonth.getSelectedItem());
                object.put(JSON_KEY_DATE_DAY, cbDay.getSelectedItem());
                object.put(JSON_KEY_TIME_HOUR, cbHour.getSelectedItem());
                object.put(JSON_KEY_TIME_MINUTE, cbMinute.getSelectedItem());
                object.put(JSON_KEY_TIME_ZONE, cbZone.getSelectedItem());
                FileWriter writer = new FileWriter(fc.getSelectedFile());
                writer.write(object.toJSONString());
                writer.close();
            }
            return null;
        });
    }

    private void onImportTable(ActionEvent evt)
    {
        AutoCatchWithDialog(() ->
        {
            final JFileChooser fc = new JFileChooser();
            int modal = fc.showOpenDialog(this);
            if (modal == JFileChooser.APPROVE_OPTION)
            {
                JSONObject obj = (JSONObject) JSONValue.parse(new FileReader(fc.getSelectedFile()));
                parseTableJSON(obj);
            }
            return null;
        });
    }

    private void onExportTable(ActionEvent evt)
    {
        AutoCatchWithDialog(() ->
        {
            final JFileChooser fc = new JFileChooser();
            int modal = fc.showSaveDialog(this);
            if (modal == JFileChooser.APPROVE_OPTION)
            {
                JSONObject jsonFile = new JSONObject();
                jsonFile.put(JSON_KEY_FILE_TYPE, JSON_KEY_FILE_TYPE_TABLE);
                JSONArray jsonTableArray = new JSONArray();
                int cnt = table1.getRowCount();
                for (int i = 0; i < cnt; ++i)
                {
                    JSONObject jsonTableObject = new JSONObject();
                    Object url = table1.getValueAt(i, 0);
                    Object path = table1.getValueAt(i, 1);
                    jsonTableObject.put(JSON_KEY_REPO_URL, url);
                    jsonTableObject.put(JSON_KEY_CLONE_PATH, path);
                    jsonTableArray.add(jsonTableObject);
                }
                jsonFile.put(JSON_KEY_CONTENT, jsonTableArray);
                FileWriter writer = new FileWriter(fc.getSelectedFile());
                writer.write(jsonFile.toJSONString());
                writer.close();
            }
            return null;
        });
    }

    private void parseDateTimeJSON(JSONObject obj)
    {
        if (obj == null || !obj.get(JSON_KEY_FILE_TYPE).equals(JSON_KEY_FILE_TYPE_DATE))
            return;
        cbYear.setSelectedItem(obj.get(JSON_KEY_DATE_YEAR));
        cbMonth.setSelectedItem(obj.get(JSON_KEY_DATE_MONTH));
        cbDay.setSelectedItem(obj.get(JSON_KEY_DATE_DAY));
        cbHour.setSelectedItem(obj.get(JSON_KEY_TIME_HOUR));
        cbMinute.setSelectedItem(obj.get(JSON_KEY_TIME_MINUTE));
        cbZone.setSelectedItem(obj.get(JSON_KEY_TIME_ZONE));
    }

    private void parseTableJSON(JSONObject obj)
    {
        if (obj == null || !obj.get(JSON_KEY_FILE_TYPE).equals(JSON_KEY_FILE_TYPE_TABLE))
            return;
        final DefaultTableModel model = (DefaultTableModel) table1.getModel();
        final Object[] actionOption = {"Overwrite", "Append"};
        final int modal = JOptionPane.showOptionDialog(
                Fetcher.this,
                "Actions to the current Table:",
                "Import Table",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                actionOption,
                actionOption[0]
        );
        // unexpected modal (ex: close the dialog)
        if (modal == -1) return;
        // option overwrite (might be based on index of object[] passed in)
        if (modal == 0) model.setRowCount(0);
        JSONArray jsonArray = (JSONArray)obj.get(JSON_KEY_CONTENT);
        Object[] row = new Object[2];
        for (int i = 0; i < jsonArray.size(); ++i)
        {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            row[0] = jsonObject.get(JSON_KEY_REPO_URL);
            row[1] = jsonObject.get(JSON_KEY_CLONE_PATH);
            model.addRow(row);
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

    private void removeBlankTableRows()
    {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        int rowCount = model.getRowCount();
        for (int i = 0; i < rowCount; ++i)
        {
            if (model.getValueAt(i, 0) == null || model.getValueAt(i, 1) == null)
                model.removeRow(i);
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
            cbZone.addItem(s);
        cbZone.setSelectedItem(zoneId.toString());
    }

    private void fillComboBoxCondition()
    {
        // make sure in order because we read the combo box index
        cbCondition.addItem("Before Time");
        cbCondition.addItem("After Time");
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
