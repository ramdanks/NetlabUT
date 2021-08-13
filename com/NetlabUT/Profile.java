package com.NetlabUT;

import java.util.function.Function;

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

    public boolean isReferenceNull()    { return reference == null || classReference == null; }
    public boolean isActualNull()       { return returns == null || throwable == null; }
    public int getReferenceStatus()     { return referenceStatus; }
    public long getProfileTime()        { return nanoTime; }
    public String getMessage()          { return message; }
    public boolean isCorrect()          { return correct; }
    public T getReference()             { return reference; }
    public Class<?> getClassReference() { return reference == null ? classReference : reference.getClass(); }
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

    public String getReferenceString(Function<Object, String> translator)
    {
        if (reference != null)
            return translator.apply(reference);
        if (classReference != null)
            return classReference.getName();
        return "null";
    }

    public String getActualString(Function<Object, String> translator)
    {
        Object obj = getActual();
        if (obj != null)
            return translator.apply(obj);
        return "null";
    }
}