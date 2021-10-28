package com.NetlabUT;

/** class to measure a metric of a method or {@link com.NetlabUT.Executable}
 * @author Ramadhan Kalih Sewu
 * @version 1.3
 */
public class Metric
{
	public Status status;
	public Object reference  = null;
    public Object actual     = null;
    public long nanoTime     = 0;
    public boolean correct   = false;
    public boolean throwing  = false;
}