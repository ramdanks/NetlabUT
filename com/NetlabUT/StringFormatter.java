package com.NetlabUT;

public class StringFormatter
{
    public static String toString(Object obj)
    {
        if (obj == null) return "null";
        return obj.toString();
    }

    public static String idString(Object obj)
    {
        if (obj == null) return "null";
        return obj.getClass().getName() + "@" + Integer.toHexString(obj.hashCode());
    }

    public static String arrToString(Object[] array)
    {
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
