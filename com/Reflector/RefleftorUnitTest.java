package com.Reflector;
import com.NetlabUT.UnitTest;

public abstract class RefleftorUnitTest extends UnitTest
{
    private final PackageR packageR;

    protected RefleftorUnitTest(PackageR packageR, String testName)
    {
        super(testName);
        this.packageR = packageR;
    }
    protected RefleftorUnitTest(PackageR packageR)
    {
        this.packageR = packageR;
    }

    public PackageR getPackageR() { return packageR; }
    protected abstract void scenario();

    /** get static method from reflector and call it and assume returns null */
    protected void assumeNull(ClassR classR, String funcName)
    { assumeNull(() -> classR.invoke(funcName)); }
    /** get member method from reflector and call it and assume returns null */
    protected void assumeNull(ClassR classR, Object object, String funcName)
    { assumeNull(() -> classR.invoke(object, funcName)); }
    /** get member method from reflector and call it and assume returns null */
    protected void assumeNull(ClassR classR, Object object, String funcName, Object... args)
    { assumeNull(() -> classR.invoke(object, funcName, args)); }

    /** get static method from reflector and call it and assume returns not null */
    protected void assumeNotNull(ClassR classR, String funcName)
    { assumeNotNull(() -> classR.invoke(funcName)); }
    /** get member method from reflector and call it and assume returns not null */
    protected void assumeNotNull(ClassR classR, Object object, String funcName)
    { assumeNotNull(() -> classR.invoke(object, funcName)); }
    /** get member method from reflector and call it and assume returns not null */
    protected void assumeNotNull(ClassR classR, Object object, String funcName, Object... args)
    { assumeNotNull(() -> classR.invoke(object, funcName, args)); }

    /** get static method from reflector and call it and assume returns reference to expected */
    protected void assumeSame(Object expected, ClassR classR, String funcName)
    { assumeSame(expected, () -> classR.invoke(funcName)); }
    /** get member method from reflector and call it and assume returns reference to expected */
    protected void assumeSame(Object expected, ClassR classR, Object object, String funcName)
    { assumeSame(expected, () -> classR.invoke(object, funcName)); }
    /** get member method from reflector and call it and assume returns reference to expected */
    protected void assumeSame(Object expected, ClassR classR, Object object, String funcName, Object... args)
    { assumeSame(expected, () -> classR.invoke(object, funcName, args)); }

    /** get static method from reflector and call it and assume returns different reference from expected */
    protected void assumeNotSame(Object expected, ClassR classR, String funcName)
    { assumeNotSame(expected, () -> classR.invoke(funcName)); }
    /** get member method from reflector and call it and assume returns different reference from expected */
    protected void assumeNotSame(Object expected, ClassR classR, Object object, String funcName)
    { assumeNotSame(expected, () -> classR.invoke(object, funcName)); }
    /** get member method from reflector and call it and assume returns different reference from expected */
    protected void assumeNotSame(Object expected, ClassR classR, Object object, String funcName, Object... args)
    { assumeNotSame(expected, () -> classR.invoke(object, funcName, args)); }

    /** get static method from reflector and call it and assume returns true */
    protected void assumeTrue(ClassR classR, String funcName)
    { assumeTrue(() -> classR.invoke(funcName)); }
    /** get member method from reflector and call it and assume returns true */
    protected void assumeTrue(ClassR classR, Object object, String funcName)
    { assumeTrue(() -> classR.invoke(object, funcName)); }
    /** get member method from reflector and call it and assume returns true */
    protected void assumeTrue(ClassR classR, Object object, String funcName, Object... args)
    { assumeTrue(() -> classR.invoke(object, funcName, args)); }

    /** get static method from reflector and call it and assume returns false */
    protected void assumeFalse(ClassR classR, String funcName)
    { assumeFalse(() -> classR.invoke(funcName)); }
    /** get member method from reflector and call it and assume returns false */
    protected void assumeFalse(ClassR classR, Object object, String funcName)
    { assumeFalse(() -> classR.invoke(object, funcName)); }
    /** get member method from reflector and call it and assume returns false */
    protected void assumeFalse(ClassR classR, Object object, String funcName, Object... args)
    { assumeFalse(() -> classR.invoke(object, funcName, args)); }

    /** get static method from reflector and call it and assume returns true from {@link java.lang.Object #equals(java.lang.Object)} */
    protected void assumeEquals(Object expected, ClassR classR, String funcName)
    { assumeEquals(expected, () -> classR.invoke(funcName)); }
    /** get member method from reflector and call it and assume returns true from {@link java.lang.Object #equals(java.lang.Object)} */
    protected void assumeEquals(Object expected, ClassR classR, Object object, String funcName)
    { assumeEquals(expected, () -> classR.invoke(object, funcName)); }
    /** get member method from reflector and call it and assume returns true from {@link java.lang.Object #equals(java.lang.Object)} */
    protected void assumeEquals(Object expected, ClassR classR, Object object, String funcName, Object... args)
    { assumeEquals(expected, () -> classR.invoke(object, funcName, args)); }

    /** get static method from reflector and call it and assume returns false from {@link java.lang.Object #equals(java.lang.Object)} */
    protected void assumeNotEquals(Object expected, ClassR classR, String funcName)
    { assumeNotEquals(expected, () -> classR.invoke(funcName)); }
    /** get member method from reflector and call it and assume returns false from {@link java.lang.Object #equals(java.lang.Object)} */
    protected void assumeNotEquals(Object expected, ClassR classR, Object object, String funcName)
    { assumeNotEquals(expected, () -> classR.invoke(object, funcName)); }
    /** get member method from reflector and call it and assume returns false from {@link java.lang.Object #equals(java.lang.Object)} */
    protected void assumeNotEquals(Object expected, ClassR classR, Object object, String funcName, Object... args)
    { assumeNotEquals(expected, () -> classR.invoke(object, funcName, args)); }

    /** get static method from reflector and call it and assume iteratively returns true from {@link java.lang.Object #equals(java.lang.Object)} */
    protected void assumeArrayEquals(Object[] expected, ClassR classR, String funcName)
    { assumeArrayEquals(expected, () -> classR.invoke(funcName)); }
    /** get member method from reflector and call it and assume iteratively returns true from {@link java.lang.Object #equals(java.lang.Object)} */
    protected void assumeArrayEquals(Object[] expected, ClassR classR, Object object, String funcName)
    { assumeArrayEquals(expected, () -> classR.invoke(object, funcName)); }
    /** get member method from reflector and call it and assume iteratively returns true from {@link java.lang.Object #equals(java.lang.Object)} */
    protected void assumeArrayEquals(Object[] expected, ClassR classR, Object object, String funcName, Object... args)
    { assumeArrayEquals(expected, () -> classR.invoke(object, funcName, args)); }

    /** get static method from reflector and call it and assume iteratively returns false from {@link java.lang.Object #equals(java.lang.Object)} */
    protected void assumeArrayNotEquals(Object[] expected, ClassR classR, String funcName)
    { assumeArrayNotEquals(expected, () -> classR.invoke(funcName)); }
    /** get member method from reflector and call it and assume iteratively returns false from {@link java.lang.Object #equals(java.lang.Object)} */
    protected void assumeArrayNotEquals(Object[] expected, ClassR classR, Object object, String funcName)
    { assumeArrayNotEquals(expected, () -> classR.invoke(object, funcName)); }
    /** get member method from reflector and call it and assume iteratively returns false from {@link java.lang.Object #equals(java.lang.Object)} */
    protected void assumeArrayNotEquals(Object[] expected, ClassR classR, Object object, String funcName, Object... args)
    { assumeArrayNotEquals(expected, () -> classR.invoke(object, funcName, args)); }
}
