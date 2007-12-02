package mordorGame;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;

import javax.swing.JLabel;

import mordorData.ItemInstance;

public class ItemLabel extends JLabel
{
	protected ItemInstance item;
	protected String text;
	
	protected DragSource dragSource;
	protected DragGestureListener dgListener;
	protected DragSourceListener dsListener;
	
	protected DropTarget dropTarget;
	protected DropTargetListener dtListener;
	protected int action = DnDConstants.ACTION_COPY;
	
	protected class DGListener implements DragGestureListener
	{
		public void dragGestureRecognized(DragGestureEvent dge)
		{
			try
			{
			    Transferable transferable = new ItemInstanceTransferable(item);
			    
			    //initial cursor, transferable, dsource listener      
			    dge.startDrag(DragSource.DefaultCopyNoDrop, transferable, dsListener);
			    // or if dragSource is an instance variable:
			    // dragSource.startDrag(e, DragSource.DefaultCopyNoDrop, transferable, dsListener);
			}
			catch(InvalidDnDOperationException idoe)
			{
			    System.err.println("DGListener (ItemLabel) error: " + idoe);
			}
		}
		
	}
	
	protected class DSListener implements DragSourceListener
	{

		public void dragDropEnd(DragSourceDropEvent dsde)
		{
		    if(dsde.getDropSuccess() == false)
		    	return;

		    int dropAction = dsde.getDropAction();
		    if(dropAction == DnDConstants.ACTION_MOVE)
		    {
			    // do whatever
		    }
		}

		public void dragEnter(DragSourceDragEvent dsde)
		{
			DragSourceContext context = dsde.getDragSourceContext();
			
			//intersection of the users selected action, and the source and target actions
			int action = dsde.getDropAction();
			if((action & DnDConstants.ACTION_COPY) != 0)
				context.setCursor(DragSource.DefaultCopyDrop);
			else
				context.setCursor(DragSource.DefaultCopyNoDrop); 
		}

		public void dragExit(DragSourceEvent dse)
		{
			// TODO Auto-generated method stub
			
		}

		public void dragOver(DragSourceDragEvent dsde)
		{
			// TODO Auto-generated method stub
			
		}

		public void dropActionChanged(DragSourceDragEvent dsde)
		{
			// TODO Auto-generated method stub
			
		}
	}
	
	protected class DTListener implements DropTargetListener
	{

		public void dragEnter(DropTargetDragEvent dtde)
		{
			if(!isDragOk(dtde))
			{
				dtde.rejectDrag();      
			    return;
			}
			      
			dtde.acceptDrag(ItemLabel.this.action);
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
		    
			dtde.acceptDrag(ItemLabel.this.action);
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

			if((sa & ItemLabel.this.action) == 0 )
			{
			    dtde.rejectDrop();             
			    return;
			}
			
			// Everything checks out.
			try
			{
				dtde.acceptDrop(ItemLabel.this.action);
				Object data = dtde.getTransferable().getTransferData(ItemInstanceTransferable.itemInstanceFlavor);
					
				if(data != null)
					ItemLabel.this.changeItem((ItemInstance)data);
				else
					ItemLabel.this.changeItem(null);
			}
			catch (Exception e)
			{
				System.err.println("DTListener (ItemLabel) error:" + e);
			}
		}

		public void dropActionChanged(DropTargetDragEvent dtde)
		{
			if(!isDragOk(dtde))
			{
				dtde.rejectDrag();      
			    return;
			}

			dtde.acceptDrag(ItemLabel.this.action); 
		}
		
		private boolean isDragOk(DropTargetDragEvent e)
		{
			if(!e.isDataFlavorSupported(ItemInstanceTransferable.itemInstanceFlavor))
				return false;
			
		    int sa = e.getSourceActions();
		    
		    if ((sa & ItemLabel.this.action) == 0)
		    	return false;
		    
		    return true;
		}
	}  

	public ItemLabel(ItemInstance newItem)
	{
		dragSource = DragSource.getDefaultDragSource();
		dgListener = new DGListener();
		dsListener = new DSListener();

		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, dgListener);
		
		dtListener = new DTListener();
		dropTarget = new DropTarget(this, this.action, dtListener, true);
		
		item = newItem;
		
		if(item != null)
			setText(item.getItem().getName());
		else
			setText("Nothing");
	}
	
	public void changeItem(ItemInstance newItem)
	{
		item = newItem;
		
		if(item != null)
			setText(item.getItem().getName());
		else
			setText("Nothing");
	}
	
	public String getText()
	{
		return (item == null) ? "Nothing" : item.getItem().getName();
	}
}
