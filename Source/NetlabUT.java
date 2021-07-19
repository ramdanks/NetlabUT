package Source;

public abstract class NetlabUT extends Benchmark
{
    private String mTestName = null;
    private long mCountTest = 0;
    private long mCountSuccess = 0;
    
    protected NetlabUT() {}
    protected NetlabUT(String testName) { mTestName = testName; }
    
    /** entry point for calculating unit test, call assume function to record testing */
    protected abstract void scenario();
    /** run unit test, it will call {@link #scenario()} as defined by the unit test class */
    public void run()
    {    
        if (mTestName == null)   System.out.println("Running Test Case");
        else                     System.out.println("Running Test Case: " + mTestName);
        scenario();
        System.out.format("Score: %.2f [%d/%d]\n", 100f * mCountSuccess / mCountTest, mCountSuccess, mCountTest);
    }

    protected void assumeNull(Object actual)                                          { recordAssumption(actual == null); }
    protected void assumeNotNull(Object actual)                                       { recordAssumption(actual != null); }
    protected void assumeSame(Object expected, Object actual)                         { recordAssumption(actual == expected); }

    protected void assumeTrue(boolean actual)                                         { recordAssumption(actual == true); }
    protected void assumeFalse(boolean actual)                                        { recordAssumption(actual == false); }

    protected void assumeEquals(boolean expected, boolean actual)                     { recordAssumption(expected == actual); }
    protected void assumeEquals(char expected, char actual)                           { recordAssumption(expected == actual); }
    protected void assumeEquals(short expected, short actual)                         { recordAssumption(expected == actual); }
    protected void assumeEquals(int expected, int actual)                             { recordAssumption(expected == actual); }
    protected void assumeEquals(long expected, long actual)                           { recordAssumption(expected == actual); }
    protected void assumeEquals(float expected, float actual)                         { recordAssumption(expected == actual); }
    protected void assumeEquals(double expected, double actual)                       { recordAssumption(expected == actual); }
    protected <T> void assumeEquals(Comparable<T> expected, Comparable<T> actual)     { recordAssumption(expected.equals(actual) == true); }
    protected void assumeNotEquals(boolean expected, boolean actual)                  { recordAssumption(expected != actual); }
    protected void assumeNotEquals(char expected, char actual)                        { recordAssumption(expected != actual); }
    protected void assumeNotEquals(short expected, short actual)                      { recordAssumption(expected != actual); }
    protected void assumeNotEquals(int expected, int actual)                          { recordAssumption(expected != actual); }
    protected void assumeNotEquals(long expected, long actual)                        { recordAssumption(expected != actual); }  
    protected void assumeNotEquals(float expected, float actual)                      { recordAssumption(expected != actual); }
    protected void assumeNotEquals(double expected, double actual)                    { recordAssumption(expected != actual); }
    protected <T> void assumeNotEquals(Comparable<T> expected, Comparable<T> actual)  { recordAssumption(expected.equals(actual) == false); }

    // primitive array type cannot use generic array type
    // conversion to wrapper class may provice cleaner code
    // but causes slower performance in runtime

    protected void assumeArrayEquals(boolean[] expected, boolean[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i] != actual[i])
                        throw new Exception();
            }
            recordAssumption(true);
        } catch (Exception e) { recordAssumption(false); }
    }
    protected void assumeArrayEquals(char[] expected, char[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i] != actual[i])
                        throw new Exception();
            }
            recordAssumption(true);
        } catch (Exception e) { recordAssumption(false); }
    }
    protected void assumeArrayEquals(short[] expected, short[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i] != actual[i])
                        throw new Exception();
            }
            recordAssumption(true);
        } catch (Exception e) { recordAssumption(false); }
    }
    protected void assumeArrayEquals(int[] expected, int[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i] != actual[i])
                        throw new Exception();
            }
            recordAssumption(true);
        } catch (Exception e) { recordAssumption(false); }
    }
    protected void assumeArrayEquals(long[] expected, long[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i] != actual[i])
                        throw new Exception();
            }
            recordAssumption(true);
        } catch (Exception e) { recordAssumption(false); }
    }
    protected void assumeArrayEquals(float[] expected, float[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i] != actual[i])
                        throw new Exception();
            }
            recordAssumption(true);
        } catch (Exception e) { recordAssumption(false); }
    }
    protected void assumeArrayEquals(double[] expected, double[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i] != actual[i])
                        throw new Exception();
            }
            recordAssumption(true);
        } catch (Exception e) { recordAssumption(false); }
    }
    protected <T> void assumeArrayEquals(Comparable<T>[] expected, Comparable<T>[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i].equals(actual[i]) == false)
                        throw new Exception();
            }
            recordAssumption(true);
        } catch (Exception e) { recordAssumption(false); }
    }

    /** record assumption count and successful attempts */
    private void recordAssumption(boolean success)
    {
        if (success) mCountSuccess++;
        mCountTest++;
    }
}
