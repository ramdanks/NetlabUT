package com.NetlabUT;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.text.DefaultFormatter;

public class ArrayDetail extends JFrame
{
    private final Object[] mArray;

    private JPanel mainPanel;
    private JTextArea textStringView;
    private JSpinner spinner;
    private JLabel labelType;
    private JLabel labelLength;

    public ArrayDetail(Object object)
    {
        this.mArray = (Object[]) object;

        labelType.setText(mArray.getClass().getComponentType().getName());
        labelLength.setText(String.valueOf(mArray.length));

        if (mArray.length <= 0)
        {
            spinner.setEnabled(false);
            return;
        }

        final int start_index = 0;
        textStringView.setText(mArray[start_index].toString());
        SpinnerModel model = new SpinnerNumberModel(start_index, 0, mArray.length - 1, 1);
        spinner.setModel(model);

        JComponent comp = spinner.getEditor();
        JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        spinner.addChangeListener(this::onSpinnerChange);

        setTitle(Profile.getObjectIdentifierString(object));
        setContentPane(mainPanel);
        setMinimumSize(mainPanel.getMinimumSize());
        setSize(mainPanel.getMinimumSize());
        setVisible(true);
    }

    private void onSpinnerChange(ChangeEvent e)
    {
        Integer value = (Integer) spinner.getValue();
        textStringView.setText(mArray[value].toString());
    }
}
