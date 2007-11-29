package mordorGame;

import javax.swing.table.AbstractTableModel;

import mordorData.ItemInstance;
import mordorData.Player;

public class SICItemTable extends AbstractTableModel
{
	private Player player;
	
	SICItemTable(Player nPlayer)
	{
		player = nPlayer;
	}
	
	public void changePlayer(Player nPlayer)
	{
		if(nPlayer != null)
			player = nPlayer;
	}
	
	public boolean isEquipped(int rowIndex)
	{
		if(rowIndex < 0 || rowIndex >= Player.MAXITEMSONHAND)
			return false;
		
		return player.isItemEquipped((byte)rowIndex);
	}
	
	public boolean isCellEditable(int row, int col)
    {
		return false;
	}

	public int getColumnCount()
	{
		// TODO Auto-generated method stub
		return 3;
	}

	public int getRowCount() {
		// TODO Auto-generated method stub
		return Player.MAXITEMSONHAND;
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(rowIndex < 0 || rowIndex > Player.MAXITEMSONHAND)
			return null;
		
		if(columnIndex == 0)
			return (player.isItemEquipped((byte)rowIndex)) ? "*" : " ";
		else if(columnIndex == 1)
			return rowIndex;
		else
			return player.getItems()[rowIndex];
	}

}
