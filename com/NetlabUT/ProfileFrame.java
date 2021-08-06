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
    private JTextArea textMessage;
    private JTextPane textReference;
    private JButton classDetailButton;
    private JButton classDetailButton1;
    private JButton toStringButton;
    private JButton toStringButton1;
    private JTextPane textActual;

    public ProfileFrame(Profile<T> profile)
    {
        setContentPane(mainPanel);
        Metric<T> metric = profile.getMetric();
        int status = profile.getReferenceStatus();

        labelCorrect.setText(profile.isCorrect() ? "Correct" : "Wrong");
        labelStatus.setText(Status.toString(status));
        labelTime.setText(String.format("%d ns", metric.nanoTime));
        textMessage.setText(profile.getMessage());

        String strReference = profile.getReferenceString();
        String strActual = metric.isThrowing() ?
                Profile.getObjectIdentifierString(metric.throwable) :
                Profile.getObjectIdentifierString(metric.returns);

        textReference.setText(strReference);
        textActual.setText(strActual);

        setSize(getMinimumSize());
        setMinimumSize(getMinimumSize());
        setVisible(true);
    }
}
