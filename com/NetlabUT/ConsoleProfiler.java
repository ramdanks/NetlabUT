package com.NetlabUT;

import java.util.Date;

public class ConsoleProfiler
{
    public ConsoleProfiler(String title, UnitTest[] unitList)
    {
        int totalTest = 0;
        int totalSuccess = 0;

        System.out.println("Running Profiler: " + title);
        for (int i = 0; i < unitList.length; i++)
        {
            System.out.println("Running: " + unitList[i].getTestName());

            if (unitList[i].getTestCount() == 0)
                unitList[i].run();

            int success = unitList[i].getSuccessCount();
            int count = unitList[i].getTestCount();
            double percentage = 100.0 * success / count;

            System.out.format(" - Earned Points: %d out of %d\n", success, count);
            System.out.format(" - Percentage: %.2f %%\n", percentage);

            totalSuccess += success;
            totalTest += count;
        }

        double percentage = 100.0 * totalSuccess / totalTest;
        System.out.println("Profile Summary: " + title);
        System.out.format("Generated: %s\n", new Date());
        System.out.format(" - Total Earned Points: %d out of %d\n", totalSuccess, totalTest);
        System.out.format(" - Percentage: %.2f %%\n", percentage);
    }
}
