package Source;

public abstract class UnitTest
{
    private long mTestCount;
    private long mSuccessCount;

    public long getTestCount() { return mTestCount; }
    public long getSuccessCount() { return mSuccessCount; }

    /** entry point for calculating unit test, call assume function to record testing */
    protected abstract void scenario();
    /** run unit test, it will call {@link #scenario()} as defined by the unit test class */
    public void run()
    {
        mTestCount = 0;
        mSuccessCount = 0;
        scenario();
    }
    
    protected boolean assumeNull(Object actual)                                          { return recordAssumption(actual == null); }
    protected boolean assumeNotNull(Object actual)                                       { return recordAssumption(actual != null); }
    protected boolean assumeSame(Object expected, Object actual)                         { return recordAssumption(actual == expected); }

    protected boolean assumeTrue(boolean actual)                                         { return recordAssumption(actual == true); }
    protected boolean assumeFalse(boolean actual)                                        { return recordAssumption(actual == false); }

    protected boolean assumeEquals(Object expected, Object actual)                       { return recordAssumption(expected.equals(actual) == true); }
    protected boolean assumeEquals(boolean expected, boolean actual)                     { return recordAssumption(expected == actual); }
    protected boolean assumeEquals(char expected, char actual)                           { return recordAssumption(expected == actual); }
    protected boolean assumeEquals(short expected, short actual)                         { return recordAssumption(expected == actual); }
    protected boolean assumeEquals(int expected, int actual)                             { return recordAssumption(expected == actual); }
    protected boolean assumeEquals(long expected, long actual)                           { return recordAssumption(expected == actual); }
    protected boolean assumeEquals(float expected, float actual)                         { return recordAssumption(expected == actual); }
    protected boolean assumeEquals(double expected, double actual)                       { return recordAssumption(expected == actual); }

    protected boolean assumeNotEquals(Object expected, Object actual)                    { return recordAssumption(expected.equals(actual) == false); }
    protected boolean assumeNotEquals(boolean expected, boolean actual)                  { return recordAssumption(expected != actual); }
    protected boolean assumeNotEquals(char expected, char actual)                        { return recordAssumption(expected != actual); }
    protected boolean assumeNotEquals(short expected, short actual)                      { return recordAssumption(expected != actual); }
    protected boolean assumeNotEquals(int expected, int actual)                          { return recordAssumption(expected != actual); }
    protected boolean assumeNotEquals(long expected, long actual)                        { return recordAssumption(expected != actual); }  
    protected boolean assumeNotEquals(float expected, float actual)                      { return recordAssumption(expected != actual); }
    protected boolean assumeNotEquals(double expected, double actual)                    { return recordAssumption(expected != actual); }

    // primitive array type cannot use generic array type
    // conversion to wrapper class may provice cleaner code
    // but causes slower performance in runtime

    protected boolean assumeArrayEquals(boolean[] expected, boolean[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i] != actual[i])
                        throw new Exception();
            }
            return recordAssumption(true);
        } catch (Exception e) { return recordAssumption(false); }
    }
    protected boolean assumeArrayEquals(char[] expected, char[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i] != actual[i])
                        throw new Exception();
            }
            return recordAssumption(true);
        } catch (Exception e) { return recordAssumption(false); }
    }
    protected boolean assumeArrayEquals(short[] expected, short[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i] != actual[i])
                        throw new Exception();
            }
            return recordAssumption(true);
        } catch (Exception e) { return recordAssumption(false); }
    }
    protected boolean assumeArrayEquals(int[] expected, int[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i] != actual[i])
                        throw new Exception();
            }
            return recordAssumption(true);
        } catch (Exception e) { return recordAssumption(false); }
    }
    protected boolean assumeArrayEquals(long[] expected, long[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i] != actual[i])
                        throw new Exception();
            }
            return recordAssumption(true);
        } catch (Exception e) { return recordAssumption(false); }
    }
    protected boolean assumeArrayEquals(float[] expected, float[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i] != actual[i])
                        throw new Exception();
            }
            return recordAssumption(true);
        } catch (Exception e) { return recordAssumption(false); }
    }
    protected boolean assumeArrayEquals(double[] expected, double[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i] != actual[i])
                        throw new Exception();
            }
            return recordAssumption(true);
        } catch (Exception e) { return recordAssumption(false); }
    }
    protected boolean assumeArrayEquals(Object[] expected, Object[] actual) {
        try {
            // check memory equality (also handle if both are null)
            if (expected != actual) {
                if (expected.length != actual.length)
                    throw new Exception();
                for (int i = 0; i < expected.length; i++)
                    if (expected[i].equals(actual[i]) == false)
                        throw new Exception();
            }
            return recordAssumption(true);
        } catch (Exception e) { return recordAssumption(false); }
    }


    /** record assumption count and successful attempts */
    protected boolean recordAssumption(boolean success)
    {
        if (success) mSuccessCount++;
        mTestCount++;
        return success;
    }

}
