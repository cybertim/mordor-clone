package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import structures.LinkedList;

import mordorEnums.Alignment;
import mordorEnums.Identification;
import mordorHelpers.Util;

/**
 * Class for specific records in the store. Maintains and
 * item and count for each of its possible alignments. Also
 * handles some of the basic calculations for the record.
 * @author August Junkala, Nov 2007
 *
 */
public class StoreRecord
{
	private Item item;
	private short itemID;
	private byte in_shop[];
	
	public static final byte MAXITEMSPERALIGNMENT = 10;
	
	public StoreRecord(short nItemID)
	{
		itemID = nItemID;
		in_shop = new byte[Alignment.values().length];
	}
	
	public StoreRecord(Item nItem)
	{
		loadItem(nItem);
		in_shop = new byte[Alignment.values().length];
		
		if(!item.isStoreItem())
			in_shop[Alignment.Neutral.value()]++;
	}
	
	public StoreRecord(ItemInstance nItem)
	{
		loadItem(nItem.getItem());
		in_shop = new byte[Alignment.values().length];
		
		if(!item.isStoreItem())
			in_shop[nItem.getAlignment().value()]++;
	}
	
	/**
	 * Load an item into this record. For use with
	 * reading records as the post-update.
	 * @param nItem
	 */
	public void loadItem(Item nItem)
	{
		item = nItem;
		itemID = (item != null) ? item.getID() : Util.NOTHING;
	}
	
	/**
	 * Retrieve the id of the item of this record.
	 * @return	short
	 */
	public short getItemID()
	{
		return itemID;
	}
	
	/**
	 * Retrieve the item of this record.
	 * @return	Item
	 */
	public Item getItem()
	{
		return item;
	}
	
	/**
	 * Sets the item for this record. Intended for final loading use.
	 * @param item
	 */
	public void setItem(Item nItem)
	{
		item = nItem;
		itemID = item.getID();
	}
	
	/**
	 * Retrieve the number of items for a specific alignment.
	 * @param al
	 * @return
	 */
	public byte getCount(Alignment al)
	{
		return in_shop[al.value()];
	}
	
	/**
	 * Sets the count for a specific alignment for an item.
	 * Ignored if this is a standard store item. Also fits
	 * the count within MAXITEMSPERALIGNMENT
	 * @param al	Alignment
	 * @param count	byte
	 */
	public void setCount(Alignment al, byte count)
	{
		if(!item.isStoreItem())
			in_shop[al.value()] = Util.FITBYTE(count, 0, MAXITEMSPERALIGNMENT);
	}
	
	/**
	 * Increment an item.
	 * @param nItem
	 */
	public boolean addItem(ItemInstance nItem)
	{
		if(nItem == null || nItem.getItem() != item)
			return false; // Invalid item

		// For non-store items, we only increment the value if we are below the maximum #
		if(!item.isStoreItem())
		{
			if(in_shop[nItem.getAlignment().value()] < MAXITEMSPERALIGNMENT)
				in_shop[nItem.getAlignment().value()]++;
			return true;
		}
		
		// Store item, so of course it can be 'added'
		if(item.isStoreItem())
			return true;
		
		return false;
	}
	
	/**
	 * Retrieve a copy of the item of the specified alignment.
	 * @param align
	 * @return ItemInstance or null if no copies left (should be checking getCount(al)
	 */
	public ItemInstance removeItem(Alignment align)
	{
		ItemInstance newItem = item.createInstance();
		
		if(item.isStoreItem())
		{
			newItem.setAlignment(align);
			return newItem;
		}
		
		// If none of the item exists, return null;
		if((item.isUnaligned() && in_shop[Alignment.Neutral.value()] < 1) || (in_shop[align.value()] < 1))
			return null;
			
		// Unaligned items are given Neutral alignment for bookkeeping.
		if(item.isUnaligned())
		{
			in_shop[Alignment.Neutral.value()]--;
			newItem.setAlignment(Alignment.Neutral);
		}
		else
		{
			in_shop[align.value()]--;
			newItem.setAlignment(align);
		}
		
		return newItem;
	}
	
	/**
	 * Check if there is any of this alignment in store. If the
	 * item is store item then it is always in store.
	 * @param align
	 * @return true if available for purchase
	 */
	public boolean alignmentInStore(Alignment align)
	{
		// TODO should probably handle 'hands'
		return (item.isStoreItem() || (in_shop[align.value()] > 0));
	}
	
	/**
	 * Based on the number of items of the given alignment in the store,
	 * calculate the next price the store will sell the item at.
	 * @return long
	 */
	public long nextSellCost(Alignment align)
	{
		if(item.isStoreItem())
			return item.getItemBaseValue();
		
		byte count = (item.isUnaligned()) ? in_shop[Alignment.Neutral.value()] : in_shop[align.value()];
		
		long cost = item.getItemBaseValue();
		cost = (long)(cost * (1.0 - (Util.STORE_BUT_UNITADJUST * count)));
		cost *= Util.STORE_SELL_MARKUP;
		
		return cost;
	}
	
	/**
	 * Based on the number of the alignment given in the store, calculate
	 * the next price the store will pay for one copy of this item.
	 * 
	 * @param align	Alignment
	 * @return	long
	 */
	public long nextBuyCost(ItemInstance sellItem)
	{
		return (item.isStoreItem()) ? (long)(sellItem.currentSellValue()) : (long)(sellItem.currentSellValue() * (1.0 / (in_shop[sellItem.getAlignment().value()] + 1)));
	}
	
	/**
	 * Determine if this record is empty. If the item is null,
	 * or it is not a store item and there are no copies of any
	 * alignment, then this is an empty record.
	 * @return true if record is empty
	 */
	public boolean isEmptyRecord()
	{
		if(item == null)
			return true;
		
		if(item.isStoreItem())
			return false;
		
		int count = 0;
		for(Alignment al : Alignment.values())
			count += in_shop[al.value()];
		
		if(count > 0)
			return false;
		
		return true;
	}
	
	/**
	 * Writes this specific record to the provided output stream
	 * @param dos	DataOutputStream
	 * @return	true if there were no errors.
	 */
	public boolean writeStoreRecord(DataOutputStream dos)
	{
		try
		{
			dos.writeShort(itemID);
			
			dos.writeInt(Alignment.values().length);
			for(Alignment al : Alignment.values())
				dos.writeByte(in_shop[al.value()]);
		}
		catch(Exception e)
		{
			System.err.println("Error saving store record for item ID: " + itemID + "\nError: " + e);
			return false;
		}
		return true;
	}
	
	/**
	 * Read a store record from the input stream provided and push the record onto the list
	 * so that the item can be properly loaded later.
	 * 
	 * @param dis	Input stream
	 * @param storeRecordLoadList	A list of all store records.
	 * @return StoreRecord	The new store record.
	 */
	public static final StoreRecord readStoreRecord(DataInputStream dis, LinkedList<StoreRecord> storeRecordLoadList)
	{
		StoreRecord record = null;
		
		try
		{
			// Create a new record for the item read.
			record = new StoreRecord(dis.readShort());
			
			storeRecordLoadList.insert(record);
			
			// Counter for the number of alignments.
			int count = dis.readInt();
			byte i = 0;
			
			// Read the number of each alignment for this item.
			for(; i < Alignment.values().length; i++)
				record.in_shop[i] = (i < count) ? dis.readByte() : (byte)0;
			
			for(; i < count; i++)
				dis.readByte();		
		}
		catch(Exception e)
		{
			System.err.println("Error loading monster");
			return null;
		}
		return record;
	}
}
