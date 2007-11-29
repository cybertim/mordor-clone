package mordorGame;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;

import mordorData.ItemInstance;

public class ItemLabel extends JLabel implements MouseMotionListener, MouseListener
{
	protected ItemInstance item;
	protected String text;
	
	public ItemLabel(ItemInstance newItem)
	{
		item = newItem;
	}
	
	public String getText()
	{
		return text;
	}
	
	public ItemInstance getItem()
	{
		return item;
	}
	
	public void setItem(ItemInstance newItem)
	{
		item = newItem;
		updateText();
	}
	
	public void updateText()
	{
		text = (item != null) ? item.getItem().getName() : " ";
	}
	
	public void setText()
	{
		this.setText(text);
	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
