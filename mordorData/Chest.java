package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;

import structures.LinkedList;
import structures.ListIter;

import mordorEnums.Trap;
import mordorHelpers.Util;

public class Chest
{
	/*private static final byte TRAP_NONE = 0;
	private static final byte TRAP_SLIME = 1;
	//A slime trap will only destroy items in the inventory of the character opening the trap. Avoid rooms which tend to spawn item-destroying monsters for the same reason (once you identify such rooms).
	// trap destroys any item, but acid from monster is up to mosnters level + 2
	private static final byte TRAP_FEAR = 2;
	//ikewise, a fear trap will only scare away the companions of the character opening the chest. So don't bother giving your thief companions.
	private static final byte TRAP_TELEPORT = 3;
	private static final byte TRAP_FATE = 4;
	//All Fate does is randomizes your SP and HP, which means you're likely to get a few SP from it.
	// teleport traps - level 4
	private static final byte TRAP_WITHERING = 5;
	// withering/fear on level 4, withering = age 4 weeks
	// slime/blackout traps = level 7
	private static final byte TRAP_BLACKOUT = 6;
	private static final byte TRAP_FLAME = 7;
	// black out removes all spell enhancements (like an extinguish square)
	// fate = level 8*/
	
	//private static final int CHEST_MAXGOLD = Integer.MAX_VALUE;
	
	public static final byte MAXITEMSINCHEST = 4;
	
	private long gold;
	private LinkedList<ItemInstance> items;
	
	private int lockLevel;
	private boolean isBox;
	private boolean magicLock;
	
	private Trap trapType;
	
	public Chest()
	{
		gold = 0;
		isBox = true;
		lockLevel = 0;
		magicLock = false;
		trapType = Trap.None;
		items = new LinkedList<ItemInstance>();
	}
	
	public void fillBox(LinkedList<ItemInstance> nItems, byte level)
	{
		isBox = (nItems == null || nItems.getSize() < 1);
		items = nItems;
		
		Random random = new Random(System.currentTimeMillis());
		
		// Make sure there aren't too many items.
		while(nItems.getSize() > MAXITEMSINCHEST)
			nItems.removeLast();
		
		Trap[] availableTraps = Trap.allowedTraps(level);
		trapType = availableTraps[random.nextInt(availableTraps.length)];
		
		// Level * 2 chance of a magic lock, capped at 50%
		magicLock = (random.nextInt(100) < (((level + 1) << 1) % 50));
		
		// ((Level + 2) * 2) + 1 is the range of lock levels allowed. So on level 10, then 25 is the lock level.
		// TODO Fix this w/ opening code.
		lockLevel = (random.nextInt((level + 2) << 1) + 1);
	}
	
	public boolean openChest(Player player)
	{
		// attempt to open chest, if succesful modify the player accordingly
		// and return true
		

		//A slime trap will only destroy items in the inventory of the character opening the trap. Avoid rooms which tend to spawn item-destroying monsters for the same reason (once you identify such rooms).
		// trap destroys any item, but acid from monster is up to mosnters level + 2
		//ikewise, a fear trap will only scare away the companions of the character opening the chest. So don't bother giving your thief companions.
		//All Fate does is randomizes your SP and HP, which means you're likely to get a few SP from it.
		// black out removes all spell enhancements (like an extinguish square)
		return false;
	}
	
	public Trap getTrapType()
	{
		return trapType;
	}
	
	/**
	 * Retrieve the amount of gold in the chest.
	 * @return
	 */
	public long getGold()
	{
		return gold;
	}
	
	public LinkedList<ItemInstance> getItems()
	{
		return items;
	}

	public int getLockLevel()
	{
		return lockLevel;
	}
	
	public boolean isBox()
	{
		return isBox;
	}
	
	public boolean isMagic()
	{
		return magicLock;
	}
	
	public void setTrapType(Trap nTrap)
	{
		trapType = nTrap;
	}
	
	public void setGold(int nGold)
	{
		gold = nGold;
	}
	
	public void setItems(LinkedList<ItemInstance> nItems)
	{
		items = nItems;
	}
	
	public void setLockLevel(byte nLockLevel)
	{
		lockLevel = nLockLevel;
	}
	
	public void setIsBox(boolean nIsBox)
	{
		isBox = nIsBox;
	}
	
	public void setMagicLock(boolean nMagicLock)
	{
		magicLock = nMagicLock;
	}
	
	/**
	 * Determine if this is an empty chest.
	 * @return true if the chest is empty.
	 */
	public boolean chestIsEmpty()
	{
		return (gold <= 0 && (items == null || items.getSize() < 1));
	}
	
	public boolean writeChest(DataOutputStream dos)
	{
		try
		{
			dos.writeInt(lockLevel);
			dos.writeLong(gold);
			dos.writeBoolean(isBox);
			dos.writeBoolean(magicLock);
			dos.writeByte(trapType.value());
			if(items != null || items.getSize() < 1)
			{
				dos.writeInt(items.getSize());
				ListIter<ItemInstance> iNode = items.getIterator();
				while(iNode.next())
					dos.writeShort(iNode.element().getItemID());
			}
			else
				dos.writeInt(Util.NOTHING);
		}
		catch(Exception e)
		{
			System.err.println("Room - writeRoom : " + e);
			return false;
		}
		
		return true;
	}
	
	public static final Chest readChest(DataInputStream dis, LinkedList<ItemInstance> itemInstLoads)
	{
		Chest chest = new Chest();
		try
		{
			chest.setLockLevel((byte)dis.readInt());
			chest.gold = dis.readLong();
			chest.setIsBox(dis.readBoolean());
			chest.magicLock = dis.readBoolean();
			chest.setTrapType(Trap.type(dis.readByte()));
			
			//short tItemID = 
		//		dis.readShort();
			int count = dis.readInt();
			int i = 0;
			if(count > 0)
			{
				for(; i < count && i < MAXITEMSINCHEST; i++)
				{
					ItemInstance nItem = new ItemInstance(dis.readShort());
					chest.items.insert(nItem);
					itemInstLoads.insert(nItem);
				}
			}
			
			for(; i < count; i++)
				dis.readShort();
			
	/*		if(tItemID == Util.NOTHING)
	//			chest.setItem(null);
			else
			{
				ItemInstance nItem;
				nItem = new ItemInstance(tItemID);
				chest.setItem(nItem);
				itemInstLoads.insert(nItem);
			}*/
			
		}
		catch(Exception e)
		{
			
			System.err.println("BankAccount - readBankAccount : " + e);
			return new Chest();
		}
		
		return chest;
	}
}
