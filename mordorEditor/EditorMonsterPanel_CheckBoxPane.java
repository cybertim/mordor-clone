package mordorEditor;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import mordorData.Monster;
import mordorEnums.ItemTypes;
import mordorEnums.MonsterAbility;
import mordorEnums.Resistance;
import mordorEnums.SpellClass;

public class EditorMonsterPanel_CheckBoxPane extends JPanel implements Scrollable, ActionListener 
{
	private JCheckBox allAbilities, allItems, allSpells;
	private JCheckBox[] abilities, itemDrops, spellClass;
	private JTextField[] resist;
	
	EditorMonsterPanel_CheckBoxPane()
	{
		abilities = new JCheckBox[MonsterAbility.values().length];
		for(byte i = 0; i < abilities.length; i++)
			abilities[i] = new JCheckBox(MonsterAbility.type(i).toString());
		itemDrops = new JCheckBox[ItemTypes.values().length];
		for(byte i = 0; i < itemDrops.length; i++)
			itemDrops[i] = new JCheckBox(ItemTypes.type(i).name());
		spellClass = new JCheckBox[SpellClass.values().length];
		for(byte i = 0; i < spellClass.length; i++)
			spellClass[i] = new JCheckBox(SpellClass.values()[i].name());
		
		resist = new JTextField[Resistance.values().length];
		for(byte i = 0; i < resist.length; i++)
			resist[i] = new JTextField(3);
		
		int height = abilities.length;
		height = (height < itemDrops.length) ? itemDrops.length : height;
		height = (height < spellClass.length) ? spellClass.length : height;
		height = (height < resist.length) ? resist.length : height;
		
		setLayout(new GridLayout(height + 2, 5));
		
		allAbilities = new JCheckBox("All Abilities");
		allItems = new JCheckBox("All Items");
		allSpells = new JCheckBox("All Spells");
		
		allAbilities.addActionListener(this);
		allItems.addActionListener(this);
		allSpells.addActionListener(this);
		
		add(new JLabel("Abilities"));
		add(new JLabel("Item Drops"));
		add(new JLabel("Spell Classes"));
		add(new JLabel("Resistances"));
		
		add(allAbilities);
		add(allItems);
		add(allSpells);
		add(new JPanel());
		
		for(int i = 0; i < height; i++)
		{
			if(i < abilities.length)
				add(abilities[i]);
			else
				add(new JPanel());
			

			if(i < itemDrops.length)
				add(itemDrops[i]);
			else
				add(new JPanel());

			if(i < spellClass.length)
				add(spellClass[i]);
			else
				add(new JPanel());
			
			if(i < resist.length)
			{
				JPanel rPanel = new JPanel();
				rPanel.setLayout(new BorderLayout());
				
				rPanel.add(new JLabel(Resistance.type(i).name()), BorderLayout.WEST);
				rPanel.add(resist[i], BorderLayout.EAST);
				
				add(rPanel);
			}
			else
			{
				add(new JPanel());
			}
		}
	}
	
	public void updatePanel(Monster monster)
	{
		for(byte i = 0; i < abilities.length; i++)
			abilities[i].setSelected(monster.hasAbility(MonsterAbility.type(i)));
		
		for(byte i = 0; i < itemDrops.length; i++)
			itemDrops[i].setSelected(monster.canDropItemType(ItemTypes.type(i)));
		
		for(byte i = 0; i < spellClass.length; i++)
			spellClass[i].setSelected(monster.canCastSpellClass(SpellClass.type(i)));
		
		for(Resistance re : Resistance.values())
			resist[re.value()].setText("" + monster.getResistance(re));
	}
	
	public void updateMonster(Monster monster)
	{
		for(MonsterAbility ma : MonsterAbility.values())
			monster.setAbility(ma, abilities[ma.value()].isSelected());
		
		for(byte i = 0; i < itemDrops.length; i++)
			monster.setItemDropType(ItemTypes.type(i), itemDrops[i].isSelected());
		
		for(byte i = 0; i < spellClass.length; i++)
			monster.setSpellClass(SpellClass.type(i), spellClass[i].isSelected());
		
		for(Resistance re : Resistance.values())
			monster.setResistances(re, Byte.parseByte(resist[re.value()].getText()));
	}
	
	public boolean validateAbilities()
	{
		for(byte i = 0; i < resist.length; i++)
			if(!MordorEditor.validityCheckTf(this, resist[i], 0, 100, Resistance.type(i).name()))
				return false;
		
		return true;
	}

	public Dimension getPreferredScrollableViewportSize()
	{
		return new Dimension(this.getWidth(), 220);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// TODO Auto-generated method stub
		return 50;
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
		return 5;
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == allAbilities)
		{
			for(byte i = 0; i < abilities.length; i++)
				abilities[i].setSelected(allAbilities.isSelected());
		}
		else if(e.getSource() == allItems)
		{
			for(byte i = 0; i < itemDrops.length; i++)
				itemDrops[i].setSelected(allItems.isSelected());
		}
		else if(e.getSource() == allSpells)
		{
			for(byte i = 0; i < spellClass.length; i++)
				spellClass[i].setSelected(allSpells.isSelected());
		}
	}
}
