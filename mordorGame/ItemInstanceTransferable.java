package mordorGame;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import mordorData.ItemInstance;

/**
 * Tranferable for ItemInstance objects.
 * Based on code from:
 * http://www.javaworld.com/javaworld/jw-08-1999/jw-08-draganddrop.html
 * http://java.sun.com/docs/books/tutorial/uiswing/examples/dnd/DragPictureDemoProject/src/dnd/PictureTransferHandler.java
 * @author August Junkala, Nov 29, 2007
 *
 */
public class ItemInstanceTransferable implements Transferable
{
	private ItemInstanceBox box;
	public static DataFlavor itemInstanceFlavor = null;
	
	public class ItemInstanceBox
	{
		public ItemInstance item;
		public byte index;
	}
	
	static
	{
		try
		{
			itemInstanceFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=mordorData.ItemInstance", "Local ItemInstance");	
		}
		catch(Exception e)
		{
			System.err.println("ItemInstanceTransferable error: " + e);
		}
	}
	
	public ItemInstanceTransferable(ItemInstance transferItem, byte newIndex)
	{
		box = new ItemInstanceBox();
		box.index = newIndex;
		box.item = transferItem;
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if(!isDataFlavorSupported(flavor))
			throw new UnsupportedFlavorException(flavor);
		
		return box;
	}

	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[] { itemInstanceFlavor };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return itemInstanceFlavor.equals(flavor);
	}

}
