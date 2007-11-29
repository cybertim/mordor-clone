package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;

import mordorEnums.Alignment;
import mordorEnums.Identification;
import mordorHelpers.Util;

/**
 * An instance of an item object. This is used to keep track of items existing
 * in the universe. E.g., the specific sword the player is carrying, rather than
 * the kind of sword it is.
 * @author August Junkala
 *
 */
public class ItemInstance
{
	private short itemID;
	private Item item;
	private boolean cursed;
	private short charges;
	private Identification idLevel;
	private Alignment alignment;
	
	public ItemInstance(Item nItem)
	{
		itemID = nItem.getID();
		item = nItem;
		cursed = item.isCursed();
		charges = item.getSpellCasts();
		idLevel = Identification.Nothing;
		alignment = getRandomAlignment();
	}
	
	ItemInstance(short nItemID)
	{
		itemID = nItemID;
		idLevel = Identification.Nothing;
		cursed = false;
		charges = 0;
		idLevel = Identification.Nothing;
		alignment = Alignment.Neutral;
	}
	
	/**
	 * Retrieve a random, legal, alignment
	 * @return Alignment
	 */
	private Alignment getRandomAlignment()
	{
		Random random = new Random(System.currentTimeMillis());
		
		// Try to randomly select a legal alignment.
		for(Alignment al : Alignment.values())
			if(item.getAlignment(al) && random.nextBoolean())
				return al;
		
		// That didn't work, select the first one.
		for(Alignment al : Alignment.values())
			if(item.getAlignment(al))
				return al;
		
		// That didn't work, something is wrong, just pass neutral and give the item
		// a legal alignment of neutral.
		item.setAlignment(Alignment.Neutral, true);
		return Alignment.Neutral;
	}
	
	/**
	 * Retrieve the alignment of this specific item.
	 * @return Alignment
	 */
	public Alignment getAlignment()
	{
		return alignment;
	}
	
	/**
	 * Set the alignment to a specific type.
	 * @param align
	 * @return True if the alignment is legal
	 */
	public boolean setAlignment(Alignment align)
	{
		if(!item.getAlignment(align))
			return false;
		
		alignment = align;
		return true;
	}
	
	/**
	 * Attempts to combine two items. This essentially adds the charges of
	 * one item to another. Note: This only works on items of a usable type.
	 * It also checks to make sure the items have the same ID :)
	 * The other item will have its charges set to 0, but should be disposed of.
	 * @param otherItem	Item to be combined.
	 * @return True if successful
	 */
	public boolean combineItems(ItemInstance otherItem)
	{
		if(itemID == otherItem.getItemID() && item.getItemType().isUsable() && otherItem.getItem().getItemType().isUsable())
		{
			charges += otherItem.charges;
			otherItem.charges = 0;
			return true;
		}
		
		return false;
	}
	
	public Item getItem()
	{
		return item;
	}
	
	public short getItemID()
	{
		return itemID;
	}
	
	public boolean isCursed()
	{
		return cursed;
	}
	
	public boolean isUsable()
	{
		return (charges > 0 || item.getItemType().isUsable());
	}
	
	public short getChargesLeft()
	{
		return charges;
	}
	
	/**
	 * Retrieve the id level of this item.
	 * @return Identification
	 */
	public Identification getIDLevel()
	{
		return idLevel;
	}
	
	public void setItemID(short nItemID)
	{
		itemID = nItemID;
	}
	
	public boolean setItem(Item nItem)
	{
		if(nItem == null)
		{
			System.err.println("ItemInstance setItem error. Item NULL");
			return false;
		}
		
		item = nItem;
		itemID = item.getID();
        return true;
	}
	
	public void setCursed(boolean nCursed)
	{
		cursed = nCursed;
	}
	
	/**
	 * Set the total number of charges for the spell this item casts.
	 * @param nCharges
	 */
	public void setCharges(short nCharges)
	{
		charges = Util.FITSHORT(nCharges, 0, Short.MAX_VALUE);
	}
	
	/**
	 * Adjust the number of charges for the spell this item casts.
	 * @param nCharges
	 */
	public void changeCharges(short nCharges)
	{
		charges = Util.FITSHORT((nCharges + charges), 0, Short.MAX_VALUE);
	}
	
	public boolean writeItemInstance(DataOutputStream dos)
	{
		try
		{
			dos.writeShort(itemID);
			
			dos.writeBoolean(cursed);
			dos.writeShort(charges);
			if(idLevel == null)
				idLevel = Identification.Nothing;
			dos.writeByte(idLevel.value());
			dos.writeByte(alignment.value());
		}
		catch(Exception e)
		{
			System.err.println("ItemInstance write error.\nID: " + itemID + "\nError: " + e);
			return false;
		}
		
		return true;
	}
	
	public static final ItemInstance readItemInstance(DataInputStream dis)
	{
		ItemInstance tItem = null;
		short itemID = Util.NOTHING;
		try
		{
			itemID = dis.readShort();
			
			if(itemID == Util.NOTHING || itemID < 0)
				return null;
			tItem = new ItemInstance(itemID);
			
			tItem.setCursed(dis.readBoolean());
			tItem.setCharges(dis.readShort());
			tItem.idLevel = Identification.type(dis.readByte());
			tItem.alignment = Alignment.type(dis.readByte());
		}
		catch(Exception e)
		{
			
			System.err.println("ItemInstance read error.\nID: " + itemID + "\nError: " + e);
			return null;
		}
		
		return tItem;
	}
	
	public String toString()
	{
		return item.getName();
	}
}