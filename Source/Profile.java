package Source;

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
        this.expected = expected.toString();
        this.message = message;
    }
}