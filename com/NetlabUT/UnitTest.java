package com.NetlabUT;

import java.util.function.BiFunction;

/** provides a test through comparing 2 object using {@code assumeXXX} method. it's a non-blocking test
 * that will record the profile of the assumption (reference, actual, message, correctness, etc.)
 * @author Ramadhan Kalih Sewu
 * @version 1.3
 */
public class UnitTest
{
	UnitTestListener listener;

    /** expect executable to throws {@code expected} that extends from {@link java.lang.Throwable} */
    protected <T extends Throwable> void assumeThrows(Class<T> expected, Executable executable)
    { record(null, Status.THROWS_TYPE, expected, executable); }
    /** expect executable to throws {@code expected} that extends from {@link java.lang.Throwable} */
    protected <T extends Throwable> void assumeThrows(Class<T> expected, Executable executable, String message)
    { record(message, Status.THROWS_TYPE, expected, executable); }
    /** expect executable to throws any instance of {@link java.lang.Throwable} */
    protected <T extends Throwable> void assumeThrows(Executable executable)
    { record(null, Status.THROWS, Throwable.class, executable); }
    /** expect executable to throws any instance of {@link java.lang.Throwable} */
    protected <T extends Throwable> void assumeThrows(Executable executable, String message)
    { record(message, Status.THROWS, Throwable.class, executable); }

    /** expect {@code actual} to be {@code null} */
    protected void assumeNull(Object actual)                                                          
    { assumeNull(null, actual); }

    /** expect {@code actual} to returns {@code null} */
    protected void assumeNull(Executable actual)                                                      
    { assumeNull(null, actual); }

    /** expect {@code actual} to be {@code null} */
    protected void assumeNull(String message, Object actual)
    { record(message, Status.REFERENCE, null, () -> actual); }
    
    /** expect {@code actual} to returns {@code null} */
    protected void assumeNull(String message, Executable actual)
    { record(message, Status.REFERENCE, null, actual); }

    /** expect {@code actual} to be not {@code null} */
    protected void assumeNotNull(Object actual)                                                       
    { assumeNotNull(null, actual); }

    /** expect {@code actual} to returns not {@code null} */
    protected void assumeNotNull(Executable actual)                                                   
    { assumeNotNull(null, actual); }

    /** expect {@code actual} to be not {@code null} */
    protected void assumeNotNull(String message, Object actual)
    { record(message, Status.NOT_REFERENCE, null, () -> actual); }

    /** expect {@code actual} to returns not {@code null} */
    protected void assumeNotNull(String message, Executable actual)
    { record(message, Status.NOT_REFERENCE, null, actual); }

    /** expect {@code actual} and {@code expected} referencing the same object */
    protected void assumeSame(Object expected, Object actual)                                        
     { assumeSame(null, expected, actual); }

    /** expect {@code actual} to returns a reference to {@code expected} */
    protected void assumeSame(Object expected, Executable actual)                                     
    { assumeSame(null, expected, actual); }

    /** expect {@code actual} and {@code expected} referencing the same object */
    protected void assumeSame(String message, Object expected, Object actual)
    { record(message, Status.REFERENCE, expected, () -> actual); }

    /** expect {@code actual} to returns a reference to {@code expected} */
    protected void assumeSame(String message, Object expected, Executable actual)
    { record(message, Status.REFERENCE, expected, actual); }

    /** expect {@code actual} and {@code expected} are <strong>not</strong> referencing the same object */
    protected void assumeNotSame(Object expected, Object actual)                                      
    { assumeNotSame(null, expected, actual); }

    /** expect {@code actual} to returns an object which <strong>not</strong> referencing to {@code expected} */
    protected void assumeNotSame(Object expected, Executable actual)                                  
    { assumeNotSame(null, expected, actual); }

    /** expect {@code actual} and {@code expected} are <strong>not</strong> referencing the same object */
    protected void assumeNotSame(String message, Object expected, Object actual)
    { record(message, Status.NOT_REFERENCE, expected, () -> actual); }

    /** expect {@code actual} to returns an object which <strong>not</strong> referencing to {@code expected} */
    protected void assumeNotSame(String message, Object expected, Executable actual)
    { record(message, Status.NOT_REFERENCE, expected, actual); }

    /** expect {@code actual} to be {@code true} */
    protected void assumeTrue(Boolean actual)                                                         
    { assumeTrue(null, actual); }

    /** expect {@code actual} to returns {@code true} */
    protected void assumeTrue(Executable actual)                                                      
    { assumeTrue(null, actual); }

    /** expect {@code actual} to be {@code true} */
    protected void assumeTrue(String message, Boolean actual)
    { record(message, Status.EQUAL, true, () -> actual); }

    /** expect {@code actual} to returns {@code true} */
    protected void assumeTrue(String message, Executable actual)
    { record(message, Status.EQUAL, true, actual); }

    /** expect {@code actual} to be {@code false} */
    protected void assumeFalse(Boolean actual)                                                        
    { assumeFalse(null, actual); }

    /** expect {@code actual} to returns {@code false} */
    protected void assumeFalse(Executable actual)                                                     
    { assumeFalse(null, actual); }

    /** expect {@code actual} to be {@code false} */
    protected void assumeFalse(String message, Boolean actual)
    { record(message, Status.EQUAL, false, () -> actual); }

    /** expect {@code actual} to returns {@code false} */
    protected void assumeFalse(String message, Executable actual)
    { record(message, Status.EQUAL, false, actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code true} when comparing 
     * {@code expected} with {@code actual} */
    protected void assumeEquals(Object expected, Object actual)                                       
    { assumeEquals(null, expected, actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code true} when comparing
     * {@code expected} with return value from {@code actual} */
    protected void assumeEquals(Object expected, Executable actual)                                   
    { assumeEquals(null, expected, actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code true} when comparing 
     * {@code expected} with {@code actual} */
    protected void assumeEquals(String message, Object expected, Object actual)
    { record(message, Status.EQUAL, expected, () -> actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code true} when comparing
     * {@code expected} with return value from {@code actual} */
    protected void assumeEquals(String message, Object expected, Executable actual)
    { record(message, Status.EQUAL, expected, actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code false} when comparing 
     * {@code expected} with {@code actual} */
    protected void assumeNotEquals(Object expected, Object actual)                                    
    { assumeNotEquals(null, expected, actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code false} when comparing 
     * {@code expected} with return value from {@code actual} */
    protected void assumeNotEquals(Object expected, Executable actual)                                
    { assumeNotEquals(null, expected, actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code false} when comparing 
     * {@code expected} with {@code actual} */
    protected void assumeNotEquals(String message, Object expected, Object actual)
    { record(message, Status.NOT_EQUAL, expected, () -> actual); }

    /** expect {@link java.lang.Object#equals(Object)} to returns {@code false} when comparing 
     * {@code expected} with return value from {@code actual} */
    protected void assumeNotEquals(String message, Object expected, Executable actual)
    { record(message, Status.NOT_EQUAL, expected, actual); }

    /** expect {@code expected} and {@code actual} have the same length and iteratively equals which 
     * determined by {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayEquals(Object[] expected, Object[] actual)                              
    { assumeArrayEquals(null, expected, actual); }

    /** expect {@code actual} to returns an array of {@link java.lang.Object} and when compared to 
     * {@code expected} will have the same length and iteratively equals which determined by 
     * {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayEquals(Object[] expected, Executable actual)                            
    { assumeArrayEquals(null, expected, actual); }

    /** expect {@code expected} and {@code actual} have the same length and iteratively equals which 
     * determined by {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayEquals(String message, Object[] expected, Object[] actual)
    { record(message, Status.ARRAY_EQUAL, expected, () -> actual); }

    /** expect {@code actual} to returns an array of {@link java.lang.Object} and when compared to 
     * {@code expected} will have the same length and iteratively equals which determined by 
     * {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayEquals(String message, Object[] expected, Executable actual)
    { record(message, Status.ARRAY_EQUAL, expected, actual); }

    /** expect {@code expected} and {@code actual} either have a different length or iteratively not 
     * equals which determined by {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayNotEquals(Object[] expected, Object[] actual)                           
    { assumeArrayNotEquals(null, expected, actual); }

    /** expect {@code actual} to returns an array of {@link java.lang.Object} and when compared to 
     * {@code expected} will have either a different length or iteratively not equals which determined by 
     * {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayNotEquals(Object[] expected, Executable actual)                         
    { assumeArrayNotEquals(null, expected, actual); }

    /** expect {@code expected} and {@code actual} either have a different length or iteratively not 
     * equals which determined by {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayNotEquals(String message, Object[] expected, Object[] actual)
    { record(message, Status.ARRAY_NOT_EQUAL, expected, () -> actual); }

    /** expect {@code actual} to returns an array of {@link java.lang.Object} and when compared to 
     * {@code expected} will have either a different length or iteratively not equals which determined by 
     * {@link java.lang.Object#equals(Object)} */
    protected void assumeArrayNotEquals(String message, Object[] expected, Executable actual)
    { record(message, Status.ARRAY_NOT_EQUAL, expected, actual); }

    /** run benchmark and check the assumption using comparator,
     * comparator will take 2 args from {@code references} and return value from {@code actual}
     * @param reference argument passed to comparator
     * @param actual contain statement or function to benchmark and as an argument passed to comparator
     * @param message information about the assumption
     * @param comparison type of comparison expected from references and actual
     * @param <T> type of object that evaluated
     */
    private <T> void record(String message, Status comparison, T reference, Executable exec)
    {
    	Metric metric    = new Metric();
    	metric.status    = comparison;
    	metric.reference = reference;
    	
    	BiFunction<Object, Object, Boolean> cpEqual    = (a, b) -> a.equals(b);
    	BiFunction<Object, Object, Boolean> cpRef      = (a, b) -> a == b;
    	BiFunction<Object, Object, Boolean> cpArrEqual = (a, b) -> {
            if (a.getClass() != b.getClass() || !a.getClass().isArray())
            	return false;
            Object[] ar1 = (Object[]) a;
            Object[] ar2 = (Object[]) b;
            if (ar1.length != ar2.length)
            	return false;
            for (int i = 0; i < ar1.length; ++i)
            	if (ar1[i].equals(ar2) == false)
            		return false;
            return true;
    	};
    	
    	try 
    	{
            metric.nanoTime = System.nanoTime();
            metric.actual   = exec.execute();
            switch (comparison)
            {
	        case EQUAL:           	metric.correct = cpEqual.apply(metric.actual, reference); break;
	        case NOT_EQUAL:      	metric.correct = !cpEqual.apply(metric.actual, reference); break;
	        case REFERENCE:       	metric.correct = cpRef.apply(metric.actual, reference); break;
	        case NOT_REFERENCE:   	metric.correct = !cpRef.apply(metric.actual, reference); break;
	        case ARRAY_EQUAL:     	metric.correct = cpArrEqual.apply(metric.actual, reference); break;
	        case ARRAY_NOT_EQUAL:	metric.correct = !cpArrEqual.apply(metric.actual, reference); break;
	        default: break;
            }
        }
        catch (Throwable t)
    	{ 
        	metric.actual   = t; 
        	metric.throwing = true;
        	switch (comparison)
        	{
        	case THROWS:			metric.correct = true;
        	case THROWS_TYPE:		metric.correct = t.getClass() == (Class<?>) reference;
        	default: break;
        	}
        }
        finally
        { 
        	metric.nanoTime = System.nanoTime() - metric.nanoTime;
        	listener.listener(metric);
        }
    }
}