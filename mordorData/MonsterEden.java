package mordorData;
import java.util.Random;

import mordorEnums.MonsterClass;
import mordorHelpers.Util;
import structures.LinkedList;
import structures.ListNode;
import structures.QuadNode;
import structures.SkipList;

/**
 * Data structure for holding collection of all monsters.
 * @author August Junkala. April 25, 2007
 *
 */
public class MonsterEden
{	
//	public static final byte GROUP_MAXSIZE = 64;
	//public static final byte GROUP_MINSIZE = 1;
	//public static final byte GROUP_MAXNUM = 4;
//	public static final byte GROUP_MINNUM = 1;
	
	/* The maximum meaningful COA value */
//	public static final byte MAXCOAVALUE = 66;
	
	private SkipList<Monster> monsters;
	private SkipList<Monster> monstersByCOA; // List of monsters, keyed by sum of COA of monsters before them. 
	private int levelKeys[];
	
//	private SkipList<Monster> lairedMonsters; // quick link to laired monsters
	
	/*
	 * Based on the level of the current player, this hold available available
	 * monsters up to and including this level and the level.
	 * Max key is the key of the last element + it's own chance of appearance (COA).
	 * Note: It's key does NOT include its own COA.  
	 */
//	private int availableLevel, maxAvailKey, maxStudKey, maxAquaKey;
//	private SkipList<Monster> availableMonsters;
//	private SkipList<Monster> studMonsters;
//	private SkipList<Monster> aquaticMonsters;
	
	MonsterEden()
	{
		monsters = new SkipList<Monster>();
		monstersByCOA = new SkipList<Monster>();
		levelKeys = new int[1];
		
	/*	lairedMonsters = new SkipList<Monster>();
		studMonsters = new SkipList<Monster>();
		aquaticMonsters= new SkipList<Monster>();
		
		availableLevel = Byte.MIN_VALUE;
		maxAvailKey = 0;
		maxStudKey = 0;*/
	}
	
	/**
	 * Retrieves the maximum level of all the monsters.
	 * @return byte
	 */
	private byte findMaxLevel()
	{
		QuadNode<Monster> mNode = monsters.firstNode();
		byte maxLevel = Util.NOTHING;
		
		while(mNode.getRight() != null)
		{
			if(mNode.getElement().getMinMapLevel() > maxLevel)
				maxLevel = mNode.getElement().getMinMapLevel();
			mNode = mNode.getRight();
		}
		
		return maxLevel;
	}
	
	/**
	 * Retrieve a skip list containing all the monsters ordered by level.
	 * @return	SkipList<Monster>
	 */
	private SkipList<Monster> getMonstersByLevel()
	{
		SkipList<Monster> levelList = new SkipList<Monster>();
		QuadNode<Monster> mNode = monsters.firstNode();
		
		while(mNode.getRight() != null)
		{
			levelList.insert(mNode.getElement(), (int)mNode.getElement().getMinMapLevel());
			mNode = mNode.getRight();
		}
		
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
		
		QuadNode<Monster> mNode = this.getMonstersByLevel().firstNode();
		int currentKey = 0;
		int currentLevel = 0;
		
		while(mNode.getRight() != null)
		{
			// Using the sum of keys up to here, add this element w/ that key.
			monstersByCOA.insert(mNode.getElement(), currentKey);
			
			// If we have moved to a new level, also set the start key for
			// the new level to the same key
			if(currentLevel < mNode.getElement().getMinMapLevel())
			{
				currentLevel = mNode.getElement().getMinMapLevel();
				levelKeys[currentLevel] = currentKey;
			}
			
			// Finally, add this monsters COA to the current key.
			currentKey += mNode.getElement().getChanceOfAppearance();
			
			mNode = mNode.getRight();
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
			if(monsterClasses == null || monsterClasses[monster.getMonsterClass().value()])
				break;
			
			// Otherwise, try again. Increment count since we have done a loop.
			monster = null;
			count++;
		}
		
		return monster;
	}
	
	/**
	 * Update the availableMonsters list.
	 * @param maxLevel	Level to update to.
	 */
/*	public void updateAvailableMonsters(byte maxLevel)
	{
		if(maxLevel == availableLevel)
			return;
		
		availableMonsters = new SkipList<Monster>();
		studMonsters = new SkipList<Monster>();
		
		availableLevel = maxLevel;
		maxAvailKey = getAvailMonByClass(availableMonsters, null, false, 0, maxLevel);
		maxStudKey = getAvailMonByClass(studMonsters, null, false,  maxLevel + 1, maxLevel + 1);
		maxAquaKey = getAvailMonByClass(aquaticMonsters, null, true, 0, maxLevel);
	}*/
	
	/**
	 * Get available monsters, organized by COA. If MonsterClass isn't null then
	 * it will only add monsters of the type specified. Will also only add monsters
	 * who have a minimum level to appear at equivalent less than or equal to maxLevel.
	 * Will not include monsters with a COA == 0. These are laired monsters or other
	 * ones that can't be "chosen" to appear.
	 * minLevel is primarily useful for stud squares.
	 * 
	 * @param monList	SkipList<Monster>	List to put monsters into. Well be cleared!
	 * @param mc	MonsterClass	If null, then all monsters
	 * @param minLevel	int		Minimum minLevel of a monster allowed
	 * @param maxLevel	int		Maximum minLevel of a monster allowed
	 * @param
	 * @return
	 */
/*	public int getAvailMonByClass(SkipList<Monster> monList, MonsterClass mc, boolean aquatic, int minLevel, int maxLevel)
	{
		// First, find out how many of each value exist.
		int totCOAcount[] = new int[MAXCOAVALUE + 1];
		int sumCOAcount[] = new int[MAXCOAVALUE + 1];
		int curCOAcount[] = new int[MAXCOAVALUE + 1];
		
		QuadNode<Monster> mNode = monsters.firstNode();
		
		// First, get how many monsters have each COA
		while(mNode.getRight() != null)
		{
			if(mNode.getElement().getMinMapLevel() >= minLevel && mNode.getElement().getMinMapLevel() <= maxLevel && (mc == null || mNode.getElement().getMonsterClass() == mc) && mNode.getElement().getChanceOfAppearance() != 0)
			{
				if((!aquatic && !mNode.getElement().isAquatic()) || (aquatic && mNode.getElement().isAquatic()))
					totCOAcount[Util.FITBYTE(mNode.getElement().getChanceOfAppearance(), 0, MAXCOAVALUE)]++;
			}

			mNode = mNode.getRight();
		}
		
		// Second, get the sum of the COAs up to each level
		sumCOAcount[0] = 0;
		sumCOAcount[1] = totCOAcount[0];
		for(int i = 2; i < sumCOAcount.length; i++)
			sumCOAcount[i] = sumCOAcount[i - 1] + (totCOAcount[i - 1] * (i - 1));
		
		// Clear the list.
		if(monList.getSize() != 0)
			monList.clearList();

		// Now generate the new keys
		mNode = monsters.firstNode();
		while(mNode.getRight() != null)
		{
			if(mNode.getElement().getMinMapLevel() >= minLevel && mNode.getElement().getMinMapLevel() <= maxLevel && (mc == null || mNode.getElement().getMonsterClass() == mc) && mNode.getElement().getChanceOfAppearance() != 0)
			{
				if((!aquatic && !mNode.getElement().isAquatic()) || (aquatic && mNode.getElement().isAquatic()))
				{
					byte COA = Util.FITBYTE(mNode.getElement().getChanceOfAppearance(), 0, MAXCOAVALUE);
					// Its key is the sum of all COA's before it.
					Integer nKey = sumCOAcount[COA] + (curCOAcount[COA] * COA);
			
					monList.insert(mNode.getElement(), nKey);
			
					curCOAcount[COA]++;
				}
			}
			
			mNode = mNode.getRight();
		}
		
		// Return the maximum possible value. That is, the key of the last node in the list + its COA 
		return monList.lastNode().getKey() + monList.lastNode().getElement().getChanceOfAppearance();
	}*/
	
	/**
	 * Retrieves a random monster of those in the available list.
	 * @return	Monster
	 */
/*	public Monster getRandomMonster(boolean getStud, boolean getAquatic)
	{
		Random random = new Random(System.currentTimeMillis());
		
		if(getStud)
			return studMonsters.findEarly(random.nextInt(maxStudKey));
		else if(getAquatic)
			return aquaticMonsters.findEarly(random.nextInt(maxAquaKey));
		
		return availableMonsters.findEarly(random.nextInt(maxAvailKey));
	}*/
	
	/**
	 * Retrieves a random monster of any type. The first use is for
	 * quests where it obviously can't choose a monster based the current
	 * level of the player (it would be 0)
	 * @return	Monster
	 */
	public Monster getRandomAnyMonster()
	{
		Random random = new Random(System.currentTimeMillis());
		
		QuadNode<Monster> mNode = monsters.firstNode();
		for(int i = random.nextInt(monsters.getSize()); i > 0; i--, mNode = mNode.getRight());
		
		return mNode.getElement();
	}
	
	/**
	 * Retrieves a random monsters of a specific class from those monsters
	 * available.
	 * @param mc	MonsterClass
	 * @return	Monster or null if one couldn't be found.
	 */
/*	public Monster getRandomMonster(MonsterClass mc, boolean getStud, boolean getAquatic)
	{
		Random random = new Random(System.currentTimeMillis());
		
		int count = 0;
		Monster monster = null;
		
		while((monster != null && monster.getMonsterClass() == mc) && count < (MonsterClass.values().length << 1))
		{
			if(getStud)
				monster = studMonsters.findEarly(random.nextInt(maxAvailKey));
			else if(getAquatic)
				monster = aquaticMonsters.findEarly(random.nextInt(maxAvailKey));
			else
				monster = availableMonsters.findEarly(random.nextInt(maxAvailKey));
			count++;
		}
		
		if(monster.getMonsterClass() != mc)
			return null;
		else
			return monster;
	}*/
	
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
		QuadNode<Monster> tNode = monsters.firstNode();
		
		while(tNode.getRight() != null)
		{
			if(tNode.getElement().getName().equalsIgnoreCase(monsterName))
				return tNode.getElement();
			
			tNode = tNode.getRight();
		}
		return null;
	}
	
	public SkipList<Monster> getMonstersByClass(MonsterClass monsterClass)
	{
		QuadNode<Monster> tNode = monsters.firstNode();
		SkipList<Monster> monsterList = new SkipList<Monster>();
		
		if(tNode == null)
			return null;
		
		while(tNode.getRight() != null)
		{
			if(tNode.getElement().getMonsterClass() == monsterClass)
				monsterList.insert(tNode.getElement(), tNode.getKey());
			tNode = tNode.getRight();
		}
		
		return monsterList;
	}
	
	public String[] getMonsterNamesByClass(MonsterClass monsterClass)
	{
		SkipList<Monster> monsterList = getMonstersByClass(monsterClass);
		if(monsterList == null || monsterList.getSize() == 0)
			return null;
		
		QuadNode<Monster> tNode = monsterList.firstNode();
		String[] monsterNames = new String[monsterList.getSize()];
		int count = 0;
		
		while(tNode.getRight() != null)
		{
			monsterNames[count] = tNode.getElement().getName();
			count++;
			tNode = tNode.getRight();
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
		
		ListNode<Monster> tMon = monstersAvail.getFirstNode();
		
		for(int i = 0; i < count; i++, tMon = tMon.getNext());
		
		// Now we have the monsters, figure out how many we want.
		// Get some amount between the 1/2 the maximum size and the max.
		
		count = (maxStackSize == 1) ? maxStackSize : (maxStackSize / 2) + rand.nextInt(maxStackSize / 2);
		
		// Now create a bunch of instances.
		LinkedList<MonsterInstance> monStack = new LinkedList<MonsterInstance>();
		for(int i = 0; i < count; i++)
			monStack.insert(tMon.getElement().createInstance());
		
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
		
		QuadNode<Monster> tMon = monsters.firstNode();
		
		while(tMon.getRight() != null)
		{
			if(tMon.getElement().getMinMapLevel() <= maximumLevel)
			{
				if(weighted)
					// TODO, this should be using a random number between high & low bound
					for(byte i = 0; i < tMon.getElement().getGroupSize(); i++)
						monList.insert(tMon.getElement());
				else
					monList.insert(tMon.getElement());
			}
					
			tMon = tMon.getRight();
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
	
/*	public boolean addLairedMonster(Monster newMonster)
	{
		if(lairedMonsters.find((int)newMonster.getID()) != null)
			return false;
		
		lairedMonsters.insert(newMonster, (int)newMonster.getID());
		return true;
	}
	
	public void removeLairedMonster(Monster oldMonster)
	{
		lairedMonsters.remove((int)oldMonster.getID());
	}*/
	
	public Monster newMonster()
	{
		short newID = getFirstID();
		if(newID == -1)
			return null;
		
		Monster newMonster = new Monster(newID);
		
		monsters.insert(newMonster, (int)newID);
		return newMonster;
	}
	
	public Monster newMonster(MonsterClass monType)
	{
		short newID = getFirstID();
		if(newID == -1)
			return null;
		
		Monster newMonster = new Monster(newID);
		
		monsters.insert(newMonster, (int)newID);
		
		newMonster.setMonsterClass(monType);
		newMonster.setName(Util.NOSTRING + monType);
		return newMonster;
	}
	
	public Monster removeMonster(short monsterID)
	{
//		if(lairedMonsters.getSize() > 0)
	//		lairedMonsters.remove((int)monsterID);
		return monsters.remove((int)monsterID);
	}
	
	public Monster removeMonster(String monsterName)
	{
		Monster tMonster = getMonster(monsterName);
		
		if(tMonster != null)
	//	{
		//	lairedMonsters.remove((int)tMonster.getID());
			return monsters.remove((int)tMonster.getID());
		//}
		
		return null;
	}
	
	/**
	 * Clear the list of any invalid monsters.
	 */
	public void removeBlankMonsters()
	{
		QuadNode<Monster> mNode = monsters.firstNode();
		while(mNode.getRight() != null)
		{
			Monster rMonster = mNode.getElement();
			mNode = mNode.getRight();
			
			if(rMonster.getName().contains(Util.NOSTRING))
				monsters.remove((int)rMonster.getID());
		}
	}
	
	public boolean validName(String monName)
	{
		return (!monName.contains(Util.NOSTRING) && (getMonster(monName) == null));
	}
	
	private short getFirstID()
	{
		if(monsters.getSize() == 0)
			return 0;
		
		QuadNode<Monster> tNode = monsters.firstNode();
		
		if(tNode.getKey() > 0)
			return 0;
		
		while(tNode.getRight() != null)
		{
			if(tNode.getKey() < Short.MAX_VALUE && tNode.getKey() < (tNode.getRight().getKey() - 1))
				return (short)(tNode.getKey() + 1);
			
			tNode = tNode.getRight();
		}
		
		return -1;
	}
}
