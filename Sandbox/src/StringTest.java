import com.Reflector.ClassR;
import com.Reflector.ReflectorUnitTest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

final class StringTest extends ReflectorUnitTest
{
    @Override
    protected void scenario()
    {
        ClassR String = new ClassR("java.lang.String");
        Class<?> cString = String.getContainingClass();

        Constructor ctor0 = String.getConstructor();
        Constructor ctor1 = String.getConstructor(char[].class);

        Method charAt = String.getMethod("charAt", int.class);
        Method getBytes = String.getMethod("getBytes");
        Object str = String.newInstance(ctor1, new char[] {'H', 'e', 'l', 'l', 'o'});

        String h = "Hello";
        byte[] arrByte = h.getBytes();

        assumeNull(null, null);
        assumeNotNull(charAt);

        assumeEquals('o', charAt, str, 4);
        assumeNotEquals("hello", str);

        assumeThrows(charAt, str, 21);
        assumeThrows(StringIndexOutOfBoundsException.class, charAt, str, 21);

        Integer[] arr1 = { 4, 5, 6, 12, 212, 51, 123, 51 };
        Integer[] arr2 = { 4, 5, 6, 12, 212, 51, 123, 51, 9 };

        assumeArrayEquals(arr1, arr1);
        assumeArrayNotEquals(arr1, arr2);

        assumeArrayEquals(arr1, charAt, str, 0);
        assumeArrayEquals(arr1, () -> charAt.invoke(str, 0));
    }
}
