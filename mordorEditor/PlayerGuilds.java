package mordorEditor;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Scrollable;

import structures.LinkedList;
import structures.ListNode;
import structures.QuadNode;

import mordorData.Guild;
import mordorData.GuildRecord;
import mordorData.GuildReference;
import mordorData.Item;
import mordorData.Monster;
import mordorEnums.GuildSkill;

public class PlayerGuilds extends JPanel implements ActionListener, Scrollable, FocusListener
{
	private PlayerPanel parent;
	
	private JLabel[] guildNames, guildQuestTarget, guildXPNeeded;
	private JCheckBox[] guildMember, guildQuest;
	private JTextField[] guildAttack, guildDefense, guildLevel, guildXP;
	private JTextField[][] guildSkills;
	private JButton[] browseItems, browseMonsters;
	private boolean[] questItem; // If the thing is the guest target is item or monster
	
	private static final String NOQUESTTEXT = "None";
	
	PlayerGuilds(PlayerPanel nParent)
	{
		parent = nParent;
		
		setupPanel();
	}
	
	private JPanel getGuildPanel(String guildName, byte guildNumber)
	{
		JPanel newGuildPanel = new JPanel();
		
		guildNames[guildNumber] = new JLabel(guildName);
		guildMember[guildNumber] = new JCheckBox("member");
		guildQuest[guildNumber] = new JCheckBox("quest");
		guildQuestTarget[guildNumber] = new JLabel(NOQUESTTEXT);
		guildAttack[guildNumber] = new JTextField(3);
		guildDefense[guildNumber] = new JTextField(3);
		guildLevel[guildNumber] = new JTextField(3);
		guildXP[guildNumber] = new JTextField(8);
		guildXPNeeded[guildNumber] = new JLabel("0");
		for(GuildSkill gs : GuildSkill.values())
			guildSkills[guildNumber][gs.value()] = new JTextField(3);
		browseItems[guildNumber] = new JButton("Item");
		browseMonsters[guildNumber] = new JButton("Monster");
		questItem = new boolean[guildNumber];
		
		browseItems[guildNumber].addActionListener(this);
		browseMonsters[guildNumber].addActionListener(this);

		browseItems[guildNumber].setToolTipText("Browse for a quest item.");
		browseMonsters[guildNumber].setToolTipText("Browse for a quest monster.");
		
		guildMember[guildNumber].addActionListener(this);
		guildLevel[guildNumber].addActionListener(this);
		guildXP[guildNumber].addActionListener(this);
		guildLevel[guildNumber].addFocusListener(this);
		guildXP[guildNumber].addFocusListener(this);
		
		// Add to an actual panel.
		int layers = GuildSkill.values().length >> 2; // How many rows of skills do we need
		newGuildPanel.setLayout(new GridLayout(3 + (layers + 1), 1));
		
		JPanel infoPane = new JPanel();
		infoPane.add(guildNames[guildNumber]);
		infoPane.add(guildMember[guildNumber]);
		infoPane.add(new JLabel("Attack"));
		infoPane.add(guildAttack[guildNumber]);
		infoPane.add(new JLabel("Defense"));
		infoPane.add(guildDefense[guildNumber]);
		newGuildPanel.add(infoPane);
		
		JPanel levelPane = new JPanel();
		levelPane.add(new JLabel("Level:"));
		levelPane.add(guildLevel[guildNumber]);
		levelPane.add(new JLabel("Experience:"));
		levelPane.add(guildXP[guildNumber]);
		levelPane.add(new JLabel("XP to level"));
		levelPane.add(guildXPNeeded[guildNumber]);
		newGuildPanel.add(levelPane);
		
		JPanel questPane = new JPanel();
		questPane.add(guildQuest[guildNumber]);
		questPane.add(guildQuestTarget[guildNumber]);
		questPane.add(browseItems[guildNumber]);
		questPane.add(browseMonsters[guildNumber]);
		newGuildPanel.add(questPane);
		
		byte j = 0;
		for(int i = 0; i < layers; i++)
		{
			JPanel skillsPane = new JPanel();
			for(; j < (i + 1) << 2 && j < GuildSkill.values().length; j++)
			{
				skillsPane.add(new JLabel(GuildSkill.type(j).toString()));
				skillsPane.add(guildSkills[guildNumber][j]);
			}
			newGuildPanel.add(skillsPane);
		}
		
		enableGuild(guildNumber, false);

		return newGuildPanel;
	}
	
	/**
	 * Set up the structure of the panel.
	 * @return
	 */
	public boolean setupPanel()
	{
		int gCount = parent.dataBank.getGuildCount();
		removeAll();
		setLayout(new GridLayout(gCount, 1));
		QuadNode<Guild> gNode = parent.dataBank.getGuilds().firstNode();
		
		guildNames = new JLabel[gCount];
		guildQuestTarget = new JLabel[gCount];
		guildXPNeeded = new JLabel[gCount];
		guildMember = new JCheckBox[gCount];
		guildQuest = new JCheckBox[gCount];
		guildAttack = new JTextField[gCount];
		guildDefense = new JTextField[gCount];
		guildLevel = new JTextField[gCount];
		guildXP = new JTextField[gCount];
		guildSkills = new JTextField[gCount][GuildSkill.values().length];
		browseItems = new JButton[gCount];
		browseMonsters = new JButton[gCount];
		
		for(byte i = 0; i < gCount; i++)
		{
			JPanel guildPanel = getGuildPanel(gNode.getElement().getName(), i);
			add(guildPanel);
			gNode = gNode.getRight();
		}
		
		return true;
	}
	
	/**
	 * Update the data for the guilds in the panel.
	 * @return
	 */
	public boolean updatePanel()
	{
		// Update layout; resize if guild count differs.
		// Update guilds (in case guilds have been added/removed)
		// Update guild info.
		ListNode<GuildRecord> gNode = parent.currentPlayer.getGuildRecords().getFirstNode();
		
		// problem. player.guildRecords != dataBank.guilds
		
		for(byte i = 0; i < guildNames.length; i++)
			enableGuild(i, false);
		
		while(gNode != null)
		{
			for(byte i = 0; i < guildNames.length; i++)
			{
				if(gNode.getElement().getGuild().getName().equalsIgnoreCase(guildNames[i].getText()))
				{
					// This is the same guild, update this panel.
					guildMember[i].setSelected(true);
					enableGuild(i, true);
					if(gNode.getElement().isQuested())
					{
						guildQuest[i].setEnabled(true);
						guildQuest[i].setSelected(true);
						if(gNode.getElement().getQuestMonster() != null)
						{
							guildQuestTarget[i].setText(gNode.getElement().getQuestMonster().getName());
							questItem[i] = false;
						}
						else
						{
							guildQuestTarget[i].setText(gNode.getElement().getQuestItem().getName());
							questItem[i] = true;
						}
						browseItems[i].setEnabled(true);
						browseMonsters[i].setEnabled(true);
					}
					else
					{
						guildQuest[i].setEnabled(false);
						guildQuest[i].setSelected(false);
						guildQuestTarget[i].setText(NOQUESTTEXT);
						browseItems[i].setEnabled(false);
						browseMonsters[i].setEnabled(false);
					}
					
					guildAttack[i].setText("" + gNode.getElement().getGuildAttack());
					guildDefense[i].setText("" + gNode.getElement().getGuildDefense());
					guildLevel[i].setText("" + gNode.getElement().getLevel());
					guildXP[i].setText("" + gNode.getElement().getExperience());
					guildXPNeeded[i].setText("" + gNode.getElement().getXPNeeded());
					
					for(GuildSkill gs : GuildSkill.values())
						guildSkills[i][gs.value()].setText("" + gNode.getElement().getGuildSkill(gs));
					break;
				}
			}
			gNode = gNode.getNext();
		}
		return true;
	}
	
	public boolean updateGuilds(boolean alreadyValidated)
	{
		if(!alreadyValidated && !validateGuilds())
			return false;
		
		LinkedList<GuildRecord> nGuilds = new LinkedList<GuildRecord>();
		
		for(byte i = 0; i < guildMember.length; i++)
		{
			if(guildMember[i].isSelected())
			{
				GuildRecord nRecord = new GuildRecord(parent.dataBank.getGuild(guildNames[i].getText()));
				
				nRecord.setGuildAttack(Short.parseShort(guildAttack[i].getText()));
				nRecord.setGuildDefense(Short.parseShort(guildDefense[i].getText()));
				nRecord.setLevel(Short.parseShort(guildLevel[i].getText()));
				nRecord.setExperience(Long.parseLong(guildXP[i].getText()));
				
				for(GuildSkill gs : GuildSkill.values())
					nRecord.setGuildSkill(gs, Short.parseShort(guildSkills[i][gs.value()].getText()));
				
				if(guildQuest[i].isSelected())
				{
					if(questItem[i])
					{
						Item tItem = parent.dataBank.getItem(guildQuestTarget[i].getText());
						nRecord.setQuestItem(tItem);
						if(tItem == null)
							nRecord.setQuestMonster(parent.dataBank.getMonsterEden().getMonster(guildQuestTarget[i].getText()));
						else
							nRecord.setQuestMonster(null);
					}
					else
					{
						Monster tMon = parent.dataBank.getMonsterEden().getMonster(guildQuestTarget[i].getText());
						nRecord.setQuestMonster(tMon);
						if(tMon == null)
							nRecord.setQuestItem(parent.dataBank.getItem(guildQuestTarget[i].getText()));
						else
							nRecord.setQuestMonster(null);
					}
				}
				
				nGuilds.insert(nRecord);
			}
		}
		
		parent.currentPlayer.setGuildRecords(nGuilds);
			
		return true;
	}
	
	public boolean validateGuilds()
	{
		// remember, if quested, then a quest monster or a quest item must be set.
		for(byte i = 0; i < guildQuest.length; i++)
		{
			if(guildMember[i].isSelected())
			{
				if(guildQuest[i].isSelected())
				{
					if(guildQuestTarget[i].getText().equalsIgnoreCase(NOQUESTTEXT))
						return false;
				}
				
				if(!MordorEditor.validityCheckTf(this, guildAttack[i], 0, GuildRecord.MAXGUILDATTACk, "guild " + guildNames[i].getText() + " attack"))
					return false;
				if(!MordorEditor.validityCheckTf(this, guildDefense[i], 0, GuildRecord.MAXGUILDDEFENSE, "guild " + guildNames[i].getText() + " defense"))
					return false;
				if(!MordorEditor.validityCheckTf(this, guildLevel[i], 0, GuildRecord.MAXGUILDLEVEL, "guild " + guildNames[i].getText() + " level"))
					return false;
				if(!MordorEditor.validityCheckTf(this, guildXP[i], 0, GuildRecord.MAXGUILDXP, "guild " + guildNames[i].getText() + " experience"))
					return false;
				
				for(GuildSkill gs : GuildSkill.values())
					if(!MordorEditor.validityCheckTf(this, guildSkills[i][gs.value()], 0, GuildRecord.MAXGUILDSKILL, "guild " + guildNames[i].getText() + " skill " + gs.toString()))
						return false;
			}
		}
		return true;
	}
	
	private void enableGuild(byte gNum, boolean enabled)
	{
		guildAttack[gNum].setEnabled(enabled);
		guildDefense[gNum].setEnabled(enabled);
		browseItems[gNum].setEnabled(enabled);
		browseMonsters[gNum].setEnabled(enabled);
		guildLevel[gNum].setEnabled(enabled);
		guildXP[gNum].setEnabled(enabled);
		guildQuest[gNum].setEnabled(enabled);
		for(GuildSkill gs : GuildSkill.values())
			guildSkills[gNum][gs.value()].setEnabled(enabled);
	}
	
	/**
	 * Adjust the structure of the panel if data may have changed on other panels.
	 */
	public void updateLists()
	{
		setupPanel();
		updatePanel();
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		for(byte i = 0; i < guildNames.length; i++)
		{
			if(e.getSource() == guildMember[i])
			{
				enableGuild(i, guildMember[i].isSelected());
			}
			else if(e.getSource() == guildLevel[i])
			{
				if(!MordorEditor.validityCheckTf(this, guildLevel[i], 0, GuildRecord.MAXGUILDLEVEL, "guild " + guildNames[i].getText() + " level"))
					return;
	
				if(!MordorEditor.validityCheckTf(this, guildXP[i], 0, GuildRecord.MAXGUILDXP, "guild " + guildNames[i].getText() + " experience"))
					return;
				
				GuildRecord nGuild = new GuildRecord(parent.dataBank.getGuild(guildNames[i].getText()));
				short nLevel = Short.parseShort(guildLevel[i].getText());
				long nXP = Long.parseLong(guildXP[i].getText());
				
				if(nXP < nGuild.getLevelXP(nLevel, nGuild.getGuild(), parent.currentPlayer.getRace()))
					guildXP[i].setText("" + nGuild.getLevelXP(nLevel, nGuild.getGuild(), parent.currentPlayer.getRace()));
				else if(nXP >= nGuild.getLevelXP((short)(nLevel + 2), nGuild.getGuild(), parent.currentPlayer.getRace()))
					guildXP[i].setText("" + (nGuild.getLevelXP((short)(nLevel + 2), nGuild.getGuild(), parent.currentPlayer.getRace()) - 1));
				
				if(nXP == nGuild.getLevelXP((short)(nLevel + 1), nGuild.getGuild(), parent.currentPlayer.getRace()) - 1)
					guildXPNeeded[i].setText("Pinned");
				if(nXP >= nGuild.getLevelXP((short)(nLevel + 1), nGuild.getGuild(), parent.currentPlayer.getRace()))
					guildXPNeeded[i].setText((nGuild.getLevelXP((short)(nLevel + 2), nGuild.getGuild(), parent.currentPlayer.getRace()) - 1 - nXP) + " to pin.");
				else
					guildXPNeeded[i].setText((nGuild.getLevelXP((short)(nLevel + 1), nGuild.getGuild(), parent.currentPlayer.getRace()) - nXP) + " to level " + (nLevel + 1));
			}
			else if(e.getSource() == guildXP[i])
			{
				if(!MordorEditor.validityCheckTf(this, guildLevel[i], 0, GuildRecord.MAXGUILDLEVEL, "guild " + guildNames[i].getText() + " level"))
					return;
				if(!MordorEditor.validityCheckTf(this, guildXP[i], 0, GuildRecord.MAXGUILDXP, "guild " + guildNames[i].getText() + " experience"))
					return;
	
				GuildRecord nGuild = new GuildRecord(parent.dataBank.getGuild(guildNames[i].getText()));
				short nLevel = Short.parseShort(guildLevel[i].getText());
				long nXP = Long.parseLong(guildXP[i].getText());
	
				while(nXP >= nGuild.getLevelXP((short)(nLevel + 2), nGuild.getGuild(), parent.currentPlayer.getRace()))
					nLevel++;
				
				while(nXP < nGuild.getLevelXP(nLevel, nGuild.getGuild(), parent.currentPlayer.getRace()))
					nLevel--;
				
				guildLevel[i].setText("" + nLevel);
				
				if(nXP == nGuild.getLevelXP((short)(nLevel + 1), nGuild.getGuild(), parent.currentPlayer.getRace()) - 1)
					guildXPNeeded[i].setText("Pinned");
				if(nXP >= nGuild.getLevelXP((short)(nLevel + 1), nGuild.getGuild(), parent.currentPlayer.getRace()))
					guildXPNeeded[i].setText((nGuild.getLevelXP((short)(nLevel + 2), nGuild.getGuild(), parent.currentPlayer.getRace()) - 1 - nXP) + " to pin.");
				else
					guildXPNeeded[i].setText((nGuild.getLevelXP((short)(nLevel + 1), nGuild.getGuild(), parent.currentPlayer.getRace()) - nXP) + " to level " + (nLevel + 1));
			}
			else if(e.getSource() == browseItems[i])
			{
				questItem[i] = true;
				JFrame itemBrowse = new JFrame();
				
				itemBrowse.add(new ListChooserPanel(itemBrowse, guildQuestTarget[i], parent.dataBank, ListChooserPanel.BrowseTypes.items));
				itemBrowse.pack();
				itemBrowse.setVisible(true);
			}
			else if(e.getSource() == browseMonsters[i])
			{
				questItem[i] = false;
				JFrame monBrowse = new JFrame();
				
				monBrowse.add(new ListChooserPanel(monBrowse, guildQuestTarget[i], parent.dataBank, ListChooserPanel.BrowseTypes.monster));
				monBrowse.pack();
				monBrowse.setVisible(true);
			}
		}
	}

	public Dimension getPreferredScrollableViewportSize() 
	{
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

	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void focusLost(FocusEvent e) 
	{
		for(byte i = 0; i < guildNames.length; i++)
		{
			if(e.getSource() == guildLevel[i])
			{
				if(!MordorEditor.validityCheckTf(this, guildLevel[i], 0, GuildRecord.MAXGUILDLEVEL, "guild " + guildNames[i].getText() + " level"))
					return;
	
				if(!MordorEditor.validityCheckTf(this, guildXP[i], 0, GuildRecord.MAXGUILDXP, "guild " + guildNames[i].getText() + " experience"))
					return;
				
				GuildRecord nGuild = new GuildRecord(parent.dataBank.getGuild(guildNames[i].getText()));
				short nLevel = Short.parseShort(guildLevel[i].getText());
				long nXP = Long.parseLong(guildXP[i].getText());
				
				if(nXP < nGuild.getLevelXP(nLevel, nGuild.getGuild(), parent.currentPlayer.getRace()))
					guildXP[i].setText("" + nGuild.getLevelXP(nLevel, nGuild.getGuild(), parent.currentPlayer.getRace()));
				else if(nXP >= nGuild.getLevelXP((short)(nLevel + 2), nGuild.getGuild(), parent.currentPlayer.getRace()))
					guildXP[i].setText("" + (nGuild.getLevelXP((short)(nLevel + 2), nGuild.getGuild(), parent.currentPlayer.getRace()) - 1));
				
				if(nXP == nGuild.getLevelXP((short)(nLevel + 1), nGuild.getGuild(), parent.currentPlayer.getRace()) - 1)
					guildXPNeeded[i].setText("Pinned");
				if(nXP >= nGuild.getLevelXP((short)(nLevel + 1), nGuild.getGuild(), parent.currentPlayer.getRace()))
					guildXPNeeded[i].setText((nGuild.getLevelXP((short)(nLevel + 2), nGuild.getGuild(), parent.currentPlayer.getRace()) - 1 - nXP) + " to pin.");
				else
					guildXPNeeded[i].setText((nGuild.getLevelXP((short)(nLevel + 1), nGuild.getGuild(), parent.currentPlayer.getRace()) - nXP) + " to level " + (nLevel + 1));
			}
			else if(e.getSource() == guildXP[i])
			{
				if(!MordorEditor.validityCheckTf(this, guildLevel[i], 0, GuildRecord.MAXGUILDLEVEL, "guild " + guildNames[i].getText() + " level"))
					return;
				if(!MordorEditor.validityCheckTf(this, guildXP[i], 0, GuildRecord.MAXGUILDXP, "guild " + guildNames[i].getText() + " experience"))
					return;
	
				GuildRecord nGuild = new GuildRecord(parent.dataBank.getGuild(guildNames[i].getText()));
				short nLevel = Short.parseShort(guildLevel[i].getText());
				long nXP = Long.parseLong(guildXP[i].getText());
	
				while(nXP >= nGuild.getLevelXP((short)(nLevel + 2), nGuild.getGuild(), parent.currentPlayer.getRace()))
					nLevel++;
				
				while(nXP < nGuild.getLevelXP(nLevel, nGuild.getGuild(), parent.currentPlayer.getRace()))
					nLevel--;
				
				guildLevel[i].setText("" + nLevel);
				
				if(nXP == nGuild.getLevelXP((short)(nLevel + 1), nGuild.getGuild(), parent.currentPlayer.getRace()) - 1)
					guildXPNeeded[i].setText("Pinned");
				if(nXP >= nGuild.getLevelXP((short)(nLevel + 1), nGuild.getGuild(), parent.currentPlayer.getRace()))
					guildXPNeeded[i].setText((nGuild.getLevelXP((short)(nLevel + 2), nGuild.getGuild(), parent.currentPlayer.getRace()) - 1 - nXP) + " to pin.");
				else
					guildXPNeeded[i].setText((nGuild.getLevelXP((short)(nLevel + 1), nGuild.getGuild(), parent.currentPlayer.getRace()) - nXP) + " to level " + (nLevel + 1));
			}		
		}
	}

}
