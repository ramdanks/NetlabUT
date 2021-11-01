@FunctionalInterface
public interface Executable<P, R, T extends Throwable>
{
    R run(P obj) throws T;
}