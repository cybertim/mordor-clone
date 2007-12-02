package mordorShared;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Scrollable;

import mordorData.Item;
import mordorData.ItemInstance;
import mordorData.Store;
import mordorData.StoreRecord;
import mordorEnums.Alignment;
import mordorHelpers.Util;
import mordorMessenger.MordorMessenger;
import mordorMessenger.MordorMessengerDestination;

import structures.QuadNode;
import structures.SkipIter;
import structures.SkipList;

/**
 * As probably too complicated class to define a store inventory pane.
 * Designed for both an editor (where the user can add/remove items, or
 * change the amount of items of different alignment are in the inventory)
 * or a selection pane. 
 * Implements scrollable.
 * @author August Junkala, Nov 24, 2007
 *
 */
public class StoreInventory extends JPanel implements Scrollable
{
	private Store store;
	private boolean editor;
	private SkipList<RecordField> records;
	private StoreInventory self;
	private MordorMessenger messenger;
	
	private RecordFieldButton activeItem;
	private Alignment activeAlignment;
	
	private class RecordField
	{
		protected StoreRecord record;
		protected JLabel name;
		protected JPanel panel;
		
		RecordField(StoreRecord nRecord)
		{
			record = nRecord;
			name = new JLabel(record.getItem().getName());
			panel = new JPanel();
			panel.add(name);
		}
	}
	
	private class RecordFieldButton extends RecordField implements ActionListener
	{
		protected JButton[] alignment;
		
		RecordFieldButton(StoreRecord record)
		{
			super(record);
			if(record.getItem().isStoreItem())
			{
				// This is a store item
				alignment = new JButton[1];
				alignment[0] = new JButton("" + record.getItem().getItemBaseValue());
				alignment[0].setEnabled(false);
				alignment[0].setToolTipText("This is a standard store item.");
				
				panel.add(alignment[0]);
			}
			else
			{
				alignment = new JButton[Alignment.values().length];
				for(Alignment al : Alignment.values())
				{
					if(record.getItem().getAlignment(al))
					{
						// This alignment is allowed, set its count.
						alignment[al.value()] = new JButton("" + record.getCount(al));
						alignment[al.value()].setToolTipText("Choose " + al.toString() + " alignment.");
						alignment[al.value()].addActionListener(this);
					}
					else
					{
						// This alignment isn't allowed
						alignment[al.value()] = new JButton("-");
						alignment[al.value()].setEnabled(false);
					}
				}
				
				// Add the alignment buttons.
				for(Alignment al : Alignment.values())
					panel.add(alignment[al.value()]);
			}
		}
		
		/**
		 * Update the text on buttons to the current record.
		 */
		public void updateRecord()
		{
			for(Alignment al : Alignment.values())
				alignment[al.value()].setText("" + record.getCount(al));
		}

		public void actionPerformed(ActionEvent e)
		{
			// Indicate this record and the chosen alignment are active.
			activeItem = this;
			for(Alignment al : Alignment.values())
				if(e.getSource() == alignment[al.value()])
					activeAlignment = al;
			
			// now make sure the store knows...
			if(messenger != null)
				messenger.postFlag(MordorMessengerDestination.StoreBuy);
		}
	}
	
	/**
	 * Record fields for the editor.
	 * @author August Junkala
	 *
	 */
	private class RecordFieldEditor extends RecordField implements ActionListener
	{
		protected JTextField[] alignment;
		protected JButton jbRemove;
		
		RecordFieldEditor(StoreRecord record)
		{
			super(record);
			jbRemove = new JButton("X");
			jbRemove.setToolTipText("Remove this record.");
			jbRemove.addActionListener(this);
			
			alignment = new JTextField[Alignment.values().length];
			
			// Add the remove button.
			panel.add(jbRemove);

			if(record.getItem().isStoreItem())
			{
				alignment = new JTextField[1];
				alignment[0] = new JTextField("N/A");
				alignment[0].setEnabled(false);
				alignment[0].setToolTipText("This is a standard store item.");
				
				panel.add(alignment[0]);
			}
			else
			{
				alignment = new JTextField[Alignment.values().length];
				for(Alignment al : Alignment.values())
				{
					if(record.getItem().getAlignment(al))
					{
						alignment[al.value()] = new JTextField(2);
						alignment[al.value()].setText("" + record.getCount(al));
						alignment[al.value()].setToolTipText("Choose " + al.toString() + " alignment.");
						alignment[al.value()].addActionListener(this);
					}
					else
					{
						alignment[al.value()] = new JTextField("0");
						alignment[al.value()].setEnabled(false);
					}
				}
				
				// Add the entry fields.
				for(Alignment al : Alignment.values())
					panel.add(alignment[al.value()]);
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			// Remove this record from the list and update the panel.
			if(e.getSource() == jbRemove)
			{
				records.remove((int)this.record.getItemID());
				updateList();
			}
		}
		
		/**
		 * Ensure the values for this record are valid.
		 * @return	true if this record is valid.
		 */
		public boolean validateRecord()
		{
			for(Alignment al : Alignment.values())
				if(!Util.validityCheckTf(self, alignment[al.value()], 0, StoreRecord.MAXITEMSPERALIGNMENT, "Invalid " + al.toString() + " count for " + record.getItem().getName()))
					return false;

			return true;
		}
		
		/**
		 * Update the record. Also check validity.
		 * @param checkValid	If true, will check the validity.
		 * @return	true if updated.
		 */
		public boolean updateRecord(boolean checkValid)
		{
			if(checkValid && !validateRecord())
				return false;
			
			for(Alignment al : Alignment.values())
				record.setCount(al, Byte.parseByte(alignment[al.value()].getText().trim()));
			
			return true;
		}
	}
	
	public StoreInventory(Store nStore, boolean nEditor, MordorMessenger nMessenger)
	{
		store = nStore;
		editor = nEditor;
		self = this;
		messenger = nMessenger;
		records = new SkipList<RecordField>();
		
		updateInventory();
		updateList();
	}
	
	/**
	 * Update the whole list.
	 */
	public void updateList()
	{
		SkipIter<RecordField> node = records.getIterator();
		
		removeAll();
		
		if(node == null)
			return;
		
		setLayout(new GridLayout(records.getSize(), 1));
		
		while(node.next())
		{
			if(!node.element().record.isEmptyRecord() || editor)
				add(node.element().panel); // This is a legal  record.
			else
				records.remove(node.key());
		}
		
		// Clear the store's current inventory.
		store.getInventory().clearList();
		
		// Now fill it up with the new list.
		node = records.getIterator();
		while(node.next())
		{
			store.getInventory().insert(node.element().record, (int)node.element().record.getItemID());
		}
	}
	
	/**
	 * Update the inventory with the stores current records.
	 */
	public void updateInventory()
	{
		records = new SkipList<RecordField>();
		
		SkipIter<StoreRecord> node = store.getInventory().getIterator();
		
		if(store.getInventory().isEmpty())
		{
			updateList();
			return;
		}
		
		while(node.next())
			insertRecord(node.element());
	}
	
	/**
	 * Validate the inventory.
	 * Removes any empty records.
	 * @return true if the inventory is valid.
	 */
	public boolean validateInventory()
	{
		SkipIter<RecordField> node = records.getIterator();
		
		while(node.next())
		{
			if(node.element().record.isEmptyRecord())
				records.remove(node.key());
			else
				if(editor && !((RecordFieldEditor)node.element()).validateRecord())
					return false;
		}
		
		return true;
	}
	
	/**
	 * If this is the editor version, update the records.
	 * @return true if all records successfully updated.
	 */
	public boolean updateStore()
	{
		if(!validateInventory())
			return false;
		
		SkipIter<RecordField> node = records.getIterator();
		
		while(node.next())
			if(editor && !((RecordFieldEditor)node.element()).updateRecord(false))
				return false;
		
		return true;
	}
	
	/**
	 * Retrieve the item that is currently selected.
	 * @return Item
	 */
	public Item getActiveItem()
	{
		return activeItem.record.getItem();
	}
	
	/**
	 * Retrieve the alignment the user chose for the specific item.
	 * @return Alignment
	 */
	public Alignment getActiveAlignment()
	{
		return activeAlignment;
	}
	
	/**
	 * Add a record based on an item instance.
	 * @param item	ItemInstance
	 */
	public void addRecord(ItemInstance item)
	{
		// Stores don't hold cursed items or null items.
		if(item == null || item.isCursed())
			return;
		
		StoreRecord record;
		
		if(records.find((int)item.getItemID()) != null)
			record = records.find((int)item.getItemID()).record;
		else
		{
			record = new StoreRecord(item);
			insertRecord(record);
		}
		
		record.addItem(item);
		
		updateList();
	}
	
	/**
	 * Add a new record based on an item.
	 * @param item
	 */
	public void addRecord(Item item)
	{
		// Stores don't hold cursed items or null items.
		if(item == null || item.isCursed())
			return;
		
		StoreRecord record;
		
		if(records.find((int)item.getID()) != null)
			record = records.find((int)item.getID()).record;
		else
		{
			record = new StoreRecord(item);
			insertRecord(record);
		}
		
		record.setCount(Alignment.Neutral, (byte)(record.getCount(Alignment.Neutral) + 1));
		
		updateList();
	}
	
	private void insertRecord(StoreRecord record)
	{
		if(editor)
			records.insert(new RecordFieldEditor(record), (int)record.getItemID());
		else
			records.insert(new RecordFieldButton(record), (int)record.getItemID());
	}

	public Dimension getPreferredScrollableViewportSize()
	{
		// TODO Auto-generated method stub
		return new Dimension(400, 400);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// TODO Auto-generated method stub
		return 25;
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
		return 5;
	}
}
