package mordorEditor;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import mordorData.DataBank;
import mordorData.Item;
import mordorData.ItemSpecials;
import mordorData.Map;
import mordorData.Spell;
import mordorData.SpellReference;
import mordorEnums.Alignment;
import mordorEnums.BodyParts;
import mordorEnums.ItemTypes;
import mordorEnums.PlayerState;
import mordorEnums.Resistance;
import mordorEnums.SpellClass;
import mordorEnums.Stats;
import mordorHelpers.Util;


public class EditorItemPanel extends JPanel implements ActionListener
{
	private DataBank dataBank;
	private Item item;
	private JPanel itemBar, infoBar;
	
	private JButton jbAddItem, jbRemoveItem, jbUpdateItem, jbName, jbGenDescription;
	private JComboBox jcItemClass, jcItems;
	private JTextArea taDescription;
	private JCheckBox[] cbAlignment;
	private JCheckBox cbCursed, cbTwoHanded, cbUnaligned;
	private JTextField tfLevel, tfChance, tfSwings, tfAttack, tfDefense, tfDamageMod, tfItemValue;
	private JTextField[] tfStatRequirement, tfStatAdjustment;
	private JScrollPane spGuild;
	private EditorItemPanel_GuildsPane guildPane;
	private JComboBox[][] jcSpecials;
	private JTextField[][] specialVals;
	private short[] spellIDs;
	
	EditorItemPanel(DataBank newDataBank)
	{
		dataBank = newDataBank;
		
		itemBar = new JPanel();
		infoBar = new JPanel();
		JPanel lowerPanel = new JPanel();
		jcItemClass = new JComboBox(ItemTypes.values());
		jcItems = new JComboBox();
		jcItems.setPrototypeDisplayValue("AAAAAAAAABBBBB"); // 15 chars

		jbAddItem = new JButton("New");
		jbRemoveItem = new JButton("Remove");
		jbUpdateItem = new JButton("Update");
		jbName = new JButton("Name:");
		
		jcItemClass.addActionListener(this);
		jbAddItem.addActionListener(this);
		jbRemoveItem.addActionListener(this);
		jbUpdateItem.addActionListener(this);
		jbName.addActionListener(this);
		jcItems.addActionListener(this);
		
		jbAddItem.setToolTipText("Add a new item");
		jbRemoveItem.setToolTipText("Remove this item");
		jbUpdateItem.setToolTipText("Update this item in dataBank");
		
		itemBar.add(jbAddItem);
		itemBar.add(jbRemoveItem);
		itemBar.add(jbUpdateItem);
		itemBar.add(jbName);
		itemBar.add(jcItemClass);
		itemBar.add(jcItems);
		
		updateItemList();

		JPanel booleanCol = new JPanel();
		booleanCol.setLayout(new GridLayout(6, 1));
		
		cbAlignment = new JCheckBox[Alignment.values().length];
		for(Alignment al : Alignment.values())
		{
			cbAlignment[al.value()] = new JCheckBox(al.name());
			cbAlignment[al.value()].setToolTipText("Requires " + al.name() + " alignment.");
			cbAlignment[al.value()].addActionListener(this);
			booleanCol.add(cbAlignment[al.value()]);
		}
		cbUnaligned = new JCheckBox("Unaligned");
		cbCursed = new JCheckBox("Cursed");
		cbTwoHanded = new JCheckBox("2-Hands");
		
		cbCursed.setToolTipText("Cursed Item");
		cbTwoHanded.setToolTipText("Uses two hands.");
		cbUnaligned.setToolTipText("Unaligned item.");
		
		cbUnaligned.addActionListener(this);
		
		booleanCol.add(cbUnaligned);
		booleanCol.add(cbCursed);
		booleanCol.add(cbTwoHanded);

		JPanel statsTextCol = new JPanel();
		JPanel statsVals = new JPanel();
		GridLayout statsTextColLayout = new GridLayout(Stats.values().length + 1, 1);
		statsTextColLayout.setVgap(5);
		statsTextCol.setLayout(statsTextColLayout);
		statsVals.setLayout(new GridLayout(Stats.values().length + 1, 2));
		
		tfStatRequirement = new JTextField[Stats.values().length];
		tfStatAdjustment = new JTextField[Stats.values().length];
		
		statsTextCol.add(new JLabel("Name"));
		statsVals.add(new JLabel("Req."));
		statsVals.add(new JLabel(" Adj."));
		for(byte i = 0; i < Stats.values().length; i++)
		{
			tfStatRequirement[i] = new JTextField(2);
			tfStatAdjustment[i] = new JTextField(2);
			statsTextCol.add(new JLabel(Stats.type(i).name()));
			statsVals.add(tfStatRequirement[i]);
			statsVals.add(tfStatAdjustment[i]);
		}
		
		JPanel otherValsText = new JPanel();
		JPanel otherValsFields = new JPanel();
		GridLayout otherValsLayout = new GridLayout(7, 1);
		otherValsLayout.setVgap(4);
		otherValsFields.setLayout(new GridLayout(7, 1));
		otherValsText.setLayout(otherValsLayout);
		
		tfLevel = new JTextField(3);
		tfChance = new JTextField(3);
		tfSwings = new JTextField(1);
		tfAttack = new JTextField(4);
		tfDefense = new JTextField(4);
		tfDamageMod = new JTextField(4);
		tfItemValue = new JTextField(4);
		
		tfLevel.setToolTipText("Minimum level for item to appear.\n-1 for store items.");
		tfChance.setToolTipText("Probability that and item will appear.");
		tfSwings.setToolTipText("Weapon's swings.");
		tfAttack.setToolTipText("Attack modifier.");
		tfDefense.setToolTipText("Defense modifier.");
		tfDamageMod.setToolTipText("Damage modifier.");
		tfItemValue.setToolTipText("Item value.");
		
		otherValsText.add(new JLabel("Min. Level"));
		otherValsFields.add(tfLevel);
		
		otherValsText.add(new JLabel("Prob. Appear"));
		otherValsFields.add(tfChance);

		otherValsText.add(new JLabel("Swings"));
		otherValsFields.add(tfSwings);

		otherValsText.add(new JLabel("Attack"));
		otherValsFields.add(tfAttack);

		otherValsText.add(new JLabel("Defense"));
		otherValsFields.add(tfDefense);

		otherValsText.add(new JLabel("Damage Mod."));
		otherValsFields.add(tfDamageMod);

		otherValsText.add(new JLabel("Item Value"));
		otherValsFields.add(tfItemValue);
		
		guildPane = new EditorItemPanel_GuildsPane(item, dataBank);
		spGuild = new JScrollPane(guildPane);
		spGuild.setBackground(this.getBackground());
		
		infoBar.add(booleanCol);
		infoBar.add(statsTextCol);
		infoBar.add(statsVals);
		infoBar.add(otherValsText);
		infoBar.add(otherValsFields);
		infoBar.add(spGuild);
		
		// add specials
		
		lowerPanel.setLayout(new BorderLayout());
		lowerPanel.add(infoBar, BorderLayout.NORTH);
		
		jcSpecials = new JComboBox[Item.ITEMSPECIAL_MAX][3];
		specialVals =  new JTextField[Item.ITEMSPECIAL_MAX][2];
		
		for(byte i = 0; i < Item.ITEMSPECIAL_MAX; i++)
		{
			jcSpecials[i][0] = new JComboBox(ItemSpecials.ITEMSPECIAL_NAMES);
			jcSpecials[i][1] = new JComboBox();
			jcSpecials[i][2] = new JComboBox();
			jcSpecials[i][0].addActionListener(this);
			jcSpecials[i][1].addActionListener(this);
			jcSpecials[i][2].addActionListener(this);
			jcSpecials[i][0].setPrototypeDisplayValue("Mmmm");
			jcSpecials[i][1].setPrototypeDisplayValue("Mmmm");
			jcSpecials[i][2].setPrototypeDisplayValue("Mmmm");
			specialVals[i][0] = new JTextField(3);
			specialVals[i][1] = new JTextField(3);
		}
		
		JPanel specDDOneLeft, specDDOneRight;
		JPanel specDDTwoLeft, specDDTwoRight;
		JPanel specDDThreeLeft, specDDThreeRight;
		JPanel specValLeft, specValRight;
		
		int specNum = (Item.ITEMSPECIAL_MAX % 2 == 0) ? Item.ITEMSPECIAL_MAX >> 1 : (Item.ITEMSPECIAL_MAX >> 1) + 1;
		GridLayout specValLayout = new GridLayout(specNum, 2);
		specValLayout.setVgap(5);
		specValLayout.setHgap(2);
		
		specDDOneLeft = new JPanel();
		specDDOneRight = new JPanel();
		specDDTwoLeft = new JPanel();
		specDDTwoRight = new JPanel();
		specDDThreeLeft  = new JPanel();
		specDDThreeRight = new JPanel();
		specValLeft = new JPanel();
		specValRight = new JPanel();
		
		specDDOneLeft.setLayout(new GridLayout(specNum, 1));
		specDDOneRight.setLayout(new GridLayout(specNum, 1));
		specDDTwoLeft.setLayout(new GridLayout(specNum, 1));
		specDDTwoRight.setLayout(new GridLayout(specNum, 1));
		specDDThreeLeft .setLayout(new GridLayout(specNum, 1));
		specDDThreeRight.setLayout(new GridLayout(specNum, 1));
		specValLeft.setLayout(specValLayout);
		specValRight.setLayout(specValLayout);
		
		for(byte i = 0; i < Item.ITEMSPECIAL_MAX; i++)
		{
			if(i < specNum)
			{
				specDDOneLeft.add(jcSpecials[i][0]);
				specDDTwoLeft.add(jcSpecials[i][1]);
				specDDThreeLeft.add(jcSpecials[i][2]);
				specValLeft.add(specialVals[i][0]);
				specValLeft.add(specialVals[i][1]);
			}
			else
			{
				specDDOneRight.add(jcSpecials[i][0]);
				specDDTwoRight.add(jcSpecials[i][1]);
				specDDThreeRight.add(jcSpecials[i][2]);
				specValRight.add(specialVals[i][0]);
				specValRight.add(specialVals[i][1]);
			}
		}
		
		JPanel specialsPanel = new JPanel();
		
		specialsPanel.add(specDDOneLeft);
		specialsPanel.add(specDDTwoLeft);
		specialsPanel.add(specDDThreeLeft);
		specialsPanel.add(specValLeft);
		specialsPanel.add(specDDOneRight);
		specialsPanel.add(specDDTwoRight);
		specialsPanel.add(specDDThreeRight);
		specialsPanel.add(specValRight);
		
		lowerPanel.add(specialsPanel, BorderLayout.SOUTH);
		
		JPanel descPanel = new JPanel();
		
		taDescription = new JTextArea(4, 40);
		taDescription.setWrapStyleWord(true);
		taDescription.setLineWrap(true);
		JScrollPane descScroll = new JScrollPane(taDescription);
		
		JPanel descTitle = new JPanel();
		descTitle.setLayout(new GridLayout(2, 1));
		jbGenDescription = new JButton("Generate");
		jbGenDescription.setToolTipText("Generate the item description.");
		jbGenDescription.addActionListener(this);
		
		descTitle.add(new JLabel("Description:"));
		descTitle.add(jbGenDescription);
		descPanel.add(descTitle);
		descPanel.add(descScroll);
		
		lowerPanel.add(descPanel, BorderLayout.CENTER);
		
		
		setLayout(new BorderLayout());
		
		add(itemBar, BorderLayout.NORTH);
		add(lowerPanel, BorderLayout.CENTER);
		
		updateItemInPanel();
		
		// mid pane: info for selected item
		//	top of mid pain - details
		//	bottom of mid pain - guilds
	}
	
	/**
	 * Takes the currently selected item, extracts its values, and updates
	 * the fields and selections in the panel with those values.
	 */
	public void updateItemInPanel()
	{
		// get selected item in cbItems
		item = dataBank.getItem((String)jcItems.getSelectedItem());
		
		jbName.setText("Name: " + item.getName());
		
		cbUnaligned.setSelected(item.isUnaligned());
		for(Alignment al : Alignment.values())
		{
			if(item.isUnaligned())
				cbAlignment[al.value()].setSelected(true);
			else
				cbAlignment[al.value()].setSelected(item.getAlignment(al));
		}
		
		cbCursed.setSelected(item.isCursed());
		
		cbTwoHanded.setEnabled(item.getItemType().getEquippingPart() == BodyParts.Weapon);
		cbTwoHanded.setSelected(item.isTwoHanded());
		
		for(Stats st : Stats.values())
		{
			tfStatRequirement[st.value()].setText("" + item.getStat(st));
			tfStatAdjustment[st.value()].setText("" + item.getStatAdjustment(st));
		}
		
		tfLevel.setText("" + item.getLevel());
		tfChance.setText("" + item.getChance());
		tfSwings.setText("" + item.getSwings());
		tfAttack.setText("" + item.getAttackModifier());
		tfDefense.setText("" + item.getDefenseModifier());
		tfDamageMod.setText("" + item.getDamageModifier());
		tfItemValue.setText("" + item.getItemBaseValue());
		
		guildPane.updatePane(item);
		
		taDescription.setText("" + item.getDescription());
		
		for(byte i = 0; i < jcSpecials.length; i++)
		{
			jcSpecials[i][0].setSelectedIndex(item.getSpecials()[i].getType());
			changeSpecial(i, false);
		}
	}
	
	/**
	 * Takes the values in the panel and updates them for the associated item
	 * in the dataBank.
	 * @return boolean	True if item was updated.
	 */
	public boolean updateItemInDataBank()
	{
		if(!validityCheck())
			return false;
		
		item.setUnaligned(cbUnaligned.isSelected());
		for(Alignment al : Alignment.values())
		{
			if(cbUnaligned.isSelected())
				item.setAlignment(al, true);
			else
				item.setAlignment(al, cbAlignment[al.value()].isSelected());
		}
		
		item.setCursed(cbCursed.isSelected());
		item.setTwoHanded(cbTwoHanded.isSelected());
		
		for(Stats st : Stats.values())
		{
			item.setStat(st, Byte.parseByte(tfStatRequirement[st.value()].getText().trim()));
			item.setStatsAdjustment(st, Byte.parseByte(tfStatAdjustment[st.value()].getText().trim()));
		}
		
		item.setLevel(Byte.parseByte(tfLevel.getText().trim()));
		item.setChance(Byte.parseByte(tfChance.getText().trim()));
		item.setSwings(Byte.parseByte(tfSwings.getText().trim()));
		item.setAttack(Short.parseShort(tfAttack.getText().trim()));
		item.setDefense(Short.parseShort(tfDefense.getText().trim()));
		item.setDamageModifier(Float.parseFloat(tfDamageMod.getText().trim()));
		item.setItemBaseValue(Long.parseLong(tfItemValue.getText().trim()));
		
		guildPane.updateItem();
		
		for(byte i = 0; i < jcSpecials.length; i++)
		{
			item.getSpecials()[i].setType((byte)jcSpecials[i][0].getSelectedIndex());
			
			switch(jcSpecials[i][0].getSelectedIndex())
			{
			case ItemSpecials.ITEMSPECIAL_RESISTANCE:
				item.getSpecials()[i].setResistanceType((Resistance)jcSpecials[i][1].getSelectedItem());
				item.getSpecials()[i].setResistanceAmount(Short.parseShort(specialVals[i][0].getText().trim()));
				break;
			case ItemSpecials.ITEMSPECIAL_SPELL:
				if(dataBank.getSpellBook().getSpellClass(SpellClass.type(jcSpecials[i][1].getSelectedIndex())).getSize() == 0)
				{
					jcSpecials[i][0].setSelectedIndex(ItemSpecials.ITEMSPECIAL_NONE);
					item.getSpecials()[i].setType(ItemSpecials.ITEMSPECIAL_NONE);
				}
				else
				{
					spellIDs = getSpellIDs(i);
					item.getSpecials()[i].setSpell(dataBank.getSpellBook().getSpell(spellIDs[jcSpecials[i][2].getSelectedIndex()]).getSpell());
					item.getSpecials()[i].setSpellLevel(Short.parseShort(specialVals[i][0].getText().trim()));
					item.getSpecials()[i].setSpellCasts(Short.parseShort(specialVals[i][1].getText().trim()));
				}
				break;
			case ItemSpecials.ITEMSPECIAL_STATE:
				item.getSpecials()[i].changeState((PlayerState)jcSpecials[i][1].getSelectedItem());
				if(specialVals[i][0].getText().trim().equalsIgnoreCase("on"))
					item.getSpecials()[i].changeStateStatus(true);
				else
					item.getSpecials()[i].changeStateStatus(false);
				break;
			case ItemSpecials.ITEMSPECIAL_OTHER:
				item.getSpecials()[i].setOtherType((short)jcSpecials[i][1].getSelectedIndex());
				break;
			case ItemSpecials.ITEMSPECIAL_NONE:
			default:
				break;
			}
		}
		
		return true;
	}
	
	/**
	 * Ensures that all fields in the panel contain valid values.
	 * @return boolean	True if all are valid.
	 */
	private boolean validityCheck()
	{
		byte itemSpellCount = 0;
		
		for(byte i = 0; i < tfStatAdjustment.length; i++)
		{
			if(!MordorEditor.validityCheckTf(this, tfStatAdjustment[i], Stats.MAXIMUMNEGADJUSTMENT, Stats.MAXIMUMPOSADJUSTMENT, "Adj. " + Stats.type(i)))
				return false;
			if(!MordorEditor.validityCheckTf(this, tfStatRequirement[i], Stats.MINIMUMVALUE, Stats.MAXIMUMVALUE, "Req. " + Stats.type(i)))
				return false;
		}
		
		if(!MordorEditor.validityCheckTf(this, tfLevel, -1, Map.MAXDEPTH, "min. Level"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfChance, 0, 100, "chance"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfSwings, 0, Item.MAXITEMSWINGS, "swings"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfAttack, Item.MINATTACK, Item.MAXATTACK, "attack"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfDefense, Item.MINDEFENSE, Item.MAXDEFENSE, "defense"))
			return false;
		{
			String text = tfDamageMod.getText().trim();
			
			if(text == null || text.length() < 1)
				tfDamageMod.setText("" + 1.0);
			
			try
			{
				float temp = Float.parseFloat(text);
				
				temp = Util.FITFLOAT(temp, Item.MINDAMAGEMODIFIER, Item.MAXDAMAGEMODIFIER);
				tfDamageMod.setText("" + temp);
			}
			catch(NumberFormatException NFE)
			{
				JOptionPane.showMessageDialog(this, "Invalid damage modifier.");
				return false;
			}
		}
		if(!MordorEditor.validityCheckTf(this, tfItemValue, 0, Long.MAX_VALUE, "item value"))
			return false;
		
		for(byte i = 0; i < jcSpecials.length; i++)
		{	
			switch(jcSpecials[i][0].getSelectedIndex())
			{
			case ItemSpecials.ITEMSPECIAL_RESISTANCE:
				if(!MordorEditor.validityCheckTf(this, specialVals[i][0], -100, 100, "resistance modifier for special " + (i + 1)))
					return false;
				break;
			case ItemSpecials.ITEMSPECIAL_SPELL:
				if(!MordorEditor.validityCheckTf(this, specialVals[i][0], Spell.VAL_MINLEVEL, Spell.VAL_MAXLEVEL, "spell level for special " + (i + 1)))
					return false;
				if(!MordorEditor.validityCheckTf(this, specialVals[i][1], 1, Short.MAX_VALUE, "number of casts for special " + (i + 1)))
					return false;
				if(itemSpellCount > Item.ITEMSPECIAL_MAXSPELLS)
				{
					JOptionPane.showMessageDialog(this, "Maximum spells per item: " + Item.ITEMSPECIAL_MAXSPELLS);
					return false;
				}
				itemSpellCount += 1;
				break;
			case ItemSpecials.ITEMSPECIAL_STATE:
				if(specialVals[i][0].getText().trim().equalsIgnoreCase("maybe"))
				{
					JOptionPane.showMessageDialog(this, "Ha ha, very funny.");
					return false;
				}
				else if(!(specialVals[i][0].getText().trim().equalsIgnoreCase("on") || specialVals[i][0].getText().trim().equalsIgnoreCase("off")))
				{
					JOptionPane.showMessageDialog(this, "Invalid state change for special " + (i + 1) + ".\nMust be on or off.");
					return false;
				}
				break;
			case ItemSpecials.ITEMSPECIAL_OTHER:
				if(!MordorEditor.validityCheckTf(this, specialVals[i][0], 0, ItemSpecials.ITEMSPECIAL_OTHERNAMES.length - 1, "invalid other type"))
					return false;
				break;
			case ItemSpecials.ITEMSPECIAL_NONE:
			default:
				break;
			}
		}
		
		return true;
	}
	
	/**
	 * Sets up the entry for a specific special. Change type indicates
	 * if the user has changed the type of special (if true) or if we
	 * are loading a new item (if false).
	 * @param index	index of the special (byte)
	 * @param changeType	If we are changing the type of special.
	 */
	private void changeSpecial(byte index, boolean changeType)
	{
		ItemSpecials temp = item.getSpecials()[index];

		switch(jcSpecials[index][0].getSelectedIndex())
		{
		case ItemSpecials.ITEMSPECIAL_RESISTANCE:
			jcSpecials[index][1].removeAllItems();
			jcSpecials[index][1].setModel(new DefaultComboBoxModel(Resistance.values()));
			jcSpecials[index][1].setEnabled(true);
			jcSpecials[index][2].setEnabled(false);
			specialVals[index][0].setEnabled(true);
			specialVals[index][1].setEnabled(false);
			if(changeType)
				specialVals[index][0].setText("0");
			else
			{
				jcSpecials[index][1].setSelectedItem(temp.getResistanceType());
				specialVals[index][0].setText("" + temp.getResistanceAmount());
			}
			specialVals[index][0].setToolTipText("Resistance Modification %");
			specialVals[index][1].setToolTipText(null);
			break;
		case ItemSpecials.ITEMSPECIAL_SPELL:
			jcSpecials[index][1].removeActionListener(this);
			jcSpecials[index][1].removeAllItems();
			jcSpecials[index][1].setModel(new DefaultComboBoxModel(SpellClass.values()));
			jcSpecials[index][1].addActionListener(this);
			jcSpecials[index][1].setEnabled(true);
			jcSpecials[index][2].removeAllItems();
			jcSpecials[index][2].setModel(new DefaultComboBoxModel(dataBank.getSpellBook().getSpellClassNames((SpellClass)jcSpecials[index][1].getSelectedItem())));//.getSpellClass((byte)jcSpecials[index][1].getSelectedIndex()).getSpellNames()));
			jcSpecials[index][2].setEnabled(true);
			specialVals[index][0].setEnabled(true);
			specialVals[index][1].setEnabled(true);
			if(changeType)
			{
				specialVals[index][0].setText("1");
				specialVals[index][1].setText("1");
			}
			else
			{
				SpellReference tSpell = temp.getSpell(dataBank);
				if(tSpell != null)
				{
					jcSpecials[index][1].setSelectedIndex(tSpell.getSpellClass().value());
					jcSpecials[index][2].setSelectedItem(tSpell.getSpell().getName());
					specialVals[index][0].setText("" + tSpell.getLevel());
					specialVals[index][1].setText("" + temp.getSpellCasts());
				}
				else
				{
					jcSpecials[index][0].setSelectedIndex(ItemSpecials.ITEMSPECIAL_NONE);
				}
			}
			specialVals[index][0].setToolTipText("Spell level to cast at");
			specialVals[index][1].setToolTipText("Number of casts for spell");
			break;
		case ItemSpecials.ITEMSPECIAL_STATE:
			jcSpecials[index][1].removeAllItems();
			jcSpecials[index][1].setModel(new DefaultComboBoxModel(PlayerState.effects()));
			jcSpecials[index][1].setEnabled(true);
			jcSpecials[index][2].setEnabled(false);
			specialVals[index][0].setEnabled(true);
			specialVals[index][1].setEnabled(false);
			if(changeType)
				specialVals[index][0].setText("Off");
			else
			{
				jcSpecials[index][1].setSelectedItem(temp.getState());
				if(temp.isStateOn())
					specialVals[index][0].setText("On");
				else
					specialVals[index][0].setText("Off");
			}
			specialVals[index][0].setToolTipText("Change state On/Off");
			specialVals[index][1].setToolTipText(null);
			break;

		case ItemSpecials.ITEMSPECIAL_OTHER:
			jcSpecials[index][1].removeAllItems();
			jcSpecials[index][1].setModel(new DefaultComboBoxModel(ItemSpecials.ITEMSPECIAL_OTHERNAMES));
			jcSpecials[index][1].setSelectedIndex(temp.getOtherType());
			jcSpecials[index][1].setEnabled(true);
			jcSpecials[index][2].setEnabled(false);
			specialVals[index][0].setEnabled(false);
			specialVals[index][1].setEnabled(false);
			specialVals[index][0].setText("");
			specialVals[index][1].setText("");
			specialVals[index][0].setToolTipText(null);
			specialVals[index][1].setToolTipText(null);
			break;
		default:
			jcSpecials[index][1].setEnabled(false);
			jcSpecials[index][2].setEnabled(false);
			specialVals[index][0].setEnabled(false);
			specialVals[index][1].setEnabled(false);
			specialVals[index][0].setToolTipText(null);
			specialVals[index][1].setToolTipText(null);
		}
		
		revalidate();
	}
	
	/**
	 * Updates the entries in the item list.
	 */
	public void updateItemList()
	{
		String[] names = dataBank.getItemNamesInClass(ItemTypes.type(jcItemClass.getSelectedIndex()));
		
		if(names == null)
		{
			Item tItem = dataBank.newItem();
			tItem.setItemType(ItemTypes.type(jcItemClass.getSelectedIndex()));
			names = new String[1];
			names[0] = tItem.getName();
		}
	
		jcItems.setModel(new DefaultComboBoxModel(names));
		revalidate();
		repaint();
	}
	
	public void addItem()
	{
		item = dataBank.newItem(ItemTypes.type(jcItemClass.getSelectedIndex()));
		updateItemList();
		updateItemInPanel();
	}
	
	public void removeItem()
	{
		dataBank.deleteItem(item);
		updateItemList();
		updateItemInPanel();
	}
	
	/**
	 * Updates the list in the panel in case their entries have changed.
	 */
	public void updateLists()
	{
		for(int i = 0; i < jcSpecials.length; i++)
			if(jcSpecials[i][0].getSelectedIndex() == ItemSpecials.ITEMSPECIAL_SPELL)
				jcSpecials[i][2].setModel(new DefaultComboBoxModel(dataBank.getSpellBook().getSpellClassNames((SpellClass)jcSpecials[i][1].getSelectedItem())));
		
		guildPane.updateLists();
	}

	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		if(e.getSource() == jcItemClass)
		{
			updateItemList();
			updateItemInPanel();
		}
		else if(e.getSource() == jbAddItem)
		{
			addItem();
		}
		else if(e.getSource() == jbRemoveItem)
		{
			removeItem();
		}
		else if(e.getSource() == jbUpdateItem)
		{
			updateItemInDataBank();
		}
		else if(e.getSource() == jbName)
		{
			while(true)
			{
				String newName = JOptionPane.showInputDialog("New name:", item.getName());
				if(newName == null || newName.length() < 2)
					break;
				else if(!dataBank.validItemName(newName))
					JOptionPane.showMessageDialog(this, "Invalid name.");
				else
				{
					item.setName(newName);
					jbName.setText("Name: " + newName);
					updateItemList();
					break;
				}
			}
		}
		else if(e.getSource() == jcItems)
		{
        	updateItemInPanel();
		}
		else if(e.getSource() == cbUnaligned && cbUnaligned.isSelected())
		{
			for(byte i = 0; i < this.cbAlignment.length; i++)
				cbAlignment[i].setSelected(true);
		}
		else if(e.getSource() == jbGenDescription)
		{
			taDescription.setText(item.generateDescription(dataBank, false));
		}
		
		for(byte i = 0; i < cbAlignment.length; i++)
			if(e.getSource() == cbAlignment[i] && !cbAlignment[i].isSelected())
				cbUnaligned.setSelected(false);
		
		for(byte i = 0; i < jcSpecials.length; i++)
		{
			if(e.getSource() == jcSpecials[i][0])
			{
				changeSpecial(i, true);
			}
		}
		
		for(byte i = 0; i < jcSpecials.length; i++)
		{
			if(jcSpecials[i][0].getSelectedIndex() == ItemSpecials.ITEMSPECIAL_SPELL && e.getSource() == jcSpecials[i][1])
			{
				String[] names = dataBank.getSpellBook().getSpellClassNames(SpellClass.type(jcSpecials[i][1].getSelectedIndex()));//.getSpellClass((byte)jcSpecials[i][1].getSelectedIndex()).getSpellNames();
				if(names != null)
				{
					jcSpecials[i][2].removeAllItems();
					jcSpecials[i][2].setModel(new DefaultComboBoxModel(names));
					spellIDs = getSpellIDs(i);
				}
				else
				{
					jcSpecials[i][2].removeAllItems();
				}
			}
		}
	}
	
	private short[] getSpellIDs(byte index)
	{
		return dataBank.getSpellBook().getSpellClassIDs((SpellClass)jcSpecials[index][1].getSelectedItem());//.getSpellClass((byte)jcSpecials[index][1].getSelectedIndex()).getIDs();
	}

}
