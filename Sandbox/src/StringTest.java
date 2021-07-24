import com.NetlabUT.NetlabUT;

public class StringTest extends NetlabUT
{
    public StringTest() { super("Formatting"); }

    @Override
    protected void scenario()
    {
        String s = "Kevin";
        assumeEquals('e', () -> s.charAt(1), "string, character at index 1");
        assumeEquals('v', () -> s.charAt(2), "string, character at index 2");
        assumeEquals(5, () -> s.length(), "string length");
        assumeFalse(() -> s.isEmpty(), "string emptyness");
        assumeFalse(()-> s.isBlank(), "string blankness");
        assumeTrue(() -> s.contains("vin"), "string contains");
        assumeFalse(() -> s.contains("ked"), "string contains");
        assumeTrue(() -> s.contains("Kevin"), "string contains");

        String s2 = "\t  ";
        assumeFalse(() -> s2.isEmpty(), "string emptyness");
        assumeTrue(() -> s2.isBlank(), "string blankness");
    }
}