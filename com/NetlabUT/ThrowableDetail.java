package com.NetlabUT;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

public class ThrowableDetail extends JFrame
{
    private JTextPane textMessage;
    private JList<String> listStackTrace;
    private JLabel textType;
    private JPanel mainPanel;

    private Throwable throwable;

    public ThrowableDetail(Object object)
    {
        if (object instanceof Throwable == false)
            return;

        this.throwable = (Throwable) object;

        textType.setText(throwable.getClass().getName());
        textMessage.setText(throwable.getMessage());

        DefaultListModel<String> model = new DefaultListModel<>();
        listStackTrace.setModel(model);

        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (StackTraceElement element : stackTrace)
            model.addElement(element.toString());

        listStackTrace.addListSelectionListener(this::onStackTraceListSelected);

        setTitle(Profile.getObjectIdentifierString(object));
        setContentPane(mainPanel);
        setMinimumSize(mainPanel.getMinimumSize());
        setSize(mainPanel.getMinimumSize());
        setVisible(true);
    }

    private void onStackTraceListSelected(ListSelectionEvent e)
    {
        int idx = listStackTrace.getSelectedIndex();
        if (idx == -1) return;
        StackTraceElement trace = throwable.getStackTrace()[idx];
        System.err.println(trace.toString());
    }
}
