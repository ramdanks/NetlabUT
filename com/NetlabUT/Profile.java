package com.NetlabUT;

public class Profile<T>
{
    public Metric<T> metric;
    public String expected;
    public String message;

    public Profile(Metric<T> metric, String expected, String message)
    {
        this.metric = metric;
        this.expected = expected;
        this.message = message;
    }
    public Profile(Metric<T> metric, T expected, String message)
    {
        this.metric = metric;
        this.expected = expected.getClass().isArray() ? toString((T[]) expected) : expected.toString();
        this.message = message;
    }
    public static <T> String toString(T[] array)
    {
        String str = "";
        if (array.length > 0)
        {
            str += array[0].toString();
            for (int i = 1; i < array.length; i++)
                str += ',' + array[i].toString();
        }
        return str;
    }
    public String getMetricReturnToString()
    {
        if (metric.returnValue.getClass().isArray())
            return toString((T[]) metric.returnValue);
        return metric.returnValue.toString();
    }

}