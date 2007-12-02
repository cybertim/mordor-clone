package mordorEditor;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import mordorData.DataBank;
import mordorData.Guild;

import structures.SkipIter;


public class EditorGuildPanel extends JPanel
{
	private JTabbedPane guildTabs;
	private EditorGuildPane[] guildPanes;
	private DataBank dataBank;
	
	EditorGuildPanel(DataBank newDataBank)
	{
		dataBank = newDataBank;
		
		guildTabs = new JTabbedPane();
		
		
		SkipIter<Guild> tNode = dataBank.getGuilds().getIterator();
		if(tNode != null)
		{
			guildPanes = new EditorGuildPane[dataBank.getGuildCount()];
			byte count = 0;
			while(tNode.next())
			{
				guildPanes[count] = new EditorGuildPane(tNode.element(), dataBank, this);
				guildTabs.add(tNode.element().getName(), guildPanes[count]);
				count += 1;
			}
		}
		else
		{
			Guild tGuild = dataBank.newGuild();
			guildPanes = new EditorGuildPane[1];
			guildPanes[0] = new EditorGuildPane(tGuild, dataBank, this);
			guildTabs.add(tGuild.getName(), guildPanes[0]);
		}
		
		add(guildTabs);
	}
	
	public void updateName(EditorGuildPane guildPane)
	{
		int index = getIndex(guildPane);
		
		if(index == -1)
			return; // this guildpane doesn't exist?!?!
		
		guildTabs.setTitleAt(index, guildPane.getName());
	}
	
	private int getIndex(EditorGuildPane guildPane)
	{
		for(int i = 0; i < guildPanes.length; i++)
			if(guildPanes[i] == guildPane)
				return i;
		
		return -1;
	}
	
	public boolean newGuild()
	{
		Guild newGuild = dataBank.newGuild();
		int guildCount;
		
		if(newGuild == null)
			return false;
		
		EditorGuildPane[] tGuildPanes = new EditorGuildPane[guildPanes.length + 1];
		
		for(guildCount = 0; guildCount < guildPanes.length; guildCount++)
			tGuildPanes[guildCount] = guildPanes[guildCount];
		
		tGuildPanes[guildCount] = new EditorGuildPane(newGuild, dataBank, this);
		
		guildPanes = tGuildPanes;
		
		updateTabs();
		
		return true;
	}
	
	public boolean removeSelectedGuild()
	{
		if(guildPanes.length <= 1)
			return false;
		
		EditorGuildPane deletedPane = (EditorGuildPane)guildTabs.getSelectedComponent();
		Guild deletedGuild = deletedPane.getGuild();
		
		dataBank.deleteGuild(deletedGuild);
		
		EditorGuildPane[] tGuildPanes =  new EditorGuildPane[guildPanes.length - 1];
		int i, j;
		i = j = 0;
		
		for(i = 0; i < guildPanes.length; i++)
		{
			if(guildPanes[i] != deletedPane)
			{
				tGuildPanes[j] = guildPanes[i];
				j++;
			}
		}
		
		guildPanes = tGuildPanes;
		
		updateTabs();
		
		return true;
	}
	
	private void updateTabs()
	{
		int guildCount;
		this.remove(guildTabs);
		guildTabs = new JTabbedPane();
		
		for(guildCount = 0; guildCount < guildPanes.length; guildCount++)
			guildTabs.add(guildPanes[guildCount].getName(), guildPanes[guildCount]);
		
		add(guildTabs);
		revalidate();
	}
	
	public boolean updatePanes()
	{
		for(int i = 0; i < guildPanes.length; i++)
		{
			if(!guildPanes[i].updateGuildValues())
				return false;
		}
		
		return true;
	}
	
	public void updateGuilds()
	{
		SkipIter<Guild> tNode = dataBank.getGuilds().getIterator();
		guildPanes = new EditorGuildPane[dataBank.getGuilds().getSize()];
		
		for(int i = 0; i < guildPanes.length; i++, tNode.next())
			guildPanes[i] = new EditorGuildPane(tNode.element(), dataBank, this);
		
		updateTabs();
	}
	
	/**
	 * Parse the panes for each guild and update their lists. For use when,
	 * for example, a race has been added/removed. Occurs whenever the user
	 * selects the guilds pane.
	 */
	public void updateLists()
	{
		for(int i = 0; i < guildPanes.length; i++)
			guildPanes[i].updateLists();
	}
}
