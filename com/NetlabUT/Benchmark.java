package com.NetlabUT;

/** class to record runtime, captures return value, and catch a {@link Throwable}
 * caused by {@link com.NetlabUT.Executable}
 * @author Ramadhan Kalih Sewu
 * @version 1.0
 */
public class Benchmark
{
    public static Metric<Object> run(Executable exec)
    {
        Metric<Object> metric = new Metric<>();
        try {
            metric.nanoTime    = System.nanoTime();
            metric.returns     = exec.execute();
        }
        catch (Throwable t) { metric.throwable = t; }
        finally             { metric.nanoTime  = System.nanoTime() - metric.nanoTime; }
        return metric;
    }
}