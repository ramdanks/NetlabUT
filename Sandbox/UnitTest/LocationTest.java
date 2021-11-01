package Sandbox.UnitTest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.NetlabUT.UnitTest.*;
import com.NetlabUT.annotations.*;

@NetlabTest
public class LocationTest
{
    @ReflectClass
    private Class<?> Location;

    @ReflectField(owner="Location")
    private Field city, province, country;
    
    @ReflectCtor(owner="Location", params={})
    private Constructor<?> ctor0;
    @ReflectCtor(owner="Location", params={String.class, String.class, String.class})
    private Constructor<?> ctor1;
    
    @ReflectMethod(owner="Location", params={})
    private Method getCity;
    @ReflectMethod(owner="Location", params={})
    private Method getProvince;
    @ReflectMethod(owner="Location", params={String.class})
    private Method isIndonesia;
    
    @Test
    void test()
    {
        assumeNotNull(Location);
        assumeNotNull(city);
        assumeNotNull(province);
        assumeNotNull(country);
    }
}
