package com.NetlabUT;

import java.util.function.Supplier;

public class Benchmark
{
    public static <T> Metric<T> run(Supplier<T> supplier)
    {
        Metric<T> metric = new Metric<T>();
        try {
            metric.nanoTime    = System.nanoTime();
            metric.returnValue = supplier.get();
            metric.nanoTime    = System.nanoTime() - metric.nanoTime;
        } catch (Exception e) {
            System.err.println(e);
        }
        return metric;
    }
}