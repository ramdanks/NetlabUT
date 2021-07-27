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

    protected <T extends Throwable> void assumeThrows(Executable executable) { assumeThrows(executable, null); }
    protected <T extends Throwable> void assumeThrows(Executable executable, String message)
    {
        Metric<T> m = Benchmark.run(executable);
        Profile<T> p = new Profile<T>(m, Throwable.class.getName(), message, Logical.throwing(m.throwable));
        increment(p.isCorrect());
        mTestProfile.add(p);
    }
    protected <T extends Throwable> void assumeThrows(Class<T> expectedType, Executable executable) { assumeThrows(expectedType, executable, null); }
    protected <T extends Throwable> void assumeThrows(Class<T> expectedType, Executable executable, String message)
    {
        Metric<T> m = Benchmark.run(executable);
        Profile<T> p = new Profile<T>(m, Throwable.class.getName(), message, Logical.throwing(expectedType, m.throwable));
        increment(p.isCorrect());
        mTestProfile.add(p);
    }

    protected void assumeNull(Object actual)                                                   { assumeNull(actual, null); }
    protected void assumeNull(Supplier<Object> actual)                                         { assumeNull(actual, null); }
    protected void assumeNotNull(Object actual)                                                { assumeNotNull(actual, null); }
    protected void assumeNotNull(Supplier<Object> actual)                                      { assumeNotNull(actual, null); }
    protected void assumeSame(Object expected, Object actual)                                  { assumeSame(expected, actual, null); }
    protected void assumeSame(Object expected, Supplier<Object> actual)                        { assumeSame(expected, actual, null); }
    protected void assumeNotSame(Object expected, Object actual)                               { assumeNotSame(expected, actual, null); }
    protected void assumeNotSame(Object expected, Supplier<Object> actual)                     { assumeNotSame(expected, actual, null); }
    protected void assumeTrue(Boolean actual)                                                  { assumeTrue(actual, null); }
    protected void assumeTrue(Supplier<Boolean> actual)                                        { assumeTrue(actual, null); }
    protected void assumeFalse(Boolean actual)                                                 { assumeFalse(actual, null); }
    protected void assumeFalse(Supplier<Boolean> actual)                                       { assumeFalse(actual, null); }
    protected void assumeEquals(Object expected, Object actual)                                { assumeEquals(expected, actual, null); }
    protected void assumeEquals(Object expected, Supplier<Object> actual)                      { assumeEquals(expected, actual, null); }
    protected void assumeNotEquals(Object expected, Object actual)                             { assumeNotEquals(expected, actual, null); }
    protected void assumeNotEquals(Object expected, Supplier<Object> actual)                   { assumeNotEquals(expected, actual, null); }
    protected void assumeArrayEquals(Object[] expected, Object[] actual)                       { assumeArrayEquals(expected, actual, null); }
    protected void assumeArrayEquals(Object[] expected, Supplier<Object[]> actual)             { assumeArrayEquals(expected, actual, null); }
    protected void assumeArrayNotEquals(Object[] expected, Object[] actual)                    { assumeArrayNotEquals(expected, actual, null); }
    protected void assumeArrayNotEquals(Object[] expected, Supplier<Object[]> actual)          { assumeArrayNotEquals(expected, actual, null); }

    protected void assumeNull(Object actual, String message)                                   { record(null, () -> actual, message, Logical::same); }
    protected void assumeNull(Supplier<Object> actual, String message)                         { record(null, actual, message, Logical::same); }
    protected void assumeNotNull(Object actual, String message)                                { recordNot(null, () -> actual, message, Logical::notSame); }
    protected void assumeNotNull(Supplier<Object> actual, String message)                      { recordNot(null, actual, message, Logical::notSame); }
    protected void assumeSame(Object expected, Object actual, String message)                  { record(expected, () -> actual, message, Logical::same); }
    protected void assumeSame(Object expected, Supplier<Object> actual, String message)        { record(expected, actual, message, Logical::same); }
    protected void assumeNotSame(Object expected, Object actual, String message)               { recordNot(expected, () -> actual, message, Logical::notSame); }
    protected void assumeNotSame(Object expected, Supplier<Object> actual, String message)     { recordNot(expected, actual, message, Logical::notSame); }
    protected void assumeTrue(Boolean actual, String message)                                  { record(true, () -> actual, message, Logical::equals); }
    protected void assumeTrue(Supplier<Boolean> actual, String message)                        { record(true, actual, message, Logical::equals); }
    protected void assumeFalse(Boolean actual, String message)                                 { record(false, () -> actual, message, Logical::equals); }
    protected void assumeFalse(Supplier<Boolean> actual, String message)                       { record(false, actual, message, Logical::equals); }
    protected void assumeEquals(Object expected, Object actual, String message)                { record(expected, () -> actual, message, Logical::equals); }
    protected void assumeEquals(Object expected, Supplier<Object> actual, String message)      { record(expected, actual, message, Logical::equals); }
    protected void assumeNotEquals(Object expected, Object actual, String message)             { recordNot(expected, () -> actual, message, Logical::notEquals); }
    protected void assumeNotEquals(Object expected, Supplier<Object> actual, String message)   { recordNot(expected, actual, message, Logical::notEquals); }

    protected void assumeArrayEquals(Object[] expected, Object[] actual, String message)
    { record(expected, () -> actual, message, Logical::isArrayEquals); }
    protected void assumeArrayEquals(Object[] expected, Supplier<Object[]> actual, String message)
    { record(expected, actual, message, Logical::isArrayEquals); }
    protected void assumeArrayNotEquals(Object[] expected, Object[] actual, String message)
    { recordNot(expected, () -> actual, message, Logical::isArrayNotEquals); }
    protected void assumeArrayNotEquals(Object[] expected, Supplier<Object[]> actual, String message)
    { recordNot(expected, actual, message, Logical::isArrayNotEquals); }

    /** run benchmark and check the assumption using comparator,
     * comparator will take 2 args from {@code expected} and return value from {@code actual}
     * @param expected as an argument passed to comparator
     * @param actual contain statement or function to benchmark and as an argument passed to comparator
     * @param message information about the assumption
     * @param comparator for comparing and state the correctness of assumption
     * @param <T> type of object that evaluated
     */
    private <T> void record(T expected, Supplier<T> actual, String message, BiFunction<T, T, Boolean> comparator)
    {
        Metric<T> m = actual == null ? new Metric<>() : Benchmark.run(actual);
        Profile<T> p = new Profile<T>(m, expected, message, comparator.apply(m.returns, expected));
        increment(p.isCorrect());
        mTestProfile.add(p);
    }
    /** run benchmark and check the assumption using comparator, add "not" in front of expected,
     * comparator will take 2 args from {@code expected} and return value from {@code actual}
     * @param expected as an argument passed to comparator
     * @param actual contain statement or function to benchmark and as an argument passed to comparator
     * @param message information about the assumption
     * @param comparator for comparing and state the correctness of assumption
     * @param <T> type of object that evaluated
     */
    private <T> void recordNot(T expected, Supplier<T> actual, String message, BiFunction<T, T, Boolean> comparator)
    {
        Metric<T> m = actual == null ? new Metric<>() : Benchmark.run(actual);
        Profile<T> p = new Profile<T>(m, "not " + Profile.toString(expected), message, comparator.apply(m.returns, expected));
        increment(p.isCorrect());
        mTestProfile.add(p);
    }

    private void increment(boolean correct)
    {
        if (correct) mSuccessCount++;
        mTestCount++;
    }

}