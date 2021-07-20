package Sandbox;

import Source.NetlabUT;

public class Formatting extends NetlabUT
{
    public Formatting() { super("Formatting"); }

    @Override
    protected void scenario()
    {
        String s = "Kevin";
        assumeEquals(
            'e',
            () -> s.charAt(1),
            "character at index 1 from string type"
        );
        assumeEquals(
            'v',
            () -> s.charAt(2),
            "character at index 2 from string type"
        );
        assumeEquals(
                5,
                () -> s.length(),
                "string length"
        );
    }
}
