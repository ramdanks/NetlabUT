package com.NetlabUT;

public class Profile<T>
{
    private Metric<T> metric;
    private String expected;
    private String message;
    private boolean correct;

    public Profile(Metric<T> metric, String expected, String message, boolean correct)
    {
        this.metric = metric;
        this.expected = expected;
        this.message = message;
        this.correct = correct;
    }
    public Profile(Metric<T> metric, T expected, String message, boolean correct)
    {
        this.metric = metric;
        this.expected = expected.getClass().isArray() ? toString((T[]) expected) : expected.toString();
        this.message = message;
        this.correct = correct;
    }

    public Metric<T> getMetric() { return metric; }
    public String getExpected() { return expected; }
    public String getMessage() { return message; }
    public boolean isCorrect() { return correct; }
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