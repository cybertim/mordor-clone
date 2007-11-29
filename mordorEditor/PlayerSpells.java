package mordorEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mordorData.SpellBook;
import mordorData.SpellReference;
import mordorHelpers.Util;
import mordorEnums.SpellClass;

public class PlayerSpells extends JPanel implements ActionListener, ListSelectionListener
{
	private PlayerPanel parent;
	private SpellBook spellbook;
	
	private JButton jbAddSpell, jbRemoveSpell;
	private JList jlSpellTypes, jlMasterSpells,  jlPlayerSpells;
	private DefaultListModel masterSpells, playerSpells;
	
	public PlayerSpells(PlayerPanel nParent)
	{
		parent = nParent;
		
		jlSpellTypes =  new JList(SpellClass.values());
		masterSpells = new DefaultListModel();
		playerSpells = new DefaultListModel();
		
		masterSpells.addElement(Util.NOSTRING);
		playerSpells.addElement(Util.NOSTRING);
		
		jlMasterSpells = new JList(masterSpells);
		jlPlayerSpells = new JList(playerSpells);
		
		jlSpellTypes.setVisibleRowCount(12);
		jlMasterSpells.setVisibleRowCount(10);
		jlPlayerSpells.setVisibleRowCount(10);
		
		jlSpellTypes.addListSelectionListener(this);
		
		jbAddSpell = new JButton("Add ->");
		jbRemoveSpell = new JButton("<- Remove");
		
		jbAddSpell.addActionListener(this);
		jbRemoveSpell.addActionListener(this);
		
		jbAddSpell.setToolTipText("Add spell to player's spellbook.");
		jbRemoveSpell.setToolTipText("Remove spell from player's spellbook.");
		
		JPanel masterPane = new JPanel();
		JPanel playerPane = new JPanel();
		masterPane.setLayout(new BorderLayout());
		playerPane.setLayout(new BorderLayout());
		
		masterPane.add(jbAddSpell, BorderLayout.NORTH);
		masterPane.add(new JScrollPane(jlMasterSpells), BorderLayout.CENTER);
		
		playerPane.add(jbRemoveSpell, BorderLayout.NORTH);
		playerPane.add(new JScrollPane(jlPlayerSpells), BorderLayout.CENTER);
		
		add(new JScrollPane(jlSpellTypes));
		add(masterPane);
		add(playerPane);
	}
	
	public boolean updatePanel()
	{
		spellbook = parent.currentPlayer.getSpellBook();
		changeSpellType();
		
		return true;
	}
	
	public boolean updateSpellBook(boolean alreadyValidated)
	{	
		if(!alreadyValidated && !validateSpellBook())
			return false;
		
		parent.currentPlayer.setSpellBook(spellbook);
		
		return true;
	}
	
	public boolean validateSpellBook()
	{
		return true;
	}
	
	private void changeSpellType()
	{
		String[] masterSpellNames = parent.dataBank.getSpellBook().getSpellClassNames((SpellClass)jlSpellTypes.getSelectedValue());
		String[] playerSpellNames = spellbook.getSpellClassNames((SpellClass)jlSpellTypes.getSelectedValue());
		
		masterSpells.removeAllElements();
		playerSpells.removeAllElements();
		
		if(masterSpellNames != null)
			for(byte i = 0; i < masterSpellNames.length; i++)
				masterSpells.addElement(masterSpellNames[i]);
		else
			masterSpells.addElement(Util.NOSTRING);
		
		if(playerSpellNames != null)
			for(byte i = 0; i < playerSpellNames.length; i++)
				if(parent.dataBank.getSpellBook().getSpell(playerSpellNames[i]) != null) // Make sure the spell exists.
					playerSpells.addElement(playerSpellNames[i]);
		else
			playerSpells.addElement(Util.NOSTRING);
	}
	
	/**
	 * Updates any lists (the masterSpells and playerSpells lists basically)
	 */
	public void updateLists()
	{
		changeSpellType();
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == jbAddSpell)
		{
			Object[] selectedSpellNames = jlMasterSpells.getSelectedValues();
			
			if(playerSpells.getSize() == 1)
				playerSpells.removeElement(Util.NOSTRING);
			
			for(byte i = 0; i < selectedSpellNames.length; i++)
			{
				SpellReference tSpell = parent.dataBank.getSpellBook().getSpell((String)selectedSpellNames[i]);
				
				if(spellbook.insertSpell(tSpell))
					playerSpells.addElement(selectedSpellNames[i]);
			}
			
		}
		else if(e.getSource() == jbRemoveSpell)
		{
			Object[] selectedSpellNames = jlPlayerSpells.getSelectedValues();
			for(byte i = 0; i < selectedSpellNames.length; i++)
			{
				SpellReference tSpell = parent.dataBank.getSpellBook().getSpell((String)selectedSpellNames[i]);
				playerSpells.removeElement(selectedSpellNames[i]);
				spellbook.removeSpell(tSpell);
			}
			
			if(playerSpells.getSize() == 0)
				playerSpells.addElement(Util.NOSTRING);
		}
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if(e.getSource() == jlSpellTypes)
			changeSpellType();
	}
}