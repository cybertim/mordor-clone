package mordorData;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import mordorHelpers.Util;


import structures.LinkedList;
import structures.ListNode;


public class BankAccount
{
	private long gold;
	private LinkedList<ItemInstance> items;
	
	public static final byte MAXITEMSINBANK = 40;
	
	BankAccount()
	{
		gold = 0;
		items = new LinkedList<ItemInstance>();
	}
	
	public long getGold()
	{
		return gold;
	}
	
	/**
	 * Set the total gold in this account.
	 * @param newGold
	 */
	public void setGold(long newGold)
	{
		gold = Util.FITLONG(newGold, Long.MIN_VALUE, Long.MAX_VALUE);
	}
	
	/**
	 * Adjust the amount of gold in this account.
	 * @param addGold
	 */
	public void changeGold(long addGold)
	{
		gold = Util.FITLONG(gold + addGold, Long.MIN_VALUE, Long.MAX_VALUE);
	}
	
	public void setItemList(LinkedList<ItemInstance> nItemList)
	{
		items = nItemList;
	}
	
	public boolean addItem(ItemInstance newItem)
	{
		if(items.getSize() > MAXITEMSINBANK)
			return false;
		
		items.insert(newItem);
		return true;
	}
	
	public ItemInstance removeItem(ItemInstance oldItem)
	{
		return items.remove(oldItem);
	}
	
	/**
	 * Retrieve all the items;
	 * @return LinkedList<ItemInstance>
	 */
	public LinkedList<ItemInstance> getItems()
	{
		return items;
	}
	
	public String[] getItemNames()
	{
		ListNode<ItemInstance> tNode = items.getFirstNode();
		String[] itemNames = new String[items.getSize()];
		short itemCount = 0;
		
		if(items.isEmpty())
		{
			itemNames = new String[1];
			itemNames[0] = "None";
		}
		
		while(tNode != null)
		{
			itemNames[itemCount] = "Fix getItemNames()";
			
			itemCount++;
			tNode = tNode.getNext();
		}
		
		return itemNames;
	}
	
	public boolean writeBankAccount(DataOutputStream dos)
	{
		try
		{
			dos.writeLong(gold);
			
			dos.writeInt(items.getSize());
			ListNode<ItemInstance> tNode = items.getFirstNode();
			
			while(tNode != null)
			{
				tNode.getElement().writeItemInstance(dos);
				tNode = tNode.getNext();
			}
		}
		catch(Exception e)
		{
			System.err.println("BankAccount - writeBankAccount : " + e);
			return false;
		}
		
		return true;
	}
	
	public static final BankAccount readBankAccount(DataInputStream dis, LinkedList<ItemInstance> itemInstLoads)
	{
		BankAccount tAccount = new BankAccount();
		try
		{
		//	ItemInstance nextItem = null;
			int count;
			
			tAccount.setGold(dis.readLong());
			count = dis.readInt();
			
			for(int i = 0; i < count; i++)
			{
				ItemInstance nextItem = ItemInstance.readItemInstance(dis);
				tAccount.addItem(nextItem);
				itemInstLoads.insert(nextItem);
			}
		}
		catch(Exception e)
		{
			
			System.err.println("BankAccount - readBankAccount : " + e);
			return new BankAccount();
		}
		
		return tAccount;
	}
}
