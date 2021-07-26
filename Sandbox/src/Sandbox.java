import com.NetlabUT.WindowProfiler;
import com.NetlabUT.UnitTest;

final class Sandbox
{
    public static void main(String[] args)
    {
        UnitTest[] unitList = { new StringTest(), new ArrayTest() };
        new WindowProfiler("Modul 1 - JDK, BlueJ, dan Git", unitList);
    }
}