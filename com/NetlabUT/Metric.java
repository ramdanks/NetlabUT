package com.NetlabUT;

public class Metric<T>
{
    public T returnValue;
    public long nanoTime = 0;
    public long bytesUsed = 0;

    @Override
    public String toString()
    {
        return String.format("Time: %d(ns), Memory: %d(bytes)", nanoTime, bytesUsed);
    }
}