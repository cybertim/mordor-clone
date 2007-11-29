package mordorEditor;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mordorData.BankAccount;
import mordorData.ItemInstance;

public class PlayerItems extends JPanel
{
	private PlayerPanel parent;
	private PlayerItemList itemList;
	// Each item needs a an equippable button. should add this to EditorPlayerItemList
	// have it have a flag to cloak the equipped stuff.
	// also can be build to use player equipping code, ensure it works.
	public PlayerItems(PlayerPanel nParent)
	{
		parent = nParent;

		itemList = new PlayerItemList(parent.dataBank, true, parent.currentPlayer, BankAccount.MAXITEMSINBANK);
		JScrollPane itemScrollPane = new JScrollPane(itemList);
		add(itemScrollPane);
	}
	
	public boolean updatePlayer(boolean alreadyvisited)
	{
		ItemInstance[] itemArray = itemList.getItemArray();
		if(itemArray == null)
			return false;
		
		return parent.currentPlayer.setItems(itemArray);
	}
	
	public void updatePanel()
	{
		itemList.setItemList(parent.currentPlayer.getItems(), parent.currentPlayer);
	}
	
	public boolean validateItems()
	{
		return itemList.checkItems();
	}
	
	public void updateLists()
	{
		itemList.updateLists();
	}
}
