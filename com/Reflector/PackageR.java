package com.Reflector;

public class PackageR
{
    private String packagePath;
    public PackageR(String path)
    {
        packagePath = path;
    }
    public ClassR getClass(String className) throws ClassNotFoundException {
        return new ClassR(Class.forName(packagePath + '.' + className));
    }
}
