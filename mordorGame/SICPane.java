package mordorGame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import mordorData.DataBank;
import mordorData.ItemInstance;
import mordorData.Player;

public class SICPane extends JPanel implements ActionListener
{
	private Player player;
	private InformationPanel infoPane;
	private DataBank dataBank;
	private JTabbedPane tabs;
	private SICItem itemTab;
	
	SICPane(Player nPlayer, InformationPanel nInfoPane, DataBank nDataBank)
	{
		player = nPlayer;
		infoPane = nInfoPane;
		dataBank = nDataBank;
		itemTab = new SICItem(player, infoPane, dataBank);
		tabs = new JTabbedPane();
		tabs.addTab("Items", itemTab);
		tabs.addTab("Spells", null);
		tabs.addTab("Companions", null);
		
		add(tabs);
	}
	
	/**
	 * Get the item that the player currently has selected in the list.
	 * @return
	 */
	public ItemInstance getItemSelected()
	{
		return itemTab.getSelectedItem();
	}
	
	/**
	 * Determine if the item the current player has selected is equipped.
	 * @return
	 */
	public boolean isSelectedEquipped()
	{
		return false;
	}
	
	public void updatePanes()
	{
		
	}
	
	public void updateItems()
	{
		itemTab.repaint();
	}
	
	public void updateSpells()
	{
		
	}
	
	public void updateCompanions()
	{
		
	}

	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub

	}

}
