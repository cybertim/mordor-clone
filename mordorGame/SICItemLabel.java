package mordorGame;

import mordorData.ItemInstance;
import mordorData.Player;
import mordorEnums.Identification;

public class SICItemLabel extends ItemLabel
{
	private byte index;
	private Player player;
	private SICItemList parent;

	public SICItemLabel(byte itemIndex, Player ownPlayer, SICItemList newParent)
	{
		super(ownPlayer.getItem(itemIndex), itemIndex);
		index = itemIndex;
		player = ownPlayer;
		parent = newParent;
		
		updatePanel();
	}
	
	public void changeItem(ItemInstance newItem)
	{
		item = newItem;
	}
	
	public void changeItem(ItemInstanceTransferable.ItemInstanceBox box)
	{
		player.swapItems(index, box.index);
		parent.updateItems();
	}
	
	/**
	 * Updates the panel by retrieving the item stored
	 * here and changing the text for the label.
	 */
	public void updatePanel()
	{
		item = player.getItem(index);
		
		String text = "<HTML>" + (index + 1) + ". ";
		if(item != null)
		{
			text += (player.isItemEquipped(index)) ? " * <B>" : " &nbsp ";
			text += (item.isCursed() && item.getIDLevel() != Identification.Everything) ? "<I>" : "";
			text += item.toString();
			text += (item.isCursed() && item.getIDLevel() != Identification.Everything) ? "</I>" : "";
			text += (player.isItemEquipped(index)) ? "</B>" : "";
		}
		
		text += "</HTML>";
		
		setText(text);
	}

}
