package mordorGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import mordorData.Player;
import mordorMessenger.MordorMessenger;
import mordorMessenger.MordorMessengerDestination;
import mordorMessenger.MordorMessengerEvent;
import mordorMessenger.MordorMessengerListener;

public class SICItemList extends JPanel implements Scrollable, MordorMessengerListener
{
	private SICItemLabel[] items;
	private Player player;
	private byte selectedIndex;
	
	public SICItemList(Player nPlayer, MordorMessenger messenger)
	{
		player = nPlayer;
		messenger.addMordorMessengerListener(this);
		
		GridLayout nLayout = new GridLayout(Player.MAXITEMSONHAND, 1);
		nLayout.setHgap(0);
		
		setLayout(nLayout);
		
		items = new SICItemLabel[Player.MAXITEMSONHAND];
		for(byte i = 0; i < Player.MAXITEMSONHAND; i++)
		{
			items[i] = new SICItemLabel(i, player, this);
			
			add(items[i]);
		}
	}
	
	/**
	 * Sets the index of the item that is currently selected.
	 * @param newIndex
	 */
	public void setIndex(byte newIndex)
	{
		items[selectedIndex].setBackground(this.getBackground());
		items[selectedIndex].setBorder(null);
		selectedIndex = newIndex;
		items[selectedIndex].setBackground(Color.DARK_GRAY);
		items[selectedIndex].setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	/**
	 * Gets the index of the item that is currently selected.
	 * @return selectedIndex
	 */
	public byte getSelectedIndex()
	{
		return selectedIndex;
	}
	
	/**
	 * Changes the player currently active.
	 * @param newPlayer
	 */
	public void updatePlayer(Player newPlayer)
	{
		player = newPlayer;
		updateItems();
	}
	
	/**
	 * Updates everything in the item list.
	 */
	public void updateItems()
	{
		for(byte i = 0; i < items.length; i++)
			items[i].updatePanel();
	}
	
	/**
	 * Updates only the currently selected item.
	 */
	public void updateSelectedItem()
	{
		items[selectedIndex].changeItem(player.getItem(selectedIndex));
	}
	
	public void itemSwap(byte indexA, byte indexB)
	{
		player.swapItems(indexA, indexB);
	}

	public Dimension getPreferredScrollableViewportSize()
	{
		return new Dimension(160, 180);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 40;
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
		return 10;
	}

	public void messagePosted(MordorMessengerEvent message)
	{
		if(message.getDestination() == MordorMessengerDestination.PlayerSIC)
			updateItems();
	}
}
