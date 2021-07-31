package com.NetlabUT;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class UnitTest
{
    private int mTestCount;
    private int mSuccessCount;

    private String mTestName = null;
    private ArrayList<Profile> mTestProfile = new ArrayList<>();
    
    protected UnitTest() { mTestName = this.getClass().getName(); }
    protected UnitTest(String testName) { mTestName = testName; }

    public String getTestName() { return mTestName; }
    public ArrayList<Profile> getTestProfile() { return mTestProfile; }
    public int getTestCount() { return mTestCount; }
    public int getSuccessCount() { return mSuccessCount; }

    /** entry point for calculating unit test, call assume function to record testing */
    protected abstract void scenario();
    /** run unit test, it will call {@link #scenario()} as defined by the unit test class */
    public void run()
    {
        mTestProfile.clear();
        mTestCount = 0;
        mSuccessCount = 0;
        scenario();
    }

    protected <T extends Throwable> void assumeThrows(Class<T> expected, Executable executable) { assumeThrows(expected, executable, null); }
    protected <T extends Throwable> void assumeThrows(Class<T> expected, Executable executable, String message)
    {
        Metric<Object> m = Benchmark.run(executable);
        boolean correct = Logical.throwing(expected, m.throwable);
        Profile p = new Profile(m, expected, Status.THROWS_TYPE, correct, message);
        increment(p.isCorrect());
        mTestProfile.add(p);
    }

    protected <T extends Throwable> void assumeThrows(Executable executable) { assumeThrows(executable, null); }
    protected <T extends Throwable> void assumeThrows(Executable executable, String message)
    {
        Metric<Object> m = Benchmark.run(executable);
        boolean correct = Logical.throwing(m.throwable);
        Profile p = new Profile(m, (Class<T>) Throwable.class, Status.THROWS, correct, message);
        increment(p.isCorrect());
        mTestProfile.add(p);
    }

    protected void assumeNull(Object actual)                                                          { assumeNull(actual, null); }
    protected void assumeNull(Executable actual)                                                      { assumeNull(actual, null); }
    protected void assumeNotNull(Object actual)                                                       { assumeNotNull(actual, null); }
    protected void assumeNotNull(Executable actual)                                                   { assumeNotNull(actual, null); }
    protected void assumeSame(Object expected, Object actual)                                         { assumeSame(expected, actual, null); }
    protected void assumeSame(Object expected, Executable actual)                                     { assumeSame(expected, actual, null); }
    protected void assumeNotSame(Object expected, Object actual)                                      { assumeNotSame(expected, actual, null); }
    protected void assumeNotSame(Object expected, Executable actual)                                  { assumeNotSame(expected, actual, null); }
    protected void assumeTrue(Boolean actual)                                                         { assumeTrue(actual, null); }
    protected void assumeTrue(Executable actual)                                                      { assumeTrue(actual, null); }
    protected void assumeFalse(Boolean actual)                                                        { assumeFalse(actual, null); }
    protected void assumeFalse(Executable actual)                                                     { assumeFalse(actual, null); }
    protected void assumeEquals(Object expected, Object actual)                                       { assumeEquals(expected, actual, null); }
    protected void assumeEquals(Object expected, Executable actual)                                   { assumeEquals(expected, actual, null); }
    protected void assumeNotEquals(Object expected, Object actual)                                    { assumeNotEquals(expected, actual, null); }
    protected void assumeNotEquals(Object expected, Executable actual)                                { assumeNotEquals(expected, actual, null); }
    protected void assumeArrayEquals(Object[] expected, Object[] actual)                              { assumeArrayEquals(expected, actual, null); }
    protected void assumeArrayEquals(Object[] expected, Executable actual)                            { assumeArrayEquals(expected, actual, null); }
    protected void assumeArrayNotEquals(Object[] expected, Object[] actual)                           { assumeArrayNotEquals(expected, actual, null); }
    protected void assumeArrayNotEquals(Object[] expected, Executable actual)                         { assumeArrayNotEquals(expected, actual, null); }

    protected void assumeNull(Object actual, String message)                                          { record(null, () -> actual, message, Status.REFERENCE); }
    protected void assumeNull(Executable actual, String message)                                      { record(null, actual, message, Status.REFERENCE); }
    protected void assumeNotNull(Object actual, String message)                                       { record(null, () -> actual, message, Status.NOT_REFERENCE); }
    protected void assumeNotNull(Executable actual, String message)                                   { record(null, actual, message, Status.NOT_REFERENCE); }
    protected void assumeSame(Object expected, Object actual, String message)                         { record(expected, () -> actual, message, Status.REFERENCE); }
    protected void assumeSame(Object expected, Executable actual, String message)                     { record(expected, actual, message, Status.REFERENCE); }
    protected void assumeNotSame(Object expected, Object actual, String message)                      { record(expected, () -> actual, message, Status.NOT_REFERENCE); }
    protected void assumeNotSame(Object expected, Executable actual, String message)                  { record(expected, actual, message, Status.NOT_REFERENCE); }
    protected void assumeTrue(Boolean actual, String message)                                         { record(true, () -> actual, message, Status.EQUAL); }
    protected void assumeTrue(Executable actual, String message)                                      { record(true, actual, message, Status.EQUAL); }
    protected void assumeFalse(Boolean actual, String message)                                        { record(false, () -> actual, message, Status.EQUAL); }
    protected void assumeFalse(Executable actual, String message)                                     { record(false, actual, message, Status.EQUAL); }
    protected void assumeEquals(Object expected, Object actual, String message)                       { record(expected, () -> actual, message, Status.EQUAL); }
    protected void assumeEquals(Object expected, Executable actual, String message)                   { record(expected, actual, message, Status.EQUAL); }
    protected void assumeNotEquals(Object expected, Object actual, String message)                    { record(expected, () -> actual, message, Status.NOT_EQUAL); }
    protected void assumeNotEquals(Object expected, Executable actual, String message)                { record(expected, actual, message, Status.NOT_EQUAL); }

    protected void assumeArrayEquals(Object[] expected, Object[] actual, String message)              { record(expected, () -> actual, message, Status.ARRAY_EQUAL); }
    protected void assumeArrayEquals(Object[] expected, Executable actual, String message)            { record(expected, actual, message, Status.ARRAY_EQUAL); }
    protected void assumeArrayNotEquals(Object[] expected, Object[] actual, String message)           { record(expected, () -> actual, message, Status.ARRAY_NOT_EQUAL); }
    protected void assumeArrayNotEquals(Object[] expected, Executable actual, String message)         { record(expected, actual, message, Status.ARRAY_NOT_EQUAL); }

    /** run benchmark and check the assumption using comparator,
     * comparator will take 2 args from {@code references} and return value from {@code actual}
     * @param references argument passed to comparator
     * @param actual contain statement or function to benchmark and as an argument passed to comparator
     * @param message information about the assumption
     * @param comparison type of comparison expected from references and actual
     * @param <T> type of object that evaluated
     */
    private <T> void record(T references, Executable actual, String message, Status comparison)
    {
        Metric<Object> m = actual == null ? new Metric<>() : Benchmark.run(actual);
        boolean correct = compare(references, m.returns, comparison);
        Profile p = new Profile(m, references, comparison, correct, message);
        increment(p.isCorrect());
        mTestProfile.add(p);
    }
    /** check the assumption using comparator,
     * comparator will take 2 args from {@code references} and return value from {@code actual}
     * @param references argument passed to comparator
     * @param actual argument passed to comparator
     * @param message information about the assumption
     * @param comparison type of comparison expected from references and actual
     * @param <T> type of object that evaluated
     */
    private <T> void record(T references, T actual, String message, Status comparison)
    {
        boolean correct = compare(references, actual, comparison);
        Profile p = new Profile(new Metric<>(), references, comparison, correct, message);
        increment(p.isCorrect());
        mTestProfile.add(p);
    }

    /** compare equality of the given args
     * @param references argument passed to comparator
     * @param actual argument passed to comparator
     * @param comparison type of comparison expected from references and actual
     * @param <T> type of object that evaluated
     */
    private <T> boolean compare(T references, T actual, Status comparison)
    {
        boolean correct = false;
        switch (comparison)
        {
            case EQUAL           : correct = Logical.equals(actual, references); break;
            case NOT_EQUAL       : correct = Logical.notEquals(actual, references); break;
            case ARRAY_EQUAL     : correct = Logical.isArrayEquals((T[]) references, (T[]) actual); break;
            case ARRAY_NOT_EQUAL : correct = Logical.isArrayNotEquals((T[]) references, (T[]) actual); break;
            case REFERENCE       : correct = Logical.same(actual, references); break;
            case NOT_REFERENCE   : correct = Logical.notSame(actual, references);
        };
        return correct;
    }

    /** increment test count and success count */
    private void increment(boolean correct)
    {
        if (correct) mSuccessCount++;
        mTestCount++;
    }

}