import com.NetlabUT.ConsoleProfiler;
import com.NetlabUT.WindowProfiler;
import com.NetlabUT.NetlabUT;

final class Sandbox
{
    public static void main(String[] args)
    {
        NetlabUT[] unitList = { new StringTest(), new ArrayTest() };
        new WindowProfiler("Modul 1 - Praktikum Pemrograman Berorientasi Obyek", unitList);
        new ConsoleProfiler("Modul 1 - Praktikum Pemrograman Berorientasi Obyek", unitList);
    }
}