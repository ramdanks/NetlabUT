package com.NetlabUT;

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