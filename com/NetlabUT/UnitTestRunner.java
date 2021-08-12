package com.NetlabUT;

import javax.swing.*;

/** Class to run test from {@link com.NetlabUT.UnitTest} and display an indeterminate progress bar
 * in a dialog window. it will auto close when the test is complete
 * @author Ramadhan Kalih Sewu
 * @version 1.1
 */
public class UnitTestRunner extends JDialog implements Runnable, ProfileReport<Object>
{
    private UnitTest unitTest;
    private long count;

    private JPanel mainPanel;
    private JProgressBar progressBar;
    private JLabel labelTest;
    private JLabel labelProgress;

    public UnitTestRunner(JFrame owner, UnitTest unitTest)
    {
        super(owner);
        this.unitTest = unitTest;
        this.count = 0;

        unitTest.addProfileReportListener(this);
        labelTest.setText(unitTest.getTestName());
        setModalityType(ModalityType.TOOLKIT_MODAL);

        setResizable(false);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setTitle("Running Unit Test");
        setContentPane(mainPanel);
        setMinimumSize(mainPanel.getMinimumSize());
        setSize(mainPanel.getMinimumSize());
        setLocationRelativeTo(owner);

        new Thread(this).start();
    }

    @Override
    public void run()
    {
        unitTest.run();
        this.dispose();
    }

    @Override
    public void report(Profile<Object> profile)
    {
        labelProgress.setText(String.format("[ %d ]", ++count));
    }
}
