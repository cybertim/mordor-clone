package mordorEditor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import mordorData.DataBank;
import mordorData.Guild;
import mordorData.Map;
import mordorData.MapLevel;
import mordorData.MapSquare;
import mordorData.Player;
import mordorData.Race;
import mordorEnums.Alignment;
import mordorEnums.Direction;
import mordorEnums.PlayerSkill;
import mordorEnums.PlayerState;
import mordorEnums.Resistance;
import mordorEnums.Stats;
import mordorHelpers.Coord;
import mordorHelpers.Util;

public class PlayerGeneral extends JPanel implements ActionListener
{
	private PlayerPanel parent;
	private JPanel topBar, fieldsBar;
	
	private JTextField tfX, tfY, tfZ;
	private JTextField tfSx, tfSy, tfSz;
	private JTextField tfNaturalStats[];
	private JTextField tfHP, tfMP, tfMaxHP, tfGoldOnHand, tfAge;
	private JTextField tfKills, tfDeaths, tfItemQuests, tfMonQuests;
	private JTextField tfSkills[];
	
	private JComboBox jcRace, jcAlignment, jcDirection;
	
	private JCheckBox[] jcbStates;

	private JLabel[] jlAcquiredResist, jlAcquiredStats;
	private JLabel jlTotalGold;
	private JLabel jlMaxMP, jlAttack, jlDefense;
	// in the above labels the first one is the type, the second
	// one is the text (0) and value (1)
	// This are not editable, this will allow 
	private static final byte ACQUIRED_NAME = 0;
	private static final byte ACQUIRED_VALUE = 1;
	
	PlayerGeneral(PlayerPanel nParent)
	{
		topBar = new JPanel();
		JPanel cbBar = new JPanel();
		JPanel locBar = new JPanel();
		JPanel healthBar = new JPanel();
		
		fieldsBar = new JPanel();

		parent = nParent;
		jcRace = new JComboBox(parent.dataBank.getRaceNames());
		jcAlignment = new JComboBox(Alignment.values());
		jlAttack = new JLabel("Attack: 000");
		jlDefense = new JLabel("Defense: 000");
		jlTotalGold = new JLabel("Total Gold: 000,000,000,000,000");
		
		tfX = new JTextField(2);
		tfY = new JTextField(2);
		tfZ = new JTextField(2);
		jcDirection = new JComboBox(Direction.values());
		
		tfHP = new JTextField(4);
		tfMP = new JTextField(4);
		tfMaxHP = new JTextField(4);
		tfAge = new JTextField(4);
		tfGoldOnHand = new JTextField(10);
		jlMaxMP = new JLabel("Max MP: 0000");
		tfKills = new JTextField(5);
		tfDeaths = new JTextField(3);
		tfSx = new JTextField(2);
		tfSy = new JTextField(2);
		tfSz = new JTextField(2);
		
		tfHP.setToolTipText("Current health points");
		tfMP.setToolTipText("Current magic points");
		tfMaxHP.setToolTipText("Maximum health points");
		jlMaxMP.setToolTipText("Maximum magic points");
		tfAge.setToolTipText("Current age");
		tfGoldOnHand.setToolTipText("Current gold pieces on player.");
		tfKills.setToolTipText("Current monster kills.");
		tfDeaths.setToolTipText("Current player deaths.");
		
		cbBar.add(new JLabel("Race: "));
		cbBar.add(jcRace);
		cbBar.add(new JLabel("Alignment: "));
		cbBar.add(jcAlignment);
		cbBar.add(jlAttack);
		cbBar.add(jlDefense);
		cbBar.add(jlTotalGold);
		
		locBar.add(new JLabel("X"));
		locBar.add(tfX);
		locBar.add(new JLabel("Y"));
		locBar.add(tfY);
		locBar.add(new JLabel("Z"));
		locBar.add(tfZ);
		locBar.add(new JLabel("Direction "));
		locBar.add(jcDirection);
		locBar.add(new JLabel("Gold"));
		locBar.add(tfGoldOnHand);
		locBar.add(new JLabel("Sanctuary: X"));
		locBar.add(tfSx);
		locBar.add(new JLabel("Y"));
		locBar.add(tfSy);
		locBar.add(new JLabel("Z"));
		locBar.add(tfSz);
		
		healthBar.add(new JLabel("HP"));
		healthBar.add(tfHP);
		healthBar.add(new JLabel("Max HP"));
		healthBar.add(tfMaxHP);
		healthBar.add(new JLabel("MP"));
		healthBar.add(tfMP);
		healthBar.add(jlMaxMP);
		healthBar.add(new JLabel("Age"));
		healthBar.add(tfAge);
		healthBar.add(new JLabel("Kills"));
		healthBar.add(tfKills);
		healthBar.add(new JLabel("Deaths"));
		healthBar.add(tfDeaths);
		
		tfNaturalStats = new JTextField[Stats.values().length + 1];
		jlAcquiredStats = new JLabel[Stats.values().length + 1];
		
		JPanel nStatsNames, nStats, aStats;
		nStatsNames = new JPanel();
		nStats = new JPanel();
		aStats = new JPanel();
		
		GridLayout nStatsNamesLayout = new GridLayout(Stats.values().length + 1, 1);
		nStatsNamesLayout.setVgap(6);
		nStatsNames.setLayout(nStatsNamesLayout);
		nStats.setLayout(new GridLayout(Stats.values().length + 1, 1));
		aStats.setLayout(nStatsNamesLayout);
		
		nStatsNames.add(new JLabel("Stats"));
		nStats.add(new JLabel("Nat."));
		aStats.add(new JLabel("Acq."));
		
		for(byte i = 0; i < Stats.values().length; i++)
		{
			tfNaturalStats[i] = new JTextField(2);
			jlAcquiredStats[i] = new JLabel("00");
			nStatsNames.add(new JLabel("" + Stats.type(i).name()));
			nStats.add(tfNaturalStats[i]);
			aStats.add(jlAcquiredStats[i]);
		}
		
		jlAcquiredResist = new JLabel[Resistance.values().length];
		JPanel resistNames;
		resistNames = new JPanel();
		resistNames.setLayout(new GridLayout(jlAcquiredResist.length + 1, 1));
		
		resistNames.add(new JLabel("Acq. Resist."));
		
		for(byte i = 0; i < jlAcquiredResist.length; i++)
		{
			jlAcquiredResist[i] = new JLabel(Resistance.type(i) + ": 00");
			resistNames.add(jlAcquiredResist[i]);
		}
		
		tfSkills = new JTextField[PlayerSkill.values().length];
		tfItemQuests = new JTextField(4);
		tfMonQuests = new JTextField(4);
		JPanel extrasNames, extrasVals;
		
		
		extrasNames = new JPanel();
		extrasVals = new JPanel();
		
		GridLayout extrasNamesLayout = new GridLayout(PlayerSkill.values().length + 2 + 2, 1);
		extrasNamesLayout.setVgap(6);
		extrasNames.setLayout(extrasNamesLayout);
		extrasVals.setLayout(new GridLayout(PlayerSkill.values().length + 2 + 2, 1));
		
		extrasNames.add(new JLabel("Skills"));
		extrasVals.add(new JLabel(""));
		for(byte i = 0; i < tfSkills.length; i++)
		{
			tfSkills[i] = new JTextField(3);
			extrasNames.add(new JLabel(PlayerSkill.type(i).name()));
			extrasVals.add(tfSkills[i]);
		}
		
		JPanel statesPane = new JPanel();
		statesPane.setLayout(new GridLayout(PlayerState.values().length >> 2, 4));
		jcbStates = new JCheckBox[PlayerState.values().length];
		
		//for(byte i = 0; i < PlayerState.values().length; i++)
		for(PlayerState ps : PlayerState.values())
		{
			jcbStates[ps.value()] = new JCheckBox(ps.name());
			if(ps != PlayerState.None)
				statesPane.add(jcbStates[ps.value()]);
		}
		
		// Make uneditable ones uneditable.
		for(byte i = 0; i < PlayerState.editables(false).length; i++)
			jcbStates[PlayerState.editables(false)[i].value()].setEnabled(false);
		
		extrasNames.add(new JLabel("Quests"));
		extrasVals.add(new JLabel(""));
		
		extrasNames.add(new JLabel("Item"));
		extrasNames.add(new JLabel("Monster"));
		
		extrasVals.add(tfItemQuests);
		extrasVals.add(tfMonQuests);
			
		topBar.setLayout(new BorderLayout());
		topBar.add(cbBar, BorderLayout.NORTH);
		topBar.add(locBar, BorderLayout.CENTER);
		topBar.add(healthBar, BorderLayout.SOUTH);

		fieldsBar.add(nStatsNames);
		fieldsBar.add(nStats);
		fieldsBar.add(aStats);
		fieldsBar.add(resistNames);
		fieldsBar.add(extrasNames);
		fieldsBar.add(extrasVals);
		fieldsBar.add(statesPane);
		
		setLayout(new BorderLayout());
		add(topBar, BorderLayout.NORTH);
		add(fieldsBar, BorderLayout.CENTER);
	}
	
	/**
	 *	Sets the selected race to the race in the active player.
	 *
	 */
	private void updateRaceInPanel()
	{
		for(int i = 0; i < jcRace.getModel().getSize(); i++)
		{
			if(parent.currentPlayer.getRace().getName().equalsIgnoreCase((String)jcRace.getItemAt(i)))
			{
				jcRace.setSelectedIndex(i);
				return;
			}
		}
	}
	
	/**
	 * 	Sets the selected alignment to the alignment stored in
	 * 	active player.
	 */
	private void updateAlignInPanel()
	{
		jcAlignment.setSelectedItem(parent.currentPlayer.getAlignment());
	}
	
	/**
	 * Updates the vales in the panel to those stored in the
	 * current player.
	 * @return	true
	 */
	public boolean updatePanel()
	{
		updateRaceInPanel();
		updateAlignInPanel();
		
		jlAttack.setText("Attack : " + parent.currentPlayer.getAttack());
		jlDefense.setText("Defense: " + parent.currentPlayer.getDefense());
		jlTotalGold.setText("Total Gold: " + parent.currentPlayer.getTotalGold());
		
		tfX.setText("" + parent.currentPlayer.getCoord().getX());
		tfY.setText("" + parent.currentPlayer.getCoord().getY());
		tfZ.setText("" + parent.currentPlayer.getCoord().getZ());
		//jcDirection.setSelectedIndex(parent.currentPlayer.getCoord().getDirection());
		jcDirection.setSelectedItem(parent.currentPlayer.getCoord().getDirection());
		
		tfHP.setText("" + parent.currentPlayer.getHP());
		tfMP.setText("" + parent.currentPlayer.getMP());
		tfMaxHP.setText("" + parent.currentPlayer.getMaxHP());
		tfAge.setText("" + parent.currentPlayer.getAgeYears());
		tfGoldOnHand.setText("" + parent.currentPlayer.getGoldOnHand());
		jlMaxMP.setText("Max MP: " + parent.currentPlayer.getMaxMP());
		tfKills.setText("" + parent.currentPlayer.getKills());
		tfDeaths.setText("" + parent.currentPlayer.getDeaths());
		
		for(Stats st : Stats.values())
		{
			tfNaturalStats[st.value()].setText("" + parent.currentPlayer.getNaturalStat(st));
			jlAcquiredStats[st.value()].setText("" + parent.currentPlayer.getAcquiredStat(st));
		}
		
		for(byte i = 0; i < jlAcquiredResist.length; i++)
			jlAcquiredResist[i].setText(Resistance.type(i).name() + ": " + parent.currentPlayer.getAcquiredResistance(Resistance.type(i)) + "%");
		
		for(PlayerSkill ps : PlayerSkill.values())
			tfSkills[ps.value()].setText("" + parent.currentPlayer.getPlayerSkill(ps));
		
		tfItemQuests.setText("" + parent.currentPlayer.finishedItemQuests());
		tfMonQuests.setText("" + parent.currentPlayer.finishedMonQuests());
		
		for(PlayerState ps : PlayerState.values())
			jcbStates[ps.value()].setSelected(parent.currentPlayer.isInState(ps));
		
		// Sanctuary coordinates
		byte[] sancCoords = parent.currentPlayer.getSanctuaryCoords();
		if(sancCoords[Player.SANCTUARY_X] < 0 || sancCoords[Player.SANCTUARY_Y] < 0 || sancCoords[Player.SANCTUARY_Z] < 0)
		{
			tfSx.setText("");
			tfSy.setText("");
			tfSz.setText("");
		}
		else
		{
			tfSx.setText("" + sancCoords[Player.SANCTUARY_X]);
			tfSy.setText("" + sancCoords[Player.SANCTUARY_Y]);
			tfSz.setText("" + sancCoords[Player.SANCTUARY_Z]);
		}
		
		return true;
	}
	
	/**
	 * Makes changes to the player based on the data currently in
	 * the panel. Provides the option to perform a validity check
	 * if one has not already been done.
	 * @param dataValidated	If the data has been previously updated.
	 * @return	boolean	false if the update could not be performed.
	 */
	public boolean updatePlayer(boolean dataValidated)
	{
		Race tRace = parent.dataBank.getRace((String)jcRace.getSelectedItem());
		if(!dataValidated)
		{
			if(!validData())
				return false;
			
			if(!validAlignment())
				setValidAlignment();
		}
		
		Player lister = parent.currentPlayer;
		Coord listerLoc = lister.getCoord();
		
		lister.setRace(tRace);
		lister.setAlignment((Alignment)jcAlignment.getSelectedItem());
		listerLoc.setX(Util.FITBYTE(Byte.parseByte(tfX.getText().trim()), 0, MapLevel.MAXWIDTH));
		listerLoc.setY(Util.FITBYTE(Byte.parseByte(tfY.getText().trim()), 0, MapLevel.MAXHEIGHT));
		listerLoc.setZ(Util.FITBYTE(Byte.parseByte(tfZ.getText().trim()), 0, Map.MAXDEPTH));
		//listerLoc.setDirection((byte)jcDirection.getSelectedIndex());
		listerLoc.setDirection((Direction)jcDirection.getSelectedItem());
		parent.dataBank.getMap().getMapSquare(listerLoc).enterSquare(lister, parent.dataBank.getMap());
		
		lister.setHP(Util.FITINT(Integer.parseInt(tfHP.getText().trim()), 0, Integer.MAX_VALUE));
		lister.setMP(Util.FITINT(Integer.parseInt(tfMP.getText().trim()), 0, Integer.MAX_VALUE));
		lister.setMaxHP(Util.FITINT(Integer.parseInt(tfMaxHP.getText().trim()), 0, Integer.MAX_VALUE));
		lister.setAge(Util.FITINT((Integer.parseInt(tfAge.getText().trim()) * 364), 0, Integer.MAX_VALUE));
		lister.setGoldOnHand(Util.FITLONG(Long.parseLong(tfGoldOnHand.getText().trim()), 0, Long.MAX_VALUE));
		lister.setKills(Util.FITINT(Integer.parseInt(tfKills.getText().trim()), 0, Integer.MAX_VALUE));
		lister.setDeaths(Util.FITINT(Integer.parseInt(tfDeaths.getText().trim()), 0, Integer.MAX_VALUE));
		
		for(Stats st : Stats.values())
			lister.setNaturalStat(st, Util.FITBYTE(Byte.parseByte(tfNaturalStats[st.value()].getText().trim()), 0, tRace.getBaseStat(st, false) + Stats.MAXIMUMEXTENDED));
		
		for(PlayerSkill ps : PlayerSkill.values())
			lister.setPlayerSkill(ps, Util.FITSHORT(Short.parseShort(tfSkills[ps.value()].getText()), 0, Short.MAX_VALUE));
		
		lister.setFinishedItemQuests(Util.FITINT(Integer.parseInt(tfItemQuests.getText()), 0, Integer.MAX_VALUE));
		lister.setFinishedMonQuests(Util.FITINT(Integer.parseInt(tfMonQuests.getText()), 0, Integer.MAX_VALUE));
		
		for(PlayerState ps : PlayerState.values())
			lister.setState(ps, jcbStates[ps.value()].isSelected(), Player.STATESPELL);
		
		// Sanctuary coordinates
		if(tfSx.getText().trim().length() < 1 || tfSy.getText().trim().length() < 1 || tfSz.getText().trim().length() < 1)
		{
			lister.setSanctuaryCoords();
		}
		else
		{
			byte[] sancCoords = lister.getSanctuaryCoords();
			sancCoords[Player.SANCTUARY_X] = Byte.parseByte(tfSx.getText().trim());
			sancCoords[Player.SANCTUARY_Y] = Byte.parseByte(tfSy.getText().trim());
			sancCoords[Player.SANCTUARY_Z] = Byte.parseByte(tfSz.getText().trim());
			lister.setSanctuaryCoords(sancCoords);
		}
		
		updatePanel();
		
		return true;
	}
	
	/**
	 * Determines if all selected and entered data is valid.
	 * @return	boolean	true if all data is valid.
	 */
	public boolean validData()
	{
		int x, y, z;
		// check if alignment allowed
		if(!validAlignment())
		{
			JOptionPane.showMessageDialog(this, "Invalid Alignment");
			return false;
		}
		
		Race tRace = parent.dataBank.getRace((String)jcRace.getSelectedItem());

		if(!Util.validityCheckTf(this, tfZ, 0, parent.dataBank.getMap().getDepth() - 1, "invalid Z"))
			return false;
		
		z = Byte.parseByte(tfZ.getText().trim());
		
		if(!Util.validityCheckTf(this, tfX, 0, parent.dataBank.getMap().getMapLevel(z).getWidth() - 1, "invalid X"))
			return false;
		if(!Util.validityCheckTf(this, tfY, 0, parent.dataBank.getMap().getMapLevel(z).getHeight() - 1, "invalid Y"))
			return false;
		
		if(!Util.validityCheckTf(this, tfHP, 0, Integer.MAX_VALUE, "invalid HP"))
			return false;
		if(!Util.validityCheckTf(this, tfMP, 0, Integer.MAX_VALUE, "invalid MP"))
			return false;
		if(!Util.validityCheckTf(this, tfMaxHP, 0, Integer.MAX_VALUE, "invalid max HP"))
			return false;
		if(!Util.validityCheckTf(this, tfAge, 0, Integer.MAX_VALUE, "invalid age"))
			return false;
		if(!Util.validityCheckTf(this, tfGoldOnHand, 0, Long.MAX_VALUE, "invalid gold on hand"))
			return false;
		if(!Util.validityCheckTf(this, tfKills, 0, Integer.MAX_VALUE, "invalid kills"))
			return false;
		if(!Util.validityCheckTf(this, tfDeaths, 0, Integer.MAX_VALUE, "invalid deaths"))
			return false;

		for(Stats st : Stats.values())
			if(!Util.validityCheckTf(this, tfNaturalStats[st.value()], tRace.getBaseStat(st, true), tRace.getBaseStat(st, false) + Stats.MAXIMUMEXTENDED, "invalid natural stat: " + st.name()))
				return false;
		for(byte i = 0; i < tfSkills.length; i++)
			if(!Util.validityCheckTf(this, tfSkills[i], 0, Short.MAX_VALUE, "invalid player skill: " + PlayerSkill.type(i).name()))
				return false;
		
		if(!Util.validityCheckTf(this, tfItemQuests, 0, Integer.MAX_VALUE, "completed item quests"))
			return false;
		if(!Util.validityCheckTf(this, tfMonQuests, 0, Integer.MAX_VALUE, "completed monster quests"))
			return false;
		
		if(tfSx.getText().trim().length() < 1 || tfSy.getText().trim().length() < 1 || tfSz.getText().trim().length() < 1)
		{
			tfSx.setText("");
			tfSy.setText("");
			tfSz.setText("");
		}
		else
		{
			if(!Util.validityCheckTf(this, tfSz, 0, parent.dataBank.getMap().getDepth(), "sanctuary z value"))
				return false;
			
			z = Byte.parseByte(tfSz.getText().trim());
			
			if(!Util.validityCheckTf(this, tfSx, 0, parent.dataBank.getMap().getMapLevel(z).getWidth(), "sanctuary x"))
				return false;
			if(!Util.validityCheckTf(this, tfSy, 0, parent.dataBank.getMap().getMapLevel(z).getHeight(), "sanctuary y"))
				return false;
			
			x = Byte.parseByte(tfSx.getText().trim());
			y = Byte.parseByte(tfSy.getText().trim());
			
			if(parent.dataBank.getMap().getMapSquare(x, y, z).isSolidRock())
			{
				JOptionPane.showMessageDialog(this, "Invalid sanctuary square.");
				return false;
			}
			
		}
		
		return true;
	}
	
	/**
	 * Determines if the currently selected alignment is valid.
	 * @return boolean	true if the alignment is valid.
	 */
	public boolean validAlignment()
	{
		return parent.dataBank.getRace((String)jcRace.getSelectedItem()).canBeAligned((Alignment)jcAlignment.getSelectedItem());
	}
	
	/**
	 * Selects the first valid type of alignment based on the
	 * currently selected race.
	 */
	private void setValidAlignment()
	{
		Race tRace = parent.dataBank.getRace((String)jcRace.getSelectedItem());
		for(Alignment al : Alignment.values())
			if(tRace.canBeAligned(al))
			{
				jcAlignment.setSelectedItem(al);
				return;
			}
	}
	
	public void updateLists()
	{
		jcRace.setModel(new DefaultComboBoxModel(parent.dataBank.getRaceNames()));
	}

	public void actionPerformed(ActionEvent e)
	{
		
	}

}
