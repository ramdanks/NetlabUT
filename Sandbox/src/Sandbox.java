import com.NetlabUT.WindowProfiler;
import com.NetlabUT.UnitTest;
import javax.swing.JOptionPane;

final class Sandbox
{
    private static String packageName;
    public static String getPackageName() { return packageName; }

    public static void main(String[] args)
    {
        packageName = JOptionPane.showInputDialog("Please specify package:");

        UnitTest[] unitList = { new StringTest() };
        new WindowProfiler("Modul 1 - JDK, BlueJ, dan Git", unitList);
    }
}