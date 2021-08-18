package Sandbox.UnitTest;

import javax.swing.JOptionPane;

import com.NetlabUT.UnitTest;
import com.NetlabUT.WindowProfiler;
import com.GitFetcher.Fetcher;

public class Source
{
    private static String packageName = null;
    public static String getPackageName() { return packageName; }
    
    public static void main(String[] args)
    {
        packageName = JOptionPane.showInputDialog("Specify package name:");

        UnitTest[] unitTestList = { new LocationTest() };
        new WindowProfiler("Modul 1", unitTestList);
    }

}
