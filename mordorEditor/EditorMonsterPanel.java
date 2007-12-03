package mordorEditor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import mordorData.DataBank;
import mordorData.ImageBank;
import mordorData.Item;
import mordorData.Map;
import mordorData.Monster;
import mordorEnums.Alignment;
import mordorEnums.DropTypes;
import mordorEnums.MonsterClass;
import mordorEnums.Size;
import mordorEnums.Stats;
import mordorHelpers.Util;

public class EditorMonsterPanel extends JPanel implements ActionListener
{
	private DataBank dataBank;
	private Monster monster;
	
	private JComboBox jcMonsterTypes, jcMonsters;
	private JButton jbNewMonster, jbRemoveMonster, jbUpdateMonster;
	private JButton jbName, monsterImg, jbGenDescription;
	private JButton jbItem, jbSecondItem, jbSpecificCompanion;
	private JComboBox jcCompanion;
	private JCheckBox jcbCompanion;
	private JRadioButton[] jrAlignment;
	private JComboBox jcSize;
	private JTextArea taDescription;
	private JTextField tfMinLevel, tfWealthMulti, tfChanceAppear, tfGroupNumber;
	private JTextField tfStrength, tfConstitution, tfDexterity;
	private JTextField tfAvgHit, tfAttack, tfDefense;
	private JTextField tfGroupSize;
	private EditorMonsterPanel_CheckBoxPane checkBoxes;
	private ButtonGroup alignGroup;
	
	private ImageChooser imageChooser;
	private JPanel itemBar, midBar;
	
	EditorMonsterPanel(DataBank nDataBank)
	{
		dataBank = nDataBank;
		
		itemBar = new JPanel();
		
		jcMonsterTypes = new JComboBox(MonsterClass.values());
		jcMonsters = new JComboBox();
		jcMonsters.setPrototypeDisplayValue("AAAAAAAAAABBBBB"); // 15 chars

		jbNewMonster = new JButton("New");
		jbRemoveMonster = new JButton("Remove");
		jbUpdateMonster = new JButton("Update");
		jbName = new JButton("Name:");
		
		jcMonsterTypes.addActionListener(this);
		jcMonsters.addActionListener(this);
		jbNewMonster.addActionListener(this);
		jbRemoveMonster.addActionListener(this);
		jbUpdateMonster.addActionListener(this);
		jbName.addActionListener(this);
		
		jbNewMonster.setToolTipText("Add a new monster");
		jbRemoveMonster.setToolTipText("Remove this monster");
		jbUpdateMonster.setToolTipText("Update this monster in dataBank");
		
		itemBar.add(jbNewMonster);
		itemBar.add(jbRemoveMonster);
		itemBar.add(jbUpdateMonster);
		itemBar.add(jbName);
		itemBar.add(jcMonsterTypes);
		itemBar.add(jcMonsters);
		
		updateMonsterList();
		
		JPanel selectionCol = new JPanel();
		selectionCol.setLayout(new GridLayout(3, 1));
		
		jrAlignment = new JRadioButton[Alignment.values().length];
		alignGroup = new ButtonGroup();
		for(Alignment al : Alignment.values())
		{
			jrAlignment[al.value()] = new JRadioButton(al.name());
			jrAlignment[al.value()].setToolTipText("Alignment: " + al.name());
			alignGroup.add(jrAlignment[al.value()]);
			selectionCol.add(jrAlignment[al.value()]);
		}
		
		monsterImg = new JButton();
		monsterImg.addActionListener(this);
		monsterImg.setToolTipText("Choose a monster image.");
		
		JPanel descriptionBox = new JPanel();
		descriptionBox.setLayout(new BorderLayout());
		jbGenDescription = new JButton("Generate");
		jbGenDescription.setToolTipText("Generate monster description.");
		jbGenDescription.addActionListener(this);
		
		JPanel descTitle = new JPanel();
		descTitle.add(new JLabel("Description"));
		descTitle.add(jbGenDescription);
		
		descriptionBox.add(descTitle, BorderLayout.NORTH);
		taDescription = new JTextArea(5, 25);
		taDescription.setLineWrap(true);
		taDescription.setWrapStyleWord(true);
		descriptionBox.add(new JScrollPane(taDescription), BorderLayout.CENTER);
		
		JPanel fieldColText = new JPanel();
		JPanel fieldCol = new JPanel();
		
		GridLayout fieldTextLayout = new GridLayout(6, 1);
		fieldTextLayout.setVgap(5);
		fieldColText.setLayout(fieldTextLayout);
		fieldCol.setLayout(new GridLayout(6, 1));
		
		fieldColText.add(new JLabel("Strength"));
		fieldColText.add(new JLabel("Constitution"));
		fieldColText.add(new JLabel("Dexterity"));
		fieldColText.add(new JLabel("Avg. Hits"));
		fieldColText.add(new JLabel("Attack"));
		fieldColText.add(new JLabel("Defense"));
		
		tfStrength = new JTextField(3);
		tfConstitution = new JTextField(3);
		tfDexterity = new JTextField(3);
		tfAvgHit = new JTextField(3);
		tfAttack = new JTextField(3);
		tfDefense = new JTextField(3);
		
		tfStrength.setToolTipText("Monster's Strength");
		tfConstitution.setToolTipText("Monster's Constitution");
		tfDexterity.setToolTipText("Monster's Dexterity");
		tfAvgHit.setToolTipText("Monster's Average Hits");
		tfAttack.setToolTipText("Monster's Attack");
		tfDefense.setToolTipText("Monster's Defense");
		
		fieldCol.add(tfStrength);
		fieldCol.add(tfConstitution);
		fieldCol.add(tfDexterity);
		fieldCol.add(tfAvgHit);
		fieldCol.add(tfAttack);
		fieldCol.add(tfDefense);
		
		JPanel combosCol = new JPanel();
		
		jcSize = new JComboBox(Size.values());
		tfMinLevel = new JTextField(3);
		tfWealthMulti = new JTextField(3);
		tfChanceAppear = new JTextField(3);
		
		combosCol.add(new JLabel("Size"));
		combosCol.add(jcSize);
		combosCol.add(new JLabel("Min. Level"));
		combosCol.add(tfMinLevel);
		combosCol.add(new JLabel("Wealth Mult."));
		combosCol.add(tfWealthMulti);
		combosCol.add(new JLabel("Appear Cha."));
		combosCol.add(tfChanceAppear);
		
		JPanel groupsRow = new JPanel();
		
		tfGroupSize = new JTextField(2);
		tfGroupNumber = new JTextField(2);
		tfGroupNumber.setToolTipText("Number of stacks of companions.");
		tfGroupNumber.setText("1");
		
		groupsRow.add(new JLabel("Size"));
		groupsRow.add(tfGroupSize);
		
		jcbCompanion = new JCheckBox("Has companion");
		jcbCompanion.setToolTipText("Does this monster have companions.");
		jcbCompanion.addActionListener(this);
		groupsRow.add(jcbCompanion);
		
		groupsRow.add(new JLabel("Stacks"));
		groupsRow.add(tfGroupNumber);
		
		jcCompanion = new JComboBox(MonsterClass.values());
		jcCompanion.setToolTipText("Type of companions for this monster.");
		groupsRow.add(jcCompanion);
		
		jbItem = new JButton(Util.NOSTRING);
		jbItem.setToolTipText("Specific item this monster drops.");
		jbItem.addActionListener(this);
		
		jbSecondItem = new JButton(Util.NOSTRING);
		jbSecondItem.setToolTipText("Second specific item this monster drops.");
		jbSecondItem.addActionListener(this);
		
		jbSpecificCompanion = new JButton(Util.NOSTRING);
		jbSpecificCompanion.setToolTipText("Set a specific companion for this monster.");
		jbSpecificCompanion.addActionListener(this);
		
		groupsRow.add(jbItem);
		groupsRow.add(jbSecondItem);
		groupsRow.add(jbSpecificCompanion);
		
		midBar = new JPanel();
		midBar.add(selectionCol);
		midBar.add(monsterImg);
		midBar.add(descriptionBox);
		midBar.add(fieldColText);
		midBar.add(fieldCol);
		midBar.add(combosCol);
		midBar.add(groupsRow);
		
		checkBoxes = new EditorMonsterPanel_CheckBoxPane();
		
		JPanel topPane = new JPanel();
		topPane.setLayout(new BorderLayout());
		topPane.add(itemBar, BorderLayout.NORTH);
		topPane.add(midBar, BorderLayout.CENTER);
		
		JPanel botBar =  new JPanel();
		botBar.add(new JScrollPane(checkBoxes));
		
		setLayout(new BorderLayout());
		add(topPane, BorderLayout.CENTER);
		add(botBar, BorderLayout.SOUTH);
		
		updateMonsterInPanel();
	}
	
	public void setImageID(short newID)
	{
		monster.setMonsterImg((byte)newID);
		monsterImg.setIcon(new ImageIcon(dataBank.getImages().getMonsterImage(monster.getMonsterImageID()).getScaledInstance(ImageBank.MONSTERIMAGESIZE, ImageBank.MONSTERIMAGESIZE, 0)));
	}
	
	/**
	 * Updates the list of monsters combobox. This is the monsters
	 * of the selected class.
	 */
	public void updateMonsterList()
	{
		String[] names = dataBank.getMonsterEden().getMonsterNamesByClass((MonsterClass)jcMonsterTypes.getSelectedItem());
		
		if(names == null)
		{
			Monster tMonster = dataBank.getMonsterEden().newMonster((MonsterClass)jcMonsterTypes.getSelectedItem());
			names = new String[1];
			names[0] = tMonster.getName();
		}
		
		jcMonsters.setModel(new DefaultComboBoxModel(names));
		revalidate();
	}
	
	public void updateMonsterInPanel()
	{
		monster = dataBank.getMonsterEden().getMonster((String)jcMonsters.getSelectedItem());
		
		jbName.setText(monster.getName());
		monsterImg.setIcon(new ImageIcon(dataBank.getImages().getMonsterImage(monster.getMonsterImageID()).getScaledInstance(ImageBank.MONSTERIMAGESIZE, ImageBank.MONSTERIMAGESIZE, 0)));
		
		//jcbAquatic.setSelected(monster.isAquatic());
		taDescription.setText(monster.getDescription());
		
		jrAlignment[monster.getAlignment().value()].setSelected(true);
		
		tfStrength.setText("" + monster.getStat(Stats.Strength));
		tfConstitution.setText("" + monster.getStat(Stats.Constitution));
		tfDexterity.setText("" + monster.getStat(Stats.Dexterity));
		tfAvgHit.setText("" + monster.getAvgHits());
		tfAttack.setText(""+ monster.getAttack());
		tfDefense.setText("" + monster.getDefense());
		
		jcSize.setSelectedItem(monster.getSize());
		
		tfMinLevel.setText("" + monster.getLevel());
		tfWealthMulti.setText("" + monster.getWealthMultiplier());
		tfChanceAppear.setText("" + monster.getChanceOfAppearance());
		
		tfGroupSize.setText("" + monster.getGroupSize());
		tfGroupNumber.setText("" + monster.getGroupNumber());
		
		if(monster.getCompanionType() == null)
		{
			jcbCompanion.setSelected(false);
			jcCompanion.setEnabled(false);
			tfGroupNumber.setEnabled(false);
		}
		else
		{
			jcbCompanion.setSelected(true);
			jcCompanion.setEnabled(true);
			tfGroupNumber.setEnabled(true);
		}
		
		if(monster.getItemDropID() == Util.NOTHING || dataBank.getItem(monster.getItemDropID()) == null)
			jbItem.setText(Util.NOSTRING);
		else
			jbItem.setText(dataBank.getItem(monster.getItemDropID()).getName());
		
		if(monster.getSecondItemDropID() == Util.NOTHING || dataBank.getItem(monster.getSecondItemDropID()) == null)
			jbSecondItem.setText(Util.NOSTRING);
		else
			jbSecondItem.setText(dataBank.getItem(monster.getSecondItemDropID()).getName());
		
		if(monster.getSpecificCompanionID() == Util.NOTHING || dataBank.getMonsterEden().getMonster(monster.getSpecificCompanionID()) == null)
			jbSpecificCompanion.setText(Util.NOSTRING);
		else
			jbSpecificCompanion.setText(dataBank.getMonsterEden().getMonster(monster.getSpecificCompanionID()).getName());
		
		checkBoxes.updatePanel(monster);
	}
	
	public boolean updateMonsterInDataBank()
	{
		if(!checkValidity())
			return false;
		//monster.setAquatic(jcbAquatic.isSelected());
		monster.setDescription(taDescription.getText().trim());
		
		for(Alignment al : Alignment.values())
			if(jrAlignment[al.value()].isSelected())
				monster.setAlignment(al);
		
		monster.setStat(Stats.Strength, Byte.parseByte(tfStrength.getText().trim()));
		monster.setStat(Stats.Constitution, Byte.parseByte(tfConstitution.getText().trim()));
		monster.setStat(Stats.Dexterity, Byte.parseByte(tfDexterity.getText().trim()));
		
		monster.setAverageHits(Short.parseShort(tfAvgHit.getText().trim()));
		monster.setAttack(Short.parseShort(tfAttack.getText().trim()));
		monster.setDefense(Short.parseShort(tfDefense.getText().trim()));
		
		monster.setSize((Size)jcSize.getSelectedItem());
		
		monster.setLevel(Byte.parseByte(tfMinLevel.getText().trim()));
		monster.setWealthMultiplier(Byte.parseByte(tfWealthMulti.getText().trim()));
		monster.setChanceOfAppearance(Byte.parseByte(tfChanceAppear.getText().trim()));
		
		monster.setGroupSize(Byte.parseByte(tfGroupSize.getText().trim()));
		
		String tItemName = jbItem.getText();
		monster.setItemDropID(Util.NOTHING);
		if(tItemName.length() > 0 && !tItemName.equalsIgnoreCase(Util.NOSTRING))
		{
			Item tItem = dataBank.getItem(tItemName);
			if(tItem != null)
				monster.setItemDropID(tItem.getID());
		}
		
		tItemName = jbSecondItem.getText();
		monster.setSecondItemDropID(Util.NOTHING);
		if(tItemName.length() > 0 && !tItemName.equalsIgnoreCase(Util.NOSTRING))
		{
			Item tItem = dataBank.getItem(tItemName);
			if(tItem != null)
				monster.setSecondItemDropID(tItem.getID());
		}
		
		tItemName = jbSpecificCompanion.getText();
		monster.setSpecificCompanionID(Util.NOTHING);
		if(tItemName.length() > 0 && !tItemName.equalsIgnoreCase(Util.NOSTRING))
		{
			Monster tMons = dataBank.getMonsterEden().getMonster(tItemName);
			if(tMons != null)
				monster.setSpecificCompanionID(tMons.getID());
		}
			
		if(jcbCompanion.isSelected())
		{
			monster.setCompanionType((MonsterClass)jcCompanion.getSelectedItem());
			monster.setGroupNumber(Byte.parseByte(tfGroupNumber.getText().trim()));
		}
		else
		{
			monster.setCompanionType(null);
			monster.setGroupNumber((byte)0);
		}
		
		checkBoxes.updateMonster(monster);
		return true;
	}
	
	private boolean checkValidity()
	{
		if(!MordorEditor.validityCheckTf(this, tfStrength, Stats.MINIMUMVALUE, Stats.MAXIMUMVALUE, "strength"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfConstitution, Stats.MINIMUMVALUE, Stats.MAXIMUMVALUE, "constitution"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfDexterity, Stats.MINIMUMVALUE, Stats.MAXIMUMVALUE, "dexterity"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfAvgHit, 0, Short.MAX_VALUE, "average hits"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfAttack, 0, Short.MAX_VALUE, "attack"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfDefense, 0, Short.MAX_VALUE, "defense"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfMinLevel, 0, Map.MAXDEPTH, "minimum level"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfWealthMulti, 0, Byte.MAX_VALUE, "wealth multiplier"))
			return false;
		if(!MordorEditor.validityCheckTf(this, tfChanceAppear, 0, 100, "chance of appearance"))
			return false;
		if(!checkBoxes.validateAbilities())
			return false;
		if(!Util.validityCheckTf(this, tfGroupSize, 1, Util.MON_MAXGROUPSIZE, "stack size"));
		if(!Util.validityCheckTf(this, tfGroupNumber, 1, Util.MON_MAXSTACKSIZE, "stack number"))
			return false;
		
		return true;
	}
	
	public boolean addMonster()
	{
		return (dataBank.getMonsterEden().newMonster((MonsterClass)jcMonsterTypes.getSelectedItem()) != null);
	}
	
	public boolean removeMonster()
	{
		return (dataBank.getMonsterEden().removeMonster(monster.getID()) != null);
	}
	
	/**
	 * Update anything in the panel that may be different due to other panels
	 */
	public void updateLists()
	{
		// Monsters are presently independent of other types.
	}

	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		if(e.getSource() == jcMonsterTypes)
		{
			updateMonsterList();
			updateMonsterInPanel();
		}
		else if(e.getSource() == jcMonsters)
		{
			updateMonsterInPanel();
		}
		else if(e.getSource() == monsterImg)
		{
			imageChooser = new ImageChooser(dataBank, this);
			imageChooser.setVisible(true);
		}
		else if(e.getSource() == jbNewMonster)
		{
			if(!addMonster())
				JOptionPane.showMessageDialog(this, "Monster could not be added.");
			else
				updateMonsterList();
		}
		else if(e.getSource() == jbRemoveMonster)
		{
			if(!removeMonster())
				JOptionPane.showMessageDialog(this, "Monster could not be removed.");
			else
			{
				updateMonsterList();
				updateMonsterInPanel();
			}
		}
		else if(e.getSource() == jbUpdateMonster)
		{
			updateMonsterInDataBank();
		}
		else if(e.getSource() == jbName)
		{
			while(true)
			{
				String newName = JOptionPane.showInputDialog("New name:", monster.getName());
				if(newName == null || newName.length() < 2)
					break;
				else if(!dataBank.getMonsterEden().validName(newName))
					JOptionPane.showMessageDialog(this, "Invalid name.");
				else
				{
					monster.setName(newName);
					jbName.setText("Name: " + newName);
					updateMonsterList();
					break;
				}
			}
		}
		else if(e.getSource() == jcbCompanion)
		{
			if(jcbCompanion.isSelected())
			{
				jcCompanion.setEnabled(true);
				tfGroupNumber.setEnabled(true);
			}
			else
			{
				jcCompanion.setEnabled(false);
				tfGroupNumber.setEnabled(false);
			}
		}
		else if(e.getSource() == jbItem)
		{
			JFrame itemBrowse = new JFrame();

			itemBrowse.add(new ListChooserPanel(itemBrowse, jbItem, dataBank, ListChooserPanel.BrowseTypes.items));
			itemBrowse.pack();
			itemBrowse.setVisible(true);
		}
		else if(e.getSource() == jbSecondItem)
		{
			JFrame itemBrowse = new JFrame();

			itemBrowse.add(new ListChooserPanel(itemBrowse, jbSecondItem, dataBank, ListChooserPanel.BrowseTypes.items));
			itemBrowse.pack();
			itemBrowse.setVisible(true);
		}
		else if(e.getSource() == jbSpecificCompanion)
		{
			JFrame monsBrowse = new JFrame();

			monsBrowse.add(new ListChooserPanel(monsBrowse, jbSpecificCompanion, dataBank, ListChooserPanel.BrowseTypes.monster));
			monsBrowse.pack();
			monsBrowse.setVisible(true);
		}
		else if(e.getSource() == jbGenDescription)
			taDescription.setText(monster.generateDescription(false));
	}

}
