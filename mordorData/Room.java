package mordorData;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;
import java.util.Vector;

import mordorEnums.ItemTypes;
import mordorEnums.MonsterClass;
import mordorEnums.Trap;
import mordorHelpers.Util;

import structures.LinkedList;
import structures.ListNode;


public class Room
{
	public static final byte MAXTRAPLEVEL = 5;
	public static final long MINRESPAWNTIME = 3600000; // Minimum time until a respawn.
	
	private LinkedList<MapSquare> squares; // structure contains x/y for each square
	private long lastCleared;
	private byte level;
	private byte[] roomIDB;
	private int roomID;
	
	private LinkedList<LinkedList<MonsterInstance>> monsterStacks;
	private LinkedList<ItemInstance> monsterItems;
	private long monsterGold;
	
	private Chest chest;
	private ItemInstance item;
	private boolean isStud;
	private boolean isExit;
	
	private boolean monsterTypes[];
	private short lairedIDs[];
	
	private LinkedList<Player> playersHere;
	
	/**
	 * Constructor for a new room.
	 * @param nLevel
	 * @param nRoomID
	 */
	public Room(byte nLevel, int nRoomID)
	{
		level = nLevel;
		roomID = nRoomID;
		roomIDB = new byte[2];
		roomIDB[0] = (byte)(nRoomID >>> 7);
		roomIDB[1] = (byte)(nRoomID % 128);
		
		squares = new LinkedList<MapSquare>();

		monsterStacks =  new LinkedList<LinkedList<MonsterInstance>>();
		
		monsterTypes = new boolean[MonsterClass.values().length];
		for(MonsterClass mc : MonsterClass.values())
			monsterTypes[mc.value()] = (mc != MonsterClass.WaterDwellers);
		
		lairedIDs = new short[Util.MON_MAXSTACKSIZE];
		for(int i = 0; i < lairedIDs.length; i++)
			lairedIDs[i] = Util.NOTHING;
		
		playersHere = new LinkedList<Player>();
		
		monsterItems = null;
		monsterGold = 0;
	}
	
	/**
	 * If this room has squares then report that the room does exist.
	 * @return	boolean
	 */
	public boolean doesRoomExist()
	{
		return squares.isEmpty();
	}
	
	/**
	 * Adds a square to this room.
	 * @param nSquare	MapSquare
	 */
	public void addSquare(MapSquare nSquare)
	{
		nSquare.updateRoom(this);
		
		if(!squares.containsElement(nSquare))
			squares.insert(nSquare);
	}
	
	/**
	 * Add a player to the list of players here.
	 * @param nPlayer Plyaer to remove.
	 */
	public void addPlayer(Player nPlayer)
	{
		if(nPlayer != null && playersHere.containsElement(nPlayer))
			playersHere.insert(nPlayer);
	}
	
	/**
	 * Remove a player from the last for this room.
	 * @param nPlayer	The player to remove.
	 */
	public void removePlayer(Player nPlayer)
	{
		if(nPlayer != null)
			playersHere.remove(nPlayer);
	}
	
	/**
	 * Retrieve the linked list of all players in this room.
	 * @return LinkedList<Player>
	 */
	public LinkedList<Player> getPlayersHere()
	{
		return playersHere;
	}
	
	/**
	 * Retrieves an array containing the names of all players in this square.
	 * Or an array with only 1 entry, "None", if no players are here.
	 * @return String[]
	 */
	public String[] getPlayerNamesHere()
	{
		if(playersHere.getSize() < 1)
		{
			String[] names = new String[1];
			names[0] = "None";
			return names;
		}
		ListNode<Player> pNode = playersHere.getFirstNode();
		String[] names = new String[playersHere.getSize()];
		int count = 0;
		
		while(pNode != null)
		{
			names[count] = pNode.getElement().getName();
			count++;
			pNode = pNode.getNext();
		}
		return names;
	}
	
	/**
	 * Set whether this room is the exit room.
	 * @param nExit
	 */
	public void setExit(boolean nExit)
	{
		isExit = nExit;
	}
	
	/**
	 * Remove a square from this room.
	 * @param oSquare
	 */
	public void removeSquare(MapSquare oSquare)
	{
		if(isStud)
		{
			if(oSquare.getSquareFeatures()[0].getType() == SquareFeature.TYPE_STUD)
				oSquare.getSquareFeatures()[0].setType(SquareFeature.TYPE_NONE);
			else if(oSquare.getSquareFeatures()[1].getType() == SquareFeature.TYPE_STUD)
				oSquare.getSquareFeatures()[1].setType(SquareFeature.TYPE_NONE);
		}
		squares.remove(oSquare);
		
//		setAquatic();
	}
	
	/**
	 * Searches the entire stack for a specific monster instance and removes
	 * it.
	 * @param nMonster
	 * @return boolean	True if monster found.
	 */
	public boolean removeMonsterFromStack(MonsterInstance nMonster)
	{
		ListNode<LinkedList<MonsterInstance>> lNode = monsterStacks.getFirstNode();
		while(lNode != null)
		{
			ListNode<MonsterInstance> mNode = lNode.getElement().getFirstNode();
			
			while(mNode != null)
			{
				if(mNode.getElement() == nMonster)
				{
					lNode.getElement().remove(mNode);
					return true;
				}
				mNode = mNode.getNext();
			}
			lNode = lNode.getNext();
		}
		
		return false;
	}
	
	/**
	 * Clears any dead monsters from the stacks.
	 */
	public void clearDeadMonstersFromStack()
	{
		ListNode<LinkedList<MonsterInstance>> lNode = monsterStacks.getFirstNode();
		ListNode<MonsterInstance> tNode;
		while(lNode != null)
		{
			ListNode<MonsterInstance> mNode = lNode.getElement().getFirstNode();
			
			while(mNode != null)
			{
				if(mNode.getElement().getHits() <= 0)
				{
					tNode = mNode;
					mNode = mNode.getNext();
					lNode.getElement().remove(tNode);
				}
				mNode = mNode.getNext();
			}
			lNode = lNode.getNext();
		}
	}
	
	/**
	 * Remove all squares from this room and add them to the Room passed
	 * as an argument.
	 * @param zeroRoom
	 */
	public void clearRoom(Room zeroRoom)
	{
		ListNode<MapSquare> tNode = squares.getFirstNode();
		
		while(tNode != null)
		{	
			MapSquare rSquare = tNode.getElement();
			rSquare.setRoom(zeroRoom);
			zeroRoom.addSquare(rSquare);
			
			tNode = tNode.getNext();
			
			squares.remove(rSquare);
		}
	}
	
	public boolean addMonsterToStack(MonsterInstance nMonster, byte stackNumber)
	{
		// should also ensure that the type is the same.
		if(stackNumber < 0 || stackNumber >= Util.MON_MAXSTACKSIZE)
			return false;
		
		ListNode<LinkedList<MonsterInstance>> tNode = monsterStacks.getFirstNode();
		byte count = 0;
		while(tNode != null && count < stackNumber)
		{
			count++;
			tNode = tNode.getNext();
		}
		
		if(tNode == null || count < stackNumber)
			return false;
		
		if(tNode.getElement().getSize() < Util.MON_MAXSTACKSIZE && tNode.getElement().getFirst().getMonsterID() == nMonster.getMonsterID())
		{
			tNode.getElement().insert(nMonster);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sets the pointer to the level containing this room.
	 * @param nLevel	The level (z) of the room. (int)
	 */
	public void setLevel(byte nLevel)
	{
		level = nLevel;
	}
	
	public void setRoomID(int newID)
	{
		roomID = newID;
		roomIDB = new byte[2];
		roomIDB[0] = (byte)(roomID >>> 8);
		roomIDB[1] = (byte)(roomID);
	}
	
	/**
	 * The monster has been killed so update the emptied time and
	 * deleted the monster.
	 * @param newClearedTime	long
	 */
	public void setLastCleared(long newClearedTime)
	{
		lastCleared = newClearedTime;
	}
	
	/**
	 * Sets all squares in this room too stud squares. Note: if
	 * both squares have a feature besides note, featureB will become
	 * the stud feature.
	 * @param newStud
	 */
	public void setStud(boolean newStud)
	{
		isStud = newStud;
		
		ListNode<MapSquare> tNode = squares.getFirstNode();
		while(tNode != null)
		{
			if(isStud && !tNode.getElement().isStudSquare())
			{
				if(tNode.getElement().getSquareFeatures()[0].getType() == SquareFeature.TYPE_NONE)
					tNode.getElement().getSquareFeatures()[0].setType(SquareFeature.TYPE_STUD);
				else
					tNode.getElement().getSquareFeatures()[1].setType(SquareFeature.TYPE_STUD);
					
			}
			else if(!isStud)
			{
				if(tNode.getElement().getSquareFeatures()[0].getType() == SquareFeature.TYPE_STUD)
					tNode.getElement().getSquareFeatures()[0].setType(SquareFeature.TYPE_NONE);

				if(tNode.getElement().getSquareFeatures()[1].getType() == SquareFeature.TYPE_STUD)
					tNode.getElement().getSquareFeatures()[1].setType(SquareFeature.TYPE_NONE);
			}
			tNode = tNode.getNext();
		}
	}
	
	public void setChest(Chest nChest)
	{
		chest = nChest;
	}
	
	public void setItem(ItemInstance nItem)
	{
		item = nItem;
	}
	
	/**
	 * Set a specific monster to a specific slot as a laired monster
	 * @param newMonster
	 * @param stackNumber
	 */
	public void setLairedMonster(Monster newMonster, byte stackNumber)
	{
		if(stackNumber < 0 || stackNumber >= Util.MON_MAXSTACKSIZE)
			return;
		
		lairedIDs[stackNumber] = (newMonster == null) ? Util.NOTHING : newMonster.getID();
		return;
	}
	
	/**
	 * Set whether a specific monster class is legal for this room.
	 * @param mc	MonsterClass
	 * @param isAllowed
	 */
	public void setMonsterType(MonsterClass mc, boolean isAllowed)
	{
		monsterTypes[mc.value()] = isAllowed;
	}
	
	/**
	 * Retrieves the time at which this room was last emptied.
	 * @return	long
	 */
	public long getLastCleared()
	{
		return lastCleared;
	}
	
	/**
	 * Retrieves the rooms number
	 * @return int	Room number
	 */
	public int getRoomNumber()
	{
		return roomID;
	}
	
	/**
	 * Retrieves the byte representation of the ID
	 * @return byte[2]
	 */
	public byte[] getRoomID()
	{
		return roomIDB;
	}
	
	/**
	 * Retrieves the number of squares in this room.
	 * @return int	Number of squares in this room.
	 */
	public int getNumberSquares()
	{
		return squares.getSize();
	}
	
	/**
	 * retrieves the chest
	 * @return
	 */
	public Chest getChest()
	{
		return chest;
	}
	
	/**
	 * Retrives the list of squares that make this room.
	 * @return LinkedList<MapSquare>
	 */
	public LinkedList<MapSquare> getSquares()
	{
		return squares;
	}
	
	/**
	 * Retrieves the item lying on the floor in this room.
	 * @return ItemInstance
	 */
	public ItemInstance getItem()
	{
		return item;
	}
	
	/**
	 * Retrieves the laired monster type.
	 * @param stackNumber	Stack to get monster from.
	 * @return Monster
	 */
	public short getLairedMonsterID(byte stackNumber)
	{
		return this.lairedIDs[stackNumber];
	}
	
	/**
	 * Retrieve the array of booleans indicated legal monster types
	 * in this room.
	 * @return boolean[]
	 */
	public boolean[] getMonsterTypes()
	{
		return monsterTypes;
	}
	
	/**
	 * Retrieve the monster stacks for this room.
	 * @return LinkedList<LinkedList<MonsterInstance>>
	 */
	public LinkedList<LinkedList<MonsterInstance>> getMonsterStacks()
	{
		return monsterStacks;
	}
	
	/**
	 * Retrieves the room's level number.
	 * @return byte
	 */
	public byte getLevel()
	{
		return level;
	}
	
	/**
	 * Determines whether this room is a stud room
	 * @return boolean	True if it is a stud room.
	 */
	public boolean isStudRoom()
	{
		return isStud;
	}
	
	/**
	 * Is a specific monster type allowed in this room.
	 * @param mc MonsterClass
	 * @return True if the class is allowed.
	 */
	public boolean isMonsterTypeAllowed(MonsterClass mc)
	{
		return monsterTypes[mc.value()];
	}
	
	/**
	 * Determine whether this room is a lair.
	 * @return boolean True if it is a lair room.
	 */
	public boolean isLair()
	{
		for(byte i = 0; i < lairedIDs.length; i++)
			if(lairedIDs[i] >= 0)
				return true;
		
		return false;
	}
	
	/**
	 * For when player enters the room. Generate new monsters
	 * if the time since last clear is long enough (and room doesn't
	 * contain monsters)
	 *
	 */
	public void enterRoom(DataBank dataBank, Player player)
	{
		if(playersHere.getSize() > 0)
			sayHello();
		
		playersHere.insert(player);
		
		if(monsterStacks.getSize() != 0 || System.currentTimeMillis() < (lastCleared + MINRESPAWNTIME) || isExit)
			return; // Not empty, too soon the refill, or it is the exit room (no monsters).
		
		fillMonsters(dataBank);
		fillTreasure(dataBank);
	}
	
	/**
	 * Generates a stack of the monster provided.
	 * @param monster	Monster to generate a stack of.
	 * @return LinkedList<MonsterInstance>
	 */
	private LinkedList<MonsterInstance> generateStack(Monster monster)
	{
		Random random = new Random(System.nanoTime());
		LinkedList<MonsterInstance> monsterStack = new LinkedList<MonsterInstance>();
		
		if(monster == null)
			return monsterStack;
		
		int count = random.nextInt(monster.getGroupSize());
		count++;
		
		for(; count > 0; count--)
			monsterStack.insert(monster.createInstance());
		
		return monsterStack;
	}
	
	/**
	 * Fills the monsterStacks with monsters.
	 * @param dataBank
	 */
	private void fillMonsters(DataBank dataBank)
	{
		Monster monster = null;
		Random random = new Random(System.nanoTime());
		boolean monsterClasses[] = new boolean[MonsterClass.values().length];
		byte roomLevel = (isStud) ? (byte)(level + 1) : (byte)level;
		int companionCount = 0;
		
		// First, if this is a lair, then roll the dice to see if the laired monster shows up.
		// TODO Magic value: 5% chance laired monster won't show.
		if(isLair() && random.nextInt(100) > 4)
		{
			for(int i = 0; i < lairedIDs.length && monsterStacks.getSize() < Util.MON_MAXSTACKSIZE; i++)
			{
				monster = dataBank.getMonsterEden().getMonster(lairedIDs[i]);
				if(monster != null)
				{
					monsterStacks.insert(generateStack(monster));
					if(monster.getCompanionType() != null)
					{
						monsterClasses[monster.getCompanionType().value()] = true;
						companionCount = monster.getGroupNumber();
					}
					
					if(monster.getSpecificCompanionID() != Util.NOTHING)
					{
						monster = dataBank.getMonsterEden().getMonster(monster.getSpecificCompanionID());
						if(monster != null) monsterStacks.insert(generateStack(monster));
					}
				}
			}
		}
		
		if(monster == null)
		{
			// No laired monster, so pick one.
			monster = dataBank.getMonsterEden().getRandomMonster(roomLevel, monsterTypes);
			
			if(monster == null)
				return; // No monsters found, nothing left to do.
			
			// Otherwise, insert a tack, add companion type.
			monsterStacks.insert(generateStack(monster));
			if(monster.getCompanionType() != null)
			{
				monsterClasses[monster.getCompanionType().value()] = true;
				companionCount = monster.getGroupNumber();
			}
			
			// Add a specific companion if it has one.
			if(monster.getSpecificCompanionID() != Util.NOTHING)
			{
				monster = dataBank.getMonsterEden().getMonster(monster.getSpecificCompanionID());
				if(monster != null) monsterStacks.insert(generateStack(monster));
			}
		}
		
		// Determine if companions should be self.
		boolean self = false;
		if(companionCount > 0)
			self = (random.nextInt(100) < (Util.power(monsterStacks.getFirst().getFirst().getMonster().getGroupSize(), 2))); 
			
		
		// Now add general companions.
		for(int i = 0; i < companionCount && monsterStacks.getSize() < Util.MON_MAXSTACKSIZE; i++)
		{
			monster = (self) ? monsterStacks.getFirst().getFirst().getMonster() : dataBank.getMonsterEden().getRandomMonster(roomLevel, monsterClasses);
			monsterStacks.insert(generateStack(monster));
		}
	}
	
	/**
	 * Create treasure
	 * @param dataBank
	 */
	private void fillTreasure(DataBank dataBank)
	{
		if(monsterStacks.getSize() < 1)
		{
			chest = null;
			monsterItems = null;
			monsterGold = 0;
			return;
		}
		
		Random random = new Random(System.nanoTime());
		long gold = 0;
		boolean[] types = new boolean[ItemTypes.values().length];
		LinkedList<ItemInstance> treasureItems = new LinkedList<ItemInstance>();
		byte maxLevel = (isStud) ? (byte)(level + 1) : level;
		
		ListNode<LinkedList<MonsterInstance>> mNode = monsterStacks.getFirstNode();
		while(mNode != null)
		{
			Monster monster = mNode.getElement().getFirst().getMonster();
			// If the monster has a special item
			// Roll dice as to if to include it
			// if so, include it.
			if(monster.getItemDropID() != Util.NOTHING)
				if(random.nextBoolean())
				{
					Item tItem = dataBank.getItem(monster.getItemDropID());
					if(tItem != null)
						treasureItems.insert(tItem.createInstance());
				}
			// Same if it has a second specific item it drops.
			if(monster.getSecondItemDropID() != Util.NOTHING)
				if(random.nextBoolean())
				{
					Item tItem = dataBank.getItem(monster.getSecondItemDropID());
					if(tItem != null)
						treasureItems.insert(tItem.createInstance());
				}
			
			byte multiplier = monster.getWealthMultiplier();
			int start = Util.power(10, multiplier);
			int range = Util.power(10, (multiplier + 1)) - start;
			
			boolean[] itemTypes = monster.getItemDropTypes();
			for(ItemTypes it : ItemTypes.values())
				if(itemTypes[it.value()])
					types[it.value()] = true;
			
			for(int i = mNode.getElement().getSize(); i > 0; i--)
				gold += start + random.nextInt(range);
			
			mNode = mNode.getNext();
		}
		
		// There is a 33 / 2^A chance of generating an item
		// Where A = number of items already created.
		// TODO: Should this be linked to the wealthmultiplier? (wealthier = more items?)
		while((33 >> treasureItems.getSize()) > 1)
		{
			if(random.nextInt(100) < (33 >> treasureItems.getSize().intValue()))
			{
				Item tItem = dataBank.getItemCloset().getRandomItem(maxLevel, types);
				
				if(tItem == null)
					break;
				else
					treasureItems.insert(tItem.createInstance());
			}
			else
				break;
		}
		
		// Chose if treasure is boxed based on chance related to wealthiness of monster?
		if(random.nextInt(20) < monsterStacks.getFirst().getFirst().getMonster().getWealthMultiplier())
		{
			chest = new Chest();
			chest.fillBox(treasureItems, level);
			monsterItems = null;
			monsterGold = 0;
		}
		else
		{
			chest = null;
			monsterItems = treasureItems;
			monsterGold = gold;
		}
	}
	
	/**
	 * Causes all players in this room to say hello.
	 */
	public void sayHello()
	{
		ListNode<Player> pNode = playersHere.getFirstNode();
		while(pNode != null)
		{
			pNode.getElement().sayHello();
			pNode = pNode.getNext();
		}
	}
	
	/**
	 * Perform any updating the room when the player leaves.
	 * Essentially, remove the player from the players here list 
	 * @param player
	 */
	public void exitRoom(Player player)
	{
		playersHere.remove(player);
	}
	
	public String toString()
	{
		String roomString = "ROOM";
		
		roomString += " Level: " + level + " RoomID:" + roomID;
		roomString += " Last Cleared (ms):" + lastCleared + "\n";
		
		return roomString;
	}
	
	public boolean writeRoom(DataOutputStream dos)
	{
		try
		{
			dos.writeByte(DataBank.FLAG_ROOM);
			dos.writeInt(roomID);
			
			dos.writeByte(level);
			
			dos.writeBoolean(isStud);
			dos.writeBoolean(isExit);
			
			dos.writeLong(lastCleared);
			
			if(chest != null && !chest.chestIsEmpty())
			{
				dos.writeBoolean(true);
				chest.writeChest(dos);
			}
			else
				dos.writeBoolean(false);
			
			if(item != null)
				dos.writeShort(item.getItemID());
			else
				dos.writeShort(Util.NOTHING);
			
			dos.writeInt(monsterStacks.getSize());
			ListNode<LinkedList<MonsterInstance>> msNode = monsterStacks.getFirstNode();
			while(msNode != null)
			{
				int sSize = msNode.getElement().getSize();
				dos.writeInt(sSize);
				if(sSize > 0)
					dos.writeShort(msNode.getElement().getFirst().getMonsterID());
				msNode = msNode.getNext();
			}
			
			// Write the monster item(s);
			if(monsterItems == null)
				dos.writeInt(Util.NOTHING);
			else
			{
				dos.writeInt(monsterItems.getSize());
				ListNode<ItemInstance> tMonItems = monsterItems.getFirstNode();
				while(tMonItems != null)
				{
					tMonItems.getElement().writeItemInstance(dos);
					tMonItems = tMonItems.getNext();
				}
			}
			
			// Write monster gold
			dos.writeLong(monsterGold);
			
			// Write allowed monster classes.
			long longs[] = Util.COMPRESSBOOLTOLONG(monsterTypes);
			dos.writeInt(longs.length);
			for(int i = 0; i < longs.length; i++)
				dos.writeLong(longs[i]);
			
			// Write lairedIDs
			dos.writeInt(lairedIDs.length);
			for(int i = 0; i < lairedIDs.length; i++)
				dos.writeShort(lairedIDs[i]);
		}
		catch(Exception e)
		{
			System.err.println("Room - writeRoom : " + e);
			return false;
		}
		
		return true;
	}

	public static final Room readRoom(Map map, DataInputStream dis, LinkedList<ItemInstance> itemInstLoadList, LinkedList<MonsterInstance> monInstLoadList)
	{
		Room room;
		
		try
		{
			//byte flag  = dis.readByte(); // Read room flag byte.
			int roomID = dis.readInt();
			byte level = dis.readByte();
			byte i;
			short tID;
			int count, j;
			
			room = new Room(level, roomID);
			
			room.setStud(dis.readBoolean());
			room.setExit(dis.readBoolean());
			room.setLastCleared(dis.readLong());
			
			if(dis.readBoolean())
			{
				Chest tChest = Chest.readChest(dis, itemInstLoadList);
				if(!tChest.chestIsEmpty())
					room.setChest(tChest);
			}
			else
				room.setChest(null);
			
			tID = dis.readShort();
			if(tID != Util.NOTHING)
			{
				ItemInstance tItem = new ItemInstance(tID);
				room.setItem(tItem);
				itemInstLoadList.insert(tItem);
			}
			
			// Read the number of stacks in existence.
			count = dis.readInt();
			
			for(i = 0; i < Util.MON_MAXSTACKSIZE; i++)
			{
				if(i < count)
				{
					j = dis.readInt();
					if(j > 0)
					{
						tID = dis.readShort();
						for(byte k = 0; k < j && k <  Util.MON_MAXSTACKSIZE; k++)
						{
							MonsterInstance tMon = new MonsterInstance(tID);
							
							room.addMonsterToStack(tMon, i);
							monInstLoadList.insert(tMon);
						}
					}
				}
			}
			
			for(;i < count; i++)
			{
				dis.readShort();
				dis.readInt();
			}
			
			// Read the monster item(s)
			count = dis.readInt();
			LinkedList<ItemInstance> nMonsterItems = null;
			
			if(count > 0)
			{
				nMonsterItems = new LinkedList<ItemInstance>();
				for(i = 0; i < count; i++)
				{
					ItemInstance tItem = ItemInstance.readItemInstance(dis);
					nMonsterItems.insert(tItem);
					itemInstLoadList.insert(tItem);
				}
			}
			
			room.monsterItems = nMonsterItems;
			room.monsterGold = dis.readLong();

			count = dis.readInt();
			long longs[] = new long[count];
			for(i = 0; i < count; i++)
				longs[i] = dis.readLong();
			
			room.monsterTypes = Util.UNCOMPRESSLONGTOBOOL(longs, room.monsterTypes.length);
			
			// Read laired monsters
			count = dis.readInt();
			for(i = 0; i < Util.MON_MAXSTACKSIZE; i++)
			{
				if(i < count)
					room.lairedIDs[i] = dis.readShort();
				else
					room.lairedIDs[i] = Util.NOTHING;
			}
			
			// Toss out no longer legal monsters.
			for(; i < count; i++)
				dis.readShort();
		}
		catch(Exception e)
		{
			
			System.err.println("Room - readRoom : " + e);
			return null;
		}
		
		return room;
	}
}
