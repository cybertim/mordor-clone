package mordorGame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import structures.LinkedList;
import structures.ListIter;

import mordorData.DataBank;
import mordorData.MonsterInstance;
import mordorData.Player;
import mordorData.Room;
import mordorEnums.RoomState;
import mordorHelpers.Coord;
import mordorHelpers.Util;
import mordorMessenger.MordorMessengerEvent;
import mordorMessenger.MordorMessengerListener;

public class DungeonFrame extends JInternalFrame implements KeyListener, InternalFrameListener, ActionListener, MordorMessengerListener
{
	private JPanel dungeonPanel;
	private ViewPanel dungeonView;
	private JButton monsterViews[] = new JButton[4];
	private JLabel peaceStatus, chestImage, chestTrapText, chestDifficultyText;
	private JLabel monsterText[] = new JLabel[4];
	private JLabel lastMessage, prevMessage;
	private DungeonMapFrame dungeonMap;
	private Mordor mordor;
	private JLabel playerCoords;
	
	private JButton mvUp, mvDown, mvRight, mvLeft;
	private JButton[] buffers;
	private JButton actPickup, actDrop, actFight, actSpell;
	private JButton actTake, actOpen, actExit;
	
	private PlayerControl player;
	private DataBank dataBank;
	private int monsterAttacking;
	private boolean offerToJoin, chest, playerHitLast;
	private RoomState roomState;
	
	public static final byte MAXBUFFERS = 10;
	
	private Dimension BUTTONSIZE = new Dimension(14, 14);
	private Insets BUTTONINSET = new Insets(1, 2, 1, 2);

	DungeonFrame(PlayerControl nPlayer, DataBank nDataBank, DungeonMapFrame nDungeonMap, Mordor nMordor)
	{
		super("Dungeon", false, false, false, true);
		
		dataBank = nDataBank;
		player = nPlayer;
		dungeonMap = nDungeonMap;
		mordor = nMordor;
		
		JPanel monsterPanel = new JPanel();
		JPanel northPanel = new JPanel();
		JPanel southPanel = new JPanel();
		JPanel movementPanel = new JPanel();
		JPanel bufferPanel = new JPanel();
		JPanel actionPanel = new JPanel();
		
		dungeonPanel = new JPanel();
		dungeonPanel.setFocusable(true);
		dungeonPanel.addKeyListener(this);
	//	dungeonPanel.setLocation(0, 0);
	//	dungeonPanel.setPreferredSize(new Dimension(130, 130));
		dungeonPanel.setLayout(new BorderLayout());
		
		// create dungeon view
		dungeonView = new ViewPanel(player.getPlayer(), dataBank);
	//	dungeonView.setBorder(new BevelBorder(BevelBorder.RAISED));
		dungeonView.setFocusable(false);
		dungeonView.setPreferredSize(new Dimension(130, 130));
	//	dungeonView.setBounds(345, 5, 130, 130);
		
		mvUp = new JButton("\u2191");
//		mvUp.setPreferredSize(BUTTONSIZE);
		mvUp.setBorder(null);
		mvUp.setMargin(BUTTONINSET);
		mvUp.setFocusable(false);
		mvDown = new JButton("\u2193");
//		mvDown.setPreferredSize(BUTTONSIZE);
		mvDown.setBorder(null);
		mvDown.setFocusable(false);
		mvLeft = new JButton("\u2190");
//		mvLeft.setPreferredSize(BUTTONSIZE);
		mvLeft.setBorder(null);
		mvLeft.setFocusable(false);
		mvRight = new JButton("\u2192");
//		mvRight.setPreferredSize(BUTTONSIZE);
		mvRight.setBorder(null);
		mvRight.setFocusable(false);
		
		mvUp.addActionListener(this);
		mvDown.addActionListener(this);
		mvLeft.addActionListener(this);
		mvRight.addActionListener(this);

		movementPanel.add(mvLeft);
		movementPanel.add(mvRight);
		movementPanel.add(mvUp);
		movementPanel.add(mvDown);
		
		buffers = new JButton[MAXBUFFERS];
		for(int i = 0; i < MAXBUFFERS; i++)
		{
			buffers[i] = new JButton("" + i);
			buffers[i].addActionListener(this);
			buffers[i].setPreferredSize(BUTTONSIZE);
			buffers[i].setBorder(null);
			buffers[i].setMargin(BUTTONINSET);
			buffers[i].setFocusable(false);
			bufferPanel.add(buffers[i]);
		}
		
		actPickup= new JButton("Pickup"); 
		actDrop= new JButton("Drop"); 
		actFight= new JButton("Fight");
		actSpell= new JButton("Spell");
		actTake= new JButton("Take"); 
		actOpen= new JButton("Open"); 
		actExit= new JButton("Exit");

	/*	actPickup.setPreferredSize(BUTTONSIZE);
		actDrop.setPreferredSize(BUTTONSIZE);
		actFight.setPreferredSize(BUTTONSIZE);
		actSpell.setPreferredSize(BUTTONSIZE);
		actTake.setPreferredSize(BUTTONSIZE);
		actOpen.setPreferredSize(BUTTONSIZE);
		actExit.setPreferredSize(BUTTONSIZE);*/
		
		actPickup.setBorder(null);
		actDrop.setBorder(null);
		actFight.setBorder(null);
		actSpell.setBorder(null);
		actTake.setBorder(null);
		actOpen.setBorder(null);
		actExit.setBorder(null);

		actPickup.setFocusable(false);
		actDrop.setFocusable(false);
		actFight.setFocusable(false);
		actSpell.setFocusable(false);
		actTake.setFocusable(false);
		actOpen.setFocusable(false);
		actExit.setFocusable(false);

		actPickup.setMargin(BUTTONINSET);
		actDrop.setMargin(BUTTONINSET);
		actFight.setMargin(BUTTONINSET);
		actSpell.setMargin(BUTTONINSET);
		actTake.setMargin(BUTTONINSET);
		actOpen.setMargin(BUTTONINSET);
		actExit.setMargin(BUTTONINSET);
		
		
		actPickup.addActionListener(this); 
		actDrop.addActionListener(this); 
		actFight.addActionListener(this);
		actSpell.addActionListener(this);
		actTake.addActionListener(this); 
		actOpen.addActionListener(this); 
		actExit.addActionListener(this);
		
		actionPanel.add(actPickup); 
		actionPanel.add(actDrop); 
		actionPanel.add(actFight);
		actionPanel.add(actSpell);
		actionPanel.add(actTake); 
		actionPanel.add(actOpen); 
		actionPanel.add(actExit);
		
		playerCoords = new JLabel(player.getCoordString());
		playerCoords.setVisible(true);
//		playerCoords.setBounds(5, 90, 100, 26)
		playerCoords.setFocusable(false);
	//	playerCoords.setBorder(new BevelBorder(BevelBorder.RAISED));
		
		
		dungeonPanel.add(playerCoords, BorderLayout.NORTH);
		dungeonPanel.add(dungeonView, BorderLayout.CENTER);
		dungeonPanel.add(movementPanel, BorderLayout.SOUTH);
		
		/* Monster images, text, messages, chest and peace status */
		monsterPanel.setLayout(new BorderLayout());
		JPanel roomImages = new JPanel();
		roomImages.setLayout(new GridLayout(1, 6));
		peaceStatus = new JLabel("");
		chestImage = new JLabel("");
		roomImages.add(peaceStatus);
		for(int i = 0; i < monsterViews.length; i++)
		{
			monsterViews[i] = new JButton();
			monsterViews[i].setBorder(new BevelBorder(BevelBorder.RAISED));
			monsterViews[i].setFocusable(false);
			monsterViews[i].addActionListener(this);
			monsterViews[i].setPreferredSize(new Dimension(70, 70));
			monsterViews[i].setMaximumSize(new Dimension(70, 70));
//			monsterViews[i].setBounds(5 + (85 * i), 5, 80, 80);
			
			roomImages.add(monsterViews[i]);
		}
		roomImages.add(chestImage);
		
		JPanel roomTextPanel = new JPanel();
		JPanel monsterTextPanel = new JPanel();
		JPanel fightMessagePanel = new JPanel();
		JPanel chestTextPanel = new JPanel();
		roomTextPanel.setLayout(new BorderLayout());
		monsterTextPanel.setLayout(new GridLayout(2, 2));
		fightMessagePanel.setLayout(new GridLayout(2, 1));
		chestTextPanel.setLayout(new GridLayout(2, 1));

		
		for(int i = 0; i < monsterText.length; i++)
		{
			monsterText[i] = new JLabel("");
			monsterTextPanel.add(monsterText[i]);
		}
		
		lastMessage = new JLabel(" ");
		prevMessage = new JLabel(" ");
		fightMessagePanel.add(lastMessage);
		fightMessagePanel.add(prevMessage);
		
		chestTrapText = new JLabel(" ");
		chestDifficultyText = new JLabel(" ");
		chestTextPanel.add(chestTrapText);
		chestTextPanel.add(chestDifficultyText);
		
		roomTextPanel.add(monsterTextPanel, BorderLayout.NORTH);
		roomTextPanel.add(fightMessagePanel, BorderLayout.CENTER);
		roomTextPanel.add(chestTextPanel, BorderLayout.SOUTH);
		
		monsterPanel.add(roomImages, BorderLayout.CENTER);
		monsterPanel.add(roomTextPanel, BorderLayout.SOUTH);
		
		northPanel.setLayout(new BorderLayout());
		northPanel.add(dungeonPanel, BorderLayout.WEST);
		northPanel.add(monsterPanel, BorderLayout.EAST);
		
		southPanel.add(bufferPanel);
		southPanel.add(actionPanel);
		
		this.setLayout(new BorderLayout());
		add(northPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
		addInternalFrameListener(this);
	}
	
	private void updateMonsterViews()
	{
		Room room = dataBank.getMap().getMapSquare(player.getPlayer().getCoord()).getRoom();
		
		// TODO: Update Peace
		int count = 0;
		ListIter<LinkedList<MonsterInstance>> mNode = room.getMonsterStacks().getIterator();
		for(int i = 0; i < Util.MON_MAXSTACKSIZE && count < monsterViews.length; i++)
		{
			if(mNode.next())
			{
				if(mNode.element().getSize() > 0)
				{
					monsterViews[count].setIcon(new ImageIcon(dataBank.getImages().getMonsterImage(mNode.element().getFirst().getMonster().getMonsterImageID()).getScaledInstance(64, 64, 0)));
					String monsText = (count == monsterAttacking) ? " * " : "   ";
					monsText += count + ". " + mNode.element().getSize() + " " + mNode.element().getFirst().getMonster().getName();
					monsterText[count].setText(monsText);
				}
			}
			else
			{
				monsterViews[count].setIcon(null);
				monsterText[count].setText("");
			}
			count++;
		}
	}
	
	private void updateChest()
	{
		// TODO Update chest
	}
	
	/**
	 * Perform updates based on timer events.
	 */
	public void timerUpdate()
	{
		//TODO
		// Update dungeonView if the player is dead.
		// Maybe the state messages should be posted here instead?
	}
	
	public void keyTyped(KeyEvent e)
	{

	}

	public void keyPressed(KeyEvent e) 
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_UP:
				moveUp();
				break;
			case KeyEvent.VK_DOWN:
				moveDown();
				break;
			case KeyEvent.VK_RIGHT:
				moveRight();
				break;
			case KeyEvent.VK_LEFT:
				moveLeft();
				break;
			case KeyEvent.VK_T:
				actionTake();
				break;
			case KeyEvent.VK_O:
				// open the chest
				break;
			case KeyEvent.VK_F:
				// fight friendly enemy
				break;
			case KeyEvent.VK_0:
				// user buffer
				break;
			case KeyEvent.VK_1:
				// user buffer
				break;
			case KeyEvent.VK_2:
				// user buffer
				break;
			case KeyEvent.VK_3:
				// user buffer
				break;
			case KeyEvent.VK_4:
				// user buffer
				break;
			case KeyEvent.VK_5:
				// user buffer
				break;
			case KeyEvent.VK_6:
				// user buffer
				break;
			case KeyEvent.VK_7:
				// user buffer
				break;
			case KeyEvent.VK_8:
				// user buffer
				break;
			case KeyEvent.VK_9:
				// user buffer
				break;
				
		}
	}

	public void keyReleased(KeyEvent e) 
	{

	}

	public void internalFrameActivated(InternalFrameEvent e)
	{
		try
		{
			if(!player.getPlayer().isLost())
			{
				dataBank.getMap().getMapSquare(player.getPlayer().getCoord()).setVisited(true);
				dungeonMap.updatePlayerSquare(player.getPlayer().getCoord());//.playerEnterDungeon();
			}
			dungeonMap.setIcon(false);
		}
		catch(Exception error)
		{
			System.err.println(error);
		}
		
		dungeonPanel.requestFocusInWindow();
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

	public void internalFrameDeiconified(InternalFrameEvent e)
	{
	}

	public void internalFrameIconified(InternalFrameEvent e)
	{
		try
		{
			dungeonMap.setIcon(true);
		}
		catch(Exception error)
		{
			System.err.println(error);
		}
		
		dungeonPanel.requestFocusInWindow();
	}

	public void internalFrameOpened(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == mvUp)
			moveUp();
		else if(e.getSource() == mvDown)
			moveDown();
		else if(e.getSource() == mvRight)
			moveRight();
		else if(e.getSource() == mvLeft)
			moveLeft();
		else if(e.getSource() == actPickup)
			actionPickup();
		else if(e.getSource() == actDrop)
			actionDrop();
		else if(e.getSource() == actFight)
			actionFight();
		else if(e.getSource() == actSpell)
			actionSpell();
		else if(e.getSource() == actTake)
			actionTake();
		else if(e.getSource() == actOpen)
			actionOpen();
		else if(e.getSource() == actExit)
			actionExit();
		else
		{
			for(int i = 0; i < MAXBUFFERS; i++)
			{
				if(e.getSource() == buffers[i])
					actionBuffers(i);
			}
		}
		
		// attack enemy clicked on
	}
	
	private void moveUp()
	{
		/* Player's coordinate before moving. */
		Coord oldCoords = new Coord(player.getPlayer().getCoord());
		/* Coordinate of square player is trying to move into. */
		Coord nextCoords = oldCoords.getNeighbour(oldCoords.getDirection());
		if(player.moveForward())
		{
			// TODO We are not updating the map properly.
			// We should update the square the player was in, the one they were entering, and the
			// one they are in now (if they are not lost and it differs form the entering square)
			
			/* Coordinate the player is now at. */
			Coord newCoords = new Coord(player.getPlayer().getCoord());
			
			/* Update the square the player was in. */
			dungeonMap.updateSquare(oldCoords);
			if(!player.getPlayer().isLost())
			{
				/* Update the square the player was entering. */
				dungeonMap.updateSquare(nextCoords);
				/* Update the square the player actually entered */
				dungeonMap.updatePlayerSquare(newCoords);
			}
			
			/* Update the dungeon view. */
			dungeonView.updateView();
			
			
			if(dataBank.getMap().getMapSquare(oldCoords).getRoom() != dataBank.getMap().getMapSquare(newCoords).getRoom())
			{
				/* In a different room, update the monster views. */
				// TODO: We should track more information.
				updateMonsterViews();
				lastMessage.setText("");
				prevMessage.setText("");
			}
			
			/* Update the coordinate string. */
			playerCoords.setText(player.getCoordString());
		}
	}
	
	private void moveDown()
	{
		player.moveBackwards();
		dungeonView.updateView();
			dungeonMap.updatePlayerSquare(player.getPlayer().getCoord());//.playerTurned();
		playerCoords.setText(player.getCoordString());
	}
	
	private void moveRight()
	{
		player.turnRight();
		dungeonView.updateView();
		if(!player.getPlayer().isLost())
			dungeonMap.updatePlayerSquare(player.getPlayer().getCoord());
		playerCoords.setText(player.getCoordString());
	}
	
	private void moveLeft()
	{
		player.turnLeft();
		dungeonView.updateView();
		if(!player.getPlayer().isLost())
			dungeonMap.updatePlayerSquare(player.getPlayer().getCoord());
		playerCoords.setText(player.getCoordString());
	}
	
	private void actionPickup()
	{
		
	}
	
	private void actionDrop()
	{
		
	}
	
	private void actionFight()
	{
		
	}
	
	private void actionSpell()
	{
		
	}
	
	private void actionTake()
	{
		Coord oldCoords = new Coord(player.getPlayer().getCoord());
		if(player.takeStairs(mordor))
		{
			if(!player.getPlayer().isLost())
			{
				dungeonMap.updateSquare(oldCoords);
				dungeonMap.updatePlayerSquare(player.getPlayer().getCoord());
			}
			dungeonView.updateView();
			playerCoords.setText(player.getCoordString());
		}
	}

	private void actionOpen()
	{
		
	}
	
	private void actionExit()
	{
		if(JOptionPane.showConfirmDialog(this, "Exit Mordor?") == JOptionPane.YES_OPTION)
		{
			// dataBank.saveData();
			System.exit(0);
		}
	}
	
	private void actionBuffers(int index)
	{
		
	}
	
	private void checkPeace()
	{
		// What to base peace on?
		// same alignment as player, are humanoid. stats/level
	}
	
	private void updateFight()
	{
		if(roomState == RoomState.Peace)
			return;
		
		if(playerHitLast)
		{
			// Monsters get a hit.
			// If they have spells, randomly roll if one is cast
			// Also, roll dice on thieve, and roll dice on runaway (runaway should increase if thieved)
		}
		else
		{
			// Players/companions get a hit
			// If player is using a spell, use chosen spell unless it is not an attacking spell, else use weapon
		}
		
		// Do an update to kill of dead.
	}
	
	private byte getBattleBalance()
	{
		// rate the balance between the player & monsters
		return 50;
	}

	public void messagePosted(MordorMessengerEvent message)
	{
		prevMessage.setText(lastMessage.getText());
		lastMessage.setText(message.getMessage());
	}
}
