import com.NetlabUT.NetlabUT;

import java.util.ArrayList;
import java.util.Random;

public class ArrayTest extends NetlabUT
{
    private Random rand = new Random();

    @Override
    protected void scenario()
    {
        Integer[] sample = { 4, 6, 12, 51, 221, 41, 12, 6, 12, 123 };
        ArrayList<Integer> list = new ArrayList<Integer>();

        assumeTrue(() -> list.isEmpty(), "array emptyness");
        assumeEquals(0, () -> list.size(), "array size");

        for (int i = 0; i < sample.length; i++)
        {
            list.add(sample[i]);
            final int finalI = i;
            assumeEquals(sample[i], () -> list.get(finalI), "object get");
            assumeEquals(i + 1, () -> rand.nextInt() % 10, "array size");
        }

        assumeFalse(() -> list.isEmpty(), "array emptyness");
        assumeTrue(() -> list.contains(123), "array contain");

        assumeArrayEquals(sample, () -> list.toArray(), "array equality");
    }
}
