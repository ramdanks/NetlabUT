package com.NetlabUT;

/** class to show a state of a assumption or comparison
 * @author Ramadhan Kalih Sewu
 * @version 1.0
 */
public class Profile<T> extends Metric<T>
{
    private final T reference;
    private final Class<?> classReference;
    private final String message;
    private final int referenceStatus;
    private final boolean correct;

    public Profile(Metric<T> metric, T reference, int referenceStatus, boolean correct, String message)
    {
        super(metric);
        this.reference = reference;
        this.classReference = null;
        this.referenceStatus = referenceStatus;
        this.message = message;
        this.correct = correct;
    }
    public Profile(Metric<T> metric, Class<?> reference, int referenceStatus, boolean correct, String message)
    {
        super(metric);
        this.reference = null;
        this.classReference = reference;
        this.referenceStatus = referenceStatus;
        this.message = message;
        this.correct = correct;
    }

    public T getReference()
    {
        return reference;
    }
    public Class<?> getClassReference()
    {
        return reference == null ? classReference : reference.getClass();
    }
    public Object getActual()
    {
        if (super.returns != null)
            return super.returns;
        if (super.throwable != null)
            return super.throwable;
        return null;
    }
    public Class<?> getClassActual()
    {
        Object actual = this.getActual();
        if (actual != null)
            return actual.getClass();
        return null;
    }
    public int getReferenceStatus()
    {
        return referenceStatus;
    }
    public long getProfileTime()
    {
        return nanoTime;
    }
    public String getReferenceString()
    {
        if (reference == null && classReference == null)
            return "null";
        return reference == null ? classReference.getName() : getObjectIdentifierString(reference);
    }
    public String getMessage()
    {
        return message;
    }
    public boolean isCorrect()
    {
        return correct;
    }

    public static String toString(Object object)
    {
        if (object == null)
            return "null";
        if (!object.getClass().isArray())
            return object.toString();

        Object[] array = (Object[]) object;
        StringBuilder str = new StringBuilder();
        if (array.length > 0)
        {
            str.append(array[0].toString());
            for (int i = 1; i < array.length; i++)
                str.append(',').append(array[i].toString());
        }
        return str.toString();
    }

    public static String getObjectIdentifierString(Object obj) {
        if (obj == null) return "null";
        return obj.getClass().getName() + "@" + Integer.toHexString(obj.hashCode());
    }
}