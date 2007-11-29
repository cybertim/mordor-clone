package mordorEditor;

import mordorData.BankAccount;
import mordorData.Item;
import mordorData.ItemInstance;
import mordorData.DataBank;
import mordorData.Player;
import mordorData.ItemSpecials;
import mordorEnums.BodyParts;
import mordorHelpers.Util;
import structures.LinkedList;
import structures.ListNode;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class PlayerItemList extends JPanel implements Scrollable, ActionListener
{
	private DataBank dataBank;
	private Player player;
	private ItemInstance[] items;
	private boolean showEquip;
	private byte maxItems;
	private byte[] bodyParts;

	private JTextField[] tfItemName;
	private JCheckBox[] tfItemCursed;
	private JTextField[] tfItemCharges;
	private JButton[] jbItemFind;
	private JCheckBox[] jbItemEquipped;

	public PlayerItemList(DataBank nDataBank, boolean nEquip, Player nPlayer, byte nMaxItems)
	{
		dataBank = nDataBank;
		showEquip = nEquip;
		player = nPlayer;
		maxItems = nMaxItems;
		items = new ItemInstance[maxItems];
		
		setLayout(new GridLayout(maxItems, 1));
		
		tfItemName = new JTextField[maxItems];
		tfItemCursed = new JCheckBox[maxItems];
		tfItemCharges = new JTextField[maxItems];
		jbItemFind = new JButton[maxItems];
		
		if(showEquip)
		{
			jbItemEquipped = new JCheckBox[maxItems];
			bodyParts = new byte[maxItems];
			
			for(byte i = 0; i < BodyParts.values().length; i++)
				bodyParts[i] = Util.NOTHING;
		}
		
		for(byte i = 0; i < maxItems; i++)
		{
			JPanel tPanel = new JPanel();
			tfItemName[i] = new JTextField(8);
			tfItemCursed[i] = new JCheckBox("Cursed:");
			tfItemCharges[i] = new JTextField(3);
			jbItemFind[i] = new JButton("Verify");
			jbItemFind[i].addActionListener(this);
			jbItemFind[i].setToolTipText("Verify this item.");
			
			tPanel.add(new JLabel("Item #" + (i + 1) + ":"));
			tPanel.add(tfItemName[i]);
			tPanel.add(jbItemFind[i]);
			tPanel.add(tfItemCursed[i]);
			tPanel.add(new JLabel("Charges:"));
			tPanel.add(tfItemCharges[i]);

			if(showEquip)
			{
				jbItemEquipped[i] = new JCheckBox("Equipped");
				jbItemEquipped[i].addActionListener(this);
				jbItemEquipped[i].setToolTipText("Equip this item.");
				tPanel.add(jbItemEquipped[i]);
			}
			
			add(tPanel);
		}
	}
	
	/**
	 * Changes the item list for the panel. E.g. when the player changes.
	 * @param nItemList	New item list to be handled.
	 * @param nPlayer	Player that owns the item list.
	 * @return true if change was succesful
	 */
	public boolean setItemList(LinkedList<ItemInstance> nItemList, Player nPlayer)
	{
		ListNode<ItemInstance> tItem = nItemList.getFirstNode();
		
		items = new ItemInstance[maxItems];
		byte count = 0;
		while(tItem != null)
		{
			items[count] = tItem.getElement();
			count++;
			tItem = tItem.getNext();
		}
		player = nPlayer;
		updatePanel();
		return true;
	}
	
	/**
	 * Sets the itemList based on an array of items.
	 * @param nItemList
	 * @param nPlayer
	 * @return
	 */
	public boolean setItemList(ItemInstance[] nItemList, Player nPlayer)
	{
		items = nItemList;
		player = nPlayer;
		updatePanel();
		return true;
	}
	
	/**
	 * Updates the information for all items in the panel.
	 * @return true if succesful
	 */
	public boolean updatePanel()
	{
		for(byte i = 0; i < items.length; i++)
			updateItemInPanel(i);
		
		return true;
	}
	
	/**
	 * Updates the specified item in the panel.
	 * @param itemNumber	Item number in item list.
	 */
	private void updateItemInPanel(byte itemNumber)
	{
		if(items[itemNumber] == null || dataBank.getItem(items[itemNumber].getItemID()) == null)
		{
			tfItemName[itemNumber].setText("");
			tfItemCursed[itemNumber].setSelected(false);
			tfItemCharges[itemNumber].setText("");
			return;
		}
		
		tfItemName[itemNumber].setText(items[itemNumber].getItem().getName());
		if(items[itemNumber].getItem().isCursed())
		{
			tfItemCursed[itemNumber].setEnabled(true);
			tfItemCursed[itemNumber].setSelected(items[itemNumber].isCursed());
		}
		else
		{
			tfItemCursed[itemNumber].setEnabled(false);
		}
		
		if(items[itemNumber].getItem().isSpellCaster(dataBank))
		{
			tfItemCharges[itemNumber].setEnabled(true);
			tfItemCharges[itemNumber].setText("" + items[itemNumber].getChargesLeft());
		}
		else
		{
			tfItemCharges[itemNumber].setEnabled(false);
		}
		
		if(showEquip)
		{
			BodyParts bodyPart = Util.getEquippingBodyPart(items[itemNumber].getItem().getItemType());
			if(bodyPart != BodyParts.None)
			{
				if(player.canPlayerEquip(items[itemNumber].getItem()))
				{
					jbItemEquipped[itemNumber].setEnabled(true);
					jbItemEquipped[itemNumber].setSelected(player.isItemEquipped(itemNumber));
					
					if(jbItemEquipped[itemNumber].isSelected() && bodyParts[bodyPart.value()] == Util.NOTHING)
					{
						bodyParts[bodyPart.value()] = itemNumber;
					}
					else
					{
						bodyParts[bodyPart.value()] = Util.NOTHING;
					}
				}
				else
				{
					jbItemEquipped[itemNumber].setEnabled(true);
					jbItemEquipped[itemNumber].setSelected(false);
				}
			}
			else
			{
				jbItemEquipped[itemNumber].setEnabled(false);
				jbItemEquipped[itemNumber].setSelected(false);
			}
		}
	}
	
	/**
	 * Updates the item list with the data currently in the panel.
	 * @return true if successful.
	 */
	public boolean updateItemList()
	{
		if(!checkItems())
			return false;
		
	//	itemList = new LinkedList<ItemInstance>();
		
		for(byte i = 0; i < maxItems; i++)
		{
			if(tfItemName[i].getText().trim().length() > 0)
			{
			//	ItemInstance nItem = new ItemInstance(dataBank.getItem(tfItemName[i].getText()));
				items[i] = new ItemInstance(dataBank.getItem(tfItemName[i].getText()));
				if(tfItemCursed[i].isEnabled())
					items[i].setCursed(tfItemCursed[i].isSelected());
				if(tfItemCharges[i].isEnabled())
					items[i].setCharges(Util.FITSHORT(Short.parseShort(tfItemCharges[i].getText().trim()), 0, ItemSpecials.MAXSPELLCASTS));
				
				//itemList.insert(nItem);
			}
		}
		return true;
	}
	
	/**
	 * Retrieves the item list.
	 * @return
	 */
	public LinkedList<ItemInstance> getItemList()
	{
		LinkedList<ItemInstance> itemList = new LinkedList<ItemInstance>();
		
		for(byte i = 0; i < items.length; i++)
			if(items[i] != null)
				itemList.insert(items[i]);
		
		return itemList;
	}
	
	/**
	 * Retrieves the present set of items as an array.
	 * @return ItemInstance[] or null if any items are invalid.
	 */
	public ItemInstance[] getItemArray()
	{
		if(!checkItems())
			return null;
		
		ItemInstance[] itemArray = new ItemInstance[maxItems];
		
		for(byte i = 0; i < maxItems; i++)
			if(findItem(i))
			{
				itemArray[i] = new ItemInstance(getItem(i));
				if(tfItemCharges[i].isEnabled())
					itemArray[i].setCharges(Util.FITSHORT(Short.parseShort(tfItemCharges[i].getText().trim()), 0, ItemSpecials.MAXSPELLCASTS));
				if(tfItemCursed[i].isEnabled())
					itemArray[i].setCursed(tfItemCursed[i].isSelected());
			}
		
		return itemArray;
	}
	
	/**
	 * Retrieves a boolean array that contains which item indices are selected.
	 * @return boolean indicating what is and is not equipped.
	 */
	public boolean[] getEquippedItems()
	{
		boolean[] equippedItems = new boolean[maxItems];
		
		for(int i = 0; i < maxItems; i++)
			equippedItems[i] = jbItemEquipped[i].isSelected();
		
		return equippedItems;
	}
	
	/**
	 * Check if the user has entered acceptable data.
	 * @return true if data was correct.
	 */
	public boolean checkItems()
	{
		for(byte i = 0; i < maxItems; i++)
		{
			// Is there an item to check?
			if(tfItemName[i].getText().length() > 0)	
			{
				// Does the item exist?
				if(!findItem(i))
					return false;
				
				// If it has charges, are they valid?
				if(tfItemCharges[i].isEnabled() && !MordorEditor.validityCheckTf(this, tfItemCharges[i], 0, ItemSpecials.MAXSPELLCASTS, "invalid spell charges for item #" + (i + 1)))
					return false;
				
				// If we we allow equipping and if this item is selected, is it actually equippable?
				if(showEquip && jbItemEquipped[i].isSelected() && !isItemEquippable(i))
					return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Searches for the item named in the item name box.
	 * @param itemNumber	index of the item
	 * @return	true if the item exists.
	 */
	private boolean findItem(byte itemNumber)
	{
		return (getItem(itemNumber) != null);
	}
	
	/**
	 * Retrieves the item at the selected index.
	 * @param itemNumber index of item to get
	 * @return Item or null if item doesn't exist.
	 */
	private Item getItem(byte itemNumber)
	{
		String itemName = tfItemName[itemNumber].getText().trim();

		if(itemName.length() < 1)
			return null;
		
		Item tItem = dataBank.getItem(itemName);
		
		if(tItem == null)
		{
			JOptionPane.showMessageDialog(this, "Item #" + (itemNumber + 1) + " does not exist.");
			tfItemName[itemNumber].setText("");
		}
		
		return tItem;
	}
	
	/**
	 * Searches for the item if it exists and if it does exist, loads the data.
	 * @param itemNumber	index of the item
	 * @return true if the item exists.
	 */
	private boolean loadItem(byte itemNumber)
	{	
		if(!findItem(itemNumber))
			return false;
		items[itemNumber] = new ItemInstance(getItem(itemNumber));
		
		updateItemInPanel(itemNumber);
		return true;
	}
	
	/**
	 * Determine if an item is equippable. That is, first, check if it is
	 * an item that can be equipped. If so, determine if any of the other
	 * items listed are of the same time, and if so, are they equipped.
	 * @param itemNumber	itemNumber of the item we are checking
	 * @return True if equippable.
	 */
	private boolean isItemEquippable(byte itemNumber)
	{
		Item tItem = getItem(itemNumber);
		BodyParts bodyPart = Util.getEquippingBodyPart(tItem.getItemType());
		
		if(!player.canPlayerEquip(tItem))
			return false;
		
		for(byte i = 0; i < tfItemName.length; i++)
		{
			if(i != itemNumber)
			{
				// Check if item exists, if does, THEN check body part.
				Item oItem = getItem(i);
				
				// If it is the same bodypart, and selected as equippped, then we have a problem.
				if(oItem != null && bodyPart == Util.getEquippingBodyPart(getItem(i).getItemType()) && jbItemEquipped[i].isSelected())
					return false;
			}
		}
		
		// Check guilds?
		
		return true;
	}
	
	/**
	 * Update anything that may have been removed.
	 */
	public void updateLists()
	{
		for(byte i = 0; i < tfItemName.length; i++)
		{
			if(findItem(i))// Clears items that may no longer exist.
				if(showEquip && jbItemEquipped[i].isSelected()) // Check if we should check equipped status
					jbItemEquipped[i].setSelected(isItemEquippable(i)); // Check if can no longer be equipped
		}
	}
	
	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return new Dimension(this.getWidth(), 300);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// TODO Auto-generated method stub
		return 10;
	}

	public boolean getScrollableTracksViewportHeight() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		for(byte i = 0; i < maxItems; i++)
		{
			if(e.getSource() == jbItemFind[i])
			{
				if(!loadItem(i))
					return;
			}
			else if(showEquip && e.getSource() == jbItemEquipped[i])
			{
				if(jbItemEquipped[i].isSelected())
				{
					// is the item equippable?
					// if not, unselect
					Item tItem = getItem(i);
					if(!isItemEquippable(i))
						jbItemEquipped[i].setSelected(false);
				}
			}
		}
		
	}

}
