import com.NetlabUT.WindowProfiler;
import com.Reflector.PackageR;
import com.NetlabUT.UnitTest;

final class Sandbox
{
    public static void main(String[] args)
    {
        PackageR packageR = new PackageR("Sample1");

        UnitTest[] unitList = { new StringTest(), new ArrayTest(), new RecruiterTest(packageR) };
        new WindowProfiler("Modul 1 - JDK, BlueJ, dan Git", unitList);
    }
}