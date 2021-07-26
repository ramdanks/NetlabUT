package com.NetlabUT;

import java.util.ArrayList;
import java.util.function.Supplier;

public abstract class NetlabUT extends UnitTest
{
    private String mTestName = null;
    private ArrayList<Profile> mTestProfile = new ArrayList<>();
    
    protected NetlabUT() { mTestName = this.getClass().getName(); }
    protected NetlabUT(String testName) { mTestName = testName; }

    public String getTestName() { return mTestName; }
    public ArrayList<Profile> getTestProfile() { return mTestProfile; }

    public void run()
    {
        mTestProfile.clear();
        super.run();
    }
     
    protected void assumeNull(Supplier<Object> actual)                                { assumeNull(actual, null); }
    protected void assumeNotNull(Supplier<Object> actual)                             { assumeNotNull(actual, null); }
    protected void assumeSame(Object expected, Supplier<Object> actual)               { assumeSame(expected, actual, null); }
    protected void assumeNotSame(Object expected, Supplier<Object> actual)            { assumeNotSame(expected, actual, null); }
    protected void assumeTrue(Supplier<Boolean> actual)                               { assumeTrue(actual, null); }
    protected void assumeFalse(Supplier<Boolean> actual)                              { assumeFalse(actual, null); }
    protected void assumeEquals(Object expected, Supplier<Object> actual)             { assumeEquals(expected, actual, null); }
    protected void assumeNotEquals(Object expected, Supplier<Object> actual)          { assumeNotEquals(expected, actual, null); }
    protected void assumeArrayEquals(Object[] expected, Supplier<Object[]> actual)    { assumeArrayEquals(expected, actual, null); }
    protected void assumeArrayNotEquals(Object[] expected, Supplier<Object[]> actual) { assumeArrayNotEquals(expected, actual, null); }

    protected void assumeNull(Supplier<Object> actual, String message) {
        Metric<Object> m = Benchmark.run(actual);
        Profile<Object> p = new Profile<Object>(m, "null", message, recordAssumption(m.returnValue == null));
        mTestProfile.add(p);
    }
    protected void assumeNotNull(Supplier<Object> actual, String message) {
        Metric<Object> m = Benchmark.run(actual);
        Profile<Object> p = new Profile<Object>(m, "not null", message, recordAssumption(m.returnValue != null));
        mTestProfile.add(p);
    }
    protected void assumeSame(Object expected, Supplier<Object> actual, String message) {
        Metric<Object> m = Benchmark.run(actual);
        Profile<Object> p = new Profile<Object>(m, expected, message, recordAssumption(m.returnValue == actual));
        mTestProfile.add(p);
    }
    protected void assumeNotSame(Object expected, Supplier<Object> actual, String message) {
        Metric<Object> m = Benchmark.run(actual);
        Profile<Object> p = new Profile<Object>(m, expected, message, recordAssumption(m.returnValue != actual));
        mTestProfile.add(p);
    }
    protected void assumeTrue(Supplier<Boolean> actual, String message) {
        Metric<Boolean> m = Benchmark.run(actual);
        Profile<Boolean> p = new Profile<Boolean>(m, true, message, recordAssumption(m.returnValue == true));
        mTestProfile.add(p);
    }
    protected void assumeFalse(Supplier<Boolean> actual, String message) {
        Metric<Boolean> m = Benchmark.run(actual);
        Profile<Boolean> p = new Profile<Boolean>(m, false, message, recordAssumption(m.returnValue == false));
        mTestProfile.add(p);
    }
    protected void assumeEquals(Object expected, Supplier<Object> actual, String message) {
        Metric<Object> m = Benchmark.run(actual);
        Profile<Object> p = new Profile<Object>(m, expected, message, recordAssumption(m.returnValue.equals(expected) == true));
        mTestProfile.add(p);
    }
    protected void assumeNotEquals(Object expected, Supplier<Object> actual, String message) {
        Metric<Object> m = Benchmark.run(actual);
        Profile<Object> p = new Profile<Object>(m, "not " + expected, message, recordAssumption(m.returnValue.equals(expected) == false));
        mTestProfile.add(p);
    }
    protected void assumeArrayEquals(Object[] expected, Supplier<Object[]> actual, String message) {
        Metric<Object[]> m = Benchmark.run(actual);
        Profile<Object[]> p = new Profile<Object[]>(m, expected, message, assumeArrayEquals(expected, m.returnValue));
        mTestProfile.add(p);
    }
    protected void assumeArrayNotEquals(Object[] expected, Supplier<Object[]> actual, String message) {
        Metric<Object[]> m = Benchmark.run(actual);
        Profile<Object[]> p = new Profile<Object[]>(m, "not " + Profile.toString(expected), message, assumeArrayNotEquals(expected, m.returnValue));
        mTestProfile.add(p);
    }

}