package mordorData;

import java.util.Random;

import mordorHelpers.Util;
import structures.QuadNode;
import structures.SkipList;

public class ItemCloset
{
	private SkipList<Item> items;
	
	// All the available items include stud items.
	private SkipList<Item> itemsByCOA;
	private int levelKeys[];

	
	ItemCloset()
	{
		items = new SkipList<Item>();
		itemsByCOA = new SkipList<Item>();
		levelKeys = new int[1];
	}
	
	/**
	 * Retrieve the list of all items.
	 * @return SkipList<Item>
	 */
	public SkipList<Item> getItems()
	{
		return items;
	}
	
	/**
	 * Retrieves the maximum level of all the items.
	 * @return byte
	 */
	private byte findMaxLevel()
	{
		QuadNode<Item> iNode = items.firstNode();
		byte maxLevel = Util.NOTHING;
		
		while(iNode.getRight() != null)
		{
			if(iNode.getElement().getMinimumLevel() > maxLevel)
				maxLevel = iNode.getElement().getMinimumLevel();
			iNode = iNode.getRight();
		}
		
		return maxLevel;
	}
	
	/**
	 * Retrieve a skip list containing all the items ordered by level.
	 * @return	SkipList<Item>
	 */
	private SkipList<Item> getItemsByLevel()
	{
		SkipList<Item> levelList = new SkipList<Item>();
		QuadNode<Item> iNode = items.firstNode();
		
		while(iNode.getRight() != null)
		{
			levelList.insert(iNode.getElement(), (int)iNode.getElement().getMinimumLevel());
			iNode = iNode.getRight();
		}
		
		return levelList;
	}
	
	/**
	 * Perform anything necessary to finalize the item closet.
	 * For example, generate the SkipList of items by their COA value.
	 */
	public void finalizeItemCloset()
	{
		byte maxLevel = findMaxLevel();
		
		if(maxLevel < 0)
			return;
			
		/* 
		 * +1 to include 0 level
		 * +1 to include level beyond last level. Since each level ends at
		 * the key of the next level.
		 */
		levelKeys = new int[maxLevel + 2];
		
		QuadNode<Item> iNode = getItemsByLevel().firstNode();
		int currentKey = 0;
		int currentLevel = 0;
		
		while(iNode.getRight() != null)
		{
			// Using the sum of keys up to here, add this element w/ that key.
			itemsByCOA.insert(iNode.getElement(), currentKey);
			
			// If we have moved to a new level, also set the start key for
			// the new level to the same key
			if(currentLevel < iNode.getElement().getMinimumLevel())
			{
				currentLevel = iNode.getElement().getMinimumLevel();
				levelKeys[currentLevel] = currentKey;
			}
			
			// Finally, add this monsters COA to the current key.
			currentKey += iNode.getElement().getChance();
			
			iNode = iNode.getRight();
		}
		
		// Lastly, set the key for the level + 1(that is, the max key
		// for the whole list)
		levelKeys[currentLevel + 1] = currentKey;
	}
	
	/**
	 * Retrieves a random item up to the level specified.
	 * @param level		Level to retrieve up to
	 * @param monsterClasses	Acceptable item types. If null, all types allowed.
	 * @return	Item
	 */
	public Item getRandomItem(byte level, boolean itemTypes[])
	{
		Random random = new Random(System.currentTimeMillis());
		
		int count = 0;		// Tracks how many times we have passed through the loop
		Item item = null;	// Item we found.
		
		while(item == null && count < Util.MAXLOOPS)
		{
			item = itemsByCOA.findEarly(random.nextInt(levelKeys[level + 1]));
			
			// If we can choose any monster class, or this is a monsterclass we want, we are done.
			if(itemTypes == null || itemTypes[item.getItemType().value()])
				break;
			
			// Otherwise, try again. Increment count since we have done a loop.
			item = null;
			count++;
		}
		
		return item;
	}
	
	/**
	 * Retrieve any random item.
	 * @return	Item
	 */
	public Item getRandomAnyItem()
	{
		Random random = new Random(System.currentTimeMillis());
		
		QuadNode<Item> iNode = items.firstNode();
		for(int i = random.nextInt(items.getSize()); i > 0; i--, iNode = iNode.getRight());
		
		return iNode.getElement();
	}
}
