package mordorEditor;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mordorData.Companion;
import mordorData.DataBank;
import mordorData.Monster;
import mordorData.Player;

public class PlayerCompanions extends JPanel implements ActionListener
{
	private DataBank dataBank;
	private PlayerPanel parent;
	private JTextField[] tfMonsters, tfMonHits, tfMonMaxHits;
	private JButton[] jbVerify, jbBrowse;
	
	public PlayerCompanions(PlayerPanel nParent)
	{
		dataBank = nParent.dataBank;
		parent = nParent;
		
		tfMonsters = new JTextField[Player.MAXCOMPANIONS];
		jbVerify = new JButton[Player.MAXCOMPANIONS];
		jbBrowse = new JButton[Player.MAXCOMPANIONS];
		tfMonHits = new JTextField[Player.MAXCOMPANIONS];
		tfMonMaxHits = new JTextField[Player.MAXCOMPANIONS];
		
		JPanel monsterPanel = new JPanel();
		monsterPanel.setLayout(new GridLayout(Player.MAXCOMPANIONS, 1));
		
		for(byte i = 0; i < Player.MAXCOMPANIONS; i++)
		{
			JPanel tPanel = new JPanel();
			tfMonsters[i] = new JTextField(10);
			jbVerify[i] = new JButton("Verify");
			jbBrowse[i] = new JButton("Browse");
			tfMonHits[i] = new JTextField(3);
			tfMonMaxHits[i] = new JTextField(3);
			
			tfMonsters[i].addActionListener(this);
			jbVerify[i].addActionListener(this);
			jbBrowse[i].addActionListener(this);
			tfMonHits[i].addActionListener(this);
			tfMonMaxHits[i].addActionListener(this);

			tfMonsters[i].setToolTipText("Name of monster.");
			jbVerify[i].setToolTipText("Verify monster entered.");
			jbBrowse[i].setToolTipText("Browse monsters to find one.");
			tfMonHits[i].setToolTipText("Current hits.");
			tfMonMaxHits[i].setToolTipText("Current maximum monster hits");
			
			
			tPanel.add(new JLabel("Companion #" + i));
			tPanel.add(tfMonsters[i]);
			tPanel.add(jbVerify[i]);
			tPanel.add(jbBrowse[i]);
			tPanel.add(new JLabel("Cur. Hits:"));
			tPanel.add(tfMonHits[i]);
			tPanel.add(new JLabel("Max Hits:"));
			tPanel.add(tfMonMaxHits[i]);
			monsterPanel.add(tPanel);
		}
		
		add(monsterPanel);
	}
	
	private boolean findMonster(byte monIndex)
	{
		return (getMonster(monIndex) != null);
	}
	
	private Monster getMonster(byte monIndex)
	{
		if(tfMonsters[monIndex].getText().trim().length() < 1)
			return null;
		
		// find the monster
		Monster tMonster = dataBank.getMonsterEden().getMonster(tfMonsters[monIndex].getText().trim());
		
		// If the monster does not exist, let the user know.
		if(tMonster == null)
		{
			JOptionPane.showMessageDialog(this, "Monster #" + (monIndex + 1) + " does not exist.");
			tfMonsters[monIndex].setText("");
			return null;
		}
		
		// adjust the monster name
		tfMonsters[monIndex].setText(tMonster.getName());
		return tMonster;
	}
	
	private void browseMonster(byte monIndex)
	{
		JFrame monBrowse = new JFrame();
		
		monBrowse.add(new ListChooserPanel(monBrowse, tfMonsters[monIndex], dataBank, ListChooserPanel.BrowseTypes.monster));
		monBrowse.pack();
		monBrowse.setVisible(true);
	}
	
	public boolean updatePlayer(boolean alreadyValid)
	{
		if(!alreadyValid && !validateCompanions())
			return false;
		
		Companion[] companions = parent.currentPlayer.getCompanions();
		
		for(byte i = 0; i < companions.length; i++)
		{
			Monster tMonster = getMonster(i);
			
			if(tMonster != null)
			{
				companions[i] = new Companion(tMonster.createInstance(), (byte)1);
				companions[i].setHits((short)Short.parseShort(tfMonHits[i].getText().trim()));
				companions[i].setMaxHits((short)Short.parseShort(tfMonMaxHits[i].getText().trim()));
			}
		}
		
		parent.currentPlayer.setCompanions(companions);
		
		return true;
	}
	
	public boolean updatePanel()
	{
		Companion[] companions = parent.currentPlayer.getCompanions();
		
		for(byte i = 0; i < companions.length; i++)
		{
			if(companions[i] != null && dataBank.getMonsterEden().getMonster(companions[i].getMonsterID()) != null)
			{
				tfMonsters[i].setText(companions[i].getMonster().getName());
				tfMonHits[i].setText("" + companions[i].getHits());
				tfMonMaxHits[i].setText("" + companions[i].getMaxhits());
			}
			else
			{
				tfMonsters[i].setText("");
				tfMonHits[i].setText("");
				tfMonMaxHits[i].setText("");
			}
		}
		
		return true;
	}
	
	public boolean validateCompanions()
	{
		for(byte i = 0; i < Player.MAXCOMPANIONS; i++)
		{
			if(tfMonsters[i].getText().trim().length() > 0)
			{
				Monster tMonster = getMonster(i);
				
				if(tMonster == null)
					return false;
	
				short max = (short)(tMonster.getAvgHits() * 1.1);
				short min = (short)(tMonster.getAvgHits() * 0.9);
				if(!MordorEditor.validityCheckTf(this, tfMonMaxHits[i], min, max, "max hits of companion #" + (i+1)))
					return false;
				
				max = Short.parseShort(tfMonMaxHits[i].getText().trim());
				
				if(!MordorEditor.validityCheckTf(this, tfMonHits[i], 0, max, "current hits of companion #" + (i + 1)))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Adjust any data that may have changed in other panels. E.g.
	 * remove companions of types that no longer exist.
	 */
	public void updateLists()
	{
		for(byte i = 0; i < tfMonsters.length; i++)
			findMonster(i);
	}

	public void actionPerformed(ActionEvent e)
	{
		for(byte i = 0; i < Player.MAXCOMPANIONS; i++)
		{
			if(e.getSource() == jbVerify[i])
			{
				findMonster(i);
				Monster tMonster = getMonster(i);
				tfMonHits[i].setText("" + tMonster.getAvgHits());
				tfMonMaxHits[i].setText("" + (short)(tMonster.getAvgHits() * 1.1));
			}
			else if(e.getSource() == jbBrowse[i])
				browseMonster(i);
		}

	}

}
