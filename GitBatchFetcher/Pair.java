
/** class that contains two element
 * @author Ramadhan Kalih Sewu
 * @version 1.0
 */
public class Pair<T, U>
{
    public T first;
    public U second;
    public Pair(T first, U second)
    {
        this.first = first;
        this.second = second;
    }
    public Pair(Pair<T, U> other)
    {
        this.first = other.first;
        this.second = other.second;
    }
}
