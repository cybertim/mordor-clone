package structures;

/**
 * New general node class.
 * @author August Junkala. March 25, 2007
 *
 * @param <E>	Element type.
 */
public abstract class GeneralNode<E>
{
	protected E element;
	
	/**
	 * Default constructor.
	 *
	 */
	public GeneralNode()
	{
		this(null);
	}
	
	/**
	 * Alternative constructor. Accepts an initial element.
	 * @param newElement
	 */
	public GeneralNode(E newElement)
	{
		element = newElement;
	}
	
	/**
	 * Retrieves the element stored here.
	 * @return	E
	 */
	public E getElement()
	{
		return element;
	}
	
	/**
	 * Sets the element stored here.
	 * @param newElement New element to store.
	 */
	public void setElement(E newElement)
	{
		element = newElement;
	}
}
