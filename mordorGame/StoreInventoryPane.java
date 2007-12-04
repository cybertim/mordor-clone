package mordorGame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import structures.SkipIter;

import mordorData.Store;
import mordorData.StoreRecord;
import mordorEnums.Alignment;

public class StoreInventoryPane extends JPanel implements Scrollable
{
	private Store store;
	private StorePane parent;
	
	public StoreInventoryPane(Store newStore, StorePane theParent)
	{
		store = newStore;
		parent = theParent;
		
		updateInventory();
	}
	
	public void updateInventory()
	{
		removeAll();
		store.clearEmptyRecords();
		setLayout(new GridLayout(store.getInventory().getSize() + 1, 1));
		
		SkipIter<StoreRecord> record = store.getInventory().getIterator();
		while(record.next())
			add(new StoreRecordPane(record.element()));
		
		revalidate();
	}
	
	private class StoreRecordPane extends JPanel implements MouseListener
	{
		private StoreRecord record;
		private JLabel jlAlign[];
		
		StoreRecordPane(StoreRecord newRecord)
		{
			record = newRecord;
			addMouseListener(this);
			
			jlAlign = new JLabel[Alignment.values().length];
			JPanel alignPane = new JPanel();
			for(Alignment al : Alignment.values())
			{
				if(record.getItem().isUnaligned())
					jlAlign[al.value()] = new JLabel("");
				else
					jlAlign[al.value()] = new JLabel("" + record.getCount(al));
				
				alignPane.add(jlAlign[al.value()]);
			}
			
			setLayout(new BorderLayout());
			add(new JLabel(record.getItem().getName()), BorderLayout.WEST);
			add(alignPane, BorderLayout.EAST);
		}
		
		public void mouseClicked(MouseEvent e)
		{
			parent.recordChosen(record);
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

	public Dimension getPreferredScrollableViewportSize()
	{
		return new Dimension(100, 200);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return visibleRect.height / 4;
	}

	public boolean getScrollableTracksViewportHeight()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getScrollableTracksViewportWidth()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 10;
	}

}
