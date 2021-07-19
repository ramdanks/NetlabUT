package Source;

import java.util.function.Function;
import java.util.function.Supplier;

public class Benchmark
{
    protected static <T> Metric<T> run(Supplier<T> supplier)
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
    protected static <T, P> Metric<T> run(Function<P, T> fn, P param)
    {
        return run(() -> fn.apply(param));
    }
}