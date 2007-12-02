package mordorEditor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import structures.LinkedList;
import structures.ListIter;
import structures.ListNode;

import mordorData.Chest;
import mordorData.DataBank;
import mordorData.Item;
import mordorData.ItemInstance;
import mordorData.Monster;
import mordorData.MonsterInstance;
import mordorData.Room;
import mordorEnums.MonsterClass;
import mordorEnums.Trap;
import mordorHelpers.Util;

public class MapRoomPanel extends JPanel implements ActionListener
{
	private Room room;
	private DataBank dataBank;
	private JFrame parent;
	
	private JCheckBox isStud, isMagicLock;
	
	private JTextField tfLairedMonsterName[];//,  tfLairedMonsterCount[];
	private JButton jbLairVerify[], jbLairBrowse[];
	
	private JTextField tfMonsters[], tfMonsterCounts[];
	private JButton jbVerify[], jbBrowse[];
	
	private JRadioButton jrBooty[];
	private ButtonGroup bootyGroup;
	
	private JTextField tfRoomItem, tfChestItems[], tfChestGold, tfChestTrapLevel;
	private JComboBox cmbChestTrap;
	private JButton jbBrowseRItem, jbBrowseCItem[], jbVerifyRItem, jbVerifyCItem[];
	
	private JComboBox cmbPlayersHere;
	
	private JButton jbOK, jbCancel;
	private JLabel jlRoomTitle;
	
	private JCheckBox monsterTypes[];
	private JButton jbMTNone, jbMTAll, jbMTInvert;
	
	public MapRoomPanel(Room nRoom, DataBank nDataBank, JFrame nParent)
	{
		room = nRoom;
		dataBank = nDataBank;
		parent = nParent;
		
		JPanel topBar = new JPanel();
		JPanel buttonBar = new JPanel();
		JPanel dataBar = new JPanel();

		JPanel monsterPane = new JPanel();
		JPanel chestPane = new JPanel();
		JPanel otherPane = new JPanel();
		
		jlRoomTitle = new JLabel("Room #");
		topBar.add(jlRoomTitle);
		
		isStud = new JCheckBox("Stud");
		isStud.setToolTipText("This is a stud room.");
		isMagicLock = new JCheckBox("Magic");
		isMagicLock.setToolTipText("Chest/box is magically sealed.");
		
		tfLairedMonsterName = new JTextField[Util.MON_MAXSTACKSIZE];
		jbLairVerify = new JButton[Util.MON_MAXSTACKSIZE];
		jbLairBrowse = new JButton[Util.MON_MAXSTACKSIZE];

		monsterPane.setLayout(new GridLayout((Util.MON_MAXSTACKSIZE << 1) + 1, 1));
		
		for(int i = 0; i < Util.MON_MAXSTACKSIZE; i++)
		{
			tfLairedMonsterName[i] = new JTextField(10);
			jbLairVerify[i] = new JButton("Verify");
			jbLairBrowse[i] = new JButton("Browse");
			
			jbLairVerify[i].addActionListener(this);
			jbLairBrowse[i].addActionListener(this);
			
			tfLairedMonsterName[i].setToolTipText("Type of monster laired here.");
			jbLairVerify[i].setToolTipText("Verify monster entered exists.");
			jbLairBrowse[i].setToolTipText("Browse for a monster.");

			JPanel lairedMonster = new JPanel();
			lairedMonster.add(new JLabel("Lair " + i + ":"));
			lairedMonster.add(tfLairedMonsterName[i]);
			lairedMonster.add(jbLairVerify[i]);
			lairedMonster.add(jbLairBrowse[i]);
			
			monsterPane.add(lairedMonster);
		}
		
		JPanel monsterHead = new JPanel();
		
		monsterHead.add(new JLabel("Monster name"));
		monsterHead.add(new JLabel("#"));
		monsterPane.add(monsterHead);
		
		tfMonsters = new JTextField[Util.MON_MAXSTACKSIZE];
		tfMonsterCounts = new JTextField[Util.MON_MAXSTACKSIZE];
		jbVerify = new JButton[Util.MON_MAXSTACKSIZE];
		jbBrowse = new JButton[Util.MON_MAXSTACKSIZE];
		for(byte i = 0; i < Util.MON_MAXSTACKSIZE; i++)
		{
			JPanel thisMon = new JPanel();
			tfMonsters[i] = new JTextField(10);
			tfMonsterCounts[i] = new JTextField(3);
			jbVerify[i] = new JButton("Verify");
			jbBrowse[i] = new JButton("Browse");
			
			jbVerify[i].addActionListener(this);
			jbBrowse[i].addActionListener(this);
			
			tfMonsters[i].setToolTipText("Type of monster laired here.");
			tfMonsterCounts[i].setToolTipText("Average number of monster laired here.");
			jbVerify[i].setToolTipText("Verify monster entered exists.");
			jbBrowse[i].setToolTipText("Browse for a monster.");
			
			thisMon.add(tfMonsters[i]);
			thisMon.add(tfMonsterCounts[i]);
			thisMon.add(jbVerify[i]);
			thisMon.add(jbBrowse[i]);
			
			monsterPane.add(thisMon);
		}
		
		jrBooty = new JRadioButton[3];
		
		jrBooty[0] = new JRadioButton("None");
		jrBooty[1] = new JRadioButton("Box");
		jrBooty[2] = new JRadioButton("Chest");
		
		jrBooty[0].addActionListener(this);
		jrBooty[1].addActionListener(this);
		jrBooty[2].addActionListener(this);

		jrBooty[0].setToolTipText("No treasure in this room.");
		jrBooty[1].setToolTipText("A box in this room.");
		jrBooty[2].setToolTipText("A chest in this room.");
		
		bootyGroup = new ButtonGroup();
		bootyGroup.add(jrBooty[0]);
		bootyGroup.add(jrBooty[1]);
		bootyGroup.add(jrBooty[2]);
		
		tfRoomItem = new JTextField(10);
		tfChestItems = new JTextField[Chest.MAXITEMSINCHEST];
		tfChestGold = new JTextField(6);
		cmbChestTrap = new JComboBox(Trap.values());
		tfChestTrapLevel = new JTextField(2);
		
		jbBrowseCItem = new JButton[Chest.MAXITEMSINCHEST];
		jbVerifyCItem = new JButton[Chest.MAXITEMSINCHEST];
		

		jbBrowseRItem = new JButton("Browse");
		jbVerifyRItem = new JButton("Verify");
		jbBrowseRItem.addActionListener(this);
		jbVerifyRItem.addActionListener(this);
		jbBrowseRItem.setToolTipText("Browse for room item.");
		jbVerifyRItem.setToolTipText("Verify room item.");
		
		JPanel[] cItems = new JPanel[Chest.MAXITEMSINCHEST];
		
		for(int i = 0; i < cItems.length; i++)
		{
			cItems[i] = new JPanel();
			cItems[i].add(new JLabel("Chest Item"));
			jbBrowseCItem[i] = new JButton("Browse");
			jbVerifyCItem[i] = new JButton("Verify");
			tfChestItems[i] = new JTextField(10);
			
			jbBrowseCItem[i].addActionListener(this);
			jbVerifyCItem[i].addActionListener(this);
			
			jbBrowseCItem[i].setToolTipText("Browse for chest item.");
			jbVerifyCItem[i].setToolTipText("Verify chest item.");

			cItems[i].add(tfChestItems[i]);
			cItems[i].add(jbVerifyCItem[i]);
			cItems[i].add(jbBrowseCItem[i]);
		}
		
		JPanel rItem = new JPanel();
	//	JPanel cItem = new JPanel();
		
		rItem.add(new JLabel("Room Item"));
		rItem.add(tfRoomItem);
		rItem.add(jbVerifyRItem);
		rItem.add(jbBrowseRItem);
		
	//	cItem.add(new JLabel("Chest Item"));
		
		JPanel goldPane = new JPanel();
		JPanel trap = new JPanel();
		//JPanel trapLevel = new JPanel();
		
		goldPane.add(new JLabel("Gold:"));
		goldPane.add(tfChestGold);
		
		trap.add(new JLabel("Trap type:"));
		trap.add(cmbChestTrap);
		trap.add(new JLabel("Trap level:"));
		trap.add(tfChestTrapLevel);
		
		cmbPlayersHere = new JComboBox();
		cmbPlayersHere.setToolTipText("Players in this room.");
		
		JPanel monsterTypePane = new JPanel();
		int mc = (MonsterClass.values().length / 3) + 2;
		monsterTypePane.setLayout(new GridLayout(mc, 3));

		jbMTNone = new JButton("None");
		jbMTAll = new JButton("All");
		jbMTInvert = new JButton("Invert");
		
		jbMTNone.addActionListener(this);
		jbMTAll.addActionListener(this);
		jbMTInvert.addActionListener(this);
		
		jbMTNone.setToolTipText("Clear all boxes.");
		jbMTAll.setToolTipText("Select all boxes.");
		jbMTInvert.setToolTipText("Invert selection.");
		
		monsterTypePane.add(jbMTNone);
		monsterTypePane.add(jbMTAll);
		monsterTypePane.add(jbMTInvert);
		
		monsterTypes = new JCheckBox[MonsterClass.values().length];
		for(MonsterClass t : MonsterClass.values())
		{
			monsterTypes[t.value()] = new JCheckBox(t.toString());
			monsterTypePane.add(monsterTypes[t.value()]);
		}
		
		jbOK = new JButton("OK");
		jbCancel = new JButton("Cancel");
		
		jbOK.addActionListener(this);
		jbCancel.addActionListener(this);
		
		jbOK.setToolTipText("Store changes to room.");
		jbCancel.setToolTipText("Cancel any changes.");
		
		otherPane = new JPanel();
		otherPane.setLayout(new GridLayout(5, 1));
		otherPane.add(jrBooty[0]);
		otherPane.add(jrBooty[1]);
		otherPane.add(jrBooty[2]);
		otherPane.add(isStud);
		otherPane.add(isMagicLock);
		
		chestPane.setLayout(new GridLayout(3 + cItems.length, 1));
		chestPane.add(rItem);
		chestPane.add(goldPane);
		chestPane.add(trap);
	//	chestPane.add(trapLevel);
		for(int i = 0; i < cItems.length; i++)
			chestPane.add(cItems[i]);
		
		dataBar.add(monsterPane);
		dataBar.add(otherPane);
		dataBar.add(chestPane);
		
		buttonBar.add(jbOK);
		buttonBar.add(jbCancel);
		buttonBar.add(new JLabel("Players:"));
		buttonBar.add(cmbPlayersHere);
		
		JPanel lowLowPane = new JPanel();
		lowLowPane.setLayout(new BorderLayout());
		lowLowPane.add(monsterTypePane, BorderLayout.CENTER);
		lowLowPane.add(buttonBar, BorderLayout.SOUTH);
		
		JPanel lowerPane = new JPanel();
		lowerPane.setLayout(new BorderLayout());
		lowerPane.add(dataBar, BorderLayout.CENTER);
		lowerPane.add(lowLowPane, BorderLayout.SOUTH);
		
		setLayout(new BorderLayout());
		add(topBar, BorderLayout.NORTH);
		add(lowerPane, BorderLayout.CENTER);
		
		updatePanel();
	}
	
	public boolean updatePanel()
	{
		if(room == null)
			return false;
		
		jlRoomTitle.setText("Room #" + room.getRoomNumber());
		
		isStud.setSelected(room.isStudRoom());
		
		if(room.getChest() == null || room.getChest().chestIsEmpty())
		{
			jrBooty[0].setSelected(true);
			tfChestGold.setEnabled(false);
			for(int i = 0; i < tfChestItems.length; i++)
			{
				tfChestItems[i].setEnabled(false);
				jbVerifyCItem[i].setEnabled(false);
				jbBrowseCItem[i].setEnabled(false);
			}
			cmbChestTrap.setEnabled(false);
			tfChestTrapLevel.setEnabled(false);
			isMagicLock.setEnabled(false);
		}
		else
		{
			Chest chest = room.getChest();
			if(chest.isBox())
				jrBooty[1].setSelected(true);
			else
				jrBooty[2].setSelected(true);
			
			tfChestGold.setEnabled(true);
			for(int i = 0; i < tfChestItems.length; i++)
			{
				tfChestItems[i].setEnabled(true);
				jbVerifyCItem[i].setEnabled(true);
				jbBrowseCItem[i].setEnabled(true);
			}
			cmbChestTrap.setEnabled(true);
			tfChestTrapLevel.setEnabled(true);
			isMagicLock.setEnabled(true);
			
			ListIter<ItemInstance> iNode = chest.getItems().getIterator();
			int count = 0;
			while(iNode.next())
			{
				if(iNode.element() != null)
					tfChestItems[count].setText(iNode.element().getItem().getName());
				else
					tfChestItems[count].setText("");
				
				count++;
			}
			
			tfChestGold.setText("" + chest.getGold());
			cmbChestTrap.setSelectedIndex(chest.getTrapType().value());
			tfChestTrapLevel.setText("" + chest.getLockLevel());
		}
		
		if(room.getItem() != null)
			tfRoomItem.setText(room.getItem().getItem().getName());
		else {
			tfRoomItem.setText("");
		}
		
		cmbPlayersHere.setModel(new DefaultComboBoxModel(room.getPlayerNamesHere()));
		
		for(byte i = 0; i < Util.MON_MAXSTACKSIZE; i++)
		{
			Monster tMon = dataBank.getMonsterEden().getMonster(room.getLairedMonsterID(i));
			if(tMon != null)
				tfLairedMonsterName[i].setText(tMon.getName());
			else
				tfLairedMonsterName[i].setText("");
		}
		
		// Update the monsters
		ListIter<LinkedList<MonsterInstance>> mNode = room.getMonsterStacks().getIterator();
		for(byte i = 0; i < tfMonsters.length; i++)
		{
			if(mNode.next())
			{
				tfMonsters[i].setText(mNode.element().getFirst().getMonster().getName());
				tfMonsterCounts[i].setText("" + mNode.element().getSize());
			}
			else
			{
				tfMonsters[i].setText("");
				tfMonsterCounts[i].setText("");
			}
		}
		
		for(MonsterClass mc : MonsterClass.values())
			monsterTypes[mc.value()].setSelected(room.isMonsterTypeAllowed(mc));
		
		return true;
	}
	
	public boolean updateRoom()
	{
		if(!validateRoom())
			return false;
		
		room.setStud(isStud.isSelected());
		
		if(jrBooty[0].isSelected())
		{
			room.setChest(null);
		}
		else if(jrBooty[1].isSelected() || jrBooty[2].isSelected())
		{
			Chest chest = new Chest();
			chest.setGold(Integer.parseInt(tfChestGold.getText().trim()));
			chest.setTrapType(Trap.type(cmbChestTrap.getSelectedIndex()));
			chest.setLockLevel(Byte.parseByte(tfChestTrapLevel.getText().trim()));
			
			chest.setItems(getChestItems());
			chest.setMagicLock(isMagicLock.isSelected());
			room.setChest(chest);
		}
		
		room.setItem(getItem(true, 0));
		
		for(byte i = 0; i < Util.MON_MAXSTACKSIZE; i++)
		{
			Monster tMon = getMonster(tfLairedMonsterName[i]);
			if(tMon != null)
				room.setLairedMonster(tMon, i);
		}
		
		LinkedList<LinkedList<MonsterInstance>> monStacks = new LinkedList<LinkedList<MonsterInstance>>();
		
		for(byte i = 0; i < tfMonsters.length; i++)
		{
			Monster tMon = getMonster(tfMonsters[i]);
			if(tMon != null)
			{
				LinkedList<MonsterInstance> monList = new LinkedList<MonsterInstance>();
				
				for(int j = 0; j < Byte.parseByte(tfMonsterCounts[i].getText().trim()); i++)
					monList.insert(tMon.createInstance());
				
				monStacks.insert(monList);
			}
		}
		
		for(MonsterClass mc : MonsterClass.values())
			room.setMonsterType(mc, monsterTypes[mc.value()].isSelected());
		
		return true;
	}
	
	/**
	 * Validate the data in the room.
	 * @return true if all fields are acceptable.
	 */
	public boolean validateRoom()
	{
		if(tfRoomItem.getText().trim().length() > 0 && !findItem(true, 0))
		{
			JOptionPane.showMessageDialog(this, "Invalid room item.");
			return false;
		}
		
		for(byte i = 0; i < Util.MON_MAXSTACKSIZE; i++)
			if(tfLairedMonsterName[i].getText().trim().length() > 0 && !findMonster(tfLairedMonsterName[i]))
				return false;
		
		for(byte i = 0; i < tfMonsters.length; i++)
		{
			if(!MordorEditor.validityCheckTf(this, tfMonsterCounts[i], 0, Util.MON_MAXSTACKSIZE, "monster count #" + i))
				return false;
			if(tfMonsters[i].getText().trim().length() > 0 && !findMonster(tfMonsters[i]))
				return false;
		}
		
		if(jrBooty[1].isSelected() || jrBooty[2].isSelected())
		{
			for(int i = 0; i < tfChestItems.length; i++)
				if(tfChestItems[i].getText().trim().length() > 0 && !findItem(false, i))
					return false;
			if(!MordorEditor.validityCheckTf(this, tfChestGold, 0, Integer.MAX_VALUE, "chest gold"));
		}

		if(!Util.validityCheckTf(this, tfChestTrapLevel, 1, Byte.MAX_VALUE, "invalid trap level."))
			return false;
		
		return true;
	}
	
	private boolean findMonster(JTextField tfMon)
	{
		return (getMonster(tfMon) != null);
	}
	
	private Monster getMonster(JTextField tfMon)
	{
		if(tfMon.getText().trim().length() < 1)
			return null;
		
		//if((lair && tfLairedMonsterName.getText().trim().length() < 1) || (!lair && tfMonsters[stackNumber].getText().trim().length() < 1))
			//return null;
		
		Monster tMon = dataBank.getMonsterEden().getMonster(tfMon.getText().trim());
		
		if(tMon == null)
		{
			JOptionPane.showMessageDialog(this, "Monster '" + tfMon.getText().trim() + " does not exist.");
			tfMon.setText("");
			return null;
		}
		
		tfMon.setText(tMon.getName());
		return tMon;
	}
	
	private boolean findItem(boolean roomItem, int index)
	{
		return (getItem(roomItem, index) != null);
	}
	
	private ItemInstance getItem(boolean roomItem, int index)
	{
		
		JTextField tf = (roomItem) ? tfRoomItem : tfChestItems[index];

		if(tf.getText().trim().length() < 1)
			return null;
		
		Item tItem = dataBank.getItem(tf.getText().trim());
		
		if(tItem == null)
		{
			if(roomItem)
				JOptionPane.showMessageDialog(this, "Room Item does not exist.");
			else
				JOptionPane.showMessageDialog(this, "Chest Item #" + index + " does not exist.");
			
			tf.setText("");
			return null;
		}
		
		tf.setText(tItem.getName());
		return new ItemInstance(tItem);
	}
	
	/**
	 * Retrieve the link list of all chest items.
	 * @return LinkedList<ItemInstance>
	 */
	private LinkedList<ItemInstance> getChestItems()
	{
		LinkedList<ItemInstance> itemList = new LinkedList<ItemInstance>();
		
		for(int i = 0; i < tfChestItems.length; i++)
		{
			ItemInstance tItem = getItem(false, i);
			if(tItem != null)
				itemList.insert(tItem);
		}
		
		return (itemList.getSize() < 1) ? null : itemList;
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == jrBooty[0])
		{
			if(jrBooty[0].isSelected())
			{
				room.setChest(null);
				tfChestGold.setEnabled(false);
				for(int i = 0; i < tfChestItems.length; i++)
				{
					tfChestItems[i].setEnabled(false);
					jbVerifyCItem[i].setEnabled(false);
					jbBrowseCItem[i].setEnabled(false);
				}
				cmbChestTrap.setEnabled(false);
				tfChestTrapLevel.setEnabled(false);
				isMagicLock.setEnabled(false);
			}
		}
		else if(e.getSource() == jrBooty[1] || e.getSource() == jrBooty[2])
		{
			if(jrBooty[1].isSelected() || jrBooty[2].isSelected())
			{
				room.setChest(new Chest());
				tfChestGold.setEnabled(true);
				for(int i = 0; i < tfChestItems.length; i++)
				{
					tfChestItems[i].setEnabled(true);
					jbVerifyCItem[i].setEnabled(true);
					jbBrowseCItem[i].setEnabled(true);
				}
				cmbChestTrap.setEnabled(true);
				tfChestTrapLevel.setEnabled(true);
				isMagicLock.setEnabled(true);
			}
		}
		else if(e.getSource() == jbBrowseRItem)
		{
			JFrame itemBrowse = new JFrame();
			
			itemBrowse.add(new ListChooserPanel(itemBrowse, tfRoomItem, dataBank, ListChooserPanel.BrowseTypes.items));
			itemBrowse.pack();
			itemBrowse.setVisible(true);
		}
		else if(e.getSource() == jbVerifyRItem)
			findItem(true, 0);
		else if(e.getSource() == jbOK)
		{
			updateRoom();
			parent.dispose();
		}
		else if(e.getSource() == jbCancel)
			parent.dispose();
		else if(e.getSource() == jbMTNone)
			for(int i = 0; i < monsterTypes.length; i++)
				monsterTypes[i].setSelected(false);
		else if(e.getSource() == jbMTAll)
			for(int i = 0; i < monsterTypes.length; i++)
				monsterTypes[i].setSelected(true);
		else if(e.getSource() == jbMTInvert)
			for(int i = 0; i < monsterTypes.length; i++)
				monsterTypes[i].setSelected(!(monsterTypes[i].isSelected()));
		else
		{
			for(byte i = 0; i < jbVerify.length; i++)
			{
				if(e.getSource() == jbVerify[i])
					findMonster(tfMonsters[i]);
				else if(e.getSource() == jbBrowse[i])
				{
					JFrame monBrowse = new JFrame();
					
					monBrowse.add(new ListChooserPanel(monBrowse, tfMonsters[i], dataBank, ListChooserPanel.BrowseTypes.monster));
					monBrowse.pack();
					monBrowse.setVisible(true);
				}
			}
			
			for(int i = 0; i < jbBrowseCItem.length; i++)
			{
				if(e.getSource() == jbBrowseCItem[i])
				{
					JFrame itemBrowse = new JFrame();
					
					itemBrowse.add(new ListChooserPanel(itemBrowse, tfChestItems[i], dataBank, ListChooserPanel.BrowseTypes.items));
					itemBrowse.pack();
					itemBrowse.setVisible(true);
				}
				else if(e.getSource() == jbVerifyCItem[i])
					findItem(false, i);
			}
			
			for(byte i = 0; i < jbLairBrowse.length; i++)
			{
				if(e.getSource() == jbLairBrowse[i])
				{
					JFrame monBrowse = new JFrame();
					
					monBrowse.add(new ListChooserPanel(monBrowse, tfLairedMonsterName[i], dataBank, ListChooserPanel.BrowseTypes.monster));
					monBrowse.pack();
					monBrowse.setVisible(true);
				}
				else if(e.getSource() == jbLairVerify[i])
					findMonster(tfLairedMonsterName[i]);
			}
		}
	}

}
