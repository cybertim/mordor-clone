package mordorGame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import mordorData.BankAccount;
import mordorData.DataBank;
import mordorData.Player;
import mordorEnums.PlayerState;
import mordorMessenger.MordorMessenger;

public class Mordor extends JFrame implements ActionListener
{	
	private String version = "0.3.4b";
	public String aboutMordor = "<HTML>Mordor Clone of Dejenol<BR>Version" + version + 
	"<BR><BR>A clone of Mordor: Depths of Dejenol<BR>Created by: August Junkala" + 
	"<BR>Based on game by:David Allen<BR><BR>" + 
	"I created this clone for several reasons:<BR>" + 
	"1. I love Mordor, but I am just down to my iBook and no one is in any hurry to make a" +
	" cross platform clone." +
	"2. I am studying CS, I need a real, deep project to develop and demonstrate my skills.<BR>" + 
	"3. I have a huge personal interest in AI and need an engine to work with.<BR><BR>" + 
	"This clone attempts to replicate the original game as closely as possible with my" +
	"current skill set and access to knowledge of the original game's mechanics. Further," +
	"with the editor and the source (open), I have tried ensure this clone is as richly" +
	"extensible as possible allowing myself, and others,to change or expand the game.</HTML>"; 
	
	private PlayerControl currentPlayer;
	private DataBank dataBank;
	
//	private ViewPanel viewPanel;
	private DungeonFrame dungeonWindow;
	private DungeonMapFrame dungeonMapWindow;
	private MessageFrame messageWindow;
	private TownPane townPane;
	private JDesktopPane desktop;
 	private	SICPane sicPane; // the objSpeComp contents
	
	private InformationPanel informationWindow;
//	private JInternalFrame dungeonWindow;
//	private JInternalFrame dungeonMapWindow;
	private JInternalFrame partyWindow;
	private JInternalFrame townWindow;
	private JInternalFrame objSpeCompWindow;
//	private JInternalFrame messageWindow;
	
	
	private JInternalFrame confinementWindow;
	private JInternalFrame guildsWindow;
	private JInternalFrame itemShopWindow;
	private JInternalFrame morgueWindow;
	private JInternalFrame seerWindow;
//	private JInternalFrame bankWindow;
	
	private JInternalFrame bankWindow;
	
	private Timer timer;
	private static final int UPDATETIME = 2500;
	
	private StartingScreen introScreen;
	
	public static void main(String[] args)
	{
		new Mordor();
	}
	
	Mordor()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch (Exception e) 
		{
			System.err.println(e);
		}
		
		MordorMessenger messenger = new MordorMessenger();
		LoadingScreen loading = new LoadingScreen("<HTML>Morder: Clone of Dejenol<BR>Version: " + version + ".</HTML>", messenger);
		
		this.setSize(900, 600);

		dataBank = new DataBank(messenger);
		dataBank.loadData();
		dataBank.getMonsterEden().finalizeMonsterEden();
		dataBank.getItemCloset().finalizeItemCloset();
		currentPlayer = new PlayerControl(dataBank, dataBank.getPlayers().first());
		
		messenger.postMessage("Building GUI...");
		
		desktop = new JDesktopPane();
		
		informationWindow = new InformationPanel("Information", true, false, true, true, dataBank);
		informationWindow.setSize(250, 330);
		informationWindow.setLocation(5, 5);
		//informationWindow.setVisible(true);
		
		dungeonMapWindow = new DungeonMapFrame(currentPlayer.getPlayer(), dataBank.getMap());
		dungeonMapWindow.setSize(185, 180);
		dungeonMapWindow.setLocation(260, 310);
		//dungeonMapWindow.setVisible(true);
		
		dungeonWindow = new DungeonFrame(currentPlayer, dataBank, dungeonMapWindow, this);
		dungeonWindow.setSize(590, 230);
		dungeonWindow.setLocation(260, 5);
		//dungeonWindow.setVisible(true);

		partyWindow = new JInternalFrame("Party", true, false, true, true);
//		partyWindow.add();
		partyWindow.setSize(250, 100);
		partyWindow.setLocation(5, 340);
		//partyWindow.setVisible(true);
		
		messageWindow = new MessageFrame(dataBank.getMessenger());
		messageWindow.setSize(500, 100);
		messageWindow.setLocation(5, 450);
		//messageWindow.setVisible(true);
		
		objSpeCompWindow = new JInternalFrame("Objects & Spells", true, false, true, true);
		sicPane = new SICPane(currentPlayer.getPlayer(), informationWindow, dataBank);
		objSpeCompWindow.add(sicPane);
		objSpeCompWindow.setSize(300, 260);
		objSpeCompWindow.setLocation(460, 310);
		//objSpeCompWindow.setVisible(true);

		townWindow = new JInternalFrame("Town", true, false, true, true);
		townPane = new TownPane(this, dataBank);
		townWindow.setContentPane(townPane);
		townWindow.setSize(520, 260);
		townWindow.setLocation(260, 5);
		//townWindow.setVisible(true);
		
		messenger.postMessage("Finalizing...");
		
		introScreen = new StartingScreen(dataBank, this);

		introScreen.setSize(500, 300);
		introScreen.setLocation(100, 100);
		introScreen.setVisible(true);
		
		//informationWindow.showMonster(dataBank, dataBank.getRandomMonster((byte)100).createInstance(), (byte)0);
	//	informationWindow.showItem(dataBank, dataBank.getRandomItem((byte)100).createInstance());
		
		desktop.add(informationWindow);
		desktop.add(dungeonWindow);
		desktop.add(dungeonMapWindow);
		desktop.add(partyWindow);
		desktop.add(messageWindow);
		desktop.add(objSpeCompWindow);
		desktop.add(townWindow);
		
		desktop.add(introScreen);
		
		setContentPane(desktop);
		
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loading.dispose();
		setVisible(true);
	}
	
	/**
	 * Exits the game and goes to the intro menu.
	 */
	public void exitMordorToIntro()
	{	
		timer.stop();
		
		informationWindow.setVisible(false);
		dungeonWindow.setVisible(false);
		dungeonMapWindow.setVisible(false);
		partyWindow.setVisible(false);
		townWindow.setVisible(false);
		messageWindow.setVisible(false);
		objSpeCompWindow.setVisible(false);
		introScreen.setVisible(true);
		
		if(bankWindow != null)
			exitBank();
	}
	
	private void updatePanels()
	{
		informationWindow.updatePanes(currentPlayer.getPlayer());
	}
	
	/**
	 * Enters into the game
	 * @param newPlayer	The player that will be active.
	 */
	public void startMordor(Player newPlayer)
	{
		//desktop.remove(introScreen);
		informationWindow.setVisible(true);
		dungeonWindow.setVisible(true);
		dungeonMapWindow.setVisible(true);
		partyWindow.setVisible(true);
	//	townWindow.setVisible(true);
		messageWindow.setVisible(true);
		objSpeCompWindow.setVisible(true);
		introScreen.setVisible(false);
		
		currentPlayer = new PlayerControl(dataBank, newPlayer);

		try
		{
			if(currentPlayer.getPlayer().isInState(PlayerState.InDejenol))
			{
				dungeonWindow.setIcon(true);
				dungeonMapWindow.setIcon(true);
	//			desktop.add(townWindow);
				townWindow.setVisible(true);
			}
			else
			{
				dungeonWindow.setIcon(false);
				dungeonMapWindow.setIcon(false);
		//		desktop.remove(townWindow);
				townWindow.setVisible(false);
			}
		}
		catch(Exception e)
		{
			System.out.println("Error iconifying/deiconifying.");
		}
		updatePanels();
		
		timer = new Timer(UPDATETIME, this);
		timer.start();
	}
	
	/**
	 * Quits mordor.
	 */
	public void quitMordor()
	{
		// TODO Uncomment. timer was null
		if(timer != null)
			timer.stop();
	//	dataBank.saveGameData();
		System.exit(0);
	}
	
	/**
	 * Enter the dungeon.
	 */
	public void enterDungeon()
	{
		if(currentPlayer.getPlayer().isInState(PlayerState.Dead))
			return;
		
		currentPlayer.getPlayer().setState(PlayerState.InDejenol, false, Player.STATENATURAL);
		
		try
		{
			dungeonWindow.setIcon(false);
			dungeonMapWindow.setIcon(false);
		//	desktop.remove(townWindow);
			townWindow.setVisible(false);
			if(bankWindow != null)
			{
				exitBank();
			}
		}
		catch(Exception e)
		{
			System.out.println("Error Entering Dungeon.");
		}
		updatePanels();
	}
	
	/**
	 * Exit the dungeon into Dejenol
	 */
	public void exitDungeon()
	{

		try
		{
			dungeonWindow.setIcon(true);
			dungeonMapWindow.setIcon(true);
		//	desktop.add(townWindow);
			townWindow.setVisible(true);
		}
		catch(Exception e)
		{
			System.out.println("Error Entering Dungeon.");
		}
		updatePanels();
	}
	
	public void enterBank()
	{
		if(bankWindow != null)
			return;
		
		bankWindow = new JInternalFrame("Bank", false, false, false, false);
		
		bankWindow.setContentPane(new BankPane(this, informationWindow, currentPlayer.getPlayer(), sicPane, dataBank));
		//bankWindow.setSize(300, 300);
		bankWindow.setLocation(100, 100);
		bankWindow.pack();
		bankWindow.setVisible(true);
		desktop.add(bankWindow);
		bankWindow.moveToFront();
	}
	
	public void exitBank()
	{
		if(bankWindow != null)
		{
			bankWindow.dispose();
			desktop.remove(bankWindow);
			bankWindow = null;
		}
	}
	
	/**
	 * Remove all the extra frames. E.g. eliminate any open stores.
	 */
	public void removeExtraFrames()
	{
		// TODO
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == timer)
		{
			currentPlayer.getPlayer().timerUpdate();
			dungeonWindow.timerUpdate();
			// Update Map? that is, make the arrow blink.
			// Random sound generator
		}
		
	}
}