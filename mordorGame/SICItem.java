/**
 * SICItem class
 * The pane in the SIC panel that shows the player's current items.
 * August Junkala, Nov 3, 2007
 */
package mordorGame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import mordorData.DataBank;
import mordorData.ItemInstance;
import mordorData.Player;

public class SICItem extends JPanel implements ActionListener
{
	private Player player;
	private InformationPanel infoPane;
	private DataBank dataBank;
//	private JTable itemTable;
//	private SICItemTable itemTableData;
	private SICItemList items;
	private JButton jbEquip, jbDrop, jbInfo, jbUse;
	
	SICItem(Player nPlayer, InformationPanel nInfoPane, DataBank nDataBank)
	{
		player = nPlayer;
		infoPane = nInfoPane;
		dataBank = nDataBank;
		setLayout(new BorderLayout());
		
		JPanel bPane = new JPanel();
		bPane.setLayout(new GridLayout(4, 1));
		
		jbEquip = new JButton("Equip");
		jbUse = new JButton("Use");
		jbDrop = new JButton("Drop");
		jbInfo = new JButton("Info");
		jbEquip.setToolTipText("Equip the selected item.");
		jbUse.setToolTipText("Use the selected item.");
		jbDrop.setToolTipText("Drop the selected item.");
		jbInfo.setToolTipText("Read about the item.");
		jbEquip.addActionListener(this);
		jbUse.addActionListener(this);
		jbDrop.addActionListener(this);
		jbInfo.addActionListener(this);
		bPane.add(jbEquip);
		bPane.add(jbUse);
		bPane.add(jbDrop);
		bPane.add(jbInfo);
		
		JPanel lPane = new JPanel();
		items = new SICItemList(player);
		add(new JScrollPane(items));

/*		itemTableData = new SICItemTable(player);
		itemTable = new JTable(itemTableData);
		
		itemTable.getColumnModel().getColumn(0).setPreferredWidth(10);
		itemTable.getColumnModel().getColumn(1).setPreferredWidth(10);
		
		itemTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		itemTable.setRowSelectionAllowed(true);
		itemTable.setColumnSelectionAllowed(false);
		itemTable.setPreferredScrollableViewportSize(new Dimension(160, 180));
		itemTable.setTableHeader(null);
		
		lPane.add(new JScrollPane(itemTable));
*/		
		
		add(lPane, BorderLayout.WEST);
		add(bPane, BorderLayout.EAST);
	}

	public void changePlayer(Player nPlayer)
	{
		if(player == null)
			return;
		
		player = nPlayer;
		updatePanel();
	}
	
	// Update the display of the list.
	public void updatePanel()
	{
		revalidate();
	}
	
	/**
	 * Retrieves the item that is currently selected.
	 * @return ItemInstance or null if nothing selected
	 */
	public ItemInstance getSelectedItem()
	{
		return player.getItem(items.getSelectedIndex());
//		if(itemTable.getSelectedRow() == -1)
		//	return null;
		
	//	return (ItemInstance)itemTableData.getValueAt(itemTable.getSelectedRow(), 2);
	}

	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		if(e.getSource() == jbEquip)
		{
			ItemInstance tItem = getSelectedItem();
			if(tItem != null)
			{
				if(player.isItemEquipped(tItem))
					player.unequipItem(tItem);
				else if(player.canPlayerEquip(tItem.getItem()))
					player.equipItem(tItem);
				
				items.updateSelectedItem();
			}
//			itemTable.repaint();
		}
		else if(e.getSource() == jbUse)
		{
			ItemInstance tItem = getSelectedItem();
			if(tItem != null && tItem.isUsable())
			{
				// TODO: Add code for item effects.
				items.updateSelectedItem();
			}
		}
		else if(e.getSource() == jbDrop)
		{
			ItemInstance tItem = getSelectedItem();
			if(tItem != null)
			{
				player.removeItem(tItem);
	//			itemTable.repaint();
				// TODO : Make sure the store has been cleared.
				items.updateSelectedItem();
			}
		}
		else if(e.getSource() == jbInfo)
		{
			ItemInstance tItem = getSelectedItem();
			if(tItem != null)
				infoPane.showItem(dataBank, tItem);
			// TODO : Fire a message instead.
		}
	}
}
