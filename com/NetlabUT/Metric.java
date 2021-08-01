package com.NetlabUT;

/** class to measure a metric of a method or {@link com.NetlabUT.Executable}
 * @author Ramadhan Kalih Sewu
 * @version 1.0
 * @param <T> return value of a method that is measured
 */
public class Metric<T>
{
    public T returns = null;
    public Throwable throwable = null;
    public long nanoTime = 0;

    public boolean isOk() { return throwable == null; }
    public boolean isThrowing() { return throwable != null; }
    @Override
    public String toString()
    {
        return returns == null ?
            String.format("Throws: %s, Time: %d(ns)", throwable, nanoTime) :
            String.format("Returns: %s, Time: %d(ns)", returns, nanoTime);
    }
}