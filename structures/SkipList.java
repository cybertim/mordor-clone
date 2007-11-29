package structures;
import java.util.Random;


/**
 * Implementation of the skip list interface.
 * @author August Junkala. Student Number B00443890
 *
 */
public class SkipList<E> implements SkipListInterface<E>
{
	private int size;
	QuadNode<E> first, last;
	
	public SkipList()
	{
		size = 0;
		first = new QuadNode<E>(null, Integer.MIN_VALUE);
		last = new QuadNode<E>(null, Integer.MAX_VALUE);
		
		first.setRight(last);
		last.setLeft(first);
	}
	
	public int getSize()
	{
		return size;
	}
	
	public boolean isEmpty()
	{
		return (size == 0);
	}
	
	/**
	 * Retrieves the element with the smallest key.
	 * @return E 
	 */
	public E first()
	{
		QuadNode<E> currentNode = firstNode();
		return (currentNode != null) ? currentNode.getElement() : null;
	}
	
	/**
	 * Retrieves the element with the highest key.
	 * @return E
	 */
	public E last() 
	{	
		QuadNode<E> currentNode = lastNode();
		return (currentNode != null) ? currentNode.getElement() : null;
	}
	
	/**
	 * Retrieves the node of the first element in the list.
	 * @return QuadNode<E> 
	 */
	public QuadNode<E> firstNode()
	{
		QuadNode<E> currentNode = first;
		
		while(currentNode.getBelow() != null)
		{
			currentNode = currentNode.getBelow();
		}
		
		currentNode = currentNode.getRight();
		
		return (currentNode.getKey() != Integer.MAX_VALUE) ? currentNode : null;
	}
	
	/**
	 * Retrieves the node with the highest key.
	 * @return
	 */
	public QuadNode<E> lastNode()
	{
		QuadNode<E> currentNode = last;
		
		while(currentNode.getBelow() != null)
		{
			currentNode = currentNode.getBelow();
		}
		
		currentNode = currentNode.getLeft();

		return (currentNode.getKey() != Integer.MIN_VALUE) ? currentNode : null;
	}
	
	/**
	 * Locates an element with a specific key and retrieves it.
	 * @param sKey Integer
	 * @return E
	 */
	public E find(Integer sKey)
	{
		QuadNode<E> foundQuad = findQuad(sKey);
	
		return (foundQuad == null) ? null : foundQuad.getElement();
	}
	
	/**
	 * Works the same as Find except if the key does not exist, return
	 * the element immediately preceding it.
	 * @param sKey
	 * @return
	 */
	public E findEarly(Integer sKey)
	{
		QuadNode<E> currentNode = first;
		
		while(currentNode != null)
		{
			if(currentNode.getBelow() == null && currentNode.getKey() <= sKey && currentNode.getRight().getKey() > sKey)
				break;
			
			currentNode = (currentNode.getRight().getKey() > sKey) ? currentNode.getBelow() : currentNode.getRight();
		}
		
		if(currentNode == null)
			return null;
		
		while(currentNode.getBelow() != null)
			currentNode = currentNode.getBelow();
		
		return currentNode.getElement();
	}
	
	/**
	 * Locates an element with a specific key and retrieves the lowest
	 * quad in the list containing it.
	 * @param sKey
	 * @return
	 */
	private QuadNode<E> findQuad(Integer sKey)
	{
		QuadNode<E> currentNode = first;
		
		while(currentNode != null && currentNode.getKey().intValue() != sKey.intValue())
			currentNode = (currentNode.getRight().getKey() > sKey) ? currentNode.getBelow() : currentNode.getRight();
		
		if(currentNode == null)
			return null;
		
		while(currentNode.getBelow() != null)
			currentNode = currentNode.getBelow();
		
		return currentNode;
	}
	
	/**
	 * Inserts a new element into the skip list.
	 * Inserts duplicate keys after last one.
	 * @param nElement New element (E)
	 * @param nKey New elements key (Integer)
	 */
	public void insert(E nElement, Integer nKey) 
	{
		// find the node the will immediately precede the new node.
		
		// first, set the currentNode to the first node (top left corner)
		QuadNode<E> currentNode = first;
		
		// now, create a new node with the provided element and key.
		QuadNode<E> newNode = new QuadNode<E>(nElement, nKey);
		
		// now search 
		while(true)
		{
			// first, if the current node is not the nearest one
			// then move to the right
			if(currentNode.getRight().getKey() <= nKey)
				currentNode = currentNode.getRight();
			// if the next one is more than the key, and this node has
			// ones below it, go down.
			else if(currentNode.getRight().getKey() > nKey && currentNode.getBelow() != null)
				currentNode = currentNode.getBelow();
			
			// otherwise, we are as far down and are at the nodes new
			// left neighbour.
			else
				break;
		}
		
		// now insert the new node into the bottom row.
		
		// first, create a new node to hold the present right neighbour
		// of the current node.
		QuadNode<E> rightNode = currentNode.getRight();
		// now set up the new node so it's left is the current
		// node and it's right is the right node, effectively inserting it
		newNode.setLeft(currentNode);
		newNode.setRight(rightNode);
		
		// now update the left & right nodes.
		currentNode.setRight(newNode);
		rightNode.setLeft(newNode);
		
		// and increase the size
		size += 1;
		
		// next we must propogate this node up.
		// this will be done based on a random value.
		Random rand = new Random(System.nanoTime());
		
		// it won't run  if the new Node is in the top row.
		while(rand.nextBoolean())
		{
			// since it was true, we are going to propogate the
			// node further up.
			
			// first, get currentNode to point to the nearest up node
			
			currentNode = newNode.getLeft();
			// if the newNode has first as its left neighbour,
			// we are at the top already, exit.
			if(currentNode == first)
				break;
			
			// keep moving left until currentNode has a node above 
			// it
			while(currentNode.getAbove() == null)
				currentNode = currentNode.getLeft();
			
			// move up to the above node
			currentNode = currentNode.getAbove();
			
			// now we basically what we did before
			// set right node to the node right of current node.
			rightNode = currentNode.getRight();
			
			// create a new node to hold the old new node.
			QuadNode<E> lowerNode = newNode;
			
			// now replicate the newNode
			newNode = newNode.copyNode();
			
			// first, adjust new node to point to all the other nodes.
			newNode.setLeft(currentNode);
			newNode.setRight(rightNode);
			newNode.setBelow(lowerNode);
			
			// now adjust them to point to it.
			currentNode.setRight(newNode);
			rightNode.setLeft(newNode);
			lowerNode.setAbove(newNode);
		}
		
		// finally, if we are at the top, we must add a new
		// layer so that the top layer is only first & last
		if(first.getRight() != last)
		{
			// first, create nodes to hold the old first/last nodes
			QuadNode<E> lowerFirst = first;
			QuadNode<E> lowerLast = last;
			
			// now replicate the nodes
			first = first.copyNode();
			last = last.copyNode();
			
			// adjust the new first nodes to point to each other
			// and their new lower nodes.
			first.setRight(last);
			first.setBelow(lowerFirst);
			
			last.setLeft(first);
			last.setBelow(lowerLast);
			
			// now make the lower nodes point up.
			lowerFirst.setAbove(first);
			lowerLast.setAbove(last);
		}
	}
	
	/**
	 * Removes the first element with a specific key.
	 * @param sKey The key IDing the node to be removed (Integer)
	 * @return E
	 */
	public E remove(Integer sKey) 
	{
		QuadNode<E> currentNode = findQuad(sKey);
		if(currentNode == first || currentNode == null || currentNode == last)
			return null;
		
		size -= 1;
		
		E oldElement = currentNode.getElement();
		
		while(currentNode != null)
		{
			QuadNode<E> tempNode = currentNode;
			currentNode = currentNode.getAbove();
			
			tempNode.getLeft().setRight(tempNode.getRight());
			tempNode.getRight().setLeft(tempNode.getLeft());
			
			tempNode.setBelow(null);
			tempNode.setAbove(null);
		}
		
		return oldElement;
	}
	
	public E removeFirst()
	{
		QuadNode<E> tNode = first;
		
		if(isEmpty())
			return null;
		
		while(tNode.getBelow() != null)
			tNode = tNode.getBelow();
		
		tNode = tNode.getRight();
		
		return remove(tNode.getKey());
	}
	
	public Integer getFirstKey()
	{
		QuadNode<E> tNode = first;
		
		if(isEmpty())
			return null;
		
		while(tNode.getBelow() != null)
			tNode = tNode.getBelow();
		
		tNode = tNode.getRight();
		
		return tNode.getKey();
	}
	
	/**
	 * Remove all elements from this list.
	 */
	public void clearList()
	{
		while(size > 0)
			removeFirst();
	}
}
