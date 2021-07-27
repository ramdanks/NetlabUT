package com.NetlabUT;

import java.util.function.Supplier;

public class Benchmark
{
    public static <T> Metric<T> run(Supplier<T> supplier)
    {
        Metric<T> metric = new Metric<T>();
        metric.nanoTime    = System.nanoTime();
        metric.returnValue = supplier.get();
        metric.nanoTime    = System.nanoTime() - metric.nanoTime;
        return metric;
    }
    public static <T extends Throwable> Metric<T> run(Executable exec)
    {
        Metric<T> metric = new Metric<>();
        try {
            metric.nanoTime    = System.nanoTime();
            exec.execute();
        }
        catch (Throwable e) { metric.returnValue = (T) e; }
        finally             { metric.nanoTime    = System.nanoTime() - metric.nanoTime; }
        return metric;
    }
}