package com.NetlabUT;

/** interface for {@link UnitTest} to evaluate the profile of unit test before it's going to be recorded.
 * the return value will override the "correctness" from {@link Profile}.
 * @author Ramadhan Kalih Sewu
 * @version 1.1
 */
@Deprecated
@FunctionalInterface
public interface ProfileFinalize<T> { boolean predicate(Profile<T> profile); }