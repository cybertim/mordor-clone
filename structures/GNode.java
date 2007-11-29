package structures;

/**
 * abstract GNode Class.
 * 
 * @author August Junkala; B00443890
 *
 */
public abstract class GNode<E, M>
{
	protected E element;
	protected M marker;
	
	/**
	 * Default Constructor
	 *
	 */
	public GNode()
	{
	}
	
	/**
	 * Secondary Constructor.
	 * 
	 * @param nElement	Object
	 * @param nMarker	Object
	 */
	public GNode(E nElement, M nMarker)
	{
		element = nElement;
		marker = nMarker;
	}
	
	/**
	 * Retrives the element stored in the node.
	 * 
	 * @return	Object
	 */
	public E getElement()
	{
		return element;
	}
	
	/**
	 * Retrives the marker for the node.
	 * 
	 * @return	Object
	 */
	public M getMarker()
	{
		return marker;
	}
	
	/**
	 * Sets the element for the node.
	 * 
	 * @param nElement	Object
	 */
	public void setElement(E nElement)
	{
		element = nElement;
	}
	
	/**
	 * Sets the marker for the node.
	 * 
	 * @param nMarker	Object
	 */
	public void setMarker(M nMarker)
	{
		marker = nMarker;
	}
}
