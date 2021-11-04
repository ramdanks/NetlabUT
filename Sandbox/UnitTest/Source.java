package Sandbox.UnitTest;
import com.NetlabUT.NetlabTestApp;

public class Source
{
    public static void main(String[] args)
    {
        NetlabTestApp.runReflect(Source.class, "Test", new String[]{"Sandbox.GoldenSample"});
    }
}
