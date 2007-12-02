package structures;

/**
 * Iterator for LinkedList<E>
 * @author August Junkala, Dec. 1, 2007
 *
 * @param <E>
 */
public class ListIter<E> implements Iterator<E>
{
	// The current node.
	private ListNode<E> node;
	
	public ListIter(ListNode<E> list) { node = list; }

	public E element() { return node.element; }
	public boolean first() { return (node.getPrevious() == null || node.getPrevious().element == null); }
	public boolean last() { return (node.getNext() == null || node.getNext().element == null); }

	public boolean next()
	{
		if(last())
			return false;
		
		node = node.getNext();
		return true;
	}

	public boolean previous()
	{
		if(first())
			return false;
		
		node = node.getPrevious();
		return true;
	}

}
