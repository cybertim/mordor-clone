package structures;

/**
 * Iterator interface for my own structures.
 * @author August Junkala, Dec. 1, 2007 
 *
 * @param <E>
 */
public interface Iterator<E>
{
	/**
	 * Move ahead an element.
	 * @return true if there was another element.
	 */
	public boolean next();
	
	/**
	 * Move back an element.
	 * @return true if there was another element.
	 */
	public boolean previous();
	
	/**
	 * Determine if this is the last element.
	 * @return true if this is the last element.
	 */
	public boolean last();
	
	/**
	 * Determine if this is the first element.
	 * @return true if this is the first element.
	 */
	public boolean first();
	
	/**
	 * Retrieve the element stored here.
	 * @return E
	 */
	public E element();
}
