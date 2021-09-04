package com.Reflector;
public interface MonoPackageTester <T extends ReflectorUnitTest>
{
    T newInstance();
    void obtainPackage(String packageName);
}