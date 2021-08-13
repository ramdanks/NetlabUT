package com.NetlabUT;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

public class ThrowableDetail extends JFrame
{
    private Throwable mThrowable;

    private JTextPane textMessage;
    private JList<String> listStackTrace;
    private JLabel textType;
    private JPanel mainPanel;

    public ThrowableDetail(Object object)
    {
        if (object instanceof Throwable == false)
            return;

        this.mThrowable = (Throwable) object;

        textType.setText(mThrowable.getClass().getName());
        textMessage.setText(mThrowable.getMessage());

        DefaultListModel<String> model = new DefaultListModel<>();
        listStackTrace.setModel(model);

        StackTraceElement[] stackTrace = mThrowable.getStackTrace();
        for (StackTraceElement element : stackTrace)
            model.addElement(element.toString());

        listStackTrace.addListSelectionListener(this::onStackTraceListSelected);

        setTitle("Throwable - " + StringFormatter.idString(object));
        setContentPane(mainPanel);
        setMinimumSize(mainPanel.getMinimumSize());
        setSize(mainPanel.getMinimumSize());
        setVisible(true);
    }

    private void onStackTraceListSelected(ListSelectionEvent e)
    {
        int idx = listStackTrace.getSelectedIndex();
        if (idx == -1) return;
        StackTraceElement trace = mThrowable.getStackTrace()[idx];
        System.err.println(trace.toString());
    }
}
