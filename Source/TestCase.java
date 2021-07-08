package Source;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

public abstract class TestCase
{
    protected String testName = null;
    
    private long mCountTest = 0;
    private long mCountSuccess = 0;
    
    protected TestCase() {}
    protected TestCase(String testName) { this.testName = testName; }
    
    protected abstract void scenario();
    public void run()
    {    
        if (testName == null)   System.out.println("Running Test Case");
        else                    System.out.println("Running Test Case: " + testName);
        scenario();
        System.out.format("Score: %.2f [%d/%d]\n", 100f * mCountSuccess / mCountTest, mCountSuccess, mCountTest);
    }
    protected void test(boolean condition, boolean value)
    {
        test(condition, value, null);
    }
    protected void test(boolean condition, boolean value, String message)
    {
        String c = condition ? "true" : "false";
        String v = value ? "true" : "false";
        test(c, v, message);
    }
    protected void test(int condition, int value)
    {
        test(condition, value, null);
    }
    protected void test(int condition, int value, String message)
    {
        String c = Integer.toString(condition);
        String v = Integer.toString(value);
        test(c, v, message);
    }
    protected void test(double condition, double value)
    {
        test(condition, value, null);
    }
    protected void test(double condition, double value, String message)
    {
        String c = Double.toString(condition);
        String v = Double.toString(value);
        test(c, v, message);
    }
    protected void test(String condition, String value)
    {
        test(condition, value, null);
    }
    protected void test(String condition, String value, String message)
    {
        // logic
        mCountTest++;
        if (condition.equals(value))
            mCountSuccess++;
        // output to screen
        String out = String.format("Case #%d: %s, Expected: %s", mCountTest, value, condition);
        if (message != null)
            out += String.format(", Message: %s", message);
        System.out.println(out);
    }
    protected Metric test(Runnable runnable)
    {
        Metric metric = new Metric();
        try {    
            metric.bytesUsed = getReallyUsedMemory();
            metric.nanoTime = System.nanoTime();
            runnable.run();
            metric.nanoTime = System.nanoTime() - metric.nanoTime;
            metric.bytesUsed -= getReallyUsedMemory();   
        } catch (Exception e) {
            System.err.println(e);
        }
        return metric;
    }
    protected void testCompare(Runnable primary, Runnable secondary)
    {
        final int PRIMARY_IDX = 0, SECONDARY_IDX = 1;

        Metric[] metric = new Metric[2];
        Runnable[] runnable = new Runnable[2];
        runnable[PRIMARY_IDX] = primary;
        runnable[SECONDARY_IDX] = secondary;

        for (int i = 0; i < 2; ++i)
        {
            metric[i] = new Metric();
            try {
                metric[i].bytesUsed = getReallyUsedMemory();
                metric[i].nanoTime = System.nanoTime();
                runnable[i].run();
                metric[i].nanoTime = System.nanoTime() - metric[i].nanoTime;
                metric[i].bytesUsed -= getReallyUsedMemory();   
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        
        float percentageTimeRelative;
        String stringTimeRelative;
        if (metric[PRIMARY_IDX].nanoTime > metric[SECONDARY_IDX].nanoTime)
        {
            stringTimeRelative = "Secondary Faster %f %%";
            percentageTimeRelative = 100f * metric[PRIMARY_IDX].nanoTime / metric[SECONDARY_IDX].nanoTime - 100f;
        }
        else
        {
            stringTimeRelative = "Primary Faster %f %%";
            percentageTimeRelative = 100f * metric[SECONDARY_IDX].nanoTime / metric[PRIMARY_IDX].nanoTime - 100f;
        }
        
        float percentageMemoryRelative;
        String stringMemoryRelative;
        if (metric[PRIMARY_IDX].bytesUsed > metric[SECONDARY_IDX].bytesUsed)
        {
            stringMemoryRelative = "Secondary Leaner %f %%";
            percentageMemoryRelative = 100f * metric[PRIMARY_IDX].bytesUsed / metric[SECONDARY_IDX].bytesUsed - 100f;
        }
        else
        {
            stringMemoryRelative = "Primary Leaner %f %%";
            percentageMemoryRelative = 100f * metric[SECONDARY_IDX].bytesUsed / metric[PRIMARY_IDX].bytesUsed - 100f;
        }

        System.out.format(
            "Time | Primary: %d ns, Secondary: %d ns | " + stringTimeRelative + "\n",
            metric[PRIMARY_IDX].nanoTime,
            metric[SECONDARY_IDX].nanoTime,
            percentageTimeRelative
        );
        System.out.format(
            "Memory | Primary: %d bytes, Secondary: %d bytes | " + stringMemoryRelative + "\n",
            metric[PRIMARY_IDX].bytesUsed,
            metric[SECONDARY_IDX].bytesUsed,
            percentageMemoryRelative
        );

    }

    private static long getGcCount()
    {
        long sum = 0;
        for (GarbageCollectorMXBean b : ManagementFactory.getGarbageCollectorMXBeans())
        {
            long count = b.getCollectionCount();
            if (count != -1) 
                sum += count;
        }
        return sum;
    }
    
    private static long getReallyUsedMemory()
    {
        long before = getGcCount();
        System.gc();
        while (getGcCount() == before);
        return getCurrentlyAllocatedMemory();
    }
    
    private static long getCurrentlyAllocatedMemory()
    {
        final Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

}
