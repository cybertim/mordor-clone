package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;

import mordorEnums.Alignment;
import structures.LinkedList;
import structures.ListIter;
import structures.ListNode;
import structures.QuadNode;
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
	
	/* TODO: Constants for store? e.g. realign rate, uncurse rate */
	
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
	 * Cause buyer to purchase item of align from the store, if possible.
	 * @param buyer	Player buying the item.
	 * @param item	Item she wants to buy
	 * @param align	Alignment she wants it in.
	 */
	public void buyItemFromStore(Player buyer, Item item, Alignment align)
	{
		
	}
	
	/**
	 * Cause seller to sell item.  
	 * @param seller
	 * @param item
	 */
	public void sellItemToStore(Player seller, ItemInstance item)
	{
		
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
