package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;

import mordorEnums.Alignment;
import mordorEnums.Identification;
import mordorEnums.ItemTypes;
import mordorHelpers.Util;
import mordorMessenger.MordorMessenger;
import structures.LinkedList;
import structures.ListIter;
import structures.SkipIter;
import structures.SkipList;

/**
 * Data structure for the store.
 * @author August Junkala, Nov. 16, 2007
 *
 */
public class Store
{
	private SkipList<StoreRecord> storeInventory;
	
	Store()
	{
		storeInventory = new SkipList<StoreRecord>();
	}
	
	/**
	 * Takes a list of items and
	 * @param items
	 */
	public void addStoreItems(SkipList<Item> items)
	{
		SkipIter<Item> item = items.getIterator();
		
		while(item.next())
		{
			// If this item is a store item AND it is not already in the list,
			// add it into the list.
			if(item.element().isStoreItem() && storeInventory.find((int)item.element().getID()) == null)
				storeInventory.insert(new StoreRecord(item.element()), (int)item.element().getID());
		}
	}
	
	/**
	 * Retrieve the record for a specific item.
	 * @param item
	 * @return StoreRecord or null if no record exists.
	 */
	public StoreRecord findRecord(Item item)
	{
		return storeInventory.find((int)item.getID());
	}
	
	/**
	 * Cause buyer to purchase item of align from the store, if possible.
	 * @param buyer	Player buying the item.
	 * @param item	Item she wants to buy
	 * @param align	Alignment she wants it in.
	 * @return true if a successful sale
	 */
	public boolean buyItemFromStore(Player buyer, ItemInstance item, MordorMessenger message)
	{
		if(item == null)
			return false;
		
		StoreRecord record = findRecord(item.getItem());
		
		// There is no record!
		if(record == null)
			return false;
		
		// Player is too poor.
		if(record.nextSellCost(item.getAlignment()) > buyer.getTotalGold())
		{
			message.postMessage("Not enough gold.");
			return false;
		}
		
		// Player has no place to store the item
		if(!buyer.hasEmptyInventorySlots())
		{
			message.postMessage("No space in inventory.");
			return false;
		}
		
		// Record doesn't have enough copies
		if(!record.alignmentInStore(item.getAlignment()))
			return false;

		// Spend the gold
		buyer.spendGold(record.nextSellCost(item.getAlignment()));
		// Remove a copy of the item
		ItemInstance newItem = record.removeItem(item.getAlignment());
		newItem.setIDLevel(Identification.Everything);
		if(record.isEmptyRecord())
			storeInventory.remove((int)record.getItemID());
		// Add the item.
		buyer.addItem(newItem);
	
		return true;
	}
	
	/**
	 * Cause seller to sell item.  
	 * @param seller
	 * @param item
	 * @return true if a successful sale
	 */
	public boolean sellItemToStore(Player seller, ItemInstance item, MordorMessenger message)
	{
		if(item == null)
			return false;
		
		// We don't buy equipped items.
		if(seller.isItemEquipped(item))
		{
			message.postMessage("Can't sell equipped items.");
			return false;
		}
		
		// We don't buy crests
		if(item.getItem().getType() == ItemTypes.GuildCrest)
		{
			message.postMessage("Can't sell guild crests.");
			return false;
		}
		
		if(item.getItem().getItemBaseValue() < 1)
		{
			message.postMessage("Can't sell that item.");
			return false;
		}
		
		StoreRecord record = storeInventory.find((int)item.getItemID());
		long sellValue = item.getItem().getItemBaseValue();
		if(record == null)
		{
			// No record? Create one then.
			record = new StoreRecord(item);
			storeInventory.insert(record, (int)record.getItemID());
		}
		else
		{
			if(item.isCursed() && item.getIDLevel() == Identification.Everything)
				sellValue = 1;
			else
			{
				// Next 'full id' cost
				sellValue = record.nextBuyCost(item);
				// Adjustment based on how much the player has id'ed the item.
				sellValue *= (long)(Util.STORE_SELL_ID_ADJUST * (item.getIDLevel().value() + 1));
			}

			record.addItem(item);
		}
		
		if(item.getStackSize() > 1)
			item.removeFromStack();
		else
			seller.removeItem(item);
		
		seller.changeGoldOnHand(sellValue);
		
		return true;
	}
	
	/**
	 * Retrieve the record for a specific item.
	 * @param item	Item to retrieve a record for.
	 * @return	StoreRecord or null if the record doesn't exist.
	 */
	public StoreRecord getStoreRecord(Item item)
	{
		return storeInventory.find((int)item.getID());
	}
	
	/**
	 * Retrieve the entire store inventory.
	 * @return
	 */
	public SkipList<StoreRecord> getInventory()
	{
		return storeInventory;
	}
	
	/**
	 * Clear any empty records.
	 */
	public void clearEmptyRecords()
	{
		SkipIter<StoreRecord> record = storeInventory.getIterator();
		while(record.next())
			if(record.element().isEmptyRecord())
				storeInventory.remove(record.key());
	}
	
	/**
	 * Writes the store to the provided output stream
	 * @param dos	DataOutputStream
	 * @return	true if there were no errors.
	 */
	public boolean writeStoreRecord(DataOutputStream dos)
	{
		try
		{
			dos.writeInt(storeInventory.getSize());
			
			SkipIter<StoreRecord> node = storeInventory.getIterator();
			
			if(node == null)
				return true;
			
			while(node.next())
				node.element().writeStoreRecord(dos);
		}
		catch(Exception e)
		{
			System.err.println("Error saving store.\nError: " + e);
			return false;
		}
		return true;
	}
	
	/**
	 * Read the records for the store from the input stream.
	 * 
	 * @param dis	Input stream
	 * @param storeRecordLoadList	A list of all store records.
	 * @return true if there were no serious errors.
	 */
	public boolean readStoreRecord(DataInputStream dis, LinkedList<StoreRecord> storeRecordLoadList)
	{
		try
		{
			storeInventory.clearList();
			int count = dis.readInt();
			
			for(; count > 0; count--)
			{
				StoreRecord record = StoreRecord.readStoreRecord(dis, storeRecordLoadList);
				if(record != null)
					storeInventory.insert(record, (int)record.getItemID());
			}	
		}
		catch(EOFException e)
		{
			System.err.println("Error: Store records are missing.");
		}
		catch(Exception e)
		{
			System.err.println("Error loading store");
			return false;
		}
		return true;
	}
	
	/**
	 * After everything else is done, this will setup all the records to have directly links to their
	 * respective items. It also removes and records for items that no longer exist.
	 * @param items
	 * @param storeRecordLoadList
	 */
	public void finishLoadingStore(SkipList<Item> items, LinkedList<StoreRecord> storeRecordLoadList)
	{
		ListIter<StoreRecord> node = storeRecordLoadList.getIterator();
		
		while(node.next())
		{
			Item item = items.find((int)node.element().getItemID());
			
			if(item == null)
			{
				/* This item no longer exists so get rid of the record. */
				storeInventory.remove((int)node.element().getItemID());
				storeRecordLoadList.remove(node.element());
			}
			else
				node.element().setItem(item);
		}
	}
}
