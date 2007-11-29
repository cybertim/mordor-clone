package mordorGame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import mordorData.DataBank;
import mordorData.Player;

/**
 * When mordor finishes loading, this is the first screen to appear.
 * @author August Junkala (Sept 13, 2007)
 *
 */
public class StartingScreen extends JInternalFrame implements ActionListener, InternalFrameListener
{
	private JButton jbNewPlayer, jbLoadPlayer, jbExchangeItems, jbChangePassword, jbDeletePlayer;
	private JButton jbPlayerList, jbHallOfRecords;
	private JButton jbHelpLesson, jbOptions, jbExit, jbAbout;
	
	private DataBank dataBank;
	private Mordor parent;
	private Player currentPlayer;
	
	StartingScreen(DataBank nDataBank, Mordor nParent)
	{
		super("", false, false, false, false);
		
		dataBank = nDataBank;
		parent = nParent;
		
		JPanel rightPane, centrePane, leftPane;
		rightPane = new JPanel();
		centrePane = new JPanel();
		leftPane = new JPanel();
		
		jbNewPlayer = new JButton("New Player");
		jbLoadPlayer = new JButton("Load Player");
		jbExchangeItems = new JButton("Exchange Items");
		jbChangePassword = new JButton("Change Password");
		jbDeletePlayer = new JButton("Delete Player");
		
		jbPlayerList =  new JButton("Character list");
		jbHallOfRecords = new JButton("Hall of Records");
		
		jbHelpLesson = new JButton("HelpLesson");
		jbOptions = new JButton("Options");
		jbExit = new JButton("Exit");
		jbAbout = new JButton("About");
		
		jbNewPlayer.setToolTipText("Create a new character.");
		jbLoadPlayer.setToolTipText("Load a character.");
		jbExchangeItems.setToolTipText("Exchange items between players.");
		jbChangePassword.setToolTipText("Change player's password.");
		jbDeletePlayer.setToolTipText("Delete this player.");
		jbPlayerList.setToolTipText("Open character list.");
		jbHallOfRecords.setToolTipText("View the Hall of Records.");
		jbHelpLesson.setToolTipText("Start the HelpLesson.");
		jbOptions.setToolTipText("Set options.");
		jbExit.setToolTipText("Exit Mordor");
		jbAbout.setToolTipText("About Mordor.");

		jbNewPlayer.addActionListener(this);
		jbLoadPlayer.addActionListener(this);
		jbExchangeItems.addActionListener(this);
		jbChangePassword.addActionListener(this);
		jbDeletePlayer.addActionListener(this);
		jbPlayerList.addActionListener(this);
		jbHallOfRecords.addActionListener(this);
		jbHelpLesson.addActionListener(this);
		jbOptions.addActionListener(this);
		jbExit.addActionListener(this);
		jbAbout.addActionListener(this);

		JPanel playerPane = new JPanel();
		JPanel mordorPane = new JPanel();
		playerPane.setLayout(new GridLayout(5, 1));
		mordorPane.setLayout(new GridLayout(2, 2));
		playerPane.add(jbNewPlayer);
		playerPane.add(jbLoadPlayer);
		playerPane.add(jbExchangeItems);
		playerPane.add(jbChangePassword);
		playerPane.add(jbDeletePlayer);

		mordorPane.add(jbHelpLesson);
		mordorPane.add(jbOptions);
		mordorPane.add(jbExit);
		mordorPane.add(jbAbout);
		
		centrePane.setLayout(new BorderLayout());
		centrePane.add(playerPane, BorderLayout.CENTER);
		centrePane.add(mordorPane, BorderLayout.SOUTH);
		
		leftPane.setLayout(new BorderLayout());
		leftPane.add(jbPlayerList, BorderLayout.CENTER);
		rightPane.setLayout(new BorderLayout());
		rightPane.add(jbHallOfRecords, BorderLayout.CENTER);
		
		JPanel fullPanel = new JPanel();
		
		fullPanel.add(leftPane);
		fullPanel.add(centrePane);
		fullPanel.add(rightPane);

		updatePlayer(null);
		
		add(fullPanel);
	}
	
	/**
	 * Updates the current loaded player.
	 * @param newCurrentPlayer
	 */
	public void updatePlayer(Player newCurrentPlayer)
	{
		currentPlayer = newCurrentPlayer;
		
		if(dataBank.getPlayers().getSize() < 2)
			jbExchangeItems.setEnabled(false);
		else
			jbExchangeItems.setEnabled(true);
			
		
		if(currentPlayer == null)
		{
			jbNewPlayer.setText("New Character");
			jbLoadPlayer.setText("Load Character");
			jbChangePassword.setEnabled(false);
			jbDeletePlayer.setEnabled(false);
			
			jbNewPlayer.setToolTipText("Create a new character.");
			jbLoadPlayer.setToolTipText("Load a character.");
		}
		else
		{
			jbNewPlayer.setText("Run " + currentPlayer.getName());
			jbLoadPlayer.setText("Unload Character");
			jbChangePassword.setEnabled(true);
			jbDeletePlayer.setEnabled(true);
			
			jbNewPlayer.setToolTipText("Play this character.");
			jbLoadPlayer.setToolTipText("Unload this character.");
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		if(e.getSource() == jbNewPlayer)
		{
			if(currentPlayer == null)
			{
				JFrame playerBuild = new JFrame();
				playerBuild.setContentPane(new NewPlayer(this, playerBuild, dataBank));
				playerBuild.pack();
				playerBuild.setVisible(true);
			}
			else
				parent.startMordor(currentPlayer);
		}
		else if(e.getSource() == jbLoadPlayer)
		{
			if(currentPlayer != null)
				updatePlayer(null);
			else
			{
				if(dataBank.getPlayers().getSize() > 0)
				{
					String playerName = (String)JOptionPane.showInputDialog(this, "Choose player", "Player", JOptionPane.PLAIN_MESSAGE, null, dataBank.getPlayerNames(), 0);
					updatePlayer(dataBank.getPlayer(playerName));
				}
				else
					JOptionPane.showMessageDialog(this, "No players to load.");
			}
		}
		else if(e.getSource() == jbExchangeItems)
		{
			JDialog exchangeDialog = new JDialog(this.parent, "Item Exchange", true);
			exchangeDialog.setContentPane(new ExchangeItems(exchangeDialog, dataBank));
			exchangeDialog.pack();
			exchangeDialog.setVisible(true);
		}
		else if(e.getSource() == jbChangePassword)
		{
			
		}
		else if(e.getSource() == jbDeletePlayer)
		{
			dataBank.removePlayer(currentPlayer);
			updatePlayer(null);
		}
		else if(e.getSource() == jbPlayerList)
		{
			JDialog playerDialog = new JDialog(this.parent, "Player List", true);
			playerDialog.setContentPane(new PlayerList(dataBank.getPlayers()));
			playerDialog.pack();
			playerDialog.setVisible(true);
		}
		else if(e.getSource() == jbHallOfRecords)
		{
			JDialog horDialog = new JDialog(this.parent, "Hall of Records", true);
			horDialog.setContentPane(new HallOfRecords(horDialog, dataBank.getPlayers()));
			horDialog.pack();
			horDialog.setVisible(true);
		}
		else if(e.getSource() == jbHelpLesson)
		{
			
		}
		else if(e.getSource() == jbOptions)
		{
			
		}
		else if(e.getSource() == jbExit)
		{
			parent.quitMordor();
		}
		else if(e.getSource() == jbAbout)
		{
			JOptionPane.showMessageDialog(this, parent.aboutMordor);
		}
	}

	public void internalFrameActivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void internalFrameClosed(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void internalFrameClosing(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void internalFrameDeactivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void internalFrameDeiconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void internalFrameIconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void internalFrameOpened(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

}
