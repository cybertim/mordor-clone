package mordorEditor;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import mordorData.DataBank;
import mordorData.Race;
import mordorEnums.Alignment;
import mordorEnums.Resistance;
import mordorEnums.Size;
import mordorEnums.Stats;
import mordorHelpers.Util;

public class EditorRacePane extends JPanel implements ActionListener 
{
	private JTextField name, maxAge;
	private JTextArea description;
	//private JCheckBox cbGood, cbNeutral, cbEvil;
	private JCheckBox[] jcbAlignment;
	private JTextField[] statsMin;
	private JTextField[] statsMax;
	private JTextField[] resist;
	private JComboBox cbXPRate, cbSizes;
	public JButton btUpdate, btReset;
	
	private Race race;
	private DataBank dataBank;
	private EditorRacePanel parent;
	
	private static final Dimension BOXSIZE = new Dimension(12, 20);
	
	EditorRacePane(EditorRacePanel nParent, Race nRace, DataBank nDataBank)
	{
		race = nRace;
		dataBank = nDataBank;
		parent = nParent;
		
		JPanel northPanel = new JPanel();
		JPanel southPanel = new JPanel();
		
		JPanel topBar = new JPanel();
		
		JPanel statsBar = new JPanel();
		JPanel resistBar = new JPanel();
		JPanel combosBar = new JPanel();
		
		name = new JTextField(10);
		name.addActionListener(this);
		
		jcbAlignment = new JCheckBox[Alignment.values().length];
		for(Alignment al : Alignment.values())
			jcbAlignment[al.value()] = new JCheckBox(al.name());
		
		description = new JTextArea(6, 30);
		description.setWrapStyleWord(true);
		description.setLineWrap(true);
		JScrollPane descPane = new JScrollPane(description);
		
		statsMin = new JTextField[Stats.values().length];
		statsMax = new JTextField[Stats.values().length];
		for(byte i = 0; i < statsMin.length; i++)
		{
			statsMin[i] = new JTextField(2);
			statsMax[i] = new JTextField(2);
			
			statsMin[i].setPreferredSize(BOXSIZE);
			statsMax[i].setPreferredSize(BOXSIZE);
		}
		
		resist = new JTextField[Resistance.values().length];
		for(byte i = 0; i < resist.length; i++)
			resist[i] = new JTextField(2);
		
		btUpdate = new JButton("Update");
		btUpdate.addActionListener(this);
		btUpdate.setToolTipText("Updates the race with these values. Does NOT save.");
		btReset = new JButton("Reset");
		btReset.addActionListener(this);
		btReset.setToolTipText("Resests the displayed values");
		
		topBar.setLayout(new FlowLayout());
		topBar.add(new JLabel("Name:"));
		topBar.add(name);
		for(Alignment al : Alignment.values())
			topBar.add(jcbAlignment[al.value()]);
		
		statsBar.setLayout(new GridLayout(Stats.values().length + 1, 3));
		statsBar.add(new JLabel("Type"));
		statsBar.add(new JLabel("Min"));
		statsBar.add(new JLabel("Max"));
		for(byte i = 0; i < statsMin.length; i++)
		{
			statsBar.add(new JLabel(Stats.type(i).name()));
			statsBar.add(statsMin[i]);
			statsBar.add(statsMax[i]);
		}
		
		maxAge = new JTextField(3);
	//	maxAge.addActionListener(this);
		
		cbXPRate = new JComboBox();
		for(byte i = 3; i < Race.EXPERIENCERATES.length; i++)
			cbXPRate.addItem(i);
		cbXPRate.addActionListener(this);
		
		cbSizes = new JComboBox(Size.values());
		cbSizes.addActionListener(this);
		
		resistBar.setLayout(new GridLayout(7, 4));
		resistBar.add(new JLabel("Resistance"));
		resistBar.add(new JLabel("%"));
		resistBar.add(new JLabel("Resistance"));
		resistBar.add(new JLabel("%"));
		for(byte i = 0; i < 6; i++)
		{
			if(i < resist.length)
			{
				resistBar.add(new JLabel(Resistance.type(i).name()));
				resistBar.add(resist[i]);
			}
			if(i + 6 < resist.length)
			{
				resistBar.add(new JLabel(Resistance.type(i + 6).name()));
				resistBar.add(resist[i + 6]);
			}
		}
		
		combosBar.setLayout(new GridLayout(4, 2));
		combosBar.add(new JLabel("Max age:"));
		combosBar.add(maxAge);
		combosBar.add(new JLabel("Experience Rate"));
		combosBar.add(cbXPRate);
		combosBar.add(new JLabel("Size"));
		combosBar.add(cbSizes);
		combosBar.add(btUpdate);
		combosBar.add(btReset);
		
		northPanel.setLayout(new BorderLayout());
		northPanel.add(topBar, BorderLayout.NORTH);
		northPanel.add(descPane, BorderLayout.CENTER);
		
		southPanel.setLayout(new FlowLayout());
		southPanel.add(statsBar);
		southPanel.add(resistBar);
		southPanel.add(combosBar, BorderLayout.EAST);
		
		setLayout(new BorderLayout());
		add(northPanel, BorderLayout.NORTH);
		add(southPanel, BorderLayout.SOUTH);
		
		loadRace();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == btUpdate)
			updateRace();
		else if(e.getSource() == btReset)
			loadRace();
	}
	
	/**
	 * Determines if a valid name has been entered.
	 * @return boolean	True if the name is valid.
	 */
	private boolean checkName()
	{
		if(name.getText().trim().length() < 1 && name.getText().trim().equalsIgnoreCase(Util.NOSTRING) && dataBank.getRace(name.getText().trim()) != null)
			return false;
		
		return true;
	}
	
	/**
	 * Returns if at least one alignment is checked.
	 * @return boolean	True if at least one is selected.
	 */
	private boolean checkAlignment()
	{
		for(int i = 0; i < jcbAlignment.length; i++)
			if(jcbAlignment[i].isSelected())
				return true;
		
		return false;
	}
	
	/**
	 * Performs checks on all items.
	 * @return boolean	True if all entered data is valid.
	 */
	private boolean checkAll()
	{
		if(!checkName())
			return false;
		if(!checkAlignment())
			return false;
		if(!MordorEditor.validityCheckTf(this, maxAge, Race.MINAGE, Race.MAXAGE, "maximum age"))
			return false;
		for(byte i = 0; i < statsMin.length; i++)
			if(!MordorEditor.validityCheckTf(this, statsMin[i], Stats.MINIMUMVALUE, Stats.MAXIMUMVALUE, Stats.type(i).name() + " minimum"))
				return false;
		for(byte i = 0; i < statsMax.length; i++)
			if(!MordorEditor.validityCheckTf(this, statsMax[i], Byte.parseByte(statsMin[i].getText().trim()), Stats.MAXIMUMVALUE, Stats.type(i).name() + " maximum"))
				return false;
		for(byte i = 0; i < resist.length; i++)
			if(!MordorEditor.validityCheckTf(this, resist[i], 0, 100, Resistance.type(i).name()))
				return false;
		
		return true;
	}
	
	/**
	 * Updates the race for this pane with the entered data.
	 * @return boolean	True if the update succeeded.
	 */
	public boolean updateRace()
	{
		if(!checkAll())
			return false;
		
		
		race.setName(name.getText().trim());
		race.setDescription(description.getText().trim());
		for(Alignment al : Alignment.values())
			race.setAlignment(al, jcbAlignment[al.value()].isSelected());
		race.setMaxAge(Short.parseShort(maxAge.getText().trim()));
		race.setSize((Size)cbSizes.getSelectedItem());
		race.setXPRate((byte)(cbXPRate.getSelectedIndex() + 3));
		for(Stats st : Stats.values())
		{
			race.setBaseStat(st, true, Byte.parseByte(statsMin[st.value()].getText().trim()));
			race.setBaseStat(st, false, Byte.parseByte(statsMax[st.value()].getText().trim()));
		}
		for(byte i = 0; i < resist.length; i++)
			race.setResistance(Resistance.type(i), Byte.parseByte(resist[i].getText().trim()));
		parent.updateNames();
		return true;
	}
	
	/**
	 * Resets the displayed values to those that are currently stored in the race.
	 *
	 */
	public void loadRace()
	{
		name.setText(race.getName());
		description.setText(race.getDescription());
		maxAge.setText("" + race.getMaxAge());
		for(Alignment al : Alignment.values())
			jcbAlignment[al.value()].setSelected(race.canBeAligned(al));
		cbXPRate.setSelectedIndex(race.getXPrate() - 3);
		cbSizes.setSelectedItem(race.getSize());
		for(Stats st : Stats.values())
		{
			statsMin[st.value()].setText("" + race.getBaseStat(st, true));
			statsMax[st.value()].setText("" + race.getBaseStat(st, false));
		}
		for(byte i = 0; i < resist.length; i++)
			resist[i].setText(""+ race.getResistance(Resistance.type(i)));
	}
	
	/**
	 * Retrieves the race in this pane.
	 * @return Race
	 */
	public Race getRace()
	{
		return race;
	}
}
