package com.NetlabUT;

/** format an object to String
 * @author Ramadhan Kalih Sewu
 * @version 1.2
 */
public class StringFormatter
{
    /** returns "null" if the given {@code obj} is null, otherwise
     * forward call to {@link java.lang.Object#toString()} */
    public static String toString(Object obj)
    {
        if (obj == null) return "null";
        return obj.toString();
    }

    /** returns "null" if the given {@code obj} is null, otherwise
     * create a signature with format = ClassName@HashCode */
    public static String idString(Object obj)
    {
        if (obj == null) return "null";
        return obj.getClass().getName() + "@" + Integer.toHexString(obj.hashCode());
    }

    /** returns "null" if the given {@code obj} is null, otherwise
     * create a contigous string that represents individual element in the
     * array using {@link java.lang.Object#toString()} and seperated by ',' */
    public static String arrToString(Object[] array)
    {
        if (array == null) return "null";
        StringBuilder str = new StringBuilder();
        str.append('[');
        if (array.length > 0)
        {
            str.append(array[0].toString());
            for (int i = 1; i < array.length; i++)
                str.append(',').append(array[i].toString());
        }
        str.append(']');
        return str.toString();
    }
}
