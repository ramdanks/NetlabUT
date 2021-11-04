package com.NetlabUT;

/** interface for {@link Assumptions} to run a method that also handles a throwable
 * @author Ramadhan Kalih Sewu
 * @version 1.0
 */
@FunctionalInterface
public interface Executable<T> { T execute() throws Throwable; }