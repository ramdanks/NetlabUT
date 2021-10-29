package Sandbox.UnitTest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.NetlabUT.UnitTest.*;
import com.NetlabUT.annotations.*;

@NetlabReflectTest("Location")
public class LocationTest
{
    @ReflectField
    private Field city;
    @ReflectField
    private Field province;
    @ReflectField
    private Field country;
    
    @ReflectCtor(params={})
    private Constructor<?> ctor0;
    @ReflectCtor(params={String.class, String.class, String.class})
    private Constructor<?> ctor1;
    
    @ReflectMethod(params={})
    private Method getCity;
    @ReflectMethod(params={})
    private Method getProvince;
    @ReflectMethod(params={String.class})
    private Method isIndonesia;
    
    @Test
    void test()
    {
        assumeNotNull(city);
        assumeNotNull(province);
        assumeNotNull(country);
    }
}
