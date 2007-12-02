package mordorEditor;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import mordorData.DataBank;
import mordorData.Race;

import structures.ListNode;
import structures.QuadNode;
import structures.SkipIter;

public class EditorRacePanel extends JPanel 
{
	private JTabbedPane raceTabs;
	private EditorRacePane[] racePanes;
	
	private DataBank dataBank;
	
	EditorRacePanel(DataBank nDataBank)
	{
		dataBank = nDataBank;
		
		raceTabs = new JTabbedPane();
		racePanes = new EditorRacePane[dataBank.getRaces().getSize()];
		
		SkipIter<Race> tNode = dataBank.getRaces().getIterator();
		byte i = 0;
		while(tNode.next())
		{
			racePanes[i] = new EditorRacePane(this, tNode.element(), dataBank);
			raceTabs.add(tNode.element().getName(), racePanes[i]);
			i += 1;
		}

		add(raceTabs);
	}
	
	/**
	 * Parses entire race set.
	 * @return boolean	True if every race was sucessfully updated.
	 */
	public boolean updateAll()
	{
		for(byte i = 0; i < racePanes.length; i++)
		{
			if(!racePanes[i].updateRace())
				return false;
		}
		
		return true;
	}
	
	/**
	 * Updates the selected race.
	 * @return boolean	True if race succesfully updated.
	 */
	public boolean updateSelected()
	{
		return racePanes[raceTabs.getSelectedIndex()].updateRace();
	}
	
	/**
	 * Reverts the selected race to whatever is currently stored in the
	 * dataBank.
	 *
	 */
	public void revertSelected()
	{
		racePanes[raceTabs.getSelectedIndex()].loadRace();
	}
	
	/**
	 * Updates the races panel if there has been a change in the number of
	 * races (e.g. new/delete/load)
	 *
	 */
	public void changeRaceCount()
	{
		this.remove(raceTabs);
		raceTabs = new JTabbedPane();
		racePanes = new EditorRacePane[dataBank.getRaces().getSize()];
		
		SkipIter<Race> tNode = dataBank.getRaces().getIterator();
		byte i = 0;
		while(tNode.next())
		{
			racePanes[i] = new EditorRacePane(this, tNode.element(), dataBank);
			raceTabs.add(tNode.element().getName(), racePanes[i]);
			i += 1;
		}
		this.add(raceTabs);
		this.revalidate();
	}
	
	public Race getSelectedRace()
	{
		return racePanes[raceTabs.getSelectedIndex()].getRace();
	}
	
	/**
	 * Updates the names of tabs.
	 *
	 */
	public void updateNames()
	{
		for(int i = 0; i < raceTabs.getTabCount(); i++)
			raceTabs.setTitleAt(i, racePanes[i].getRace().getName());
	}
	
	public void updateLists()
	{
		// Does nothing presently. Should update anything changeable in
		// another panel.
	}
}
