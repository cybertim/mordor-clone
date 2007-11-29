package structures;
/**
 * Interface for a skip lisst.
 * @author August Junkala. Student Number: B00443890
 *
 * @param <E> Element Type
 */
public interface SkipListInterface<E>
{
	public void insert(E nElement, Integer nKey);
	public E remove(Integer sKey);
	public E find(Integer sKey);
	public E first();
	public E last();
}
