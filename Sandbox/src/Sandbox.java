import com.NetlabUT.GradeForm;
import com.NetlabUT.NetlabUT;

final class Sandbox
{
    public static void main(String[] args)
    {
        NetlabUT[] unitList = { new StringTest(), new ArrayTest() };
        new GradeForm("Modul 1 - Praktikum Pemrograman Berorientasi Obyek", unitList).setVisible(true);
    }
}