package structures;

/**
 * Iterator for SkipList<E>
 * @author August Junkala, Dec. 1, 2007
 *
 * @param <E>
 */
public class SkipIter<E> implements Iterator<E>
{
	QuadNode<E> node;
	
	public SkipIter(QuadNode<E> list) { node = list; }

	public E element() { return node.element; }
	
	/**
	 * Retrieve the current key.
	 * @return int
	 */
	public int key() { return node.marker; }

	public boolean first() { return node.getLeft() == null || node.getLeft().element == null; }
	public boolean last() { return node.getRight() == null || node.getRight().element == null; }

	public boolean next()
	{
		if(last())
			return false;

		node = node.getRight();
		return true;
	}

	public boolean previous()
	{
		if(first())
			return false;
		
		node = node.getLeft();
		return true;
	}

}
