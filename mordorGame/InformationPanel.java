package mordorGame;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;

import mordorData.DataBank;
import mordorData.ItemInstance;
import mordorData.MonsterInstance;
import mordorData.Player;
import mordorMessenger.MordorMessengerDestination;
import mordorMessenger.MordorMessengerEvent;
import mordorMessenger.MordorMessengerListener;

public class InformationPanel extends JInternalFrame implements MordorMessengerListener
{
	private PlayerPane playerPane;
	private BuffersPane buffersPane;
	private ResistancePane resistPane;
	private MiscPane miscPane;
	private JTabbedPane tabs;
	private DataBank databank;
	// Look, misc, resist, char, guild
	
	// For dropping
	protected DropTarget dropTarget;
	protected DropTargetListener dtListener;
	protected int action = DnDConstants.ACTION_COPY;
	
	protected class DTListener implements DropTargetListener
	{

		public void dragEnter(DropTargetDragEvent dtde)
		{
			if(!isDragOk(dtde))
			{
				dtde.rejectDrag();      
			    return;
			}
			      
			dtde.acceptDrag(InformationPanel.this.action);
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
		    
			dtde.acceptDrag(InformationPanel.this.action);
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

			if((sa & InformationPanel.this.action) == 0 )
			{
			    dtde.rejectDrop();             
			    return;
			}
			
			// Everything checks out.
			try
			{
				dtde.acceptDrop(InformationPanel.this.action);
				Object data = dtde.getTransferable().getTransferData(ItemInstanceTransferable.itemInstanceFlavor);
					
				if(data != null)
					InformationPanel.this.showItem(((ItemInstanceTransferable.ItemInstanceBox)data).item);
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

			dtde.acceptDrag(InformationPanel.this.action); 
		}
		
		private boolean isDragOk(DropTargetDragEvent e)
		{
			if(!e.isDataFlavorSupported(ItemInstanceTransferable.itemInstanceFlavor))
				return false;
			
		    int sa = e.getSourceActions();
		    
		    if ((sa & InformationPanel.this.action) == 0)
		    	return false;
		    
		    return true;
		}
	}
	
	InformationPanel(String title, boolean first, boolean second, boolean third, boolean fourth, DataBank theDB)
	{
		super(title, first, second, third, fourth);
		tabs = new JTabbedPane();
		databank = theDB;
		databank.getMessenger().addMordorMessengerListener(this);

		dtListener = new DTListener();
		dropTarget = new DropTarget(this, this.action, dtListener, true);
		
		playerPane = new PlayerPane();
		buffersPane = new BuffersPane();
		resistPane = new ResistancePane();
		miscPane = new MiscPane(databank);
		
		tabs.addTab("Stats", playerPane);
		tabs.addTab("Resist.", resistPane);
		tabs.addTab("Buffers", buffersPane);
		tabs.addTab("Misc.", miscPane);
		
		add(tabs);
	}
	
	public void showMonster(DataBank dataBank, MonsterInstance monster, byte from)
	{
		miscPane.showMonster(dataBank, monster, from);
		tabs.setSelectedComponent(miscPane);
		// TODO refocus on misc pane
	}
	
	public void showItem(ItemInstance item)
	{
		miscPane.showItem(item);
		tabs.setSelectedComponent(miscPane);
		// TODO refocus on misc pane
	}
	
	public void updatePanes(Player player)
	{
		playerPane.updatePanel(player, false); // TODO Teams
		buffersPane.updatePanel(player);
		resistPane.updatePanel(player);
		miscPane.updatePanel();
	}

	public void messagePosted(MordorMessengerEvent message)
	{
		if(message.getThing() == null)
			return;
		else if(message.getDestination() == MordorMessengerDestination.ItemInfo)
			showItem((ItemInstance)message.getThing());
		else if(message.getDestination() == MordorMessengerDestination.MonsterInfo)
			showMonster(databank, (MonsterInstance)message.getThing(), MiscPane.MONHEAD_COMPANION);
	}
}
