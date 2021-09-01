package com.Reflector;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;

import javax.swing.JOptionPane;
import com.NetlabUT.UnitTest;
import com.NetlabUT.WindowProfiler;

/** Run ReflectorUnitTest with a single or multiple packages specified by the user.
 * This class expect your {@link ReflectorUnitTest} to obtained it subject from {@link ClassR#ClassR(String, String)}
 * using a specific package. This will help automate test for a multiple packages.
 * @author Ramadhan Kalih Sewu
 * @version 1.2
 */
public class PackageRunner
{
    /** run {@code unitTestClasses} for every packages that was
     * listed from [Property of "user.dir"]/TestedPackages.txt.
     * Refer to your ReflectorUnitTest Classes to run the test
     * from the given package name {@link MonoPackageTester#obtainPackage(String)}.
     * @param title the title of unit test passed to {@link com.NetlabUT.WindowProfiler}
     * @param unitTestClasses list of reflector unit test for each package to run */
    @SafeVarargs
    public static <T extends ReflectorUnitTest & MonoPackageTester> void run(String title, T... unitTestClasses)
    {
        ArrayList<String> packages = new ArrayList<>(1);
        File file = new File(System.getProperty("user.dir") + "/TestedPackages.txt");
        try
        {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine())
                packages.add(reader.nextLine());
            reader.close();
        }
        catch (FileNotFoundException ignored)
        {
            JOptionPane.showMessageDialog(
                null,
                "Please create a file: \n" +
                file.getAbsolutePath() +
                "\nSpecify the list of packages you want to run using Reflector Unit Test" +
                "\nThe list should contains the name of existing packages and separated by line!" +
                "\nIf empty, it will run under the scope of your unit test package!",
                "Specify Packages",
                JOptionPane.INFORMATION_MESSAGE
            );
        }

        for (String pkg : packages)
        {
            for (T unitTestClass : unitTestClasses)
                unitTestClass.obtainPackage(pkg);
            WindowProfiler wnd = new WindowProfiler(title, unitTestClasses);
            wnd.setTitle("Package: " + pkg);
        }
    }
}