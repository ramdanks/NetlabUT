package Sandbox.UnitTest;
import com.Reflector.PackageRunner;
public class Source
{
    public static void main(String[] args)
    {
        PackageRunner runner = new PackageRunner("Title");
        runner.setVisible(true);
        runner.add(new LocationTest());
    }
}
