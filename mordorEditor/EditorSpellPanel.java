package mordorEditor;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import mordorData.DataBank;
import mordorData.MonsterEden;
import mordorData.Spell;
import mordorData.SpellReference;
import mordorEnums.CharmType;
import mordorEnums.MonsterClass;
import mordorEnums.PlayerState;
import mordorEnums.Resistance;
import mordorEnums.SpellClass;
import mordorEnums.SpellType;
import mordorEnums.Stats;
import mordorHelpers.Util;

public class EditorSpellPanel extends JPanel implements ActionListener
{
	private DataBank dataBank;
	private Spell currentSpell;
	
	private JComboBox jcSpellClass, jcSpell, jcSpellType, jcSpellSubtype;
	private JTextField tfEffectName, tfNumGroups, tfBaseLevel, tfStrength;
	private JTextField[] tfRequiredStats;
	private JTextArea taDescription;
	private JButton jbAdd, jbRemove, jbUpdate, jbName;
	private JRadioButton jrAdd, jrRemove;
	private ButtonGroup stateGroup;
	private JLabel jlStrength;
	
	EditorSpellPanel(DataBank nDataBank)
	{
		dataBank = nDataBank;
		
		jcSpellClass = new JComboBox(SpellClass.values());
		jcSpell = new JComboBox(Util.NOSTRINGARRAY);
		jcSpellType = new JComboBox(SpellType.values());
		jcSpellSubtype = new JComboBox(Util.NOSTRINGARRAY);
		
		jcSpellClass.setToolTipText("Choose the class of spells to work with.");
		jcSpell.setToolTipText("Choose the spell to work with.");
		jcSpellType.setToolTipText("Choose the type of effect this spell has.");
		jcSpellSubtype.setToolTipText("Choose the subtype of effect this spell has.");
		
		jcSpellClass.addActionListener(this);
		jcSpell.addActionListener(this);
		jcSpellType.addActionListener(this);
		
		tfEffectName = new JTextField(10);
		tfNumGroups = new JTextField(3);
		tfBaseLevel = new JTextField(4);
		tfStrength = new JTextField(3);
		jlStrength = new JLabel("Strength");
		
		tfRequiredStats = new JTextField[Stats.values().length];
		for(byte i = 0; i < tfRequiredStats.length; i++)
			tfRequiredStats[i] = new JTextField(3);
		
		taDescription = new JTextArea(3, 20);
		taDescription.setWrapStyleWord(true);
		taDescription.setLineWrap(true);
		JScrollPane taDescriptionScroll = new JScrollPane(taDescription);
		
		jbAdd = new JButton("Add");
		jbRemove = new JButton("Remove");
		jbUpdate = new JButton("Update");
		jbName = new JButton(Util.NOSTRING);
		
		jbAdd.setToolTipText("Add a new spell to this spell class.");
		jbRemove.setToolTipText("Remove the current spell from the spell book.");
		jbUpdate.setToolTipText("Update the current spell with the data in the panel.");
		jbName.setToolTipText("Change the name of the spell.");
		
		jbAdd.addActionListener(this);
		jbRemove.addActionListener(this);
		jbUpdate.addActionListener(this);
		jbName.addActionListener(this);
		
		jrAdd = new JRadioButton("Add");
		jrRemove = new JRadioButton("Remove");
		
		jrAdd.setToolTipText("Add this state to the target.");
		jrRemove.setToolTipText("Remove this state from the target.");
		
		stateGroup = new ButtonGroup();
		stateGroup.add(jrAdd);
		stateGroup.add(jrRemove);
		
		JPanel topBar = new JPanel();
		topBar.add(jbAdd);
		topBar.add(jbUpdate);
		topBar.add(jbRemove);
		topBar.add(jcSpellClass);
		topBar.add(jcSpell);
		
		JPanel nameBar = new JPanel();
		nameBar.add(jbName);
		nameBar.add(new JLabel("Effect Name:"));
		nameBar.add(tfEffectName);
		nameBar.add(new JLabel("Base level:"));
		nameBar.add(tfBaseLevel);
		
		JPanel sdBar = new JPanel();
		JPanel statsPane = new JPanel();
		JPanel descPane = new JPanel();
		BorderLayout sdLayout = new BorderLayout();
		sdLayout.setHgap(20);
		sdBar.setLayout(sdLayout);
		int rows = 4;
		int cols = ((tfRequiredStats.length % rows) == 0) ? tfRequiredStats.length / rows : (tfRequiredStats.length / rows) + 1;
		statsPane.setLayout(new GridLayout(rows, cols));
		descPane.setLayout(new BorderLayout());
		
		for(byte i = 0; i < tfRequiredStats.length; i++)
		{
			JPanel tPane = new JPanel();
			tPane.setLayout(new BorderLayout());
			tPane.add(new JLabel(" " + Stats.type(i).name() + " "), BorderLayout.WEST);
			tPane.add(tfRequiredStats[i], BorderLayout.EAST);
			
			statsPane.add(tPane);
		}
		
		descPane.add(new JLabel("Spell Description:"), BorderLayout.NORTH);
		descPane.add(taDescriptionScroll);
		
		sdBar.add(statsPane, BorderLayout.WEST);
		sdBar.add(descPane, BorderLayout.EAST);
		
		JPanel detailsBar = new JPanel();
		detailsBar.add(jcSpellType);
		detailsBar.add(jcSpellSubtype);
		detailsBar.add(jlStrength);
		detailsBar.add(tfStrength);
		detailsBar.add(new JLabel("Groups"));
		detailsBar.add(tfNumGroups);
		detailsBar.add(jrAdd);
		detailsBar.add(jrRemove);
		
		JPanel uPane = new JPanel();
		uPane.setLayout(new GridLayout(3, 1));
		uPane.add(topBar);
		uPane.add(nameBar);
		uPane.add(detailsBar);
		
		//setLayout(new GridLayout(2, 1));
		add(uPane);
		add(sdBar);
		
		initPanel();
		
		// Top bar: buttons, class list, spell list
		// Name bar: Spell name, effect name, base level
		// S&D bar: Stats in grid on left, description on right
		// Details bar: Spell type, Subtype, num groups, strength
		
	}
	
	public void initPanel()
	{
		setSpellList();
		setCurrentSpellFromList();
	}
	
	/**
	 * Update the data in the panel with the current spells info.
	 */
	public void updatePanel()
	{
		// No current spell has been set, no point in doing anything.
		if(currentSpell == null)
			return;
		
		// Setup the strings
		jbName.setText(currentSpell.getName());
		tfEffectName.setText(currentSpell.getEffectString());
		taDescription.setText(currentSpell.getDescription());
		
		// Setup the spell type info
		jcSpellType.setSelectedItem(currentSpell.getType());
		setSpellSubtypeList();
		tfNumGroups.setText("" + currentSpell.getNumberGroupsAffect());
		tfStrength.setText("" + currentSpell.getSpellStrength());
		if((SpellType)jcSpellType.getSelectedItem() == SpellType.State)
		{
			if(currentSpell.getSpellStrength() == Spell.STATE_ADD)
				jrAdd.setSelected(true);
			else
				jrRemove.setSelected(true);
			
			jcSpellSubtype.setSelectedItem(PlayerState.type(currentSpell.getSubType()));
		}
		else if(jcSpellSubtype.isEnabled())
		{
			if(currentSpell.getSubType() >= jcSpellSubtype.getItemCount())
				jcSpellSubtype.setSelectedIndex(0);
			else
				jcSpellSubtype.setSelectedIndex(currentSpell.getSubType());
		}
		
		// Setup the base Level
		tfBaseLevel.setText("" + currentSpell.getLevel());
		
		// Setup the required stats.
		for(Stats st : Stats.values())
			tfRequiredStats[st.value()].setText("" + currentSpell.getStat(st));
	}
	
	public boolean updateSpell()
	{
		if(!validateSpell())
			return false;
		
		currentSpell.setEffectString(tfEffectName.getText());
		currentSpell.setDescription(taDescription.getText());
		currentSpell.setLevel(Short.parseShort(tfBaseLevel.getText().trim()));
		for(Stats st : Stats.values())
			currentSpell.setStat(st, Byte.parseByte(tfRequiredStats[st.value()].getText().trim()));
		currentSpell.setType((SpellType)jcSpellType.getSelectedItem());
		if((SpellType)jcSpellType.getSelectedItem() == SpellType.State)
			currentSpell.setSubType(((PlayerState)jcSpellSubtype.getSelectedItem()).value());
		else
			currentSpell.setSubType((byte)jcSpellSubtype.getSelectedIndex());
		if(tfNumGroups.isEnabled())
			currentSpell.setNumberGroups(Byte.parseByte(tfNumGroups.getText().trim()));
		else
			currentSpell.setNumberGroups(Util.NOTHING);
		if(tfStrength.isEnabled())
			currentSpell.setSpellStrength(Byte.parseByte(tfStrength.getText().trim()));
		else
			currentSpell.setSpellStrength(Util.NOTHING);

		if((SpellType)jcSpellType.getSelectedItem() == SpellType.State)
		{
			if(jrAdd.isSelected())
				currentSpell.setSpellStrength(Spell.STATE_ADD);
			else
				currentSpell.setSpellStrength(Spell.STATE_REMOVE);
		}
		
		// Since the spell reference should hold the base level for spell types, we need to set
		// it here. This is a feature of how spell books work to make all of them consistent.
		dataBank.getSpellBook().getSpell(currentSpell.getID()).setLevel(currentSpell.getLevel());
		
		return true;
	}
	
	public boolean validateSpell()
	{
		if(tfEffectName.getText().trim().length() < 1)
		{
			JOptionPane.showMessageDialog(this, "Must enter effect name.");
			return false;
		}
		if(!MordorEditor.validityCheckTf(this, tfBaseLevel, Spell.VAL_MINLEVEL, Spell.VAL_MAXLEVEL, "base level"))
			return false;
		if(jcSpellType.getSelectedItem() == SpellType.Resistance)
		{
			if(!MordorEditor.validityCheckTf(this, tfStrength, -100, 100, "strength"))
				return false;
		}
		else
		{
			if(tfStrength.isEnabled() && !MordorEditor.validityCheckTf(this, tfStrength, Util.MON_MINGROUPSIZE, Util.MON_MAXGROUPSIZE, "group size"))
				return false;
		}
		
		// TODO, this should be split depending on if it affects player's team
		// or monsters
		if(tfNumGroups.isEnabled() && !MordorEditor.validityCheckTf(this, tfNumGroups, Util.MON_MINSTACKSIZE, Util.MON_MAXSTACKSIZE, "number of groups"))
			return false;
		
		for(byte i = 0; i < tfRequiredStats.length; i++)
			if(!MordorEditor.validityCheckTf(this, tfRequiredStats[i], Stats.MINIMUMVALUE, Stats.MAXIMUMVALUE, Stats.type(i).name()))
				return false;
		
		return true;
	}
	
	/**
	 * Choose the current spell in the spell list jcSpell
	 * @return true if currentSpell was changed.
	 */
	public boolean setCurrentSpellFromList()
	{
		SpellReference tSpell = dataBank.getSpellBook().getSpell((String)jcSpell.getSelectedItem());
		
		if(tSpell == null)
		{
			dataBank.getSpellBook().newSpell((SpellClass)jcSpellClass.getSelectedItem());
			setSpellList();
			tSpell = dataBank.getSpellBook().getSpell((String)jcSpell.getSelectedItem());

			// Something is very wrong.
			if(tSpell == null || ((String)jcSpell.getSelectedItem()).equalsIgnoreCase(Util.NOSTRING))
				return false;
		}
		
		currentSpell = tSpell.getSpell();
		updatePanel();
		return true;
	}
	
	/**
	 * Sets the spell names listed in jcSpell
	 */
	private void setSpellList()
	{
		// Get the names of spells in the currently selected class.
		String[] spellNames = dataBank.getSpellBook().getSpellClassNames((SpellClass)jcSpellClass.getSelectedItem());
		
		// There are no spells in this class.
		if(spellNames == null || spellNames.length < 1)
		{
			jcSpell.setModel(new DefaultComboBoxModel(Util.NOSTRINGARRAY));
			return;
		}
		
		// Set the spell list.
		jcSpell.setModel(new DefaultComboBoxModel(spellNames));
	}
	
	/**
	 * Sets the values the exist in the subtype list based on the currently
	 * selected spell type.
	 */
	private void setSpellSubtypeList()
	{
		SpellType sType = (SpellType)jcSpellType.getSelectedItem();
		
		switch(sType)
		{
		case Damage:
			jcSpellSubtype.setEnabled(true);
			jcSpellSubtype.setModel(new DefaultComboBoxModel(SpellType.SubtypeDamage.values()));
			tfNumGroups.setEnabled(true);
			tfStrength.setEnabled(true);
			tfStrength.setToolTipText("Number of targets in group affected.");
			jlStrength.setText("Group Size");
			break;
		case Kill:
			jcSpellSubtype.setEnabled(true);
			jcSpellSubtype.setModel(new DefaultComboBoxModel(SpellType.SubtypeKill.values()));
			tfNumGroups.setEnabled(true);
			tfStrength.setEnabled(true);
			tfStrength.setToolTipText("Number of targets in group affected.");
			jlStrength.setText("Group Size");
			break;
		case State:
			jcSpellSubtype.setEnabled(true);
			jcSpellSubtype.setModel(new DefaultComboBoxModel(PlayerState.effects()));
			tfNumGroups.setEnabled(true);
			tfStrength.setEnabled(false);
			tfStrength.setToolTipText("");
			jlStrength.setText("Group Size");
			break;
		case Charm:
			jcSpellSubtype.setEnabled(true);
			jcSpellSubtype.setModel(new DefaultComboBoxModel(CharmType.values()));
			tfNumGroups.setEnabled(true);
			tfStrength.setEnabled(true);
			tfStrength.setToolTipText("Number of targets to charm.");
			break;
		case Bind:
			jcSpellSubtype.setEnabled(false);
			jcSpellSubtype.setModel(new DefaultComboBoxModel(Util.NOSTRINGARRAY));
			tfNumGroups.setEnabled(false);
			tfStrength.setEnabled(false);
			break;
		case Health:
			jcSpellSubtype.setEnabled(true);
			jcSpellSubtype.setModel(new DefaultComboBoxModel(SpellType.SubtypeHealth.values()));
			tfNumGroups.setEnabled(true);
			tfStrength.setEnabled(false);
			break;
		case Movement:
			jcSpellSubtype.setEnabled(true);
			jcSpellSubtype.setModel(new DefaultComboBoxModel(SpellType.SubtypeMovement.values()));
			tfNumGroups.setEnabled(false);
			tfStrength.setEnabled(false);
			break;
		case Map:
			jcSpellSubtype.setEnabled(true);
			jcSpellSubtype.setModel(new DefaultComboBoxModel(SpellType.SubtypeMap.values()));
			tfNumGroups.setEnabled(false);
			tfStrength.setEnabled(false);
			break;
		case Resistance:
			jcSpellSubtype.setEnabled(true);
			jcSpellSubtype.setModel(new DefaultComboBoxModel(Resistance.values()));
			tfNumGroups.setEnabled(false);
			tfStrength.setEnabled(true);
			tfStrength.setToolTipText("Change in resistance (neg or pos.)");
			jlStrength.setText("Strength");
			break;
		case Special:
			jcSpellSubtype.setEnabled(true);
			jcSpellSubtype.setModel(new DefaultComboBoxModel(SpellType.SubtypeSpecial.values()));
			tfNumGroups.setEnabled(false);
			tfStrength.setEnabled(false);
			break;
		default:
			jcSpellSubtype.setEnabled(false);
			jcSpellSubtype.setModel(new DefaultComboBoxModel(Util.NOSTRINGARRAY));
			tfNumGroups.setEnabled(false);
			tfStrength.setEnabled(false);
		}
		
		if(!tfStrength.isEnabled())
			tfStrength.setToolTipText("");
		
		if(sType == SpellType.State)
		{
			jrAdd.setEnabled(true);
			jrRemove.setEnabled(true);
		}
		else
		{
			jrAdd.setEnabled(false);
			jrRemove.setEnabled(false);
		}
	}
	
	public void updateLists()
	{
		// Does nothing presently.
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == jbAdd)
		{
			// Create the new spell.
			SpellReference tSpell = dataBank.getSpellBook().newSpell((SpellClass)jcSpellClass.getSelectedItem());
			
			if(tSpell == null)
				JOptionPane.showMessageDialog(this, "Spell could not be created.");
			else
				// Update the spell list so it shows the new spell.
				setSpellList();
		}
		else if(e.getSource() == jbUpdate)
			updateSpell();
		else if(e.getSource() == jbRemove)
		{
			// Get the reference for the spell.
			SpellReference tSpell = dataBank.getSpellBook().getSpell(currentSpell.getID());
			// Remove the reference
			dataBank.getSpellBook().removeSpell(tSpell);
			// Update the spell list to reflect the change.
			setSpellList();
			// Reset the current spell from the list.
			setCurrentSpellFromList();
		}
		else if(e.getSource() == jbName)
		{
			while(true)
			{
				String newName = JOptionPane.showInputDialog("New name:", currentSpell.getName());
				if(newName == null || newName.length() < 2)
					break;
				else if(!dataBank.validSpellName(newName))
					JOptionPane.showMessageDialog(this, "Invalid name.");
				else
				{
					currentSpell.setName(newName);
					jbName.setText("Name: " + newName);
					setSpellList();
					jcSpell.setSelectedItem(newName);
					break;
				}
			}
		}
		else if(e.getSource() == jcSpellClass)
			setSpellList();
		else if(e.getSource() == jcSpell)
			setCurrentSpellFromList();
		else if(e.getSource() == jcSpellType)
			setSpellSubtypeList();
	}
}
