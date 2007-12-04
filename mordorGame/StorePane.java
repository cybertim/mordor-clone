package mordorGame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import structures.LinkedList;
import structures.ListIter;

import mordorData.DataBank;
import mordorData.Item;
import mordorData.ItemInstance;
import mordorData.Player;
import mordorData.Store;
import mordorData.StoreRecord;
import mordorEnums.Alignment;
import mordorEnums.BodyParts;
import mordorEnums.Identification;
import mordorHelpers.Util;
import mordorMessenger.MordorMessengerDestination;

/**
 * Class for the store pane in the game.
 * @author August Junkala, Nov 26, 2007
 *
 */
public class StorePane extends JPanel implements ActionListener {
	
	private StoreInventoryPane inventory;
	private JButton jbExit;
	private JButton jbUncurse, jbCombine, jbInfoSell, jbSell, jbID, jbBuy, jbInfoBuy;
	private JButton[] jbAlign;
	private JTextField jlBuyCost, jlSellValue, jlIDCost, jlUncurseCost, jlCombineItems, jlBuyItem;
	private JTextField tfBuySearch;
	private long IDCost, uncurseCost;
	private ItemInstance buyItem;
	private LinkedList<ItemInstance> combineItems;
	private StoreItemLabel ipSell, ipCombine, ipUncurse;
	
	private Mordor parent;
	private Player player;
	private DataBank databank;
	private Store store;
	
	public StorePane(Mordor theParent, Player activePlayer, DataBank theDatabank)
	{
		parent = theParent;
		player = activePlayer;
		databank = theDatabank;
		store = databank.getStore();
		
		IDCost = uncurseCost = 0;
		combineItems = new LinkedList<ItemInstance>();
		
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();

		JPanel lTopPane = new JPanel();
		JPanel ltTopPane = new JPanel();
		JPanel ltBotPane = new JPanel();
		JPanel lBotPane = new JPanel();
		

		// Uncurse panel
		ltTopPane.setBorder(BorderFactory.createTitledBorder("Uncurse Items"));
		ltTopPane.setLayout(new GridLayout(2, 1));
		
		JPanel ucItem = new JPanel();
		JPanel ucFunc = new JPanel();
		ipUncurse = new StoreItemLabel(this);
		jbUncurse = new JButton("Uncurse");
		jlUncurseCost = new JTextField(8);
		
		ipUncurse.updateLabel();
		jbUncurse.setToolTipText("Uncurse item.");
		jbUncurse.addActionListener(this);
		jlUncurseCost.setEditable(false);
		
		ucItem.add(new JLabel("Item"));
		ucItem.add(ipUncurse);
		ucFunc.add(new JLabel("Item"));
		ucFunc.add(jlUncurseCost);
		ucFunc.add(jbUncurse);
		
		ltTopPane.add(ucItem);
		ltTopPane.add(ucFunc);
		
		// Combine panel
		ltBotPane.setBorder(BorderFactory.createTitledBorder("Combine Items"));
		ltBotPane.setLayout(new GridLayout(2, 1));
		
		JPanel cbItem = new JPanel();
		JPanel cbFunc = new JPanel();
		
		ipCombine = new StoreItemLabel(this);
		jlCombineItems = new JTextField(8);
		jbCombine = new JButton("Combine");
		
		ipCombine.updateLabel();
		jbCombine.setToolTipText("Combine items.");
		jbCombine.addActionListener(this);
		jlCombineItems.setEditable(false);
		
		cbItem.add(new JLabel("Item"));
		cbItem.add(ipCombine);
		cbFunc.add(new JLabel("Items"));
		cbFunc.add(jlCombineItems);
		cbFunc.add(jbCombine);
		
		ltBotPane.add(cbItem);
		ltBotPane.add(cbFunc);
		
		// Identify, realign, sell
		lBotPane.setBorder(BorderFactory.createTitledBorder("ID, Align, Sell"));
		lBotPane.setLayout(new GridLayout(4, 1));
		
		JPanel sirItem = new JPanel();
		JPanel sirValue = new JPanel();
		JPanel sirButton = new JPanel();
		JPanel sirIDCost = new JPanel();
		
		ipSell = new StoreItemLabel(this);
		jbAlign = new JButton[Alignment.values().length];
		for(Alignment al : Alignment.values())
		{
			jbAlign[al.value()] = new JButton("" + al.shortName());
			jbAlign[al.value()].setToolTipText("Align to " + al.toString());
			jbAlign[al.value()].addActionListener(this);
			sirButton.add(jbAlign[al.value()]);
		}
		jbInfoSell = new JButton("Info");
		jbSell = new JButton("Sell");
		jbID = new JButton("ID");
		jlSellValue = new JTextField(8);
		jlIDCost = new JTextField(8);
		
		jlSellValue.setEditable(false);
		jlIDCost.setEditable(false);
		
		jbInfoSell.setToolTipText("Info on this item.");
		jbSell.setToolTipText("Sell the item.");
		jbID.setToolTipText("Identify the item.");
		
		jbInfoSell.addActionListener(this);
		jbSell.addActionListener(this);
		jbID.addActionListener(this);
		
		sirItem.add(new JLabel("Item"));
		sirItem.add(ipSell);
		sirValue.add(new JLabel("Value"));
		sirValue.add(jlSellValue);
		sirButton.add(jbInfoSell);
		sirButton.add(jbSell);
		sirButton.add(jbID);
		sirIDCost.add(new JLabel("ID Cost"));
		sirIDCost.add(jlIDCost);
		sirIDCost.add(jbID);
		
		lBotPane.add(sirItem);
		lBotPane.add(sirValue);
		lBotPane.add(sirButton);
		lBotPane.add(sirIDCost);
		
		lTopPane.setLayout(new BorderLayout());
		lTopPane.add(ltTopPane, BorderLayout.NORTH);
		lTopPane.add(ltBotPane, BorderLayout.SOUTH);
		
		leftPane.setLayout(new BorderLayout());
		leftPane.add(lTopPane, BorderLayout.NORTH);
		leftPane.add(lBotPane, BorderLayout.SOUTH);
		
		
		// Inventory & Buy pane
		rightPane.setLayout(new BorderLayout());

		inventory = new StoreInventoryPane(store, this);
		JScrollPane inventoryScroll = new JScrollPane(inventory);
		
		JPanel buyPane = new JPanel();
		buyPane.setLayout(new GridLayout(3, 1));
		
		jbBuy = new JButton("Buy");
		jbInfoBuy = new JButton("Info");
		jlBuyCost = new JTextField(8);
		jlBuyItem = new JTextField(15);
		tfBuySearch = new JTextField(10);
		buyItem = null;
		
		jlBuyCost.setEditable(false);
		jlBuyItem.setEditable(false);
		
		jbBuy.setToolTipText("Buy chosen item.");
		jbInfoBuy.setToolTipText("Retrieve information on chosen item.");
		
		jbBuy.addActionListener(this);
		jbInfoBuy.addActionListener(this);
		tfBuySearch.addActionListener(this);
		
		JPanel jlBItem = new JPanel();
		JPanel jlBFunc = new JPanel();
		JPanel jlBSearch = new JPanel();
		
		jlBItem.add(new JLabel("Item"));
		jlBItem.add(jlBuyItem);
		jlBFunc.add(new JLabel("Cost"));
		jlBFunc.add(jlBuyCost);
		jlBFunc.add(jbBuy);
		jlBFunc.add(jbInfoBuy);
		jlBSearch.add(new JLabel("Search"));
		jlBSearch.add(tfBuySearch);
		
		buyPane.add(jlBItem);
		buyPane.add(jlBFunc);
		buyPane.add(jlBSearch);
		
		JPanel ibPane = new JPanel();
		ibPane.setLayout(new BorderLayout());
		ibPane.add(inventoryScroll, BorderLayout.CENTER);
		ibPane.add(buyPane, BorderLayout.SOUTH);
		
		JPanel exitPane = new JPanel();
		exitPane.setLayout(new BorderLayout());
		
		jbExit = new JButton("Exit");
		jbExit.addActionListener(this);
		exitPane.add(new JLabel(""), BorderLayout.CENTER);
		exitPane.add(jbExit, BorderLayout.EAST);
		
		rightPane.add(ibPane, BorderLayout.CENTER);
		rightPane.add(exitPane, BorderLayout.SOUTH);
		
		setLayout(new BorderLayout());
		add(leftPane, BorderLayout.WEST);
		add(rightPane, BorderLayout.EAST);
	}
	
	public void itemUpdated(StoreItemLabel ip, ItemInstance oldItem, ItemInstance newItem)
	{
		if(ip == ipUncurse)
		{
			
			// Set the uncurse cost. Unless there is no item or the item is
			// not cursed.
			updateUncurse();
		}
		else if(ip == ipCombine)
		{
			// If the item has changed, clear the list.
			if(newItem == null)
				jlCombineItems.setText("");
			else if(newItem.getIDLevel() != Identification.Everything)
			{
				ipCombine.changeItem(null);
				jlCombineItems.setText("");
			}
			else if(oldItem != null && !oldItem.equivalent(newItem))
				jlCombineItems.setText("");
			else
			{
				// Else find the index of the old item and add it.
				byte playerIndex = player.getItemIndex(oldItem);
				
				// The old item no longer exists.
				if(playerIndex == Util.NOTHING)
					return;
				
				// No items yet, add the index of this one to the list.
				// Else, if the item is not in the list, add it.
				if(jlCombineItems.getText().length() < 1)
				{
					jlCombineItems.setText("" + playerIndex);
					combineItems = new LinkedList<ItemInstance>();
				}
				else if(!jlCombineItems.getText().contains("" + playerIndex))
				{
					jlCombineItems.setText(", " + playerIndex);
					combineItems.insertLast(oldItem);
				}
			}
		}
		else if(ip == ipSell)
		{
			updateSellPane();
		}
	}
	
	private void updateLeftPane()
	{
		updateUncurse();
		updateCombine();
		updateSellPane();
	}
	
	/**
	 * Update the whole of the sell pane.
	 */
	private void updateSellPane()
	{
		ItemInstance item = ipSell.getItem();
		
		if(item != null && player.getItemIndex(item) == Util.NOTHING)
		{
			ipSell.changeItem(null);
			item = null;
		}
		
		if(item == null)
		{
			// No item
			
			jbSell.setEnabled(false);
			jbID.setEnabled(false);
			ipSell.updateLabel();

			jlSellValue.setText("");
			jlIDCost.setText("");
			
			for(Alignment al : Alignment.values())
				jbAlign[al.value()].setEnabled(true);
			
			return;
		}
		
		updateID();
		updateSell();
	}
	
	/**
	 * Updates the sell data whenever something changes.
	 * @param newItem
	 */
	private void updateSell()
	{
		ItemInstance item = ipSell.getItem();
		
		long sellValue = 0;
		StoreRecord record = store.findRecord(item.getItem());
		
		if(record != null)
			sellValue = record.nextBuyCost(item);
		else if(item.isCursed() && item.getIDLevel() == Identification.Everything)
			sellValue = 1;
		else
			sellValue = item.currentSellValue();
		
		jbSell.setEnabled(true);

		// Setup alignments
		if(item.getItem().isUnaligned())
			for(Alignment al : Alignment.values())
				jbAlign[al.value()].setEnabled(true);
		else
			for(Alignment al : Alignment.values())
			{
				if(al == item.getAlignment())
					jbAlign[al.value()].setEnabled(false);
				else
					jbAlign[al.value()].setEnabled(item.getItem().getAlignment(al));
			}
		
		// Update the labels
		ipSell.updateLabel();
		jlIDCost.setText("" + IDCost);
		jlSellValue.setText("" + sellValue);
	}
	
	private void updateID()
	{
		ItemInstance item = ipSell.getItem();
		
		if(item.getIDLevel() == Identification.Everything)
		{
			// Item totally identified.
			IDCost = 0;
			jbID.setEnabled(false);
		}
		else
		{
			// Item that isn't yet identified
			IDCost = (long)(item.currentSellValue() * Util.STORE_ID_ADJUSTMENT);
			jbID.setEnabled(true);
		}
	}
	
	private void updateUncurse()
	{
		ItemInstance item = ipUncurse.getItem();
		if(item == null)
		{
			ipUncurse.updateLabel();
			jlUncurseCost.setText("");
		}
		else if(player.getItemIndex(ipUncurse.getItem()) == Util.NOTHING)
		{
			ipUncurse.changeItem(null);
			jlUncurseCost.setText("");
		}
		else
		{
			ipUncurse.updateLabel();
			uncurseCost = (!item.isCursed() || item.getIDLevel() != Identification.Everything) ? 0 : (long)(item.getItem().getItemBaseValue() * Util.ITEM_UNCURSE_MULTIPLIER);
			jlUncurseCost.setText("" + uncurseCost);
		}
	}
	
	private void updateCombine()
	{
		ItemInstance item =ipCombine.getItem();
		if(item == null)
		{
			ipCombine.updateLabel();
			jlCombineItems.setText("");
		}
		else if(player.getItemIndex(item) == Util.NOTHING)
		{
			ipCombine.changeItem(null);
			jlCombineItems.setText("");
		}
		else
		{
			ipCombine.updateLabel();
		}
	}
	
	public void recordChosen(StoreRecord newRecord)
	{
		// Something is wonky
		if(newRecord == null || newRecord.isEmptyRecord())
		{
			buyItem = null;
			jlBuyCost.setText("");
			jlBuyItem.setText("");
			return;
		}
		
		buyItem = newRecord.getItem().createInstance();
		
		if(!newRecord.getItem().isUnaligned() && !newRecord.isEmptyRecord())
		{
			// How many alignments are available?
			int count = 0;
			for(Alignment al : Alignment.values())
				if(newRecord.alignmentInStore(al))
					count++;
			
			Alignment aligns[] = new Alignment[count];

			// Which ones are available?
			count = 0;
			for(Alignment al : Alignment.values())
				if(newRecord.alignmentInStore(al))
				{
					aligns[count] = al;
					count++;
				}
			
			count = JOptionPane.showOptionDialog(this, "Which Alignment?", "Alignment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, aligns, aligns[0]);
			
			// User chose an alignment.
			if(count != JOptionPane.CLOSED_OPTION)
				buyItem.setAlignment(aligns[count]);
			else
			{
				// Opted not to choose an alignment, clear everything.
				buyItem = null;
				jlBuyCost.setText("");
				jlBuyItem.setText("");
				return;
			}
		}
		
		// Update the buy pane info.
		jlBuyItem.setText(buyItem.toString());
		jlBuyCost.setText("" + newRecord.nextSellCost(buyItem.getAlignment()));
	}

	public void actionPerformed(ActionEvent e)
	{
		ItemInstance item;
		
		if(e.getSource() == jbUncurse)
		{
			item = ipUncurse.getItem();
			
			// Is this item even cursed.
			if(item != null && item.isCursed() && item.getIDLevel() == Identification.Everything)
			{
				// Can the player afford to uncurse
				if(player.spendGold(uncurseCost))
				{
					// Change to uncursed.
					item.setCursed(false);
					
					// Unequip the item (if it bounded to the player)
					player.unequipItem(item);
				}
			}

			databank.getMessenger().postFlag(MordorMessengerDestination.PlayerSIC);
			updateLeftPane();
		}
		else if(e.getSource() == jbCombine && combineItems.getSize() > 0)
		{
			item = ipCombine.getItem();
			
			// Only objects can be combined.
			if(item.getItem().getItemType().getEquippingPart() != BodyParts.Objects)
				return;
			
			// Parse the combination list and add the charges from those items to this item.
			ListIter<ItemInstance> node = combineItems.getIterator();
			
			while(node.next())
			{
				item.changeCharges(node.element().getChargesLeft());
				player.removeItem(node.element());
			}

			databank.getMessenger().postFlag(MordorMessengerDestination.PlayerSIC);
			updateLeftPane();
		}
		else if(e.getSource() == jbInfoSell)
		{
			databank.getMessenger().postThing(MordorMessengerDestination.ItemInfo, ipSell.getItem());
		}
		else if(e.getSource() == jbSell)
		{
			item = ipSell.getItem();
			
			// Don't sell if the item is equipped or doesn't exist.
			if(item == null || player.isItemEquipped(item))
				return;
			
			
			if(store.sellItemToStore(player, item, databank.getMessenger()))
			{
				// Update sellItem and sellCost
				inventory.updateInventory();
				
				//Inform SIC pane of change
				databank.getMessenger().postFlag(MordorMessengerDestination.PlayerSIC);
				
				// Update IP panes here.
				updateLeftPane();
			}
		}
		else if(e.getSource() == jbID)
		{
			item = ipSell.getItem();
			
			// Can't ID an item that doesn't exist!
			if(item == null)
				return;
			
			if(player.spendGold(IDCost))
			{
				// Increase the ID Level
				item.setIDLevel(Identification.type((byte)(item.getIDLevel().value() + 1)));
				databank.getMessenger().postFlag(MordorMessengerDestination.PlayerSIC);
				updateLeftPane();
			}
		}
		else if(e.getSource() == jbBuy && buyItem != null)
		{
			if(store.buyItemFromStore(player, buyItem, databank.getMessenger()))
			{
				buyItem = null;
				jlBuyCost.setText("");
				jlBuyItem.setText("");
				inventory.updateInventory();
				databank.getMessenger().postFlag(MordorMessengerDestination.PlayerSIC);
			}
		}
		else if(e.getSource() == jbInfoBuy && buyItem != null)
		{
			databank.getMessenger().postThing(MordorMessengerDestination.ItemInfo, buyItem);
		}
		else if(e.getSource() == tfBuySearch)
		{
			Item s_item = databank.getItem(tfBuySearch.getText().trim());
			
			if(s_item == null)
				return;
			
			// Search for record.
			StoreRecord record = store.findRecord(s_item);
			
			// Activate 'selected record' box
			if(record != null)
				recordChosen(record);
		}
		else if(e.getSource() == jbExit)
		{
			parent.exitStore();
		}
		
		for(Alignment al : Alignment.values())
		{
			if(e.getSource() == jbAlign[al.value()])
			{
				item = ipSell.getItem();
				// Can't realign something that doesn't exist, is equipped
				// or haven't yet identified.
				if(item == null || player.isItemEquipped(item) || item.getIDLevel() == Identification.Everything)
					return;
				
				// If we can afford it, realign it.
				if(player.spendGold((long)(item.getItem().getItemBaseValue() * Util.STORE_ALIGN_ADJUSTMENT)))
				{
					item.setAlignment(al);
					databank.getMessenger().postFlag(MordorMessengerDestination.PlayerSIC);
					updateSellPane();
				}
			}
		}
	}
}
