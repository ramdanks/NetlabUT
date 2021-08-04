package com.NetlabUT;

import javax.swing.*;

/** a frame to show a detail of {@link com.NetlabUT.Profile}
 * @author Ramadhan Kalih Sewu
 * @version 1.0
 * @param <T> type of profile
 */
final class ProfileFrame<T> extends JFrame
{
    private JPanel mainPanel;
    private JLabel labelCorrect;
    private JLabel labelStatus;
    private JLabel labelTime;
    private VariableDetail panelActual;
    private VariableDetail panelReference;
    private JTabbedPane tabbedPane1;
    private JTextArea textMessage;

    public ProfileFrame(Profile<T> profile)
    {
        setContentPane(mainPanel);
        Metric<T> metric = profile.getMetric();

        labelCorrect.setText(profile.isCorrect() ? "Correct" : "Wrong");
        labelStatus.setText("");
        labelTime.setText(String.format("%d ns", metric.nanoTime));
        textMessage.setText(profile.getMessage());

        int status = profile.getReferenceStatus();
        if (status == Status.THROWS || status == Status.THROWS_TYPE)
        {
            if (metric.isThrowing()) panelActual.setContent(metric.throwable);
            else panelActual.setContent(metric.returns);
            panelReference.setContent(profile.getClassReference());
        }
        else
        {
            panelActual.setContent(metric.isThrowing() ? metric.throwable : metric.returns);
            panelReference.setContent(profile.getReference());
        }

        setSize(getMinimumSize());
        setMinimumSize(getMinimumSize());
        setVisible(true);
    }
}
