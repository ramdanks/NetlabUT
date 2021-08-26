import javax.swing.*;

public class MessageDialog extends JDialog
{
    private JPanel mainPanel;
    private JTextArea textArea1;
    private JButton OKButton;

    MessageDialog(JFrame owner, String title, boolean editable)
    {
        super(owner);
        setTitle(title);
        setLocationRelativeTo(owner);
        setContentPane(mainPanel);
        textArea1.setEditable(editable);
        OKButton.addActionListener((e) -> this.dispose());
    }

    public void append(String text)
    {
        textArea1.append(text);
    }

    public void appendln(String text)
    {
        textArea1.append(text);
        textArea1.append("\n");
    }

}
