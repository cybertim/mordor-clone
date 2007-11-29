package mordorGame;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.TransferHandler;

import mordorData.ItemInstance;

/**
 * Class for a single item in the SICItemList.
 * @author August Junkala, Nov 26, 2007.
 * Based on:
 * http://java.sun.com/docs/books/tutorial/uiswing/examples/dnd/DragPictureDemoProject/src/dnd/DTPicture.java
 *
 */
public class SICItemPanel extends JLabel implements MouseListener, MouseMotionListener
{
	private ItemInstance item;
	private byte index;
	private boolean equipped;
	private MouseEvent initEvent;
	private String text;
	private SICItemList parent;
	
	public SICItemPanel(byte itemIndex, ItemInstance nItem, boolean isEquipped, SICItemList newParent)
	{
		index = itemIndex;
		item = nItem;
		equipped = isEquipped;
		parent = newParent;
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		updateText();
	}
	
	/**
	 * Change the item.
	 * @param nItem
	 */
	public void setItem(ItemInstance nItem)
	{
		item = nItem;
		updateText();
	}
	
	/**
	 * Change whether this item is equipped.
	 * @param isEquipped
	 */
	public void equip(boolean isEquipped)
	{
		equipped = isEquipped;
		updateText();
	}
	
	/**
	 * Change the item, and if it is equipped
	 * @param nItem
	 * @param isEquipped
	 */
	public void changeItem(ItemInstance nItem, boolean isEquipped)
	{
		item = nItem;
		equipped = isEquipped;
		
		updateText();
	}
	
	public void changeItem(SICItemPanel oItem)
	{
		// Store old things item.
		ItemInstance tItem = item;
		boolean tEquip = equipped;
		
		// Make this the old item.
		item = oItem.item;
		equipped = oItem.equipped;
		
		// Change the dropped item.
		oItem.item = tItem;
		oItem.equipped = tEquip;
		
		// Update the text of both.
		updateText();
		oItem.updateText();
		
		// Need to update player.
		parent.itemSwap(index, oItem.index);
		parent.setIndex(index);
	}
	
	public ItemInstance getItem()
	{
		return item;
	}
	
	public boolean getEquipped()
	{
		return equipped;
	}
	
	/**
	 * Update the text for this item.
	 */
	public void updateText()
	{
		text = "<HTML>";
		text += (index + 1) + ". ";
		text += (equipped && item != null) ? " * " : "   ";
		if(item != null)
		{
			text += (equipped) ? "<B>" : "";
			text += (item.isCursed()) ? "<I>" : "";
			text += item.getItem().getName();
			text += (equipped) ? "</B>" : "";
			text += (item.isCursed()) ? "</I>" : "";
		}
		else
			text += "";
		text += "</HTML>";
		
		setText(text);
	}

	public void mouseDragged(MouseEvent e)
	{
		// No item, nothing to drag.
		if(item == null)
			return;
		
		if(initEvent != null)
		{
			int action = TransferHandler.COPY;

            int dx = Math.abs(e.getX() - initEvent.getX());
            int dy = Math.abs(e.getY() - initEvent.getY());
            
            if (dx > 5 || dy > 5)
            {
                //This is a drag, not a click.
                JComponent c = (JComponent)e.getSource();
                TransferHandler handler = c.getTransferHandler();
                //Tell the transfer handler to initiate the drag.
                handler.exportAsDrag(c, initEvent, action);
                initEvent = null;
            }
		}
	}

	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
	}

	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e)
	{
		// Let the list know we were clicked on.
		parent.setIndex(index);
		
		if(item != null)
			initEvent = e; // Track where we started.
	}

	public void mouseReleased(MouseEvent e)
	{
		initEvent = null; // Don't need this any longer.	
	}
}
