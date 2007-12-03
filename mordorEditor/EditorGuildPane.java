package mordorEditor;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import mordorData.DataBank;
import mordorData.Guild;
import mordorData.GuildRecord;
import mordorData.Item;
import mordorData.ItemInstance;
import mordorData.Player;
import mordorData.Race;
import mordorData.SpellBook;
import mordorData.SpellReference;

import mordorEnums.Alignment;
import mordorEnums.GuildSkill;
import mordorEnums.SpellClass;
import mordorEnums.Stats;
import mordorHelpers.Util;

import structures.ListIter;
import structures.ListNode;

public class EditorGuildPane extends JPanel implements ActionListener, ListDataListener, ItemListener 
{
	private Guild guild;
	private DataBank dataBank;
	private EditorGuildPanel parent;
	
	private JTextArea taDescription;
	private JTextField tfNumExtraSwings;
	private JTextField tfMaxAttack, tfMaxDefense, tfMaxLevelAD;
	private JTextField tfJoinCost, tfGuildGoldFactor;
	private JTextField tfAH, tfMH, tfML, tfEP, tfQP;
	private JCheckBox[] jcbAlignment;
	private JTextField[] tfRequiredStats;
	private JTextField[] tfSkills;
	private JTextField[] tfExtraSwings;
	private JComboBox cbSpellType;
	private JList spellsList, raceList, guildSpellList;
	private DefaultListModel guildSpellLModel;
	private JScrollPane spellScroll, guildSpellScroll;
	private JButton update, delete, reset;
	private JButton jbGuildName, jbChooseMaster, jbChooseCrest, jbSpellAdd, jbSpellRemove;
	private JPanel listBar, spellPanel;
	private DefaultListModel spellModel;
	
	private String guildMasterName, guildMasterLevel, guildName;
	private Player guildMaster;
	//private Item guildCrest;
	
	EditorGuildPane(Guild newGuild, DataBank newDataBank, EditorGuildPanel newParent)
	{
		guild = newGuild;
		dataBank = newDataBank;
		parent = newParent;
		
		JPanel nameBar = new JPanel();
		listBar = new JPanel();
		JPanel buttonBar = new JPanel();
		JPanel detailsBar = new JPanel();
		
		jbGuildName = new JButton(Util.NOSTRING);
		jbChooseMaster = new JButton("Guild Master: None (0)");
		jbChooseCrest = new JButton(Util.NOSTRING);
		update = new JButton("Update");
		delete = new JButton("Delete");
		reset = new JButton("Reset");
		
		taDescription = new JTextArea(4, 16);
		taDescription.setWrapStyleWord(true);
		taDescription.setLineWrap(true);
		JScrollPane descPane = new JScrollPane(taDescription);
		
		jbGuildName.setToolTipText("Set the guild name.");
		jbChooseMaster.setToolTipText("Choose a guild master from players.");
		jbChooseCrest.setToolTipText("Choose a guild crest from items.");
		update.setToolTipText("Update guild in dataBank.");
		delete.setToolTipText("Delete guild from dataBank.");
		reset.setToolTipText("Reset guild from dataBank.");

		jbGuildName.addActionListener(this);
		jbChooseMaster.addActionListener(this);
		jbChooseCrest.addActionListener(this);
		update.addActionListener(this);
		delete.addActionListener(this);
		reset.addActionListener(this);
		
		buttonBar.add(jbChooseMaster);
		buttonBar.add(jbChooseCrest);
		buttonBar.add(update);
		buttonBar.add(delete);
		buttonBar.add(reset);

		jcbAlignment = new JCheckBox[Alignment.values().length];
		for(Alignment al : Alignment.values())
			jcbAlignment[al.value()] = new JCheckBox(al.name());

		nameBar.add(jbGuildName);
		for(int i = 0; i < jcbAlignment.length; i++)
			nameBar.add(jcbAlignment[i]);
		
		GridLayout statsTextLayout = new GridLayout(Stats.values().length + 1, 1);
		GridLayout skillsTextLayout = new GridLayout(GuildSkill.values().length + 1, 1);
		GridLayout extrasTextLayout = new GridLayout(5, 1);
		GridLayout swingsTextLayout = new GridLayout(Guild.SWINGS_MAX + 1, 1);
		statsTextLayout.setVgap(4);
		skillsTextLayout.setVgap(4);
		extrasTextLayout.setVgap(4);
		swingsTextLayout.setVgap(4);
		
		JPanel statsPanel = new JPanel();
		JPanel statsValPanel = new JPanel();
		JPanel skillsPanel = new JPanel();
		JPanel skillsValPanel = new JPanel();
		JPanel extrasPanelA = new JPanel();
		JPanel extrasPanelB = new JPanel();
		JPanel extrasPanelC = new JPanel();
		JPanel extrasPanelD = new JPanel();
		JPanel swingsText = new JPanel();
		JPanel swingsVal = new JPanel();
		statsPanel.setLayout(statsTextLayout);
		statsValPanel.setLayout(new GridLayout(Stats.values().length + 1, 1));
		skillsPanel.setLayout(skillsTextLayout);
		skillsValPanel.setLayout(new GridLayout(GuildSkill.values().length + 1, 1));
		extrasPanelA.setLayout(extrasTextLayout);
		extrasPanelB.setLayout(new GridLayout(5, 1));
		extrasPanelC.setLayout(extrasTextLayout);
		extrasPanelD.setLayout(new GridLayout(5, 1));
		swingsText.setLayout(swingsTextLayout);
		swingsVal.setLayout(new GridLayout(Guild.SWINGS_MAX + 1, 1));

		tfRequiredStats = new JTextField[Stats.values().length]; 
		tfSkills = new JTextField[GuildSkill.values().length];
		
		// add in stats
		statsPanel.add(new JLabel("Req. Stats"));
		statsValPanel.add(new JLabel());
		for(byte x = 0; x < tfRequiredStats.length; x++)
		{
			tfRequiredStats[x] = new JTextField(2);
			tfRequiredStats[x].setPreferredSize(new Dimension(12, 20));
			statsPanel.add(new JLabel(Stats.type(x).name()));
			statsValPanel.add(tfRequiredStats[x]);
			
		}
		
		skillsPanel.add(new JLabel("Skills"));
		skillsValPanel.add(new JLabel());
		for(byte x = 0; x < GuildSkill.values().length; x++)
		{
			tfSkills[x] = new JTextField(2);
			tfSkills[x].setPreferredSize(new Dimension(12, 20));
			skillsPanel.add(new JLabel(GuildSkill.type(x).toString()));
			skillsValPanel.add(tfSkills[x]);
		}
		
		tfAH = new JTextField(2);
		tfAH.setToolTipText("Average hits awarded before max level.");
		tfAH.setPreferredSize(new Dimension(12, 20));
		tfMH = new JTextField(2);
		tfMH.setToolTipText("Hits awarded after max level.");
		tfMH.setPreferredSize(new Dimension(12, 20));
		tfML = new JTextField(2);
		tfML.setToolTipText("Max level when average hits stops.");
		tfML.setPreferredSize(new Dimension(12, 20));
		tfEP = new JTextField(2);
		tfEP.setToolTipText("Experience penalty of guild.");
		tfEP.setPreferredSize(new Dimension(12, 20));
		tfQP = new JTextField(2);
		tfQP.setToolTipText("Quest percentage of guild.");
		tfQP.setPreferredSize(new Dimension(12, 20));
		tfMaxAttack = new JTextField(5);
		tfMaxAttack.setToolTipText("Max attack awarded by guild.");
		tfMaxAttack.setPreferredSize(new Dimension(12, 20));
		tfMaxDefense = new JTextField(5);
		tfMaxDefense.setToolTipText("Max defense awarded by guild.");
		tfMaxDefense.setPreferredSize(new Dimension(12, 20));
		tfMaxLevelAD = new JTextField(5);
		tfMaxLevelAD.setToolTipText("Max level that max A/D are reached.");
		tfMaxLevelAD.setPreferredSize(new Dimension(12, 20));
		tfJoinCost = new JTextField(5);
		tfJoinCost.setToolTipText("Cost to join the guild.");
		tfJoinCost.setPreferredSize(new Dimension(12, 20));
		tfGuildGoldFactor = new JTextField(1);
		tfGuildGoldFactor.setToolTipText("Gold factor for guild.");
		tfGuildGoldFactor.setPreferredSize(new Dimension(12, 20));

		extrasPanelA.add(new JLabel("Avg Hits"));
		extrasPanelB.add(tfAH);
		extrasPanelC.add(new JLabel("Max Attack"));
		extrasPanelD.add(tfMaxAttack);

		extrasPanelA.add(new JLabel("Max Hits"));
		extrasPanelB.add(tfMH);
		extrasPanelC.add(new JLabel("Max Defense"));
		extrasPanelD.add(tfMaxDefense);

		extrasPanelA.add(new JLabel("Max Level"));
		extrasPanelB.add(tfML);
		extrasPanelC.add(new JLabel("Max AD Level"));
		extrasPanelD.add(tfMaxLevelAD);
		
		extrasPanelA.add(new JLabel("Exp. Penalty"));
		extrasPanelB.add(tfEP);
		extrasPanelC.add(new JLabel("Join Cost"));
		extrasPanelD.add(tfJoinCost);

		extrasPanelA.add(new JLabel("Quest %"));
		extrasPanelB.add(tfQP);
		extrasPanelC.add(new JLabel("GGF"));
		extrasPanelD.add(tfGuildGoldFactor);
		
		tfNumExtraSwings = new JTextField(2);
		tfNumExtraSwings.setPreferredSize(new Dimension(12, 20));
		tfNumExtraSwings.setToolTipText("Number of extra swings.");
		tfNumExtraSwings.addActionListener(this);
		tfExtraSwings = new JTextField[Guild.SWINGS_MAX];
		
		swingsText.add(new JLabel("# Swings"));
		swingsVal.add(tfNumExtraSwings);
		for(int i = 1; i <= Guild.SWINGS_MAX; i++)
		{
			tfExtraSwings[i - 1] = new JTextField(2);
			tfExtraSwings[i - 1].setPreferredSize(new Dimension(12, 20));
			if(i < 11 || i > 13)
			{
				switch((i) % 10)
				{
				case 1:
					swingsText.add(new JLabel("lvl of " + i + "st"));
					break;
				case 2:
					swingsText.add(new JLabel("lvl of " + i + "nd"));
					break;
				case 3:
					swingsText.add(new JLabel("lvl of " + i + "rd"));
					break;
				default:
					swingsText.add(new JLabel("lvl of " + i + "th"));
				}
			}
			else
				swingsText.add(new JLabel("lvl of " + i + "th"));
			tfExtraSwings[i - 1].setToolTipText("Level player receives this swing.");
			swingsVal.add(tfExtraSwings[i - 1]);
		}
		
		detailsBar.add(statsPanel);
		detailsBar.add(statsValPanel);
		detailsBar.add(skillsPanel);
		detailsBar.add(skillsValPanel);
		detailsBar.add(extrasPanelA);
		detailsBar.add(extrasPanelB);
		detailsBar.add(extrasPanelC);
		detailsBar.add(extrasPanelD);
		detailsBar.add(swingsText);
		detailsBar.add(swingsVal);
		
		DefaultListModel raceModel = new DefaultListModel();
		String[] raceNames = dataBank.getRaceNames();
		for(int i = 0; i < raceNames.length; i++)
			raceModel.addElement(raceNames[i]);
		raceList = new JList(raceModel);
		raceList.setVisibleRowCount(5);
		JScrollPane raceScroll = new JScrollPane(raceList);
				
		cbSpellType = new JComboBox(SpellClass.values());
		cbSpellType.addActionListener(this);
		
		String[] spellNames = dataBank.getSpellBook().getSpellClassNames((SpellClass)cbSpellType.getSelectedItem());//.getSpellClass((String)cbSpellType.getSelectedItem()).getSpellNames();
		
		if(spellNames == null)
		{
			spellModel = new DefaultListModel();
			spellsList = new JList();
			spellsList.setModel(spellModel);
			spellsList.setPrototypeCellValue("Burninantor");
		}
		else
		{
			spellsList = new JList();
			spellModel = new DefaultListModel();
			for(byte i = 0; i < spellNames.length; i++)
				spellModel.addElement(spellNames[i]);
			spellsList.setModel(spellModel);
			spellsList.setPrototypeCellValue("Burninantor");
		}
		
		spellsList.setVisibleRowCount(5);
		
		jbSpellAdd = new JButton("Add Spell");
		jbSpellRemove = new JButton("Remove Spell");
		
		jbSpellAdd.addActionListener(this);
		jbSpellRemove.addActionListener(this);
		
		spellScroll = new JScrollPane(spellsList);
		
		guildSpellLModel = new DefaultListModel();
		guildSpellList = new JList(guildSpellLModel);
		guildSpellList.setVisibleRowCount(5);
		guildSpellList.setLayoutOrientation(JList.VERTICAL_WRAP);
		guildSpellScroll = new JScrollPane(guildSpellList);
		guildSpellScroll.setPreferredSize(new Dimension(200, 90));
		
		JPanel spellSwitcher = new JPanel();
		spellSwitcher.setLayout(new GridLayout(3, 1));
		
		spellSwitcher.add(cbSpellType);
		spellSwitcher.add(jbSpellAdd);
		spellSwitcher.add(jbSpellRemove);
		
		JPanel descPanel = new JPanel();
		descPanel.setLayout(new BorderLayout());
		descPanel.add(new JLabel("Description"), BorderLayout.NORTH);
		descPanel.add(descPane, BorderLayout.CENTER);
		
		JPanel racePanel = new JPanel();
		racePanel.setLayout(new BorderLayout());
		racePanel.add(new JLabel("Allowed\nRaces"), BorderLayout.NORTH);
		racePanel.add(raceScroll, BorderLayout.CENTER);
		
		JPanel guildSpellPanel = new JPanel();
		guildSpellPanel.setLayout(new BorderLayout());
		guildSpellPanel.add(new JLabel("Guild's\nSpells"), BorderLayout.NORTH);
		guildSpellPanel.add(guildSpellScroll, BorderLayout.CENTER);
		
		spellPanel = new JPanel();
		spellPanel.setLayout(new BorderLayout());
		spellPanel.add(new JLabel(((SpellClass)cbSpellType.getSelectedItem()).name()), BorderLayout.NORTH);
		spellPanel.add(spellScroll, BorderLayout.CENTER);

		listBar.add(descPanel);
		listBar.add(racePanel);
		listBar.add(spellPanel);
		listBar.add(spellSwitcher);
		listBar.add(guildSpellPanel);
		
		setLayout(new BorderLayout());
		
		JPanel topBar = new JPanel();
		topBar.setLayout(new GridLayout(2, 1));
		topBar.add(nameBar);
		topBar.add(buttonBar);
		
		add(topBar, BorderLayout.NORTH);
		add(listBar, BorderLayout.CENTER);
		add(detailsBar, BorderLayout.SOUTH);
		
		updatePanelValues();
	}
	
	/**
	 * Changes the spells shown in spellsList. This is for when the player
	 * changes the spell class they are currently looking at. 
	 * @param spellType	The name of the new spell class (String)
	 */
	private void changeSpellList()
	{
		String[] spellNames = dataBank.getSpellBook().getSpellClassNames((SpellClass)cbSpellType.getSelectedItem());
		
		if(spellNames == null)
		{
			spellModel.removeAllElements();
			//spellsList.setPrototypeCellValue("Burninator");
		}
		else
		{
			spellModel.removeAllElements();
			for(int i = 0; i < spellNames.length; i++)
				spellModel.addElement(spellNames[i]);
				
		}
	}
	
	/**
	 * Updates the selections in the panel to what is in the guild.
	 *
	 */
	private void updatePanelValues()
	{
		this.setName(guild.getName());
		
		jbGuildName.setText("Guild Name: " + guild.getName());
		jbChooseMaster.setText("Guild Master: " + guild.getGuildMasterName() + " (" + guild.getGuildMasterLevel() + ")");
		if(guild.getCrest() != null)
			jbChooseCrest.setText(guild.getCrest().getName());
		else
			jbChooseCrest.setText(Util.NOSTRING);
		
		guildMasterName = guild.getName();
		guildMasterLevel = String.valueOf(guild.getGuildMasterLevel());
		guildMaster = guild.getGuildMaster();
		//guildCrest = guild.getCrest();
		guildName = guild.getName();
		
		for(Alignment al : Alignment.values())
			jcbAlignment[al.value()].setSelected(guild.allowedAlignment(al));

		taDescription.setText(guild.getDescription());
		
		ListIter<Race> rNode = guild.getAllowedRaces().getIterator();
		DefaultListModel raceModel = (DefaultListModel)raceList.getModel();
		int size = raceModel.getSize();
		int count = 0;
		boolean[] selection = new boolean[size];
		for(int i = 0; i < selection.length; i++)
			selection[i] = false;
		
		while(rNode.next())
		{
			for(int i = 0; i < size; i++)
				if(((String)raceModel.getElementAt(i)).equalsIgnoreCase(rNode.element().getName()))
				{
					selection[i] = true;
					count++;
					break;
				}
		}
		
		int[] vals = new int[count];
		count = 0;
		
		for(int i = 0; i < selection.length; i++)
			if(selection[i])
			{
				vals[count] = i;
				count++;
			}
		
		raceList.setSelectedIndices(vals);
		
		ListIter<SpellReference> sNode = guild.getLearnedSpells().getIterator();
		
		guildSpellLModel = new DefaultListModel();
		
		while(sNode.next())
			guildSpellLModel.addElement(sNode.element().getSpell().getName() + " - " + sNode.element().getLevel());
		
		guildSpellList.setModel(guildSpellLModel);
		
		for(byte i = 0; i < tfRequiredStats.length; i++)
			tfRequiredStats[i].setText("" + guild.getStat(Stats.type(i)));
		for(GuildSkill gs : GuildSkill.values())
			tfSkills[gs.value()].setText("" + guild.getSkillFactor(gs));
		
		tfAH.setText("" + guild.getAH());
		tfMH.setText("" + guild.getMH());
		tfML.setText("" + guild.getML());
		tfEP.setText("" + guild.getEP());
		tfQP.setText("" + guild.getQP());
		
		tfMaxAttack.setText("" + guild.getAttack());
		tfMaxDefense.setText("" + guild.getDefense());
		tfMaxLevelAD.setText("" + guild.getLevel());
		tfJoinCost.setText("" + guild.getJoinCost());
		tfGuildGoldFactor.setText("" + guild.getGGF());
		
		count = 0;
		for(byte i = 0; i < Guild.SWINGS_MAX; i++)
		{
			if(guild.getExtraSwingLevel(i) > 0)
			{
				count += 1;
				tfExtraSwings[i].setText("" + guild.getExtraSwingLevel(i));
				tfExtraSwings[i].setEnabled(true);
			}
			else
				tfExtraSwings[i].setEnabled(false);
		}
		
		tfNumExtraSwings.setText("" + count);
	}
	
	/**
	 * Updates the values stored in the guild to what is displayed.
	 *
	 */
	public boolean updateGuildValues()
	{
		if(guildName.contains(Util.NOSTRING))
		{
			JOptionPane.showMessageDialog(this, "Invalid name.");
			return false;
		}
		// master, crest are checked already
		// description doesn't need to be checked
		// spells don't need to be checked (happens passively)
		// num of extra swings is checked already
		if(!detailsValid())
			return false;
		
		// things check out, so set the guild values.
		guild.setName(guildName);
		guild.setDescription(taDescription.getText());
		if(guildMaster != null)
			guild.setGuildMaster(guildMaster);
		else
			guild.setGuildMaster(guildMasterName, Short.parseShort(guildMasterLevel));

		guild.setCrest(getCrest());
		
		for(Alignment al : Alignment.values())
			guild.setAlignment(al, jcbAlignment[al.value()].isSelected());
		
		int[] rVals = raceList.getSelectedIndices();
		for(int i = 0; i < rVals.length; i++)
		{
			guild.addAllowedRace(dataBank.getRace((String)raceList.getModel().getElementAt(rVals[i])));
		}
		
		if(rVals == null || rVals.length < 1)
			guild.addAllowedRace(dataBank.getRaces().first());
		
		SpellReference sTemp;
		for(int i = 0; i < guildSpellLModel.getSize(); i++)
		{
			String spellName = getSpellName((String)guildSpellLModel.get(i));
			short spellLevel = getSpellLevel((String)guildSpellLModel.get(i));
		
			sTemp = dataBank.getSpellBook().getSpell(spellName);
			
			if(spellLevel >= 0)
				sTemp.setLevel(spellLevel);
			
			guild.addSpell(sTemp);
		}
		
		// get all the little values at the bottom. they have all been checked
		// so parses are safe
		
		for(byte i = 0; i < tfRequiredStats.length; i++)
			guild.setStat(Stats.type(i), Byte.parseByte(tfRequiredStats[i].getText().trim()));
		for(GuildSkill gs : GuildSkill.values())
			guild.setSkillFactor(gs, Float.parseFloat(tfSkills[gs.value()].getText().trim()));
		
		guild.setAH(Byte.parseByte(tfAH.getText().trim()));
		guild.setMH(Byte.parseByte(tfMH.getText().trim()));
		guild.setML(Short.parseShort(tfML.getText().trim()));
		guild.setEP(Byte.parseByte(tfEP.getText().trim()));
		guild.setQP(Byte.parseByte(tfQP.getText().trim()));
		
		guild.setAttack(Short.parseShort(tfMaxAttack.getText().trim()));
		guild.setDefense(Short.parseShort(tfMaxDefense.getText().trim()));
		guild.setLevel(Short.parseShort(tfMaxLevelAD.getText().trim()));
		guild.setJoinCost(Long.parseLong(tfJoinCost.getText().trim()));
		guild.setGuildGoldFactor(Byte.parseByte(tfGuildGoldFactor.getText().trim()));
		
		for(byte i = 0; i < Guild.SWINGS_MAX; i++)
		{
			if(tfExtraSwings[i].isEnabled())
				guild.setExtraSwings(i, Short.parseShort(tfExtraSwings[i].getText().trim()));
			else
				guild.setExtraSwings(i, (short)-1);
		}
		
		return true;
	}
	
	/**
	 * Checks to ensure all the fields have valid values.
	 * @return True of all fields are valid.
	 */
	private boolean detailsValid()
	{
		for(byte i = 0; i < tfRequiredStats.length; i++)
		{
			if(!MordorEditor.validityCheckTf(this, tfRequiredStats[i], Stats.MINIMUMVALUE, Stats.MAXIMUMVALUE, Stats.type(i).name()))
				return false;
		}
		
		for(GuildSkill gs : GuildSkill.values())
		{
			float temp = -1;
			
			try
			{
				temp = Float.parseFloat(tfSkills[gs.value()].getText());
			}
			catch(NumberFormatException NOE)
			{
				JOptionPane.showMessageDialog(this, "Invalid value for " + gs.toString());
				return false;
			}
			if(temp < 0.0 || temp > 32.0)
			{
				JOptionPane.showMessageDialog(this, "Invalid value for " + gs.toString());
				return false;
			}
		}
		
		if(!MordorEditor.validityCheckTf(this, tfAH, 0, Byte.MAX_VALUE, "average hits"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfMH, 0, Byte.MAX_VALUE, "maximum hits"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfML, 0, Short.MAX_VALUE, "maximum level"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfEP, 0, Byte.MAX_VALUE, "experience penalty"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfQP, 0, 100, "quest percentage"))
			return false;
		
		if(!MordorEditor.validityCheckTf(this, tfMaxAttack, 0, Short.MAX_VALUE, "maximum attack"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfMaxDefense, 0, Short.MAX_VALUE, "maximum defense"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfMaxLevelAD, 0, Short.MAX_VALUE, "maximum attack/defense level"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfJoinCost, 0, Long.MAX_VALUE, "join cost"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfGuildGoldFactor, 0, Byte.MAX_VALUE, "guild gold factor(GGF)"))
			return false;
		
		// num swings already checked
		for(int i = 0; i < Integer.parseInt(tfNumExtraSwings.getText()); i++)
		{
			if(!MordorEditor.validityCheckTf(this, tfExtraSwings[i], 0, Short.MAX_VALUE, "swing #" + i))
				return false;
		}
		return true;
	}
	
	private String getSpellName(String spellListName)
	{
		int separator = spellListName.indexOf('-');
		
		if(separator > 0)
			return spellListName.substring(0, separator - 1).trim();
		else
			return spellListName;
	}
	
	private short getSpellLevel(String spellListName)
	{
		int separator = spellListName.indexOf('-');
		
		if(separator <= 0)
			return -1;
		
		String temp = spellListName.substring(separator + 1);
		
		try
		{
			return Short.parseShort(temp.trim());
		}
		catch(NumberFormatException NOE)
		{
			return -1;
		}
	}
	
	public void updateLists()
	{
		changeSpellList();

		DefaultListModel raceModel = new DefaultListModel();
		String[] raceNames = dataBank.getRaceNames();
		for(int i = 0; i < raceNames.length; i++)
			raceModel.addElement(raceNames[i]);
		raceList.setModel(raceModel);
		
		ListIter<Race> rNode = guild.getAllowedRaces().getIterator();
		int size = raceModel.getSize();
		int count = 0;
		boolean[] selection = new boolean[size];
		for(int i = 0; i < selection.length; i++)
			selection[i] = false;
		
		while(rNode.next())
		{
			for(int i = 0; i < size; i++)
				if(((String)raceModel.getElementAt(i)).equalsIgnoreCase(rNode.element().getName()))
				{
					selection[i] = true;
					count++;
					break;
				}
		}
		
		int[] vals = new int[count];
		count = 0;
		
		for(int i = 0; i < selection.length; i++)
			if(selection[i])
			{
				vals[count] = i;
				count++;
			}
		
		raceList.setSelectedIndices(vals);

		SpellReference sTemp;
		for(int i = 0; i < guildSpellLModel.getSize(); i++)
		{
			String spellName = getSpellName((String)guildSpellLModel.get(i));
		
			sTemp = dataBank.getSpellBook().getSpell(spellName);
			
			if(sTemp == null)
			{
				guildSpellLModel.removeElementAt(i);
				i--; // Something has been removed, so everything after should have a lower index.
			}
		}
	}
	
	private void adjustActiveExtraSwings(int newNumber)
	{
		for(int i = 0; i < Guild.SWINGS_MAX; i++)
		{
			if(i < newNumber)
				tfExtraSwings[i].setEnabled(true);
			else
				tfExtraSwings[i].setEnabled(false);
		}
	}
	
	private Item getCrest()
	{
		if(jbChooseCrest.getText().trim().equalsIgnoreCase(Util.NOSTRING))
			return null;
		else
			return dataBank.getItem(jbChooseCrest.getText().trim());
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == jbGuildName)
		{
			String name = JOptionPane.showInputDialog("Enter new guild name:" , guild.getName());
			
			if(name != null && name.length() > 1 && dataBank.validGuildName(name))
			{
				jbGuildName.setText("Guild Name: " + name);
				this.setName(name);
				parent.updateName(this);
				guildName = name;
			}
			else if(name != null && name.length() < 2)
				JOptionPane.showMessageDialog(this, "Invalid guild name.");
		}
		else if(e.getSource() == jbChooseMaster)
		{
			String name = JOptionPane.showInputDialog("Enter new guild master name:", "Darth Augodar");
			String level = "42";
			Player newGuildMaster = null;
			if(name != null && name.length() > 0)
			{
				newGuildMaster = dataBank.getPlayer(name);
				if(newGuildMaster != null)
				{
					// Get the level from here, retrieve it from its guilds
					GuildRecord tGuild = newGuildMaster.getGuildRecord((byte)guild.getID());
					if(tGuild == null)
					{
						JOptionPane.showMessageDialog(this, "Player not a member of this guild.");
						return;
					}
					else
					{
						level = "" + tGuild.getLevel();
					}
				}
				else
				{
					// player does not exist, so we are making it up.
					while(true)
					{
						level = JOptionPane.showInputDialog("Enter new guild master level:", "42");

						if(level == null)
							return;
						
						try 
						{
							Short.parseShort(level);
						}
						catch(NumberFormatException NOE)
						{
							JOptionPane.showMessageDialog(this, "Invalid level.");
							return;
						}
						
						if(Short.parseShort(level) < 0)
							JOptionPane.showMessageDialog(this, "Invalid level.");
						else
							break;
					}
				}
			}
			else if(name != null)
				JOptionPane.showMessageDialog(this, "Invalid player name.");
			else
				return;
			
			guildMasterName = name;
			guildMasterLevel = level;
			guildMaster = newGuildMaster;
			jbChooseMaster.setText("Guild Master: " + name + " (" + level + ")");
				
		}
		else if(e.getSource() == jbChooseCrest)
		{
			JFrame crestBrowse = new JFrame();
			
			crestBrowse.add(new ListChooserPanel(crestBrowse, jbChooseCrest, dataBank, ListChooserPanel.BrowseTypes.items));
			crestBrowse.pack();
			crestBrowse.setVisible(true);
			
			
		/*	String crest = "Vapours";
			Item newGuildCrest = null;

			while(true)
			{
				crest = JOptionPane.showInputDialog("Enter name of guild crest:", "Amulet of extreme plot significance.");
				if(crest != null)
				{
					newGuildCrest = dataBank.getItem(crest);
					if(newGuildCrest != null)
						break;
					else
					{
						JOptionPane.showMessageDialog(this, "Silly August, you need to add in items!");
						break;
					}
				}
				else
					return;
			}
			
			guildCrest = newGuildCrest;
			jbChooseCrest.setText("Guild Crest: " + crest);*/
		}
		else if(e.getSource() == update)
		{
			if(JOptionPane.showConfirmDialog(this, "Use these values for guild? (No save)", "Update Guild", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				updateGuildValues();
		}
		else if(e.getSource() == reset)
		{
			if(JOptionPane.showConfirmDialog(this, "Reset guild?", "Reset guild", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				updatePanelValues();
		}
		else if(e.getSource() == delete)
		{
			if(!parent.removeSelectedGuild())
				JOptionPane.showMessageDialog(this, "Guild could not be deleted.");
		}
		else if(e.getSource() == cbSpellType)
		{
			changeSpellList();
		}
		else if(e.getSource() == jbSpellAdd)
		{
			Object[] spellNames = spellsList.getSelectedValues();
			for(int i = 0; i < spellNames.length; i++)
			{
				if(!(guildSpellLModel.contains(spellNames[i])))
				{
					String level;
					while(true)
					{
						level = JOptionPane.showInputDialog("New level.", dataBank.getSpellBook().getSpell((String)spellNames[i]).getLevel());
						if(level == null)
							break;
						
						try
						{
							int temp = Short.parseShort(level);
							if(temp < 0 || temp > Short.MAX_VALUE)
								JOptionPane.showMessageDialog(this, "Invalid level.");
							else
							{
								guildSpellLModel.addElement((String)spellNames[i] + " - " + level);
								break;
							}		
						}
						catch(NumberFormatException NOE)
						{
							JOptionPane.showMessageDialog(this, "Invalid level.");
						}
						
						
						

					}
				}
			}
		}
		else if(e.getSource() == jbSpellRemove)
		{
			int[] guildSpells = guildSpellList.getSelectedIndices();
			for(int i = 0; i < guildSpells.length; i++)
			{
				guildSpellLModel.removeElementAt(guildSpells[i]);
			}
		}
		else if(e.getSource() == tfNumExtraSwings)
		{
			String newNumberString = tfNumExtraSwings.getText();
			int newNumber = 0;
			
			try
			{
				newNumber = Integer.parseInt(newNumberString);
			}
			catch(Exception anyE)
			{
				tfNumExtraSwings.setText("0");
				adjustActiveExtraSwings(0);
				return;
			}
			
			if(newNumber >= Guild.SWINGS_MAX)
			{
				tfNumExtraSwings.setText("" + Guild.SWINGS_MAX);
				adjustActiveExtraSwings(Guild.SWINGS_MAX);
			}
			else
				adjustActiveExtraSwings(newNumber);
		}
	}

	public void contentsChanged(ListDataEvent e) {
		// TODO Auto-generated method stub

	}

	public void intervalAdded(ListDataEvent e) {
		// TODO Auto-generated method stub

	}

	public void intervalRemoved(ListDataEvent e) {
		// TODO Auto-generated method stub

	}

	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub

	}
	
	public Guild getGuild()
	{
		return guild;
	}
}
