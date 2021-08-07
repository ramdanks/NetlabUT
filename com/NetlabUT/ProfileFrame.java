package com.NetlabUT;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
    private JButton buttonClassReference;
    private JButton buttonClassActual;
    private JButton toStringButton;
    private JButton toStringButton1;
    private JTextPane textActual;

    private Profile<T> profile;

    public ProfileFrame(Profile<T> profile)
    {
        this.profile = profile;

        setTitle("Profile Frame");
        setContentPane(mainPanel);
        int status = profile.getReferenceStatus();

        labelCorrect.setText(profile.isCorrect() ? "Correct" : "Wrong");
        labelStatus.setText(Status.toString(status));
        labelTime.setText(String.format("%d ns", profile.getProfileTime()));
        textMessage.setText(profile.getMessage());

        String strReference = profile.getReferenceString();
        String strActual = Profile.getObjectIdentifierString(profile.getActual());

        textReference.setText(strReference);
        textActual.setText(strActual);

        setSize(getMinimumSize());
        setMinimumSize(getMinimumSize());
        setVisible(true);

        if (profile.getClassReference() == null)
        {
            buttonClassReference.setVisible(false);
        }
        if (profile.getReference() == null)
        {
            toStringButton.setVisible(false);
        }
        if (profile.getActual() == null)
        {
            toStringButton1.setVisible(false);
            buttonClassActual.setVisible(false);
        }

        buttonClassReference.addActionListener((e) -> new ClassDetail(this.profile.getClassReference()));
        buttonClassActual.addActionListener((e) -> new ClassDetail(this.profile.getClassActual()));
    }
}
