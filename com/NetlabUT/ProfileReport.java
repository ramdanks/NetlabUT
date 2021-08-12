package com.NetlabUT;

/** interface for {@link UnitTest} to send a report of {@link Profile} after being recorded.
 * @author Ramadhan Kalih Sewu
 * @version 1.1
 */
@FunctionalInterface
public interface ProfileReport<T> { void report(Profile<T> profile); }
