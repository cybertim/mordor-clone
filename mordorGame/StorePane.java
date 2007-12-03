package mordorGame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import structures.LinkedList;
import structures.ListIter;
import structures.ListNode;

import mordorData.DataBank;
import mordorData.ItemInstance;
import mordorData.Player;
import mordorData.Store;
import mordorEnums.Alignment;
import mordorEnums.BodyParts;
import mordorEnums.Identification;
import mordorHelpers.Util;
import mordorMessenger.MordorMessengerDestination;
import mordorMessenger.MordorMessengerEvent;
import mordorMessenger.MordorMessengerListener;
import mordorShared.StoreInventory;

/**
 * Class for the store pane in the game.
 * @author August Junkala, Nov 26, 2007
 *
 */
public class StorePane extends JPanel implements ActionListener,
		MordorMessengerListener {
	
	private StoreInventory inventory;
	private JButton jbUncurse, jbCombine, jbInfoSell, jbSell, jbID, jbBuy, jbInfoBuy;
	private JButton[] jbAlign;
	private JLabel jlBuyCost, jlSellValue, jlIDCost, jlUncurseCost, jlCombineItems, jlBuyItem;
	private JTextField tfBuySearch;
	private long buyCost, sellValue, IDCost, uncurseCost;
	private LinkedList<ItemInstance> combineItems;
	private StoreItemLabel ipSell, ipCombine, ipUncurse;
	
	private Player player;
	private DataBank databank;
	private Store store;
	
	public StorePane(Player activePlayer, DataBank theDatabank, Store theStore)
	{
		player = activePlayer;
		databank = theDatabank;
		store = theStore;
		
		buyCost = sellValue = IDCost = uncurseCost = 0;
		combineItems = new LinkedList<ItemInstance>();
		
		inventory = new StoreInventory(store, false, databank.getMessenger());
		
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();

		JPanel lTopPane = new JPanel();
		JPanel ltTopPane = new JPanel();
		JPanel ltBotPane = new JPanel();
		JPanel lBotPane = new JPanel();
		

		// Uncurse panel
		ltTopPane.setBorder(BorderFactory.createLoweredBevelBorder());
		ltTopPane.setLayout(new GridLayout(3, 1));
		
		JPanel ucItem = new JPanel();
		JPanel ucFunc = new JPanel();
		ipUncurse = new StoreItemLabel(this);
		jbUncurse = new JButton("Uncurse");
		jlUncurseCost = new JLabel("");
		
		ipUncurse.updateLabel();
		jbUncurse.setToolTipText("Uncurse item.");
		jbUncurse.addActionListener(this);
		
		ucItem.add(new JLabel("Item"));
		ucItem.add(ipUncurse);
		ucFunc.add(new JLabel("Item"));
		ucFunc.add(jlUncurseCost);
		ucFunc.add(jbUncurse);
		
		ltTopPane.add(new JLabel("Uncurse Items"));
		ltTopPane.add(ucItem);
		ltTopPane.add(ucFunc);
		
		// Combine panel
		ltBotPane.setBorder(BorderFactory.createTitledBorder("Combine Items"));
		ltBotPane.setLayout(new GridLayout(2, 1));
		
		JPanel cbItem = new JPanel();
		JPanel cbFunc = new JPanel();
		
		ipCombine = new StoreItemLabel(this);
		jlCombineItems = new JLabel("");
		jbCombine = new JButton("Combine");
		
		ipCombine.updateLabel();
		jbCombine.setToolTipText("Combine items.");
		jbCombine.addActionListener(this);
		
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
		jlSellValue = new JLabel("");
		jlIDCost = new JLabel("");
		
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
		/*this.jbBuy
		jbInfoBuy
		this.jlBuyCost
		this.jlBuyItem
		tfBuySearch
		buyCost (long)*/
		
		setLayout(new BorderLayout());
		add(leftPane, BorderLayout.WEST);
		add(rightPane, BorderLayout.EAST);
	}
	
	public void itemUpdated(StoreItemLabel ip, ItemInstance oldItem, ItemInstance newItem)
	{
		if(ip == ipUncurse)
		{
			
			// Set the uncurse cost. Unless there is no item or the item is
			// no cursed.
			uncurseCost = (newItem == null || !newItem.isCursed()) ? 0 : (long)(newItem.getItem().getItemBaseValue() * Util.ITEM_UNCURSE_MULTIPLIER);
			jlUncurseCost.setText("" + uncurseCost);
		}
		else if(ip == ipCombine)
		{
			// If the item has changed, clear the list.
			if(!oldItem.equivalent(newItem))
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
	
	/**
	 * Update the whole of the sell pane.
	 */
	private void updateSellPane()
	{
		ItemInstance item = ipSell.getItem();
		
		if(item == null)
		{
			// No item
			
			sellValue = 0;
			jbSell.setEnabled(false);
			
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
		
		// Get the sell cost.
		sellValue = (item.isCursed() && item.getIDLevel() == Identification.Everything) ? 1 : (long)((Util.STORE_SELL_ID_ADJUST * (item.getIDLevel().value() + 1))  * store.getStoreRecord(item.getItem()).nextBuyCost(item.getAlignment()));
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
			IDCost = (long)(item.getItem().getItemBaseValue() * (item.getIDLevel().value() * Util.STORE_ID_ADJUSTMENT));
			jbID.setEnabled(true);
		}
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
			updateSellPane();
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
			updateSellPane();
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
			
			// Remove one copy form the stack and give the player her gold.
			item.removeFromStack();
			player.changeGoldOnHand(sellValue);
			
			// If there is no more of the item on the stack, get rid of it.
			if(item.getStackSize() < 1)
			{
				player.removeItem(item);
				ipCombine.changeItem(null);
			}
			
			databank.getMessenger().postFlag(MordorMessengerDestination.PlayerSIC);
			
			updateSellPane();
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
				updateSellPane();
			}
		}
		else
		{
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

	public void messagePosted(MordorMessengerEvent message)
	{
		if(message.getDestination() == MordorMessengerDestination.StoreBuy)
		{
			// Coming from store's inventory
		}
		else if(message.getDestination() == MordorMessengerDestination.StoreMod)
		{
			// Coming from player's inventory
		}
	}
}
