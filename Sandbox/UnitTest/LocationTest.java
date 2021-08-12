package Sandbox.UnitTest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.Reflector.*;
import com.NetlabUT.*;

// mapping class dengan menggunakan Reflector, jangan lupa "import java.lang.reflect"
// disini kita juga akan extends ReflectorUnitTest dan beri definisi pada method scenario
// sebagai entry point untuk melakukan unit test
public class LocationTest extends ReflectorUnitTest
{
    private final ClassR Location;

    private final Field city;
    private final Field province;
    private final Field country;

    private final Constructor ctor0;
    private final Constructor ctor1;

    private final Method getCity;
    private final Method getProvince;
    private final Method isIndonesia;

    public LocationTest()
    {
        // package name adalah tempat dimana class berada. seperti contohnya
        // class "String" yang berada dalam package "java.lang".
        this.Location       = new ClassR(Source.getPackageName(), "Location");
        // meminta field yang dideklarasi dalam class Location,
        // alternatifnya menggunakan getField(), yang hanya membolehkan
        // untuk meminta field yang bersifat accessible (public).
        // baiknya selalu gunakan getDeclaredField() untuk mengantisipasi access modifier yg tak pasti.
        this.city           = Location.getDeclaredField("city");
        this.province       = Location.getDeclaredField("province");
        this.country        = Location.getDeclaredField("country");
        // meminta constructor pada class Location
        // sesuaikan argumen pada getConstructor() sesuai dengan parameter ctor yang diharapkan
        this.ctor0          = Location.getConstructor();
        this.ctor1          = Location.getConstructor(String.class, String.class, String.class);
        // meminta method yang dideklarasi dalam class Location,
        // alternatifnya menggunakan getMethod(), yang hanya membolehkan
        // untuk meminta method yang bersifat accessible (public).
        // baiknya selalu gunakan getDeclaredField() untuk mengantisipasi access modifier yg tak pasti.
        this.getCity        = Location.getDeclaredMethod("getCity");
        this.getProvince    = Location.getDeclaredMethod("getProvince");
        this.isIndonesia    = Location.getDeclaredMethod("isIndonesia", String.class);
        // gunakan untuk melihat detail dari Class dalam bentuk GUI (opsional)
        Class<?> cLocation  = Location.getContainingClass();
        if (cLocation != null)
            new ClassDetail(cLocation);
    }

    @Override
    protected void scenario()
    {
        // poin untuk deklarasi field, method, dan ctor sesuai dengan (nama, parameter)
        {
            assumeNotNull(city);
            try { Thread.sleep(1000); } catch (Throwable ignored) {}
            assumeNotNull(province);
            try { Thread.sleep(1000); } catch (Throwable ignored) {}
            assumeNotNull(country);
            try { Thread.sleep(1000); } catch (Throwable ignored) {}
            assumeNotNull(ctor0);
            try { Thread.sleep(1000); } catch (Throwable ignored) {}
            assumeNotNull(ctor1);
            try { Thread.sleep(1000); } catch (Throwable ignored) {}
            assumeNotNull(getCity);
            try { Thread.sleep(1000); } catch (Throwable ignored) {}
            assumeNotNull(getProvince);
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
            try { obj = getForceAccess(Location, isIndonesia, null); }
            catch (Throwable ignored) {}
            assumeTrue(isIndonesia, obj, "indonesia");
            assumeTrue(isIndonesia, obj, "IndoNesIa");
            assumeTrue(isIndonesia, obj, "INDONESIA");
            assumeFalse(isIndonesia, obj, "Japan");
        }
    }
    
}
