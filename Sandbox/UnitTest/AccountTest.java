package Sandbox.UnitTest;

import com.NetlabUT.annotations.*;
import static com.NetlabUT.UnitTest.*;

import java.lang.reflect.Field;

@NetlabTest
public class AccountTest
{
    @ReflectClass
    Class<?> Account;

    @ReflectField(owner="Account")
    Field name, email, password;

    @Test
    void test()
    {
        assumeNotNull(Account);
        assumeNotNull(name);
        assumeNotNull(email);
        assumeNotNull(password);

        assumeEquals(String.class, () -> name.getType());
        assumeEquals(String.class, () -> email.getType());
        assumeEquals(String.class, () -> password.getType());
    }
}
