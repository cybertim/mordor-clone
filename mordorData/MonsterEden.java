package mordorData;
import java.util.Random;

import mordorEnums.MonsterClass;
import mordorHelpers.Util;
import structures.LinkedList;
import structures.ListIter;
import structures.SkipIter;
import structures.SkipList;

/**
 * Data structure for holding collection of all monsters.
 * @author August Junkala. April 25, 2007
 *
 */
public class MonsterEden
{
	
	private SkipList<Monster> monsters;
	private SkipList<Monster> monstersByCOA; // List of monsters, keyed by sum of COA of monsters before them. 
	private int levelKeys[];
	
	MonsterEden()
	{
		monsters = new SkipList<Monster>();
		monstersByCOA = new SkipList<Monster>();
		levelKeys = new int[1];
	}
	
	/**
	 * Retrieves the maximum level of all the monsters.
	 * @return byte
	 */
	private byte findMaxLevel()
	{
		SkipIter<Monster> mNode = monsters.getIterator();
		byte maxLevel = Util.NOTHING;
		
		while(mNode.next())
			if(mNode.element().getLevel() > maxLevel)
				maxLevel = (byte)mNode.element().getLevel();
		
		return maxLevel;
	}
	
	/**
	 * Retrieve a skip list containing all the monsters ordered by level.
	 * @return	SkipList<Monster>
	 */
	private SkipList<Monster> getMonstersByLevel()
	{
		SkipList<Monster> levelList = new SkipList<Monster>();
		SkipIter<Monster> mNode = monsters.getIterator();
		
		while(mNode.next())
			levelList.insert(mNode.element(), (int)mNode.element().getLevel());
		
		return levelList;
	}
	
	/**
	 * Perform anything necessary to finalize the monster eden.
	 * For example, generate the SkipList of monsters by their COA value.
	 */
	public void finalizeMonsterEden()
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
		
		SkipIter<Monster> mNode = this.getMonstersByLevel().getIterator();
		int currentKey = 0;
		int currentLevel = 0;
		
		while(mNode.next())
		{
			// Using the sum of keys up to here, add this element w/ that key.
			monstersByCOA.insert(mNode.element(), currentKey);
			
			// If we have moved to a new level, also set the start key for
			// the new level to the same key
			if(currentLevel < mNode.element().getLevel())
			{
				currentLevel = mNode.element().getLevel();
				levelKeys[currentLevel] = currentKey;
			}
			
			// Finally, add this monsters COA to the current key.
			currentKey += mNode.element().getChanceOfAppearance();
		}
		
		// Lastly, set the key for the level + 1(that is, the max key
		// for the whole list)
		levelKeys[currentLevel + 1] = currentKey;
	}
	
	/**
	 * Retrieves a random monster up to the level specified.
	 * @param level		Level to retrieve up to
	 * @param monsterClasses	Acceptable monster classes. If null, all classes allowed.
	 * @return	Monster
	 */
	public Monster getRandomMonster(byte level, boolean monsterClasses[])
	{
		Random random = new Random(System.currentTimeMillis());
		
		int count = 0;				// Tracks how many times we have passed through the loop
		Monster monster = null;		// Monster we found.
		
		while(monster == null && count < Util.MAXLOOPS)
		{
			monster = monstersByCOA.findEarly(random.nextInt(levelKeys[level + 1]));
			
			// If we can choose any monster class, or this is a monsterclass we want, we are done.
			if(monsterClasses == null || monsterClasses[monster.getType().value()])
				break;
			
			// Otherwise, try again. Increment count since we have done a loop.
			monster = null;
			count++;
		}
		
		return monster;
	}
	
	/**
	 * Retrieves a random monster of any type. The first use is for
	 * quests where it obviously can't choose a monster based the current
	 * level of the player (it would be 0)
	 * @return	Monster
	 */
	public Monster getRandomAnyMonster()
	{
		Random random = new Random(System.currentTimeMillis());
		
		SkipIter<Monster> mNode = monsters.getIterator();
		for(int i = random.nextInt(monsters.getSize()); i > 0; i--, mNode.next());
		
		return mNode.element();
	}
	
	/**
	 * Retrieve the skip list of all the monsters.
	 * @return SkipList<Monster>
	 */
	public SkipList<Monster> getMonsters()
	{
		return monsters;
	}
	
	public Monster getMonster(short monsterID)
	{
		return monsters.find((int)monsterID);
	}
	
	public Monster getMonster(String monsterName)
	{
		SkipIter<Monster> tNode = monsters.getIterator();
		
		while(tNode.next())
			if(tNode.element().getName().equalsIgnoreCase(monsterName))
				return tNode.element();
			
		return null;
	}
	
	public SkipList<Monster> getMonstersByClass(MonsterClass monsterClass)
	{
		SkipIter<Monster> tNode = monsters.getIterator();
		SkipList<Monster> monsterList = new SkipList<Monster>();
		
		if(tNode.last())
			return null;
		
		while(tNode.next())
			if(tNode.element().getType() == monsterClass)
				monsterList.insert(tNode.element(), tNode.key());
		
		return monsterList;
	}
	
	public String[] getMonsterNamesByClass(MonsterClass monsterClass)
	{
		SkipList<Monster> monsterList = getMonstersByClass(monsterClass);
		if(monsterList == null || monsterList.getSize() == 0)
			return null;
		
		SkipIter<Monster> tNode = monsterList.getIterator();
		String[] monsterNames = new String[monsterList.getSize()];
		int count = 0;
		
		while(tNode.next())
		{
			monsterNames[count] = tNode.element().getName();
			count++;
		}
		
		return monsterNames;
	}
	
	/**
	 * Choose a random monster that has a minimum level of at most the maximum level provided.
	 * Then picks a random number of them to create between maxStackSize and 1/2 maxStackSize
	 * Finally creates a linked list of new instances of this monster. to return.
	 * @param maximumLevel
	 * @param maxStackSize
	 * @return
	 */
	public LinkedList<MonsterInstance> getRandomMonster(byte maximumLevel, int maxStackSize)
	{
		LinkedList<Monster> monstersAvail = getMonstersByMaxLevel(maximumLevel, true);
		if(monstersAvail.getSize() < 1)
			return null;
		
		Random rand = new Random(System.nanoTime());
		int count = rand.nextInt(monstersAvail.getSize());
		
		ListIter<Monster> tMon = monstersAvail.getIterator();
		
		for(int i = 0; i < count; i++, tMon.next());
		
		// Now we have the monsters, figure out how many we want.
		// Get some amount between the 1/2 the maximum size and the max.
		
		count = (maxStackSize == 1) ? maxStackSize : (maxStackSize / 2) + rand.nextInt(maxStackSize / 2);
		
		// Now create a bunch of instances.
		LinkedList<MonsterInstance> monStack = new LinkedList<MonsterInstance>();
		for(int i = 0; i < count; i++)
			monStack.insert(tMon.element().createInstance());
		
		return monStack;
	}
	
	/**
	 * Retrieves all the monsters with a minimum level below the maximum level.
	 * Option exists to make it weighted, that means the monsters close to the maximum
	 * level will be inserted multiple times. This is for use with getRandomMonster so
	 * that monsters associated with a level show up more than others (e.g., so you don't
	 * have the same chance of getting a level 15 monsters as a level 1 monster on level 15.
	 * @param maximumLevel
	 * @param weighted True if the list should be weighted
	 * @return
	 */
	public LinkedList<Monster> getMonstersByMaxLevel(byte maximumLevel, boolean weighted)
	{
		if(monsters.getSize() < 1)
			return null;
		
		LinkedList<Monster> monList = new LinkedList<Monster>();
		
		SkipIter<Monster> tMon = monsters.getIterator();
		
		while(tMon.next())
		{
			if(tMon.element().getLevel() <= maximumLevel)
			{
				if(weighted)
					// TODO, this should be using a random number between high & low bound
					for(byte i = 0; i < tMon.element().getGroupSize(); i++)
						monList.insert(tMon.element());
				else
					monList.insert(tMon.element());
			}
		}
		return monList;
	}
	
	public boolean addMonster(Monster newMonster)
	{
		if(monsters.find((int)newMonster.getID()) != null)
			return false;
		
		monsters.insert(newMonster, (int)newMonster.getID());
		return true;
	}
	
	public Monster newMonster()
	{
		short newID = getFirstID();
		if(newID == Util.NOTHING)
			return null;
		
		Monster newMonster = new Monster(newID);
		
		monsters.insert(newMonster, (int)newID);
		return newMonster;
	}
	
	public Monster newMonster(MonsterClass monType)
	{
		short newID = getFirstID();
		if(newID == Util.NOTHING)
			return null;
		
		Monster newMonster = new Monster(newID);
		
		monsters.insert(newMonster, (int)newID);
		
		newMonster.setType(monType);
		newMonster.setName(Util.NOSTRING + monType);
		return newMonster;
	}
	
	public Monster removeMonster(short monsterID)
	{
		return monsters.remove((int)monsterID);
	}
	
	public Monster removeMonster(String monsterName)
	{
		Monster tMonster = getMonster(monsterName);
		
		if(tMonster != null)
			return monsters.remove((int)tMonster.getID());
		
		return null;
	}
	
	/**
	 * Clear the list of any invalid monsters.
	 */
	public void removeBlankMonsters()
	{
		SkipIter<Monster> mNode = monsters.getIterator();
		while(mNode.next())
		{
			if(mNode.element().getName().contains(Util.NOSTRING))
				monsters.remove(mNode.key());
		}
	}
	
	public boolean validName(String monName)
	{
		return (!monName.contains(Util.NOSTRING) && (getMonster(monName) == null));
	}
	
	/**
	 * Retrieve the first available key.
	 * @return Lowest available key or -1 if no valid keys.
	 */
	private short getFirstID()
	{
		if(monsters.getSize() == 0)
			return 0;
		
		SkipIter<Monster> tNode = monsters.getIterator();
		
		short lastKey = 0;
		while(tNode.next())
		{
			if(tNode.key() < Short.MAX_VALUE && tNode.key() > lastKey)
				return lastKey;
			
			lastKey = (short)(tNode.key() + 1);
		}
		
		return (tNode.key() < Short.MAX_VALUE) ? (short)(tNode.key() + 1) : Util.NOTHING;
	}
}
