package mordorEditor;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import mordorData.DataBank;
import mordorData.Player;
import mordorHelpers.Util;

/**
 * 
 */

/**
 * @author August Junkala
 * Panel for players used in Mordor Editor
 * July 15 2007
 *
 */
public class PlayerPanel extends JPanel implements ActionListener
{
    public DataBank dataBank;
    public Player currentPlayer;
    private JPanel topBar;
    
    private JButton jbAdd, jbUpdate, jbRemove, jbUndo, jbName;
    private JComboBox jcPlayerList;
    
    private JTabbedPane jtpPlayerData;
    
    private PlayerGeneral generalPanel;
    private PlayerBank bankPanel;
    private PlayerItems itemsPanel;
    private PlayerCompanions compPanel;
    private PlayerGuildsSuper guildPanel;
    private PlayerSpells spellPanel;
    
    PlayerPanel(DataBank nDataBank)
    {
        dataBank = nDataBank;
        
        generalPanel = new PlayerGeneral(this);
        bankPanel = new PlayerBank(this);
        itemsPanel = new PlayerItems(this);
        compPanel = new PlayerCompanions(this);
        guildPanel = new PlayerGuildsSuper(this);
        spellPanel = new PlayerSpells(this);
        
        topBar = new JPanel();
        
        jcPlayerList = new JComboBox(Util.NOSTRINGARRAY);
        jcPlayerList.addActionListener(this);
        jcPlayerList.setToolTipText("Choose player to edit.");
        
        jbAdd = new JButton("Add");
        jbUpdate = new JButton("Update");
        jbRemove = new JButton("Remove");
        jbUndo = new JButton("Undo");
        jbName = new JButton("Name: " + Util.NOSTRING);
        
        jbAdd.addActionListener(this);
        jbUpdate.addActionListener(this);
        jbRemove.addActionListener(this);
        jbUndo.addActionListener(this);
        jbName.addActionListener(this);
        
        jbAdd.setToolTipText("Add a new player.");
        jbUpdate.setToolTipText("Update this player.");
        jbRemove.setToolTipText("Remove this player.");
        jbUndo.setToolTipText("Undo changes made.");
        jbName.setToolTipText("Change player's name.");
        
        topBar.add(jbName);
        topBar.add(jbAdd);
        topBar.add(jbUpdate);
        topBar.add(jbRemove);
        topBar.add(jbUndo);
        
        jtpPlayerData = new JTabbedPane();
        
     //   JScrollPane guildSPane = new JScrollPane(guildPanel);
        
        jtpPlayerData.addTab("General", generalPanel);
        jtpPlayerData.addTab("Bank", bankPanel);
        jtpPlayerData.addTab("Items", itemsPanel);
        jtpPlayerData.addTab("Companions", compPanel);
        jtpPlayerData.addTab("Guilds", guildPanel);
        jtpPlayerData.addTab("Spells", spellPanel);
        
        topBar.add(jcPlayerList);
        
        this.setLayout(new BorderLayout());
        add(topBar, BorderLayout.NORTH);
        add(jtpPlayerData, BorderLayout.CENTER);
        
        updatePlayerList();
    }
    
    public boolean updatePlayer()
    {
    	// validate text fields
    	if(!validatePlayer())
    		return false;
    	// if fail, report, 
    	// else update values in databank
    	generalPanel.updatePlayer(true);
    	bankPanel.updatePlayer(true);
    	itemsPanel.updatePlayer(true);
    	compPanel.updatePlayer(true);
    	guildPanel.updateGuilds(true);
    	spellPanel.updateSpellBook(true);
    	
        return true;
    }
    
    private boolean updatePanel()
    {
    	// if jcPlayerList has no entires, create a new player
    	// set player based on what is selected
    	// update fields
    	generalPanel.updatePanel();
    	bankPanel.updatePanel();
    	itemsPanel.updatePanel();
    	compPanel.updatePanel();
    	guildPanel.updatePanel();
    	spellPanel.updatePanel();
        return true;
    }
  
    public boolean updatePlayerList()
    {
    	topBar.remove(jcPlayerList);
    	String[] names = dataBank.getPlayerNames();
    	
    	if(names == null)
    	{
    		//currentPlayer = 
    		dataBank.newPlayer();
    		names = dataBank.getPlayerNames();
    	}
    	jcPlayerList = new JComboBox(names);
    	

        jcPlayerList.addActionListener(this);
        jcPlayerList.setToolTipText("Choose player to edit.");
        
        topBar.add(jcPlayerList);
        
        jcPlayerList.setSelectedIndex(findPlayerListIndex());
        currentPlayer = dataBank.getPlayer((String)jcPlayerList.getSelectedItem());
        
        jbName.setText("Name: " + currentPlayer.getName());
        updatePanel();

		revalidate();
		repaint();
    	
    	return true;
    }
    
    /**
     * Finds the index of the player set to current player.
     * If no current player, or if the player was no found, returns 0
     * @return	short
     */
    private int findPlayerListIndex()
    {
    	if(currentPlayer == null)
    		return 0;
    	
    	for(int i = 0; i < jcPlayerList.getItemCount(); i++)
    	{
    		if(jcPlayerList.getItemAt(i) != null && currentPlayer.getName().equalsIgnoreCase((String)jcPlayerList.getItemAt(i)))
    			return i;
    	}
    	
    	return 0;
    }
    
    private boolean validatePlayer()
    {
    	// remember, Unnamed is not a valid name
    	if(!generalPanel.validData())
    		return false;
    	if(!bankPanel.validateBankAccount())
    		return false;
    	if(!itemsPanel.validateItems())
    		return false;
    	if(!compPanel.validateCompanions())
    		return false;
    	if(!guildPanel.validateGuilds())
    		return false;
    	if(!spellPanel.validateSpellBook())
    		return false;
    	
        return true;
    }
    
    public boolean addPlayer()
    {
    	// TODO a flag so don't do player updates on add?
    	updatePlayer();
    	Player tPlayer = dataBank.newPlayer();
    	if(tPlayer == null)
    		return false;
    	else
    	{
    		currentPlayer = tPlayer;
        	updatePlayerList();
            return true;
    	}
    }
    
    public boolean removePlayer()
    {
    	dataBank.removePlayer(currentPlayer);
    	currentPlayer = null;
    	updatePlayerList();
    	return true;
    }
    
    /**
     * Adjusts any data that may have changed in other panels.
     */
    public void updateLists()
    {
    	generalPanel.updateLists();
    	bankPanel.updateLists();
    	itemsPanel.updateLists();
    	compPanel.updateLists();
    	guildPanel.updateLists();
    	spellPanel.updateLists();
    }
    
    public void actionPerformed(ActionEvent e)
    {
        // TODO Auto-generated method stub
        if(e.getSource() == jbAdd)
        {
            if(!addPlayer())
            	JOptionPane.showMessageDialog(this, "Player could not be created.");
        }
        else if(e.getSource() == jbUpdate)
        {
            updatePlayer();
        }
        else if(e.getSource() == jbRemove)
        {
            removePlayer();
        }
        else if(e.getSource() == jbUndo)
        {
        	updatePanel();
        }
        else if(e.getSource() == jcPlayerList)
        {
        	if(((String)jcPlayerList.getSelectedItem()).equalsIgnoreCase(Util.NOSTRING))
        		return;
        	
        	currentPlayer = dataBank.getPlayer((String)jcPlayerList.getSelectedItem());
        	updatePanel();
        }
        else if(e.getSource() == jbName)
        {
        	while(true)
			{
				String newName = JOptionPane.showInputDialog("New name:", currentPlayer.getName());
				if(newName == null || newName.length() < 2)
					break;
				else if(!dataBank.validPlayerName(newName))
					JOptionPane.showMessageDialog(this, "Invalid name.");
				else
				{
					currentPlayer.setName(newName);
					jbName.setText("Name: " + newName);
					updatePlayerList();
					break;
				}
			}
        }
    }

}
