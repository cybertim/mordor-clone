package mordorGame;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JLabel;
import javax.swing.JTextField;

import mordorData.ItemInstance;

public class StoreItemLabel extends JTextField
{
	private ItemInstance item;
	private StorePane parent;
	
	protected DropTarget dropTarget;
	protected DropTargetListener dtListener;
	protected int action = DnDConstants.ACTION_COPY;
	
	protected class DTListener implements DropTargetListener
	{
		private StoreItemLabel owner;
		
		public DTListener(StoreItemLabel newOwner) { owner = newOwner; }

		public void dragEnter(DropTargetDragEvent dtde)
		{
			if(!isDragOk(dtde))
			{
				dtde.rejectDrag();      
			    return;
			}
			      
			dtde.acceptDrag(StoreItemLabel.this.action);
		}

		public void dragExit(DropTargetEvent dte)
		{
			
		}

		public void dragOver(DropTargetDragEvent dtde)
		{
			if(!isDragOk(dtde))
			{
				dtde.rejectDrag();      
				return;
		    }
		    
			dtde.acceptDrag(StoreItemLabel.this.action);
		}

		public void drop(DropTargetDropEvent dtde)
		{
			// We don't do external drops.
			if (!dtde.isLocalTransfer())
			{
				dtde.rejectDrop();
				return;
			}
			
			// We don't do drops with bad flavours.
			if(!dtde.isDataFlavorSupported(ItemInstanceTransferable.itemInstanceFlavor))
			{
				dtde.rejectDrop();
				return;
			}
			
			// We don't do drops with bizarre actions
			int sa = dtde.getSourceActions();

			if((sa & StoreItemLabel.this.action) == 0 )
			{
			    dtde.rejectDrop();             
			    return;
			}
			
			// Everything checks out.
			try
			{
				dtde.acceptDrop(StoreItemLabel.this.action);
				Object data = dtde.getTransferable().getTransferData(ItemInstanceTransferable.itemInstanceFlavor);
					
				if(data != null)
					owner.changeItem(((ItemInstanceTransferable.ItemInstanceBox)data).item);
				else
					owner.changeItem(null);
			}
			catch (Exception e)
			{
				System.err.println("DTListener (StoreItemLabel) error:" + e);
			}
		}

		public void dropActionChanged(DropTargetDragEvent dtde)
		{
			if(!isDragOk(dtde))
			{
				dtde.rejectDrag();      
			    return;
			}

			dtde.acceptDrag(StoreItemLabel.this.action); 
		}
		
		private boolean isDragOk(DropTargetDragEvent e)
		{
			if(!e.isDataFlavorSupported(ItemInstanceTransferable.itemInstanceFlavor))
				return false;
			
		    int sa = e.getSourceActions();
		    
		    if ((sa & StoreItemLabel.this.action) == 0)
		    	return false;
		    
		    return true;
		}
	}
	
	public StoreItemLabel(StorePane theParent)
	{
		super(15);
		parent = theParent;
		
		this.setEditable(false);
		
		dtListener = new DTListener(this);
		dropTarget = new DropTarget(this, this.action, dtListener, true);
	}
	
	public void changeItem(ItemInstance newItem)
	{
		ItemInstance oldItem = item;
		item = newItem;
		parent.itemUpdated(this, oldItem, newItem);
		updateLabel();
	}
	
	public ItemInstance getItem()
	{
		return item;
	}
	
	public void updateLabel()
	{
		if(item != null)
			setText(item.toString());
		else
			setText("");
	}
}
