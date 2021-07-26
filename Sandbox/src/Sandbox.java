import com.NetlabUT.WindowProfiler;
import com.NetlabUT.NetlabUT;

final class Sandbox
{
    public static void main(String[] args)
    {
        NetlabUT[] unitList = { new StringTest(), new ArrayTest() };
        new WindowProfiler("Modul 1 - JDK, BlueJ, dan Git", unitList);
    }
}