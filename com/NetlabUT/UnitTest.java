package com.NetlabUT;

import java.util.ArrayList;

/** provides a test through comparing 2 object using {@code assumeXXX} method. it's a non-blocking test
 * that will record the profile of the assumption (reference, actual, message, correctness, etc.)
 * @author Ramadhan Kalih Sewu
 * @version 1.1
 */
public abstract class UnitTest
{
    private int mTestCount;
    private int mSuccessCount;

    private ProfileReport mReportListener;
    private ProfileFinalize mFinalizeListener;

    private String mTestName = null;
    private ArrayList<Profile<Object>> mTestProfile = new ArrayList<>();

    /** assign a test name using the name of the inherited class */
    protected UnitTest() { mTestName = this.getClass().getName(); }
    /** assign a specific / custom test name */
    protected UnitTest(String testName) { mTestName = testName; }

    /** get the name of the test */
    public String getTestName() { return mTestName; }
    /** get the result of the test that was recorded using {@link UnitTest#record(Profile)} */
    public ArrayList<Profile<Object>> getTestProfile() { return mTestProfile; }
    /** get how much test has been done and recorded */
    public int getTestCount() { return mTestCount; }
    /** get how much test that is considered to be correct (success) */
    public int getSuccessCount() { return mSuccessCount; }
    /** add a listener which will be called after the test has been recorded */
    public void addProfileReportListener(ProfileReport listener) { mReportListener = listener; }
    /** add a listener which will be called before recording the result of the test.
     * Listener will get an information about the test result passed to the function param of {@link ProfileFinalize#predicate(Profile)}.
     * The correctness of the test will be determined by the return value of that function as well */
    protected void addProfileFinalizeListener(ProfileFinalize listener) { mFinalizeListener = listener; }

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

    /** expect executable to throws {@code expected} that extends from {@link java.lang.Throwable} */
    protected <T extends Throwable> void assumeThrows(Class<T> expected, Executable executable)
    { assumeThrows(expected, executable, null); }
    /** expect executable to throws {@code expected} that extends from {@link java.lang.Throwable} */
    protected <T extends Throwable> void assumeThrows(Class<T> expected, Executable executable, String message)
    {
        Metric<Object> m = Benchmark.run(executable);
        boolean correct = Logical.throwing(expected, m.throwable);
        record(new Profile<Object>(m, expected, Status.THROWS_TYPE, correct, message));
    }
    /** expect executable to throws any instance of {@link java.lang.Throwable} */
    protected <T extends Throwable> void assumeThrows(Executable executable)
    { assumeThrows(executable, null); }
    /** expect executable to throws any instance of {@link java.lang.Throwable} */
    protected <T extends Throwable> void assumeThrows(Executable executable, String message)
    {
        Metric<Object> m = Benchmark.run(executable);
        boolean correct = Logical.throwing(m.throwable);
        record(new Profile<Object>(m, Throwable.class, Status.THROWS, correct, message));
    }

    /** expect {@code actual} to be {@code null} */
    protected void assumeNull(Object actual)                                                          
    { assumeNull(null, actual); }

    /** expect {@code actual} to returns {@code null} */
    protected void assumeNull(Executable actual)                                                      
    { assumeNull(null, actual); }

    /** expect {@code actual} to be {@code null} */
    protected void assumeNull(String message, Object actual)
    { record(message, Status.REFERENCE, null, actual); }
    
    /** expect {@code actual} to returns {@code null} */
    protected void assumeNull(String message, Executable actual)
    { record(message, Status.REFERENCE, null, actual); }

    /** expect {@code actual} to be not {@code null} */
    protected void assumeNotNull(Object actual)                                                       
    { assumeNotNull(null, actual); }

    /** expect {@code actual} to returns not {@code null} */
    protected void assumeNotNull(Executable actual)                                                   
    { assumeNotNull(null, actual); }

    /** expect {@code actual} to be not {@code null} */
    protected void assumeNotNull(String message, Object actual)
    { record(message, Status.NOT_REFERENCE, null, actual); }

    /** expect {@code actual} to returns not {@code null} */
    protected void assumeNotNull(String message, Executable actual)
    { record(message, Status.NOT_REFERENCE, null, actual); }

    /** expect {@code actual} and {@code expected} referencing the same object */
    protected void assumeSame(Object expected, Object actual)                                        
     { assumeSame(null, expected, actual); }

    /** expect {@code actual} to returns a reference to {@code expected} */
    protected void assumeSame(Object expected, Executable actual)                                     
    { assumeSame(null, expected, actual); }

    /** expect {@code actual} and {@code expected} referencing the same object */
    protected void assumeSame(String message, Object expected, Object actual)
    { record(message, Status.REFERENCE, expected, actual); }

    /** expect {@code actual} to returns a reference to {@code expected} */
    protected void assumeSame(String message, Object expected, Executable actual)
    { record(message, Status.REFERENCE, expected, actual); }

    /** expect {@code actual} and {@code expected} are <strong>not</strong> referencing the same object */
    protected void assumeNotSame(Object expected, Object actual)                                      
    { assumeNotSame(null, expected, actual); }

    /** expect {@code actual} to returns an object which <strong>not</strong> referencing to {@code expected} */
    protected void assumeNotSame(Object expected, Executable actual)                                  
    { assumeNotSame(null, expected, actual); }

    /** expect {@code actual} and {@code expected} are <strong>not</strong> referencing the same object */
    protected void assumeNotSame(String message, Object expected, Object actual)
    { record(message, Status.NOT_REFERENCE, expected, actual); }

    /** expect {@code actual} to returns an object which <strong>not</strong> referencing to {@code expected} */
    protected void assumeNotSame(String message, Object expected, Executable actual)
    { record(message, Status.NOT_REFERENCE, expected, actual); }

    /** expect {@code actual} to be {@code true} */
    protected void assumeTrue(Boolean actual)                                                         
    { assumeTrue(null, actual); }

    /** expect {@code actual} to returns {@code true} */
    protected void assumeTrue(Executable actual)                                                      
    { assumeTrue(null, actual); }

    /** expect {@code actual} to be {@code true} */
    protected void assumeTrue(String message, Boolean actual)
    { record(message, Status.EQUAL, true, actual); }

    /** expect {@code actual} to returns {@code true} */
    protected void assumeTrue(String message, Executable actual)
    { record(message, Status.EQUAL, true, actual); }

    /** expect {@code actual} to be {@code false} */
    protected void assumeFalse(Boolean actual)                                                        
    { assumeFalse(null, actual); }

    /** expect {@code actual} to returns {@code false} */
    protected void assumeFalse(Executable actual)                                                     
    { assumeFalse(null, actual); }

    /** expect {@code actual} to be {@code false} */
    protected void assumeFalse(String message, Boolean actual)
    { record(message, Status.EQUAL, false, actual); }

    /** expect {@code actual} to returns {@code false} */
    protected void assumeFalse(String message, Executable actual)
    { record(message, Status.EQUAL, false, actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code true} when comparing 
     * {@code expected} with {@code actual} */
    protected void assumeEquals(Object expected, Object actual)                                       
    { assumeEquals(null, expected, actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code true} when comparing
     * {@code expected} with return value from {@code actual} */
    protected void assumeEquals(Object expected, Executable actual)                                   
    { assumeEquals(null, expected, actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code true} when comparing 
     * {@code expected} with {@code actual} */
    protected void assumeEquals(String message, Object expected, Object actual)
    { record(message, Status.EQUAL, expected, actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code true} when comparing
     * {@code expected} with return value from {@code actual} */
    protected void assumeEquals(String message, Object expected, Executable actual)
    { record(message, Status.EQUAL, expected, actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code false} when comparing 
     * {@code expected} with {@code actual} */
    protected void assumeNotEquals(Object expected, Object actual)                                    
    { assumeNotEquals(null, expected, actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code false} when comparing 
     * {@code expected} with return value from {@code actual} */
    protected void assumeNotEquals(Object expected, Executable actual)                                
    { assumeNotEquals(null, expected, actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code false} when comparing 
     * {@code expected} with {@code actual} */
    protected void assumeNotEquals(String message, Object expected, Object actual)
    { record(message, Status.NOT_EQUAL, expected, actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code false} when comparing 
     * {@code expected} with return value from {@code actual} */
    protected void assumeNotEquals(String message, Object expected, Executable actual)
    { record(message, Status.NOT_EQUAL, expected, actual); }

    /** expect {@code expected} and {@code actual} have the same length and iteratively equals which 
     * determined by {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayEquals(Object[] expected, Object[] actual)                              
    { assumeArrayEquals(null, expected, actual); }

    /** expect {@code actual} to returns an array of {@link java.lang.Object} and when compared to 
     * {@code expected} will have the same length and iteratively equals which determined by 
     * {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayEquals(Object[] expected, Executable actual)                            
    { assumeArrayEquals(null, expected, actual); }

    /** expect {@code expected} and {@code actual} have the same length and iteratively equals which 
     * determined by {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayEquals(String message, Object[] expected, Object[] actual)
    { record(message, Status.ARRAY_EQUAL, expected, actual); }

    /** expect {@code actual} to returns an array of {@link java.lang.Object} and when compared to 
     * {@code expected} will have the same length and iteratively equals which determined by 
     * {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayEquals(String message, Object[] expected, Executable actual)
    { record(message, Status.ARRAY_EQUAL, expected, actual); }

    /** expect {@code expected} and {@code actual} either have a different length or iteratively not 
     * equals which determined by {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayNotEquals(Object[] expected, Object[] actual)                           
    { assumeArrayNotEquals(null, expected, actual); }

    /** expect {@code actual} to returns an array of {@link java.lang.Object} and when compared to 
     * {@code expected} will have either a different length or iteratively not equals which determined by 
     * {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayNotEquals(Object[] expected, Executable actual)                         
    { assumeArrayNotEquals(null, expected, actual); }

    /** expect {@code expected} and {@code actual} either have a different length or iteratively not 
     * equals which determined by {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayNotEquals(String message, Object[] expected, Object[] actual)
    { record(message, Status.ARRAY_NOT_EQUAL, expected, actual); }

    /** expect {@code actual} to returns an array of {@link java.lang.Object} and when compared to 
     * {@code expected} will have either a different length or iteratively not equals which determined by 
     * {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayNotEquals(String message, Object[] expected, Executable actual)
    { record(message, Status.ARRAY_NOT_EQUAL, expected, actual); }

    /** record a profile test */
    protected void record(Profile profile)
    {
        boolean correct = profile.isCorrect();
        // ask finalize listener to correct the output of unit test
        if (mFinalizeListener != null)
            correct = mFinalizeListener.predicate(profile);
        // record unit test profile
        increment(correct);
        mTestProfile.add(profile);
        // send report to listener after profile is recorded
        if (mReportListener != null)
            mReportListener.report(profile);
    }

    /** run benchmark and check the assumption using comparator,
     * comparator will take 2 args from {@code references} and return value from {@code actual}
     * @param references argument passed to comparator
     * @param actual contain statement or function to benchmark and as an argument passed to comparator
     * @param message information about the assumption
     * @param comparison type of comparison expected from references and actual
     * @param <T> type of object that evaluated
     */
    private <T> void record(String message, int comparison, T references, Executable actual)
    {
        Metric<Object> m = actual == null ? new Metric<Object>() : Benchmark.run(actual);
        if (!m.isThrowing() && comparison == Status.ARRAY_EQUAL || comparison == Status.ARRAY_NOT_EQUAL)
            if (arrayTypeMismatch(references, m.returns))
                m.throwable = new MismatchTypeException(references, m.returns);
        boolean correct = !m.isThrowing() && compare(references, m.returns, comparison);
        record(new Profile<>(m, references, comparison, correct, message));
    }

    /** check the assumption using comparator,
     * comparator will take 2 args from {@code references} and return value from {@code actual}
     * @param references argument passed to comparator
     * @param actual argument passed to comparator
     * @param message information about the assumption
     * @param comparison type of comparison expected from references and actual
     * @param <T> type of object that evaluated
     */
    private <T> void record(String message, int comparison, T references, T actual)
    {
        Metric<Object> m = new Metric<Object>(actual, 0, null);
        boolean correct = compare(references, actual, comparison);
        record(new Profile<Object>(m, references, comparison, correct, message));
    }

    /** compare equality of the given args
     * @param references argument passed to comparator
     * @param actual argument passed to comparator
     * @param comparison type of comparison expected. This will determine comparator
     * @param <T> type of object that evaluated
     */
    protected static <T> boolean compare(T references, T actual, int comparison)
    {
        switch (comparison)
        {
            case Status.EQUAL           : return Logical.equals(actual, references);
            case Status.NOT_EQUAL       : return Logical.notEquals(actual, references);
            case Status.ARRAY_EQUAL     : return Logical.isArrayEquals((T[]) references, (T[]) actual);
            case Status.ARRAY_NOT_EQUAL : return Logical.isArrayNotEquals((T[]) references, (T[]) actual);
            case Status.REFERENCE       : return Logical.same(actual, references);
            case Status.NOT_REFERENCE   : return Logical.notSame(actual, references);
        };
        return false;
    }

    /** check if both params are the same type of array */
    private <T> boolean arrayTypeMismatch(T a, T b)
    {
        // array should not be null object
        if (a == null || b == null)
            return true;
        // both class should be the same
        if (a.getClass() != b.getClass())
            return true;
        // if we come at this point (meaning we know both are the same class)
        // check if one of them is array type
        return !a.getClass().isArray();
    }

    /** increment test count and success count */
    private void increment(boolean correct)
    {
        if (correct) mSuccessCount++;
        mTestCount++;
    }

}