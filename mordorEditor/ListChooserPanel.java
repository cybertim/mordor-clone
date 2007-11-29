package mordorEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Scrollable;

import mordorData.DataBank;
import mordorEnums.ItemTypes;
import mordorEnums.MonsterClass;
import mordorEnums.SpellClass;
import mordorHelpers.Util;

public class ListChooserPanel extends JPanel implements Scrollable, ActionListener
{
	private JButton jbOK, jbCancel, jbNone;
	private JComboBox jcbFirstList, jcbSecondList;
	private JFrame parent;
	private JTextField tfName;
	private JLabel jlName;
	private JButton jbName;
	private DataBank dataBank;
	private boolean bothListsActive;
	private ModTypes modType;
	private BrowseTypes browseType;
	
	private enum ModTypes
	{
		textField, label, buttonText;
	}
	
	public enum BrowseTypes
	{
		monster, spells, guilds, items, races, players;
	}
	
	public ListChooserPanel(JFrame nParent, JTextField ntfName, DataBank nDataBank, BrowseTypes nBrowseType)
	{
		parent = nParent;
		tfName = ntfName;
		dataBank = nDataBank;
		browseType = nBrowseType;
		modType = ModTypes.textField;

		initializePanel();
	}
	
	public ListChooserPanel(JFrame nParent, JLabel njlName, DataBank nDataBank, BrowseTypes nBrowseType)
	{
		parent = nParent;
		jlName = njlName;
		dataBank = nDataBank;
		browseType = nBrowseType;
		modType = ModTypes.label;

		initializePanel();
	}
	
	public ListChooserPanel(JFrame nParent, JButton njbName, DataBank nDataBank, BrowseTypes nBrowseType)
	{
		parent = nParent;
		jbName = njbName;
		dataBank = nDataBank;
		browseType = nBrowseType;
		modType = ModTypes.buttonText;

		initializePanel();
	}
	
	private void initializePanel()
	{
		JPanel topBar = new JPanel();
		JPanel buttonBar = new JPanel();
		
		switch(browseType)
		{
		case monster:
			bothListsActive = true;
			jcbFirstList = new JComboBox(MonsterClass.values());
			jcbFirstList.setPrototypeDisplayValue("1234567890");
			jcbFirstList.addActionListener(this);
			jcbSecondList = new JComboBox(dataBank.getMonsterEden().getMonsterNamesByClass(MonsterClass.type((byte)0)));
			jcbSecondList.setPrototypeDisplayValue("1234567890");
			break;
		case spells:
			bothListsActive = true;
			jcbFirstList = new JComboBox(SpellClass.values());
			jcbFirstList.setPrototypeDisplayValue("1234567890");
			jcbFirstList.addActionListener(this);
			jcbSecondList = new JComboBox(dataBank.getSpellBook().getSpellClassNames(SpellClass.Fire));//.getSpellNames());
			jcbSecondList.setPrototypeDisplayValue("1234567890");
			break;
		case guilds:
			bothListsActive = false;
			jcbFirstList = new JComboBox(dataBank.getGuildNames());
			jcbFirstList.setPrototypeDisplayValue("1234567890");
			break;
		case items:
			bothListsActive = true;
			jcbFirstList = new JComboBox(ItemTypes.values());
			jcbFirstList.setPrototypeDisplayValue("1234567890");
			jcbFirstList.addActionListener(this);
			jcbSecondList = new JComboBox(dataBank.getItemNamesInClass(ItemTypes.type(0)));
			jcbSecondList.setPrototypeDisplayValue("1234567890");
			break;
		case races:
			bothListsActive = false;
			jcbFirstList = new JComboBox(dataBank.getRaceNames());
			jcbFirstList.setPrototypeDisplayValue("1234567890");
			break;
		case players:
			bothListsActive = false;
			jcbFirstList = new JComboBox(dataBank.getPlayerNames());
			jcbFirstList.setPrototypeDisplayValue("1234567890");
			break;
		}
		jbOK = new JButton("OK");
		jbCancel = new JButton("Cancel");
		jbNone = new JButton("None");
		
		jbOK.addActionListener(this);
		jbCancel.addActionListener(this);
		jbNone.addActionListener(this);
		
		jbNone.setToolTipText("Set to none.");
		
		
		topBar.add(jcbFirstList);
		if(bothListsActive)
			topBar.add(jcbSecondList);
		buttonBar.add(jbOK);
		buttonBar.add(jbCancel);
		buttonBar.add(jbNone);
		
		setLayout(new BorderLayout());
		this.add(topBar, BorderLayout.NORTH);
		this.add(buttonBar, BorderLayout.CENTER);
	}

	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return null;
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

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == jbOK)
		{
			if(bothListsActive)
			{
				String newText = (String)jcbSecondList.getSelectedItem();
				if(newText.trim().equalsIgnoreCase(Util.NOSTRING))
					return;
				else
				{
					switch(modType)
					{
					case textField:
						tfName.setText(newText);
						break;
					case label:
						jlName.setText(newText);
						break;
					case buttonText:
						jbName.setText(newText);
					}
				}
			}
			else
			{
				switch(modType)
				{
				case textField:
					tfName.setText((String)jcbFirstList.getSelectedItem());
					break;
				case label:
					jlName.setText((String)jcbFirstList.getSelectedItem());
					break;
				case buttonText:
					jbName.setText((String)jcbFirstList.getSelectedItem());
					break;
				}
			}
			parent.dispose();
		}
		else if(e.getSource() == jbCancel)
		{
			parent.dispose();
		}
		else if(e.getSource() == jbNone)
		{
			switch(modType)
			{
			case textField:
				tfName.setText("");
				break;
			case label:
				jlName.setText("");
				break;
			case buttonText:
				jbName.setText(Util.NOSTRING);
				break;
			}
			parent.dispose();
		}
		else if(e.getSource() == jcbFirstList)
		{
			switch(browseType)
			{
			case monster:
				changeSecondList(dataBank.getMonsterEden().getMonsterNamesByClass((MonsterClass)jcbFirstList.getSelectedItem()));
				break;
			case spells:
				changeSecondList(dataBank.getSpellBook().getSpellClassNames((SpellClass)jcbFirstList.getSelectedItem()));//.getSpellNames());
				break;
			case items:
				changeSecondList(dataBank.getItemNamesInClass(ItemTypes.type(jcbFirstList.getSelectedIndex())));
				break;
			}
		}
	}
	
	private void changeSecondList(String[] newNames)
	{
		if(newNames == null || newNames.length < 1)
		{
			newNames = new String[1];
			newNames[0] = Util.NOSTRING;
		}

		jcbSecondList.setModel(new DefaultComboBoxModel(newNames));
	}
}
