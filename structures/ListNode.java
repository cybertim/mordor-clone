package structures;


/**
 * New List Node class.
 * @author August Junkala. March 25, 2007
 *
 * @param <E>	Element type.
 */
public class ListNode<E> extends GeneralNode<E>
{
	private ListNode<E> nextNode;
	private ListNode<E> previousNode;
	
	/**
	 * Default contstructor.
	 *
	 */
	public ListNode()
	{
		this(null);
	}
	
	/**
	 * Alternate constructor. Element but no previous/next nodes.
	 * @param newElement	The element to be stored.
	 */
	public ListNode(E newElement)
	{
		this(newElement, null, null);
	}
	
	/**
	 * Complete constructor.
	 * @param newElement	The element to be stored. (E)
	 * @param newNextNode	The next node to this one (ListNode<E>)
	 * @param newPrevNode	The previous node to this one (ListNode<E>)
	 */
	public ListNode(E newElement, ListNode<E> newNextNode, ListNode<E> newPrevNode)
	{
		super(newElement);
		nextNode = newNextNode;
		previousNode = newPrevNode;
	}
	
	/**
	 * Retrieve the node following this one.
	 * @return	ListNode<E>
	 */
	public ListNode<E> getNext()
	{
		return nextNode;
	}
	
	/**
	 * Retrieve the node preceding this one.
	 * @return	ListNode<E>
	 */
	public ListNode<E> getPrevious()
	{
		return previousNode;
	}
	
	/**
	 * Set the node following this one.
	 * @param newNextNode	The new node that will follow this one. (ListNode<E>)
	 */
	public void setNext(ListNode<E> newNextNode)
	{
		nextNode = newNextNode;
	}
	
	/**
	 * Set the node preceding this one.
	 * @param newPrevNode	The new node that will precede this one. (listNode<E>)
	 */
	public void setPrevious(ListNode<E> newPrevNode)
	{
		previousNode = newPrevNode;
	}
	
	/**
	 * Determine if a node is equal to this one. Returns true if it is.
	 * @param testNode	Node to compare (ListNode<E>)
	 * @return	boolean
	 */
	public boolean equals(ListNode<E> testNode)
	{
		return (element.equals(testNode.getElement()) && testNode.getNext() == nextNode && testNode.getPrevious() == previousNode);
	}
	
	/**
	 * Returns a new node that is an exact copy of this one.
	 * @return	ListNode<E>
	 */
	public ListNode<E> clone()
	{
		return (new ListNode<E>(element, nextNode, previousNode));
	}
}
