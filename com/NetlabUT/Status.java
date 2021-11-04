package com.NetlabUT;

/** used as parameters to tell how the result of comparison should happen
 * @author Ramadhan Kalih Sewu
 * @version 1.0
 */
@Deprecated
public class Status
{
    public static final int EQUAL = 0;
    public static final int NOT_EQUAL = 1;
    public static final int REFERENCE = 2;
    public static final int NOT_REFERENCE = 3;
    public static final int THROWS = 4;
    public static final int THROWS_TYPE = 5;
    public static final int ARRAY_EQUAL = 6;
    public static final int ARRAY_NOT_EQUAL = 7;

    public static String toString(int status)
    {
        switch (status)
        {
            case EQUAL           : return "Equal";
            case NOT_EQUAL       : return "Not Equal";
            case REFERENCE       : return "Reference";
            case NOT_REFERENCE   : return "Not Reference";
            case THROWS          : return "Throws";
            case THROWS_TYPE     : return "Throws Type";
            case ARRAY_EQUAL     : return "Array Equal";
            case ARRAY_NOT_EQUAL : return "Array Not Equal";
        }
        return "<undefined>";
    }
}
