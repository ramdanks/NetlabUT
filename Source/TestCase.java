package Source;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

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
            Thread.sleep(1000);
            metric.bytesUsed = getReallyUsedMemory();
            metric.nanoTime = System.nanoTime();
            runnable.run();
            metric.nanoTime = System.nanoTime() - metric.nanoTime;
            long afterFunc = getReallyUsedMemory();
            metric.bytesUsed -= afterFunc;
        } catch (Exception e) {
            System.err.println(e);
        }
        return metric;
    }
    protected void testCompare(Runnable primary, Runnable secondary)
    {
        Metric primaryMetric = test(primary);
        Metric secondaryMetric = test(secondary);

        float percentageTimeRelative;
        String stringTimeRelative;
        if (primaryMetric.nanoTime > secondaryMetric.nanoTime)
        {
            stringTimeRelative = "Secondary Faster %f %%";
            percentageTimeRelative = 100f * primaryMetric.nanoTime / secondaryMetric.nanoTime - 100f;
        }
        else
        {
            stringTimeRelative = "Primary Faster %f %%";
            percentageTimeRelative = 100f * secondaryMetric.nanoTime / primaryMetric.nanoTime - 100f;
        }
        System.out.format(
            "Time | Primary: %d ns, Secondary: %d ns | " + stringTimeRelative + "\n",
            primaryMetric.nanoTime,
            secondaryMetric.nanoTime,
            percentageTimeRelative
        );
        
        float percentageMemoryRelative;
        String stringMemoryRelative;
        if (primaryMetric.bytesUsed > secondaryMetric.bytesUsed)
        {
            stringMemoryRelative = "Secondary Leaner %f %%";
            percentageMemoryRelative = 100f * primaryMetric.bytesUsed / secondaryMetric.bytesUsed - 100f;
        }
        else
        {
            stringMemoryRelative = "Primary Leaner %f %%";
            percentageMemoryRelative = 100f * secondaryMetric.bytesUsed / primaryMetric.bytesUsed - 100f;
        }
        System.out.format(
            "Memory | Primary: %d bytes, Secondary: %d bytes | " + stringMemoryRelative + "\n",
            primaryMetric.bytesUsed,
            secondaryMetric.bytesUsed,
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
