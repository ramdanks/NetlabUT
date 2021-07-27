package com.NetlabUT;

final class Logical
{
    public static boolean equals(Object a, Object b)    { return a != null && a.equals(b); }
    public static boolean notEquals(Object a, Object b) { return a != null && !a.equals(b); }
    public static boolean same(Object a, Object b)      { return a == b; }
    public static boolean notSame(Object a, Object b)   { return a != b; }

    public static <T extends Throwable> boolean throwing(T throwable)
    { return throwable != null; }
    public static <T extends Throwable> boolean throwing(Class<T> expectedType, Throwable throwable)
    { return throwable != null && throwable.getClass() == expectedType; }

    // primitive array type cannot use generic array type
    // conversion to wrapper class may provice cleaner code
    // but causes slower performance in runtime

    public static boolean isArrayNotEquals(boolean[] a, boolean[] b) { return !isArrayEquals(a, b); }
    public static boolean isArrayNotEquals(char[] a, char[] b)       { return !isArrayEquals(a, b); }
    public static boolean isArrayNotEquals(short[] a, short[] b)     { return !isArrayEquals(a, b); }
    public static boolean isArrayNotEquals(int[] a, int[] b)         { return !isArrayEquals(a, b); }
    public static boolean isArrayNotEquals(long[] a, long[] b)       { return !isArrayEquals(a, b); }
    public static boolean isArrayNotEquals(float[] a, float[] b)     { return !isArrayEquals(a, b); }
    public static boolean isArrayNotEquals(double[] a, double[] b)   { return !isArrayEquals(a, b); }
    public static boolean isArrayNotEquals(Object[] a, Object[] b)   { return !isArrayEquals(a, b); }

    public static boolean isArrayEquals(boolean[] a, boolean[] b) {
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
    public static boolean isArrayEquals(char[] a, char[] b) {
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
    public static boolean isArrayEquals(short[] a, short[] b) {
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
    public static boolean isArrayEquals(int[] a, int[] b) {
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
    public static boolean isArrayEquals(long[] a, long[] b) {
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
    public static boolean isArrayEquals(float[] a, float[] b) {
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
    public static boolean isArrayEquals(double[] a, double[] b) {
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
    public static boolean isArrayEquals(Object[] a, Object[] b) {
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
}
