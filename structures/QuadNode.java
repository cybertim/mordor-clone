package structures;

/**
 * Multi use 4 directional node.
 * @author August Junkala. Student Number B00443890
 *
 * @param <E>	Element Type
 */
public class QuadNode<E> extends GNode<E, Integer> 
{
	private QuadNode<E> right, left, above, below;
	
	public QuadNode(E nElement, Integer nKey)
	{
		super(nElement, nKey);
		right = left = below = above = null;
	}
	
	/**
	 * Set the node to the left of this one.
	 * @param nLeft	QuadNode<E>
	 */
	public void setLeft(QuadNode<E> nLeft)
	{
		left = nLeft;
	}
	
	/**
	 * Set the node to the right of this one.
	 * @param nRight QuadNode<E>
	 */
	public void setRight(QuadNode<E> nRight)
	{
		right = nRight;
	}
	
	/**
	 * Set the node above this one.
	 * @param nAbove QuadNode<E>
	 */
	public void setAbove(QuadNode<E> nAbove)
	{
		above = nAbove;
	}
	
	/**
	 * Set the node below this one.
	 * @param nBelow QuadNode<E>
	 */
	public void setBelow(QuadNode<E> nBelow)
	{
		below = nBelow;
	}
	
	/**
	 * Retrieve the node to the left of this one.
	 * @return QuadNode<E>
	 */
	public QuadNode<E> getLeft()
	{
		return left;
	}
	
	/**
	 * Retrieve the node to the right of this one.
	 * @return QuadNode<E>
	 */
	public QuadNode<E> getRight()
	{
		return right;
	}
	
	/**
	 * Retrieve the node to above this one.
	 * @return QuadNode<E>
	 */
	public QuadNode<E> getAbove()
	{
		return above;
	}
	
	/**
	 * Retrieve the node below this one.
	 * @return QuadNode<E>
	 */
	public QuadNode<E> getBelow()
	{
		return below;
	}
	
	/**
	 * Retrieve the key for this node.
	 * @return Integer
	 */
	public Integer getKey()
	{
		return marker;
	}
	
	public QuadNode<E> copyNode()
	{
		QuadNode<E> newNode = new QuadNode<E>(element, marker);
		newNode.setLeft(left);
		newNode.setRight(right);
		newNode.setAbove(above);
		newNode.setBelow(below);
		
		return newNode;
	}
}
