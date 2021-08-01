package com.NetlabUT;

import java.util.ArrayList;

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
        record(new Profile(m, expected, Status.THROWS_TYPE, correct, message));
    }

    protected <T extends Throwable> void assumeThrows(Executable executable) { assumeThrows(executable, null); }
    protected <T extends Throwable> void assumeThrows(Executable executable, String message)
    {
        Metric<Object> m = Benchmark.run(executable);
        boolean correct = Logical.throwing(m.throwable);
        record(new Profile(m, (Class<T>) Throwable.class, Status.THROWS, correct, message));
    }

    protected void assumeNull(Object actual)                                                          { assumeNull(null, actual); }
    protected void assumeNull(Executable actual)                                                      { assumeNull(null, actual); }
    protected void assumeNotNull(Object actual)                                                       { assumeNotNull(null, actual); }
    protected void assumeNotNull(Executable actual)                                                   { assumeNotNull(null, actual); }
    protected void assumeSame(Object expected, Object actual)                                         { assumeSame(null, expected, actual); }
    protected void assumeSame(Object expected, Executable actual)                                     { assumeSame(null, expected, actual); }
    protected void assumeNotSame(Object expected, Object actual)                                      { assumeNotSame(null, expected, actual); }
    protected void assumeNotSame(Object expected, Executable actual)                                  { assumeNotSame(null, expected, actual); }
    protected void assumeTrue(Boolean actual)                                                         { assumeTrue(null, actual); }
    protected void assumeTrue(Executable actual)                                                      { assumeTrue(null, actual); }
    protected void assumeFalse(Boolean actual)                                                        { assumeFalse(null, actual); }
    protected void assumeFalse(Executable actual)                                                     { assumeFalse(null, actual); }
    protected void assumeEquals(Object expected, Object actual)                                       { assumeEquals(null, expected, actual); }
    protected void assumeEquals(Object expected, Executable actual)                                   { assumeEquals(null, expected, actual); }
    protected void assumeNotEquals(Object expected, Object actual)                                    { assumeNotEquals(null, expected, actual); }
    protected void assumeNotEquals(Object expected, Executable actual)                                { assumeNotEquals(null, expected, actual); }
    protected void assumeArrayEquals(Object[] expected, Object[] actual)                              { assumeArrayEquals(null, expected, actual); }
    protected void assumeArrayEquals(Object[] expected, Executable actual)                            { assumeArrayEquals(null, expected, actual); }
    protected void assumeArrayNotEquals(Object[] expected, Object[] actual)                           { assumeArrayNotEquals(null, expected, actual); }
    protected void assumeArrayNotEquals(Object[] expected, Executable actual)                         { assumeArrayNotEquals(null, expected, actual); }

    protected void assumeNull(String message, Object actual)                                          { record(message, Status.REFERENCE, null, actual); }
    protected void assumeNull(String message, Executable actual)                                      { record(message, Status.REFERENCE, null, actual); }
    protected void assumeNotNull(String message, Object actual)                                       { record(message, Status.NOT_REFERENCE, null, actual); }
    protected void assumeNotNull(String message, Executable actual)                                   { record(message, Status.NOT_REFERENCE, null, actual); }
    protected void assumeSame(String message, Object expected, Object actual)                         { record(message, Status.REFERENCE, expected, actual); }
    protected void assumeSame(String message, Object expected, Executable actual)                     { record(message, Status.REFERENCE, expected, actual); }
    protected void assumeNotSame(String message, Object expected, Object actual)                      { record(message, Status.NOT_REFERENCE, expected, actual); }
    protected void assumeNotSame(String message, Object expected, Executable actual)                  { record(message, Status.NOT_REFERENCE, expected, actual); }
    protected void assumeTrue(String message, Boolean actual)                                         { record(message, Status.EQUAL, true, actual); }
    protected void assumeTrue(String message, Executable actual)                                      { record(message, Status.EQUAL, true, actual); }
    protected void assumeFalse(String message, Boolean actual)                                        { record(message, Status.EQUAL, false, actual); }
    protected void assumeFalse(String message, Executable actual)                                     { record(message, Status.EQUAL, false, actual); }
    protected void assumeEquals(String message, Object expected, Object actual)                       { record(message, Status.EQUAL, expected, actual); }
    protected void assumeEquals(String message, Object expected, Executable actual)                   { record(message, Status.EQUAL, expected, actual); }
    protected void assumeNotEquals(String message, Object expected, Object actual)                    { record(message, Status.NOT_EQUAL, expected, actual); }
    protected void assumeNotEquals(String message, Object expected, Executable actual)                { record(message, Status.NOT_EQUAL, expected, actual); }

    protected void assumeArrayEquals(String message, Object[] expected, Object[] actual)              { record(message, Status.ARRAY_EQUAL, expected, actual); }
    protected void assumeArrayEquals(String message, Object[] expected, Executable actual)            { record(message, Status.ARRAY_EQUAL, expected, actual); }
    protected void assumeArrayNotEquals(String message, Object[] expected, Object[] actual)           { record(message, Status.ARRAY_NOT_EQUAL, expected, actual); }
    protected void assumeArrayNotEquals(String message, Object[] expected, Executable actual)         { record(message, Status.ARRAY_NOT_EQUAL, expected, actual); }

    /** record a profile test */
    protected void record(Profile profile)
    {
        increment(profile.isCorrect());
        mTestProfile.add(profile);
    }
    /** run benchmark and check the assumption using comparator,
     * comparator will take 2 args from {@code references} and return value from {@code actual}
     * @param references argument passed to comparator
     * @param actual contain statement or function to benchmark and as an argument passed to comparator
     * @param message information about the assumption
     * @param comparison type of comparison expected from references and actual
     * @param <T> type of object that evaluated
     */
    private <T> void record(String message, Status comparison, T references, Executable actual)
    {
        Metric<Object> m = actual == null ? new Metric<>() : Benchmark.run(actual);
        boolean correct = m.isThrowing() ? false : compare(references, m.returns, comparison);
        record(new Profile(m, references, comparison, correct, message));
    }

    /** check the assumption using comparator,
     * comparator will take 2 args from {@code references} and return value from {@code actual}
     * @param references argument passed to comparator
     * @param actual argument passed to comparator
     * @param message information about the assumption
     * @param comparison type of comparison expected from references and actual
     * @param <T> type of object that evaluated
     */
    private <T> void record(String message, Status comparison, T references, T actual)
    {
        boolean correct = compare(references, actual, comparison);
        record(new Profile(new Metric<>(), references, comparison, correct, message));
    }

    /** compare equality of the given args
     * @param references argument passed to comparator
     * @param actual argument passed to comparator
     * @param comparison type of comparison expected from references and actual
     * @param <T> type of object that evaluated
     */
    protected static <T> boolean compare(T references, T actual, Status comparison)
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