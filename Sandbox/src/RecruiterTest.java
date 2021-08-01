import com.Reflector.ReflectorUnitTest;
import com.Reflector.ClassR;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class RecruiterTest extends ReflectorUnitTest
{
    @Override
    protected void scenario()
    {
        final String packageName = Sandbox.getPackageName();
        ClassR Recruiter = new ClassR(packageName, "Recruiter");

        Constructor<?> defCtor = Recruiter.getConstructor();
        final Object rec1 = Recruiter.newInstance(defCtor);

        Constructor<?> c = Recruiter.getConstructor(int.class, char.class);
        final Object rec2 = Recruiter.newInstance(c, 5, 'e');

        Method add = Recruiter.getMethod("add", int.class, int.class);
        Method secret = Recruiter.getMethod("secret");
        Method greet = Recruiter.getMethod("greet");
        Method throwing = Recruiter.getMethod("throwing");

        assumeEquals(10, add, rec1);
        assumeThrows(throwing, rec1);

        assumeThrows(secret, null);
    }
}
