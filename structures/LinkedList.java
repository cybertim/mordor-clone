package structures;

/**
 * New linked list class. Functions as a singly linked list (using insert/remove)
 * or a doubly linked list (using insertFirst/last, removeFirst/last).
 * Additional encapsulation over original linked list. Now stores a
 * recent node that is the last node that was accessed by the using
 * class. getnext/previous will set it to the next node after it
 * then retrieve the stored element. This means the using class does 
 * not need to handle ListNodes.
 * @author August Junkala. March 25, 2007
 *
 * @param <E>	Element type.
 */
public class LinkedList<E>
{
	private ListNode<E> firstNode;
	private ListNode<E> lastNode;
	private ListNode<E> recentNode;
	private Integer size;
	
	/**
	 * Default constructor.
	 *
	 */
	public LinkedList()
	{
		firstNode = lastNode = recentNode = null;
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
		
		recentNode = null;
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
		
		if(firstNode == null)
		{
			firstNode = new ListNode<E>(newElement, null, null);
			lastNode = firstNode;
			
			size = 1;
			return;
		}
		
		ListNode<E> newNode = new ListNode<E>(newElement, firstNode, null);
		firstNode.setPrevious(newNode);
		firstNode = newNode;
		
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
		
		if(lastNode == null)
		{
			firstNode = new ListNode<E>(newElement, null, null);
			lastNode = firstNode;
			
			size = 1;
			return;
		}
		
		ListNode<E> newNode = new ListNode<E>(newElement, null, lastNode);
		lastNode.setNext(newNode);
		lastNode = newNode;
		
		size += 1;
	}
	
	/**
	 * Retrieves the actual list node holding the first element.
	 * Advantage: Quicker parsing of list. Avoids problems w/ multiple instances
	 * 
	 * @return	ListNode<E>
	 */
	public ListNode<E> getFirstNode()
	{
		return firstNode;
	}
	
	/**
	 * Retrieves the actual list node holding the last element.
	 * Provides a performance boost over use of recent suite when
	 * list will be accessed several time (necessitating use of getNext(E))
	 * Warning: Gives direct access to list.
	 * 
	 * @return	ListNode<E>
	 */
	public ListNode<E> getLastNode()
	{
		return lastNode;
	}
	
	/**
	 * Retrieves the element stored at the start of the list.
	 * @return	E
	 */
	public E getFirst()
	{
		if(isEmpty())
			return null;
		
		recentNode = firstNode;
		return firstNode.getElement();
	}
	
	/**
	 * Retrieves the element stored at the end of the list.
	 * @return	E
	 */
	public E getLast()
	{
		if(isEmpty())
			return null;
		
		recentNode = lastNode;
		return lastNode.getElement();
	}
	
	/**
	 * Retrieves the element following the last element retrieved.
	 * @return E or null
	 */
	public E getNext()
	{
		if(recentNode == null)
			return null;
		
		recentNode = recentNode.getNext();
		
		return (recentNode == null) ? null : recentNode.getElement();
	}
	
	/**
	 * Retrieves the element following the provided element.
	 * @param searchElement	The provided element.
	 * @return	E
	 */
	public E getNext(E searchElement)
	{
		recentNode = findNode(searchElement);
		
		if(recentNode == null)
			return null;
		
		recentNode = recentNode.getNext();
		
		return (recentNode == null) ? null : recentNode.getElement();
	}
	
	public E get(E e)
	{
		recentNode = findNode(e);
		
		return (recentNode == null) ? null : recentNode.getElement();
	}
	
	/**
	 * Retrieves the element preceding the last element retrieved.
	 * @return E or null
	 */
	public E getPrevious()
	{
		if(recentNode == null)
			return null;
		
		recentNode = recentNode.getPrevious();
		return (recentNode == null) ? null :  recentNode.getElement();
	}
	
	/**
	 * Retrieves the element preceding the first instance of the 
	 * provided element.
	 * @param searchElement The provided element.
	 * @return	E
	 */
	public E getPrevious(E searchElement)
	{
		recentNode = findNode(searchElement);
		
		if(recentNode == null)
			return null;
		
		recentNode = recentNode.getPrevious();
		
		return (recentNode == null) ? null : recentNode.getElement();
	}
	
	/**
	 * Retrieves the element mostly recently retrieved.
	 * @return	E or null
	 */
	public E getRecent()
	{
		return recentNode.getElement();
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
	 * @return E
	 */
	public E removeFirst()
	{
		if(firstNode == null)
			return null;
		
		ListNode<E> oldFirst = firstNode;
		E oldElement = oldFirst.getElement();
		
		firstNode = oldFirst.getNext();
		oldFirst.setNext(null);
		if(firstNode != null)
			firstNode.setPrevious(null);
		
		size -= 1;
		
		return oldElement;
	}
	
	/**
	 * Removes the last element in the list and returns it.
	 * @return E
	 */
	public E removeLast()
	{
		if(lastNode == null)
			return null;
		
		ListNode<E> oldLast = lastNode;
		E oldElement = oldLast.getElement();
		
		lastNode = oldLast.getPrevious();
		oldLast.setPrevious(null);
		if(lastNode != null)
			lastNode.setNext(null);
		
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
		if(oldNode == lastNode)
			return removeLast();
		if(oldNode == firstNode)
			return removeFirst();
		
		ListNode<E> prevNode = oldNode.getPrevious();
		ListNode<E> nextNode = oldNode.getNext();
		
		if(prevNode != null)
			prevNode.setNext(nextNode);
		if(nextNode != null)
			nextNode.setPrevious(prevNode);
		
		oldNode.setNext(null);
		oldNode.setPrevious(null);
		
		size -= 1;
		
		return oldNode.getElement();
	}
	
	public E remove(ListNode<E> dNode)
	{
		if(dNode == firstNode)
			return removeFirst();
		if(dNode == lastNode)
			return removeLast();
		
		E oldElement = dNode.getElement();
		
		ListNode<E> pNode = dNode.getPrevious();
		ListNode<E> nNode = dNode.getNext();
		
		dNode.setPrevious(null);
		dNode.setNext(null);
		
		pNode.setNext(nNode);
		nNode.setPrevious(pNode);
		
		size -= 1;
		
		return oldElement;
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
		ListNode<E> tempNode = firstNode;
		
		while(tempNode != null)
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
		ListNode<E> tempNode = firstNode;
		
		while(tempNode != null)
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
		if(size == 0)
			return null;
		
		LinkedList<E> newList = new LinkedList<E>();
		
		E recentElement = getFirst();
		
		newList.insertFirst(recentElement);
		
		if(size == 1)
			return newList;
		
		while(true)
		{
			recentElement = getNext();
			
			if(recentElement == null)
				break;
			
			newList.insertLast(recentElement);
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
		
		E thisList = getFirst();
		E thatList = testList.getFirst();
		
		while(true)
		{
			if(!thisList.equals(thatList))
				return false;
			
			thisList = getNext();
			thatList = testList.getNext();
			
			if(thisList == null)
				break;
		}
		
		return true;
	}
}
