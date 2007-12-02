package structures;

/**
 * New linked list class. Functions as a singly linked list (using insert/remove)
 * or a doubly linked list (using insertFirst/last, removeFirst/last).
 * @author August Junkala. March 25, 2007
 *
 * @param <E>	Element type.
 */
public class LinkedList<E>
{
	private ListNode<E> firstNode;
	private ListNode<E> lastNode;
	private Integer size;
	
	/**
	 * Default constructor.
	 *
	 */
	public LinkedList()
	{
		firstNode = new ListNode<E>(null);
		lastNode = new ListNode<E>(null);
		firstNode.setNext(lastNode);
		lastNode.setPrevious(firstNode);
		size = 0;
	}
	
	/**
	 * Array constructor. Constructs a new linked list and sets its
	 * starting elements to a provided list.
	 * @param newNodes Array of elements (E)
	 */
	public LinkedList(E[] newNodes)
	{
		size = 0;
		for(int i = 0; i < newNodes.length; i++)
		{
			if(newNodes[i] != null)
			{
				insertLast(newNodes[i]);
				size += 1;
			}
		}
	}
	
	/**
	 * Retrieves the size of the linked list.
	 * @return Integer
	 */
	public Integer getSize()
	{
		return size;
	}
	
	/**
	 * Tests if the list is empty.
	 * @return boolean
	 */
	public boolean isEmpty()
	{
		return (size == 0);
	}
	
	/**
	 * Inserts an element at the end of the list.
	 * @param newElement	Element to be inserted (E)
	 */
	public void insert(E newElement)
	{
		insertLast(newElement);
	}
	
	/**
	 * Inserts an element at the beginning of the list.
	 * @param newElement	Element to be inserted (E)
	 */
	public void insertFirst(E newElement)
	{
		if(newElement == null)
			return;
		
		// Create a new node between the firstNode and second node.
		ListNode<E> newNode = new ListNode<E>(newElement, firstNode.getNext(), firstNode);
		// Update the second node.
		firstNode.getNext().setPrevious(newNode);
		// Update the first node.
		firstNode.setNext(newNode);
		
		size += 1;
	}
	
	/**
	 * Inserts an element at the end of the list.
	 * @param newElement	Element to be inserted (E)
	 */
	public void insertLast(E newElement)
	{
		if(newElement == null)
			return;
		
		// Create a new node between the second last node and lastNode. 
		ListNode<E> newNode = new ListNode<E>(newElement, lastNode, lastNode.getPrevious());
		// Update the second last node.
		lastNode.getPrevious().setNext(newNode);
		// Update the lastNode
		lastNode.setPrevious(newNode);
		
		size += 1;
	}
	
	/**
	 * Retrieve a starting iterator for this list.
	 * @return ListIter<E> or null if list is empty
	 */
	public ListIter<E> getIterator()
	{
		return new ListIter<E>(firstNode);
	}
	
	/**
	 * Retrieve an iterator starting at the end.
	 * @return ListIter<E> or null if list is empty
	 */
	public ListIter<E> getReverseIterator()
	{
		return new ListIter<E>(lastNode);
	}
	
	/**
	 * Retrieves the element stored at the start of the list.
	 * @return	E
	 */
	public E getFirst()
	{
		if(isEmpty())
			return null;
		
		return firstNode.getNext().getElement();
	}
	
	/**
	 * Retrieves the element stored at the end of the list.
	 * @return	E
	 */
	public E getLast()
	{
		if(isEmpty())
			return null;
		
		return lastNode.getPrevious().getElement();
	}
	
	/**
	 * Removes the first element in the list and returns it.
	 * @return E
	 */
	public E remove()
	{
		return removeFirst();
	}
	
	/**
	 * Removes the first element in the list and returns it.
	 * @return E or null if empty
	 */
	public E removeFirst()
	{
		if(isEmpty())
			return null;
		
		ListNode<E> deadNode = firstNode.getNext();
		E oldElement = deadNode.element;
		
		firstNode.setNext(deadNode.getNext());
		deadNode.getNext().setPrevious(firstNode);
		
		size -= 1;
		return oldElement;
	}
	
	/**
	 * Removes the last element in the list and returns it.
	 * @return E
	 */
	public E removeLast()
	{
		if(isEmpty())
			return null;
		
		ListNode<E> deadNode = lastNode.getPrevious();
		E oldElement = deadNode.getElement();
		
		lastNode.setPrevious(deadNode.getPrevious());
		deadNode.getPrevious().setNext(lastNode);
		
		size -= 1;
		return oldElement;
	}
	
	/**
	 * Remove a specific element from the list.
	 * @param oldElement	Element to be removed.
	 * @return E
	 */
	public E remove(E oldElement)
	{
		ListNode<E> oldNode = findNode(oldElement);
		
		if(oldNode == null)
			return null;
		
		ListNode<E> prevNode = oldNode.getPrevious();
		ListNode<E> nextNode = oldNode.getNext();
		
		prevNode.setNext(nextNode);
		nextNode.setPrevious(prevNode);
		
		size -= 1;
		
		return oldNode.getElement();
	}
	
	/**
	 * Determines if an element exists in the list.
	 * @param searchElement Element being searched for (E)
	 * @return	boolean
	 */
	public boolean containsElement(E searchElement)
	{
		if(findNode(searchElement) != null)
			return true;
		
		return false;
	}
	
	/**
	 * Searches for the node containing a specific element instance. If it
	 * doesn't exist, uses findNodeB to find a node contains the same
	 * element (same element based on equals())
	 * @param searchElement	Element being searched for (E)
	 * @return	ListNode<E>
	 */
	private ListNode<E> findNode(E searchElement)
	{
		ListNode<E> tempNode = firstNode.getNext();
		
		while(tempNode != lastNode)
		{
			if(tempNode.getElement() == searchElement)
				return tempNode;
			
			tempNode = tempNode.getNext();
		}
		
		return findNodeB(searchElement);
	}
	
	/**
	 * Searches the list for a node containing an element that equals
	 * the provided element. Note: Returns the first node with an
	 * equivalent element. findNode is recommended instead.
	 * @param searchElement	Element being searched for (E)
	 * @return	ListNode<E>
	 */
	private ListNode<E> findNodeB(E searchElement)
	{
		ListNode<E> tempNode = firstNode.getNext();
		
		while(tempNode != lastNode)
		{
			if(tempNode.getElement().equals(searchElement))
				return tempNode;
			
			tempNode = tempNode.getNext();
		}
		
		return null;
	}
	
	/**
	 * Replicates this list and returns an exact copy.
	 * @return Linkedlist<E>
	 */
	public LinkedList<E> clone()
	{
		LinkedList<E> newList = new LinkedList<E>();
		ListNode<E> node = firstNode.getNext();
		
		while(node != lastNode)
		{
			newList.insertLast(node.element);
			node = node.getNext();
		}
		
		return newList;
	}
	
	/**
	 * Determines whether a list is equal to this one.
	 * @param testList	The list to be tested (LinkedList<E>)
	 * @return boolean
	 */
	public boolean equals(LinkedList<E> testList)
	{
		if(testList.getSize() != size)
			return false;
		
		ListNode<E> thisNode = firstNode.getNext();
		ListNode<E> thatNode = testList.firstNode.getNext();
		
		while(thisNode != lastNode)
		{
			if(thisNode.element != null && !thisNode.getElement().equals(thatNode.getElement()))
				return false;
			
			thisNode = thisNode.getNext();
			thatNode = thatNode.getNext();
		}
		
		return true;
	}
}