package Source;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class UnitTest
{
    private int mTestCount;
    private int mSuccessCount;

    public int getTestCount() { return mTestCount; }
    public int getSuccessCount() { return mSuccessCount; }

    /** entry point for calculating unit test, call assume function to record testing */
    protected abstract void scenario();
    /** run unit test, it will call {@link #scenario()} as defined by the unit test class */
    public void run()
    {
        mTestCount = 0;
        mSuccessCount = 0;
        scenario();
    }
    
    protected boolean assumeNull(Object actual)                                  { return recordAssumption(actual == null); }
    protected boolean assumeNotNull(Object actual)                               { return recordAssumption(actual != null); }
    protected boolean assumeSame(Object expected, Object actual)                 { return recordAssumption(actual == expected); }

    protected boolean assumeTrue(boolean actual)                                 { return recordAssumption(actual == true); }
    protected boolean assumeFalse(boolean actual)                                { return recordAssumption(actual == false); }

    protected boolean assumeEquals(Object expected, Object actual)               { return recordAssumption(expected.equals(actual) == true); }
    protected boolean assumeEquals(boolean expected, boolean actual)             { return recordAssumption(expected == actual); }
    protected boolean assumeEquals(char expected, char actual)                   { return recordAssumption(expected == actual); }
    protected boolean assumeEquals(short expected, short actual)                 { return recordAssumption(expected == actual); }
    protected boolean assumeEquals(int expected, int actual)                     { return recordAssumption(expected == actual); }
    protected boolean assumeEquals(long expected, long actual)                   { return recordAssumption(expected == actual); }
    protected boolean assumeEquals(float expected, float actual)                 { return recordAssumption(expected == actual); }
    protected boolean assumeEquals(double expected, double actual)               { return recordAssumption(expected == actual); }

    protected boolean assumeNotEquals(Object expected, Object actual)            { return recordAssumption(expected.equals(actual) == false); }
    protected boolean assumeNotEquals(boolean expected, boolean actual)          { return recordAssumption(expected != actual); }
    protected boolean assumeNotEquals(char expected, char actual)                { return recordAssumption(expected != actual); }
    protected boolean assumeNotEquals(short expected, short actual)              { return recordAssumption(expected != actual); }
    protected boolean assumeNotEquals(int expected, int actual)                  { return recordAssumption(expected != actual); }
    protected boolean assumeNotEquals(long expected, long actual)                { return recordAssumption(expected != actual); }
    protected boolean assumeNotEquals(float expected, float actual)              { return recordAssumption(expected != actual); }
    protected boolean assumeNotEquals(double expected, double actual)            { return recordAssumption(expected != actual); }

    protected boolean assumeArrayEquals(boolean[] expected, boolean[] actual)    { return recordAssumption(isArrayEquals(expected, actual) == true); }
    protected boolean assumeArrayEquals(char[] expected, char[] actual)          { return recordAssumption(isArrayEquals(expected, actual) == true); }
    protected boolean assumeArrayEquals(short[] expected, short[] actual)        { return recordAssumption(isArrayEquals(expected, actual) == true); }
    protected boolean assumeArrayEquals(int[] expected, int[] actual)            { return recordAssumption(isArrayEquals(expected, actual) == true); }
    protected boolean assumeArrayEquals(long[] expected, long[] actual)          { return recordAssumption(isArrayEquals(expected, actual) == true); }
    protected boolean assumeArrayEquals(float[] expected, float[] actual)        { return recordAssumption(isArrayEquals(expected, actual) == true); }
    protected boolean assumeArrayEquals(double[] expected, double[] actual)      { return recordAssumption(isArrayEquals(expected, actual) == true); }
    protected boolean assumeArrayEquals(Object[] expected, Object[] actual)      { return recordAssumption(isArrayEquals(expected, actual) == true); }

    protected boolean assumeArrayNotEquals(boolean[] expected, boolean[] actual) { return recordAssumption(isArrayEquals(expected, actual) == false); }
    protected boolean assumeArrayNotEquals(char[] expected, char[] actual)       { return recordAssumption(isArrayEquals(expected, actual) == false); }
    protected boolean assumeArrayNotEquals(short[] expected, short[] actual)     { return recordAssumption(isArrayEquals(expected, actual) == false); }
    protected boolean assumeArrayNotEquals(int[] expected, int[] actual)         { return recordAssumption(isArrayEquals(expected, actual) == false); }
    protected boolean assumeArrayNotEquals(long[] expected, long[] actual)       { return recordAssumption(isArrayEquals(expected, actual) == false); }
    protected boolean assumeArrayNotEquals(float[] expected, float[] actual)     { return recordAssumption(isArrayEquals(expected, actual) == false); }
    protected boolean assumeArrayNotEquals(double[] expected, double[] actual)   { return recordAssumption(isArrayEquals(expected, actual) == false); }
    protected boolean assumeArrayNotEquals(Object[] expected, Object[] actual)   { return recordAssumption(isArrayEquals(expected, actual) == false); }


    // primitive array type cannot use generic array type
    // conversion to wrapper class may provice cleaner code
    // but causes slower performance in runtime

    private boolean isArrayEquals(boolean[] a, boolean[] b) {
        try {
            // check memory equality (also handle if both are null)
            if (a != b) {
                if (a.length != b.length)
                    throw new Exception();
                for (int i = 0; i < a.length; i++)
                    if (a[i] != b[i])
                        throw new Exception();
            }
            return true;
        } catch (Exception e) { return false; }
    }
    private boolean isArrayEquals(char[] a, char[] b) {
        try {
            // check memory equality (also handle if both are null)
            if (a != b) {
                if (a.length != b.length)
                    throw new Exception();
                for (int i = 0; i < a.length; i++)
                    if (a[i] != b[i])
                        throw new Exception();
            }
            return true;
        } catch (Exception e) { return false; }
    }
    private boolean isArrayEquals(short[] a, short[] b) {
        try {
            // check memory equality (also handle if both are null)
            if (a != b) {
                if (a.length != b.length)
                    throw new Exception();
                for (int i = 0; i < a.length; i++)
                    if (a[i] != b[i])
                        throw new Exception();
            }
            return true;
        } catch (Exception e) { return false; }
    }
    private boolean isArrayEquals(int[] a, int[] b) {
        try {
            // check memory equality (also handle if both are null)
            if (a != b) {
                if (a.length != b.length)
                    throw new Exception();
                for (int i = 0; i < a.length; i++)
                    if (a[i] != b[i])
                        throw new Exception();
            }
            return true;
        } catch (Exception e) { return false; }
    }
    private boolean isArrayEquals(long[] a, long[] b) {
        try {
            // check memory equality (also handle if both are null)
            if (a != b) {
                if (a.length != b.length)
                    throw new Exception();
                for (int i = 0; i < a.length; i++)
                    if (a[i] != b[i])
                        throw new Exception();
            }
            return true;
        } catch (Exception e) { return false; }
    }
    private boolean isArrayEquals(float[] a, float[] b) {
        try {
            // check memory equality (also handle if both are null)
            if (a != b) {
                if (a.length != b.length)
                    throw new Exception();
                for (int i = 0; i < a.length; i++)
                    if (a[i] != b[i])
                        throw new Exception();
            }
            return true;
        } catch (Exception e) { return false; }
    }
    private boolean isArrayEquals(double[] a, double[] b) {
        try {
            // check memory equality (also handle if both are null)
            if (a != b) {
                if (a.length != b.length)
                    throw new Exception();
                for (int i = 0; i < a.length; i++)
                    if (a[i] != b[i])
                        throw new Exception();
            }
            return true;
        } catch (Exception e) { return false; }
    }
    private boolean isArrayEquals(Object[] a, Object[] b) {
        try {
            // check memory equality (also handle if both are null)
            if (a != b) {
                if (a.length != b.length)
                    throw new Exception();
                for (int i = 0; i < a.length; i++)
                    if (a[i].equals(b[i]) == false)
                        throw new Exception();
            }
            return true;
        } catch (Exception e) { return false; }
    }

    /** record assumption count and successful attempts */
    protected boolean recordAssumption(boolean success)
    {
        if (success) mSuccessCount++;
        mTestCount++;
        return success;
    }

}
