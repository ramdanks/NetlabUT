import com.Reflector.*;

public class RecruiterTest extends RefleftorUnitTest
{
    public RecruiterTest(PackageR packageR) { super(packageR); }

    @Override
    protected void scenario()
    {
        PackageR packageR = getPackageR();
        try {
            
            final ClassR Recruiter = packageR.getClass("Recruiter");
            Object rec = Recruiter.newInstance();

            assumeEquals("Hello", Recruiter, "greet");
            assumeNull(Recruiter, rec, "secret");

        } catch (Exception e) { e.printStackTrace(); }
    }
}
