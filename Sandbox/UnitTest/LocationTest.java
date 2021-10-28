package Sandbox.UnitTest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.Reflector.*;
import com.NetlabUT.*;

// mapping class dengan menggunakan Reflector, jangan lupa "import java.lang.reflect"
// disini kita juga akan extends ReflectorUnitTest dan beri definisi pada method scenario
// sebagai entry point untuk melakukan unit test
@ReflectTester("Location")
public class LocationTest
{
    @ReflectField
    private Field city;
    @ReflectField
    private Field province;
    @ReflectField
    private Field country;
    
    @ReflectCtor
    private Constructor<?> ctor0;
    @ReflectCtor(params={String.class, String.class, String.class})
    private Constructor<?> ctor1;
    
    @ReflectMethod(params={})
    private Method getCity;
    @ReflectMethod(params={})
    private Method getProvince;
    @ReflectMethod(params={String.class})
    private Method isIndonesia;
    
    @ReflectTest
    void modifier() throws Exception { assumeModifier(ReflectorModifier.PUBLIC, getCity);  }

    @Override
    protected void scenario()
    {

        // poin untuk modifier yang sesuai
        {
            String msg = "Modifier ";
            assumeModifier(msg + "getCity", ReflectorModifier.PUBLIC, getCity);
            assumeModifier(msg + "getProvince", ReflectorModifier.PUBLIC, getProvince);
            assumeModifier(msg + "isIndonesia", ReflectorModifier.PUBLIC, isIndonesia);
        }

        // poin untuk return type method yang sesuai
        {
            // assumeSame akan menggunakan operand ==
            assumeSame(java.lang.String.class, () -> getCity.getReturnType());
            // assumeEquals menggunakan fungsi java.lang.Object.equals(Object)
            assumeEquals(java.lang.String.class, () -> getProvince.getReturnType());
            // ingat bahwa return value dari method ClassR bisa "null".
            // hindari mengaksesnya secara langsung karena dapat menimbulkan "java.lang.NullPointerException".
            // gunakan Lambda dalam mengakses method assumeXXX yang tidak menerima arg java.lang.reflect.Method.
        }
        
        // instansiasi objek dan cek kebenaran method
        {
            // instansiasi object dengan menggunakan ctor1. Sesuaikan juga args sesuai dengan
            // yang diharapkan oleh ctor1 pada saat di-assign menggunakan getConstructor()
            Object myLocation = Location.newInstance(ctor1, "City", "Province", "Country");
            // lakukan casting untuk memastikan fungsi yang digunakan sesuai
            // ada beberapa overload fungsi assumeEquals serta assumeXXX lainnya.
            // dalam kasus ini, yang ingin kita lakukan adalah mengevaluasi return type dari
            // sebuah "reflect" method kemudian membandingkannya dengan arg "expected" yang bernilai "City"
            assumeEquals((Object) "City", getCity, myLocation);
            assumeNotEquals((Object) "Kecamatan", getProvince, myLocation);

            // instansiasi object dengan ctor0 yang merupakan default ctor. Karena default ctor
            // tidak perlu arg, maka kosongkan juga arg pada fungsi newInstance().
            // alternatif instansiasi dengan default ctor juga dapat dicapai dengan:
            // Object defLocation = Location.newInstance();
            Object defLocation = Location.newInstance(ctor0);
            assumeNull(getCity, defLocation);
            assumeNull(getProvince, defLocation);

            // lakukan hal yang mustahil dilakukan seperti memaksa private field
            // untuk di mutasi nilainya. Ini dapat kita manfaatkan untuk memastikan
            // kebenaran method seperti kegunaan dari method "accessor".
            // alternatifnya gunakan method setField() dimana hanya berlaku pada public field
            Location.setFieldForce(city, defLocation, "Jakarta");
            assumeEquals((Object) "Jakarta", getCity, defLocation);

            // static method maka tidak diperlukan sebuah instantiation untuk mengaksesnya.
            // arg object dapat berupa null atau object dengan tipe yang sama.
            // tetapi sangat disarankan untuk diset menjadi null agar dapat dipastikan
            // bahwa fungsi ini benar-benar 'static' dan kode lebih mudah dibaca.
            Object obj = null;
            // meminta object yang dapat mengakses lokasi tersebut (apabila non static).
            // Ini juga memaksa akses terhadap method apabila memiliki modifier private.
            try { obj = ClassR.getForceAccess(isIndonesia, null); }
            catch (Throwable ignored) {}
            assumeTrue(isIndonesia, obj, "indonesia");
            assumeTrue(isIndonesia, obj, "IndoNesIa");
            assumeTrue(isIndonesia, obj, "INDONESIA");
            assumeFalse(isIndonesia, obj, "Japan");
        }
    }
}
