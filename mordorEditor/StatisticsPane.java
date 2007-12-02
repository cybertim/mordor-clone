package mordorEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import structures.ListIter;
import structures.SkipIter;

import mordorData.DataBank;
import mordorData.Guild;
import mordorData.GuildRecord;
import mordorData.Player;
import mordorData.Race;
import mordorEnums.ItemTypes;
import mordorEnums.MonsterClass;
import mordorEnums.SpellClass;
import mordorEnums.SpellType;

public class StatisticsPane extends JPanel implements ActionListener, Scrollable
{
	private JLabel jlNumSpells[];		//Number of spells by type.
	private JLabel jlNumMonsters[];		// Number of monsters by type.
	private JLabel jlNumItems[];		// Number of items by type.
	private JLabel jlNumGuildVals[];	// Number of members by guild & amount of experience in each guild.
	private JLabel jlNumRacePop[];		// Number of players by race.
	// Totals
	private JLabel jlTotItems, jlTotMonsters, jlTotSpells, jlTotGuilds, jlTotPlayers, jlTotLevels, jlTotRace;
	// World totals
	private JLabel jlTotGold, jlTotItemInst, jlTotMonInstance, jlTotXP;
	
	private JButton jbUpdate;
	
	private DataBank dataBank;
	
	StatisticsPane(DataBank nDataBank)
	{
		dataBank = nDataBank;
		
		jbUpdate = new JButton("Update");
		jbUpdate.addActionListener(this);
		jbUpdate.setToolTipText("Update statistics.");
		
		JPanel tPane = new JPanel();
		tPane.setLayout(new GridLayout(4, 1));
		tPane.add(jbUpdate);
		
		JPanel totPane = new JPanel();
		JPanel totPaneB = new JPanel();
		jlTotItems = new JLabel("");
		jlTotMonsters = new JLabel("");
		jlTotSpells = new JLabel("");
		jlTotGuilds = new JLabel("");
		jlTotPlayers = new JLabel("");
		jlTotLevels = new JLabel("");
		jlTotRace = new JLabel("");

		totPane.add(jlTotItems);
		totPane.add(jlTotMonsters);
		totPane.add(jlTotSpells);
		totPane.add(jlTotGuilds);
		
		
		totPaneB.add(jlTotPlayers);
		totPaneB.add(jlTotLevels);
		totPaneB.add(jlTotRace);
		
		tPane.add(totPane);
		tPane.add(totPaneB);
		
		JPanel worldPane = new JPanel();
		jlTotGold = new JLabel("");
		jlTotItemInst = new JLabel("");
		jlTotMonInstance = new JLabel("");
		jlTotXP = new JLabel("");
		worldPane.add(jlTotGold);
		worldPane.add(jlTotItemInst);
		worldPane.add(jlTotMonInstance);
		worldPane.add(jlTotXP);
		
		tPane.add(worldPane);
		
		JPanel bPane = new JPanel();
		((FlowLayout)bPane.getLayout()).setHgap(20);
		// add columns to bPane, but make columns same length
		int numRows = ItemTypes.values().length;
		numRows = (numRows < SpellClass.values().length) ? SpellClass.values().length : numRows;
		numRows = (numRows < MonsterClass.values().length) ? MonsterClass.values().length : numRows;
		numRows = (numRows < dataBank.getGuildCount()) ? dataBank.getGuildCount() : numRows;
		numRows = (numRows < dataBank.getRaces().getSize()) ? dataBank.getRaces().getSize() : numRows;
		
		JPanel iPane = new JPanel();
		JPanel mPane = new JPanel();
		JPanel sPane = new JPanel();
		JPanel rPane = new JPanel();
		JPanel gPane = new JPanel();
		GridLayout bLayout = new GridLayout(numRows, 1);
		
		jlNumSpells = new JLabel[SpellClass.values().length];
		jlNumMonsters = new JLabel[MonsterClass.values().length];
		jlNumItems = new JLabel[ItemTypes.values().length];
		jlNumGuildVals = new JLabel[dataBank.getGuildCount()];
		jlNumRacePop = new JLabel[dataBank.getRaces().getSize()];
		
		sPane.setLayout(bLayout);
		for(int i = 0; i < jlNumSpells.length; i++)
		{
			jlNumSpells[i] = new JLabel("");
			sPane.add(jlNumSpells[i]);
		}
		
		iPane.setLayout(bLayout);
		for(int i = 0; i < jlNumItems.length; i++)
		{
			jlNumItems[i] = new JLabel("");
			iPane.add(jlNumItems[i]);
		}
		
		mPane.setLayout(bLayout);
		for(int i = 0; i < jlNumMonsters.length; i++)
		{
			jlNumMonsters[i] = new JLabel("");
			mPane.add(jlNumMonsters[i]);
		}
		
		rPane.setLayout(bLayout);
		for(int i = 0; i < jlNumRacePop.length; i++)
		{
			jlNumRacePop[i] = new JLabel("");
			rPane.add(jlNumRacePop[i]);
		}
		
		gPane.setLayout(bLayout);
		for(int i = 0; i < jlNumGuildVals.length; i++)
		{
			jlNumGuildVals[i] = new JLabel("");
			gPane.add(jlNumGuildVals[i]);
		}
		
		bPane.add(rPane);
		bPane.add(gPane);
		bPane.add(sPane);
		bPane.add(iPane);
		bPane.add(mPane);
		
		setLayout(new BorderLayout());
		add(tPane, BorderLayout.NORTH);
		add(bPane, BorderLayout.CENTER);
		
		updateStatistics();
	}
	
	public void updateStatistics()
	{
		jlTotItems.setText("Total items: " + dataBank.getItems().getSize());
		jlTotMonsters.setText("Total monsters: " + dataBank.getMonsterEden().getMonsters().getSize());
		jlTotSpells.setText("Total spells: " + dataBank.getSpellBook().getAllSpells().getSize());
		jlTotGuilds.setText("Total guilds: " + dataBank.getGuilds().getSize());
		jlTotPlayers.setText("Total players: " + dataBank.getPlayers().getSize());
		jlTotLevels.setText("Total dungeon levels: " + dataBank.getMap().getDepth());
		jlTotRace.setText("Total races: " + dataBank.getRaces().getSize());

		SkipIter<Player> pNode = dataBank.getPlayers().getIterator();
		long totGold = 0;
		int totItems = 0;
		int totMons = 0;
		long totXp = 0;
		int totGuildMembers[] = new int[dataBank.getGuildCount()];
		long totGuildXp[] = new long[dataBank.getGuildCount()];
		byte guildIDs[] = new byte[dataBank.getGuildCount()];
		int totRacePop[] = new int[dataBank.getRaces().getSize()];
		byte raceIDs[] = new byte[dataBank.getRaces().getSize()];
		
		SkipIter<Guild> qNode = dataBank.getGuilds().getIterator();
		for(int i = 0; qNode.next() && i < guildIDs.length; i++)
		{
			guildIDs[i] = qNode.element().getGuildID();
			totGuildMembers[i] = 0;
		}
		
		SkipIter<Race> rNode = dataBank.getRaces().getIterator();
		for(int i = 0; rNode.next() && i < raceIDs.length; i++)
		{
			raceIDs[i] = rNode.element().getRaceID();
			totRacePop[i] = 0;
		}
		
		while(pNode.next())
		{
			if(Long.MAX_VALUE - totGold > pNode.element().getTotalGold())
				totGold += pNode.element().getTotalGold();
			else
				totGold = Long.MAX_VALUE;
			if(Long.MAX_VALUE - totXp > pNode.element().getTotalExperience())
				totXp += pNode.element().getTotalExperience();
			else
				totGold = Long.MAX_VALUE;
			int i = 0;
			for(; i < raceIDs.length && raceIDs[i] != pNode.element().getRaceID(); i++);
			
			totRacePop[i]++;
			
			ListIter<GuildRecord> grNode = pNode.element().getGuildRecords().getIterator();
			while(grNode.next())
			{
				i = 0;
				for(; i < guildIDs.length && guildIDs[i] != grNode.element().getGuildID(); i++);
				
				if(Long.MAX_VALUE - totGuildXp[i] > grNode.element().getExperience())
					totGuildXp[i] += grNode.element().getExperience();
				else
					totGuildXp[i] = Long.MAX_VALUE;
				
				totGuildMembers[i]++;
			}
			
			for(i = 0; i < Player.MAXCOMPANIONS; i++)
				if(pNode.element().getCompanions()[i] != null)
					totMons++;
		}
		jlTotGold.setText("Total gold: " + totGold);
		jlTotItemInst.setText("Total existing items: " + totItems); // TODO: need to include store
		jlTotMonInstance.setText("Total existing monsters: " + totMons); // TODO: need to include confinement
		jlTotXP.setText("Total experience collected: " + totXp);
		
		for(SpellClass st : SpellClass.values())
			jlNumSpells[st.value()].setText(st + ": " + dataBank.getSpellBook().getSpellClass(st).getSize());
		
		for(ItemTypes it : ItemTypes.values())
			jlNumItems[it.value()].setText(it + ": " + dataBank.getItemsOfType(it).getSize());
		
		for(MonsterClass mc : MonsterClass.values())
			jlNumMonsters[mc.value()].setText(mc + ": " + dataBank.getMonsterEden().getMonstersByClass(mc).getSize());
		
		for(int i = 0; i < jlNumRacePop.length; i++)
			jlNumRacePop[i].setText(dataBank.getRace(raceIDs[i]).getName() + ": " + totRacePop[i]);
		
		for(int i = 0; i < jlNumGuildVals.length; i++)
			jlNumGuildVals[i].setText(dataBank.getGuild(guildIDs[i]).getName() + " - Members: " + totGuildMembers[i] + " XP: " + totGuildXp[i]);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == jbUpdate)
			updateStatistics();
	}

	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return new Dimension(this.getWidth(), 300);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// TODO Auto-generated method stub
		return 0;
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
		return 0;
	}

}
