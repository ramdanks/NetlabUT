package com.NetlabUT;

import javax.swing.*;

/** a frame to show a detail of {@link com.NetlabUT.Profile}
 * @author Ramadhan Kalih Sewu
 * @version 1.0
 * @param <T> type of profile
 */
final class ProfileFrame<T> extends JFrame
{
    private final Profile<T> profile;

    private static final byte TYPE_NULL      = 0;
    private static final byte TYPE_OBJECT    = 1;
    private static final byte TYPE_ARRAY     = 2;
    private static final byte TYPE_THROWABLE = 3;

    private JPanel mainPanel;
    private JLabel labelCorrect;
    private JLabel labelStatus;
    private JLabel labelTime;
    private JTextArea textMessage;
    private JTextPane textReference;
    private JButton buttonClassReference;
    private JButton buttonClassActual;
    private JButton buttonObjectReference;
    private JButton buttonObjectActual;
    private JTextPane textActual;

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

        String strReference = profile.getReferenceString(StringFormatter::idString);
        String strActual = profile.getActualString(StringFormatter::idString);

        textReference.setText(strReference);
        textActual.setText(strActual);

        setSize(getMinimumSize());
        setMinimumSize(getMinimumSize());
        setVisible(true);

        Class<?> classReference = profile.getClassReference();
        Class<?> classActual = profile.getClassActual();

        byte typeObjectReference = getTypeObject(profile.getReference());
        byte typeObjectActual    = getTypeObject(profile.getActual());

        adjustButton(typeObjectReference, classReference, buttonObjectReference, buttonClassReference);
        adjustButton(typeObjectActual, classActual, buttonObjectActual, buttonClassActual);

        buttonClassReference.addActionListener((e) -> new ClassDetail(this.profile.getClassReference()));
        buttonClassActual.addActionListener((e) -> new ClassDetail(this.profile.getClassActual()));

        buttonObjectReference.addActionListener((e) -> newObjectWindow(profile.getReference(), typeObjectReference));
        buttonObjectActual.addActionListener((e) -> newObjectWindow(profile.getActual(), typeObjectActual));
    }

    private void newObjectWindow(Object object, byte typeObject)
    {
        switch (typeObject)
        {
            case TYPE_ARRAY     : new ArrayDetail(object); break;
            case TYPE_THROWABLE : new ThrowableDetail(object); break;
            case TYPE_OBJECT    : new ObjectDetail(object); break;
        }
    }

    private byte getTypeObject(Object obj)
    {
        if      (obj == null)               return TYPE_NULL;
        else if (obj.getClass().isArray())  return TYPE_ARRAY;
        else if (obj instanceof Throwable)  return TYPE_THROWABLE;
        return TYPE_OBJECT;
    }

    private void adjustButton(byte typeObject, Class<?> clazz, JButton btnObj, JButton btnClazz)
    {
        if      (clazz == null)                 btnClazz.setVisible(false);
        if      (typeObject == TYPE_NULL)       btnObj.setVisible(false);
        else if (typeObject == TYPE_ARRAY)      btnObj.setText("Inspect Array");
        else if (typeObject == TYPE_THROWABLE)  btnObj.setText("Inspect Throwable");
    }
}
