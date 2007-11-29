package mordorEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mordorData.DataBank;
import mordorData.Map;
import mordorData.Race;
import mordorGame.LoadingScreen;
import mordorHelpers.Util;
import mordorMessenger.MordorMessenger;


public class MordorEditor implements ActionListener, ItemListener, ChangeListener
{
	private static final String version = "0.7.3";
	// 7 is stats and other updates, store, item change
	// 8 is confinement
	// 9 is images/mechanics
	
	private static final int TABS_MAP = 0;
	
	private JMenuItem fileExit;
	private JMenuItem fileSave;
	private JMenuItem fileReload;
	private JMenuItem fileNew;
	
	private JMenuItem mapNew;
	private JMenuItem mapNewLevel;
	private JMenuItem mapNewRoom;
	private JMenuItem mapRemoveLevel;
	private JMenuItem mapClearRoom;
	private JMenuItem mapResizeLevel;
	private JMenuItem mapClearLevel;
	private JMenuItem mapSetVisited;
	private JMenuItem mapSetUnvisited;
	private JMenuItem mapReload;
	private JMenuItem mapSave;
	private JCheckBoxMenuItem mapRoomView;
	private JCheckBoxMenuItem mapVisitedView;
	
	private JMenuItem raceNew;
	private JMenuItem raceReload;
	private JMenuItem raceSave;
	private JMenuItem raceRevert;
	private JMenuItem raceUpdate;
	private JMenuItem raceDelete;
	
	private JMenuItem spellNewBook;
	private JMenuItem spellSaveBook;
	private JMenuItem spellLoadBook;
	private JMenuItem spellNew;
	private JMenuItem spellDelete;
	
	private JMenuItem guildNewGuild;
	private JMenuItem guildSaveGuilds;
	private JMenuItem guildLoadGuilds;
	private JMenuItem guildRemoveGuild;
	
	private JMenuItem itemSaveItems;
	private JMenuItem itemLoadItems;
	private JMenuItem itemNewItem;
	private JMenuItem itemRemoveItem;
	
	private JMenuItem monSaveMonsters;
	private JMenuItem monLoadMonsters;
	private JMenuItem monNewMonsters;
	
	private JMenuItem playSavePlayers;
	private JMenuItem playLoadPlayers;
	private JMenuItem playNewPlayers;
	private JMenuItem playImportPlayer;
	private JMenuItem playExportPlayer;
	
	private JMenuItem storeSaveStore;
	private JMenuItem storeLoadStore;
	private JMenuItem storeNewStore;
	
	private JMenuItem helpAbout;
	
	private JFrame frame;
	private String title = "Mordor Editor : " + version;
	private JTabbedPane editors;
	private EditorTrueViewPanel trueView;
	private EditorRacePanel racePanel;
	private EditorSpellPanel spellPanel;
	private EditorGuildPanel guildPanel;
	private EditorItemPanel itemPanel;
	private EditorMonsterPanel monsterPanel;
	private PlayerPanel playerPanel;
	private StatisticsPane statsPanel;
	private StorePanel storePanel;
	
	private DataBank dataBank;

	public static void main(String[] args)
	{
		new MordorEditor();
	}
	
	MordorEditor()
	{
		MordorMessenger messenger = new MordorMessenger();
		LoadingScreen loading = new LoadingScreen("<HTML>Mordor: Clone of Dejenol Editor<BR>Version: " + version + ".<BR>Created by: August Junkala.</HTML>", messenger);
		dataBank = new DataBank(messenger);
		dataBank.loadData();

		messenger.postMessage("Creating GUI...");
		frame = new JFrame(title);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu mapMenu = new JMenu("Map");
		JMenu monsterMenu = new JMenu("Monsters");
		JMenu itemMenu = new JMenu("Items");
		JMenu guildMenu = new JMenu("Guilds");
		JMenu raceMenu = new JMenu("Races");
		JMenu spellMenu = new JMenu("Spell");
		JMenu playerMenu = new JMenu("Player");
		JMenu statsMenu = new JMenu("Stats");
		JMenu storeMenu = new JMenu("Store");
		JMenu confineMenu = new JMenu("Confinement");
		JMenu helpMenu = new JMenu("Help");
		

		fileExit = new JMenuItem("Exit");
		fileExit.setToolTipText("Exit program with saving");
		fileSave = new JMenuItem("Save All");
		fileSave.setToolTipText("Save all data");
		fileReload = new JMenuItem("Reload All");
		fileReload.setToolTipText("Reload all data from last save.");
		fileNew = new JMenuItem("Clear All");
		fileNew.setToolTipText("Clear all data");

		fileExit.addActionListener(this);
		fileSave.addActionListener(this);
		fileReload.addActionListener(this);
		fileNew.addActionListener(this);
		
		fileMenu.add(fileNew);
		fileMenu.add(fileReload);
		fileMenu.add(fileSave);
		fileMenu.addSeparator();
		fileMenu.add(fileExit);
		
		mapNew = new JMenuItem("New Map");
		mapNew.setToolTipText("Clear all map data.");
		mapNewLevel = new JMenuItem("New Level");
		mapNewLevel.setToolTipText("Insert a new level.");
		mapNewRoom = new JMenuItem("New Room");
		mapNewRoom.setToolTipText("Add a new room to selected level.");
		mapRemoveLevel = new JMenuItem("Remove Level");
		mapRemoveLevel.setToolTipText("Remove selected level.");
		mapClearRoom = new JMenuItem("Clear Rooms");
		mapClearRoom.setToolTipText("Clear any empty rooms on current level.");
		mapResizeLevel = new JMenuItem("Resize Level");
		mapResizeLevel.setToolTipText("Resize selected level.");
		mapClearLevel = new JMenuItem("Clear Level");
		mapClearLevel.setToolTipText("Clear selected Level.");
		mapSetVisited = new JMenuItem("Set Visited");
		mapSetVisited.setToolTipText("Set all squares to visited.");
		mapSetUnvisited = new JMenuItem("Set Unisited");
		mapSetUnvisited.setToolTipText("Set all squares to unvisited.");
		mapReload = new JMenuItem("Map Revert");
		mapReload.setToolTipText("Revert map to last save.");
		mapSave = new JMenuItem("Save Map");
		mapSave.setToolTipText("Save map data.");
		mapRoomView = new JCheckBoxMenuItem("RoomView");
		mapRoomView.setToolTipText("Toggle RoomView");
		mapVisitedView = new JCheckBoxMenuItem("Visited");
		mapVisitedView.setToolTipText("Toggle visited tag.");

		mapNew.addActionListener(this);
		mapNewLevel.addActionListener(this);
		mapNewRoom.addActionListener(this);
		mapRemoveLevel.addActionListener(this);
		mapClearRoom.addActionListener(this);
		mapResizeLevel.addActionListener(this);
		mapClearLevel.addActionListener(this);
		mapSetVisited.addActionListener(this);
		mapSetUnvisited.addActionListener(this);
		mapReload.addActionListener(this);
		mapSave.addActionListener(this);
		mapRoomView.addItemListener(this);
		mapVisitedView.addItemListener(this);
		
		mapMenu.add(mapNew);
		mapMenu.add(mapNewLevel);
		mapMenu.add(mapNewRoom);
		mapMenu.addSeparator();
		mapMenu.add(mapRemoveLevel);
		mapMenu.add(mapClearRoom);
		mapMenu.addSeparator();
		mapMenu.add(mapResizeLevel);
		mapMenu.add(mapSetVisited);
		mapMenu.add(mapSetUnvisited);
		mapMenu.addSeparator();
		mapMenu.add(mapReload);
		mapMenu.addSeparator();
		mapMenu.add(mapSave);
		mapMenu.addSeparator();
		mapMenu.add(mapRoomView);
		mapMenu.add(mapVisitedView);
		
		helpAbout = new JMenuItem("About");
		helpAbout.setToolTipText("About program.");
		helpAbout.addActionListener(this);
		
		helpMenu.add(helpAbout);
		
		raceNew = new JMenuItem("Insert new race");
		raceReload = new JMenuItem("Reload races");
		raceSave = new JMenuItem("Save races");
		raceRevert = new JMenuItem("Revert race");
		raceUpdate = new JMenuItem("Update race");
		raceDelete = new JMenuItem("Delete race");
		
		raceNew.setToolTipText("Add a new race, if possible.");
		raceReload.setToolTipText("Reload all the race data.");
		raceSave.setToolTipText("Save all the race data.");
		raceRevert.setToolTipText("Refresh the current race. Does not load.");
		raceUpdate.setToolTipText("Update the current race. Does not save.");
		raceDelete.setToolTipText("Delete the current race.");
		
		raceNew.addActionListener(this);
		raceReload.addActionListener(this);
		raceSave.addActionListener(this);
		raceRevert.addActionListener(this);
		raceUpdate.addActionListener(this);
		raceDelete.addActionListener(this);
		
		raceMenu.add(raceNew);
		raceMenu.add(raceReload);
		raceMenu.add(raceSave);
		raceMenu.addSeparator();
		raceMenu.add(raceRevert);
		raceMenu.add(raceUpdate);
		raceMenu.add(raceDelete);
		

		spellNewBook = new JMenuItem("New Book");
		spellSaveBook = new JMenuItem("Save Book");
		spellLoadBook = new JMenuItem("Load Book");
		spellNew = new JMenuItem("New Spell");
		spellDelete = new JMenuItem("Delete Spell");

		spellNewBook.addActionListener(this);
		spellSaveBook.addActionListener(this);
		spellLoadBook.addActionListener(this);
		spellNew.addActionListener(this);
		spellDelete.addActionListener(this);

		spellNewBook.setToolTipText("Remove all spells from the spell book.");
		spellSaveBook.setToolTipText("Save the spell book");
		spellLoadBook.setToolTipText("Load the saved spell book");
		spellNew.setToolTipText("Create a new spell in the current spell class.");
		spellDelete.setToolTipText("Delete the current spell.");

		spellMenu.add(spellNewBook);
		spellMenu.add(spellSaveBook);
		spellMenu.add(spellLoadBook);
		spellMenu.addSeparator();
		spellMenu.add(spellNew);
		spellMenu.add(spellDelete);
		
		guildSaveGuilds = new JMenuItem("Save guilds");
		guildLoadGuilds = new JMenuItem("Load guilds");
		guildNewGuild = new JMenuItem("New guild");
		guildRemoveGuild = new JMenuItem("Remove guild");
		
		guildSaveGuilds.addActionListener(this);
		guildLoadGuilds.addActionListener(this);
		guildNewGuild.addActionListener(this);
		guildRemoveGuild.addActionListener(this);
		
		guildMenu.add(guildSaveGuilds);
		guildMenu.add(guildLoadGuilds);
		guildMenu.addSeparator();
		guildMenu.add(guildNewGuild);
		guildMenu.add(guildRemoveGuild);
		
		itemSaveItems = new JMenuItem("Save Items");
		itemLoadItems = new JMenuItem("Load Items");
		itemNewItem = new JMenuItem("New Item");
		itemRemoveItem = new JMenuItem("Remove Item");
		
		itemSaveItems.addActionListener(this);
		itemLoadItems.addActionListener(this);
		itemNewItem.addActionListener(this);
		itemRemoveItem.addActionListener(this);
		
		itemMenu.add(itemSaveItems);
		itemMenu.add(itemLoadItems);
		itemMenu.add(itemNewItem);
		itemMenu.add(itemRemoveItem);
		
		monSaveMonsters = new JMenuItem("Save Monsters");
		monLoadMonsters = new JMenuItem("Load Monsters");
		monNewMonsters = new JMenuItem("New Monsters");
		
		monSaveMonsters.addActionListener(this);
		monLoadMonsters.addActionListener(this);
		monNewMonsters.addActionListener(this);
		
		monsterMenu.add(monSaveMonsters);
		monsterMenu.add(monLoadMonsters);
		monsterMenu.add(monNewMonsters);
		
		playSavePlayers = new JMenuItem("Save Players to file");
		playLoadPlayers = new JMenuItem("Load Players from file");
		playNewPlayers = new JMenuItem("New Players file");
		playImportPlayer = new JMenuItem("Import Player");
		playExportPlayer = new JMenuItem("Export Player");
		
		playSavePlayers.addActionListener(this);
		playLoadPlayers.addActionListener(this);
		playNewPlayers.addActionListener(this);
		playImportPlayer.addActionListener(this);
		playExportPlayer.addActionListener(this);
		
		playerMenu.add(playSavePlayers);
		playerMenu.add(playLoadPlayers);
		playerMenu.add(playNewPlayers);
		playerMenu.addSeparator();
		playerMenu.add(playImportPlayer);
		playerMenu.add(playExportPlayer);
		
		storeSaveStore = new JMenuItem("Save Store Inventory");
		storeLoadStore = new JMenuItem("Load Store Inventory");
		storeNewStore = new JMenuItem("Clear/New Store Inventory");

		storeSaveStore.addActionListener(this);
		storeLoadStore.addActionListener(this);
		storeNewStore.addActionListener(this);
		
		storeMenu.add(storeSaveStore);
		storeMenu.add(storeLoadStore);
		storeMenu.add(storeNewStore);
		
		menuBar.add(fileMenu);
		menuBar.add(mapMenu);
		menuBar.add(monsterMenu);
		menuBar.add(itemMenu);
		menuBar.add(guildMenu);
		menuBar.add(raceMenu);
		menuBar.add(spellMenu);
		menuBar.add(playerMenu);
		menuBar.add(statsMenu);
		menuBar.add(storeMenu);
		menuBar.add(confineMenu);
		menuBar.add(helpMenu);
		
		editors = new JTabbedPane();

		messenger.postMessage("Creating TrueView Pane...");
		trueView = new EditorTrueViewPanel(dataBank);
		messenger.postMessage("Creating Race Pane...");
		racePanel = new EditorRacePanel(dataBank);
		messenger.postMessage("Creating Spell Pane...");
		spellPanel = new EditorSpellPanel(dataBank);
		messenger.postMessage("Creating Guild Pane...");
		guildPanel = new EditorGuildPanel(dataBank);
		messenger.postMessage("Creating Item Pane...");
		itemPanel = new EditorItemPanel(dataBank);
		messenger.postMessage("Creating Monster Pane...");
		monsterPanel = new EditorMonsterPanel(dataBank);
		messenger.postMessage("Creating Player pane...");
		playerPanel = new PlayerPanel(dataBank);
		messenger.postMessage("Creating Store pane...");
		storePanel = new StorePanel(dataBank);
		messenger.postMessage("Creating Confinement pane...");
		
		messenger.postMessage("Creating Stats pane...");
		statsPanel = new StatisticsPane(dataBank);
		
		JScrollPane statsScroll = new JScrollPane(statsPanel);
		

		messenger.postMessage("Finalizing...");
		editors.addTab("Map", trueView);
		editors.addTab("Monsters", monsterPanel);
		editors.addTab("Items", itemPanel);
		editors.addTab("Guilds", guildPanel);
		editors.addTab("Races", racePanel);
		editors.addTab("Spells", spellPanel);
		editors.addTab("Players", playerPanel);
		editors.addTab("Images", null);
		editors.addTab("Mechanics", null);
		editors.addTab("Stats", statsScroll);
		editors.addTab("Store", storePanel);
		editors.addTab("Confine", null);
		
		editors.addChangeListener(this);
		
		frame.getContentPane().add(editors);

		frame.setJMenuBar(menuBar);
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		loading.dispose();
		run();
	}
	
	private void run()
	{
		while(true)
		{
			
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == fileExit)
		{
			System.exit(0);
		}
		else if(e.getSource() == fileSave)
		{
			// TODO: update all panels before save
			dataBank.saveData();
		}
		else if(e.getSource() == fileReload)
		{
			//TODO: Update all panels
			dataBank.loadData();
			trueView.reloadView();
		}
		else if(e.getSource() == fileNew)
		{
			// reset data bank, update all panels
		}
		else if(e.getSource() == mapNew)
		{
			// dispose for
			dataBank.newMap();
			trueView = new EditorTrueViewPanel(dataBank);
			editors.setComponentAt(TABS_MAP, trueView);
			editors.revalidate();
		}
		else if(e.getSource() == mapNewLevel)
		{
			if(dataBank.getMap().getDepth() != Map.MAXDEPTH)
			{
				EditorLevelNewDialog temp = new EditorLevelNewDialog(trueView, dataBank);
			}
			else
				JOptionPane.showMessageDialog(frame, "Maximum number of levels reached.\nRemove levels first or modify them.");
		}
		else if(e.getSource() == mapNewRoom)
		{
			while(true)
			{
				int newRooms = 0;
				
				try
				{
					newRooms = Integer.parseInt(JOptionPane.showInputDialog("Number of new rooms", 0));
				}
				catch (NumberFormatException NFE)
				{
					JOptionPane.showMessageDialog(frame, "Invalid input.");
				}
				
				if(newRooms < 0 || newRooms > (16129 - dataBank.getMap().getMapLevel(trueView.getCurrentLevelIndex()).getNumRooms()))
				{
					JOptionPane.showMessageDialog(frame, "Invalid input.");
				}
				
				if(newRooms != 0)
				{
					for(int i = 0; i < newRooms; i++)
						dataBank.getMap().getMapLevel(trueView.getCurrentLevelIndex()).addRoom();
					
					trueView.updateRoomsBox();
				}
				return;
			}
		}
		else if(e.getSource() == mapRemoveLevel)
		{
			if(JOptionPane.showConfirmDialog(frame, "Remove level " + trueView.getCurrentLevelIndex() + "?", "Remove level", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				dataBank.getMap().removeLevel((byte)trueView.getCurrentLevelIndex());
				trueView.changeLevelCount();
			}
		}
		else if(e.getSource() == mapClearRoom)
		{
			if(JOptionPane.showConfirmDialog(frame, "Clear empty rooms on level  " + trueView.getCurrentLevelIndex() + "?", "Clear rooms", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				dataBank.getMap().getMapLevel(trueView.getCurrentLevelIndex()).clearEmptyRooms();
				trueView.updateRoomsBox();
			}
		}
		else if(e.getSource() == mapResizeLevel)
		{	
			EditorLevelResizeFrame temp = new EditorLevelResizeFrame(trueView, dataBank.getMap().getMapLevel(trueView.getCurrentLevelIndex()), dataBank);
			temp.setVisible(true);
		}
		else if(e.getSource() == mapClearLevel)
		{
			if(JOptionPane.showConfirmDialog(frame, "Set all squares on level " + trueView.getCurrentLevelIndex() + " new rooms?", "Clear squares.", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				dataBank.getMap().getMapLevel(trueView.getCurrentLevelIndex()).setAllSquaresEmpty(dataBank.getImages());
				dataBank.getMap().setExitSquare(dataBank.getMap().getMapSquare(0, 0, 0));
			}
		}
		else if(e.getSource() == mapSetVisited)
		{
			if(JOptionPane.showConfirmDialog(frame, "Set all squares on level " + trueView.getCurrentLevelIndex() + " to visited?", "Visited level", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				dataBank.getMap().getMapLevel(trueView.getCurrentLevelIndex()).setAllSquaresVisited(true);
				trueView.reLoadCurrentView();
			}
		}
		else if(e.getSource() == mapSetUnvisited)
		{
			if(JOptionPane.showConfirmDialog(frame, "Set all squares on level " + trueView.getCurrentLevelIndex() + " to unvisited?", "Unvisited level", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				dataBank.getMap().getMapLevel(trueView.getCurrentLevelIndex()).setAllSquaresVisited(false);
				trueView.reLoadCurrentView();
			}
		}
		else if(e.getSource() == mapReload)
		{
			dataBank.loadOnlyMapData();
			trueView.changeLevelCount();
		}
		else if(e.getSource() == mapSave)
		{
			dataBank.saveMapData();
		}
		else if(e.getSource() == monSaveMonsters)
		{
			if(JOptionPane.showConfirmDialog(frame, "Save monsters?", "Save monsters", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				if(monsterPanel.updateMonsterInDataBank())
					dataBank.saveMonsterData();
				else
					JOptionPane.showMessageDialog(frame, "Could not save.");
			}
		}
		else if(e.getSource() == monLoadMonsters)
		{
			if(JOptionPane.showConfirmDialog(frame, "Reload monsters?", "Load monsters", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				dataBank.loadMonsterData();
				monsterPanel.updateMonsterList();
				monsterPanel.updateMonsterInPanel();
			}
		}
		else if(e.getSource() == monNewMonsters)
		{
			if(JOptionPane.showConfirmDialog(frame, "New monsters list?", "New monsters", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				dataBank.newMonsterEden();
				monsterPanel.updateMonsterList();
				monsterPanel.updateMonsterInPanel();
			}
		}
		else if(e.getSource() == itemSaveItems)
		{
			if(JOptionPane.showConfirmDialog(frame, "Save items?", "Save Items", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				if(itemPanel.updateItemInDataBank())
					dataBank.saveItemData();
				else
					JOptionPane.showMessageDialog(frame, "Could not save.");
			}
		}
		else if(e.getSource() == itemLoadItems)
		{
			if(JOptionPane.showConfirmDialog(frame, "Load items? (Loses current data)", "Load Items", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				dataBank.loadItemData();
				itemPanel.updateItemList();
				itemPanel.updateItemInPanel();
			}
		}
		else if(e.getSource() == itemNewItem)
		{
			itemPanel.addItem();
			itemPanel.updateItemList();
			itemPanel.updateItemInPanel();
		}
		else if(e.getSource() == itemRemoveItem)
		{
			itemPanel.removeItem();
			itemPanel.updateItemList();
			itemPanel.updateItemInPanel();
		}
		else if(e.getSource() == guildSaveGuilds)
		{
			if(!guildPanel.updatePanes())
			{
				JOptionPane.showMessageDialog(frame, "Could not update guilds. No save.");
				return;
			}
			if(!dataBank.saveGuildData())
				JOptionPane.showMessageDialog(frame, "Errors saving guilds.");
		}
		else if(e.getSource() == guildLoadGuilds)
		{
			if(JOptionPane.showConfirmDialog(frame, "Load guilds from file?") == JOptionPane.YES_OPTION)
			{
				dataBank.loadGuildData();
				
				guildPanel.updateGuilds();
			}
		}
		else if(e.getSource() == guildNewGuild)
		{
			
			if(!guildPanel.newGuild())
				JOptionPane.showMessageDialog(frame, "New guild could not be created.");
		}
		else if(e.getSource() == guildRemoveGuild)
		{
			if(!guildPanel.removeSelectedGuild())
				JOptionPane.showMessageDialog(frame, "Guild could not be deleted.");
		}
		else if(e.getSource() == raceNew)
		{
			if(dataBank.newRace() != null)
				racePanel.changeRaceCount();
			else
				JOptionPane.showMessageDialog(frame, "New race could not be created.");
		}
		else if(e.getSource() == raceReload)
		{
			dataBank.loadRaceData();
			racePanel.changeRaceCount();
		}
		else if(e.getSource() == raceSave)
		{
			if(racePanel.updateAll())
				dataBank.saveRaceData();
			else
				JOptionPane.showMessageDialog(frame, "Save not completed.");
		}
		else if(e.getSource() == raceRevert)
		{
			racePanel.revertSelected();
		}
		else if(e.getSource() == raceUpdate)
		{
			racePanel.updateSelected();
		}
		else if(e.getSource() == raceDelete)
		{
			if(JOptionPane.showConfirmDialog(frame, "Delete " + racePanel.getSelectedRace().getName() + "?") == JOptionPane.YES_OPTION)
			{
				Race tRace = racePanel.getSelectedRace();
				dataBank.deleteRace(tRace);
				racePanel.changeRaceCount();
			}
		}
		else if(e.getSource() == spellNewBook)
		{
			if(JOptionPane.showConfirmDialog(frame, "Start a new spell book?") == JOptionPane.YES_OPTION)
			{
				dataBank.newSpellBook();
				spellPanel.initPanel();
				// TODO: need to do updates in player, item, guild, and monster
				// tabs
			}
		}
		else if(e.getSource() == spellSaveBook)
		{
			dataBank.getSpellBook().clearEmptySpells();
			dataBank.saveSpellData();
		}
		else if(e.getSource() == spellLoadBook)
		{
			dataBank.loadSpellData();
			spellPanel.initPanel();
			// TODO: Update player, item, guild?
		}
		else if(e.getSource() == playSavePlayers)
		{
			if(JOptionPane.showConfirmDialog(frame, "Save players to file?", "Save Players", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				if(playerPanel.updatePlayer())
					dataBank.savePlayerData();
			}
		}
		else if(e.getSource() == playLoadPlayers)
		{
			if(JOptionPane.showConfirmDialog(frame, "Load players from file?", "Load Players", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				dataBank.loadOnlyPlayerData();
				playerPanel.updatePlayerList();
			}
		}
		else if(e.getSource() == playNewPlayers)
		{
			if(JOptionPane.showConfirmDialog(frame, "Create new player list?", "New Players", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				dataBank.newPlayers();
				playerPanel.updatePlayerList();
			}
		}
		else if(e.getSource() == playImportPlayer)
		{
			// TODO: import code
			// should assign new player iD
			// should demand a new name if name already exists
		}
		else if(e.getSource() == playExportPlayer)
		{
			// TODO export code
			// should ignore player ID since value may not be availdable
		}
		else if(e.getSource() == storeLoadStore)
		{
			if(JOptionPane.showConfirmDialog(frame, "Load store data?", "Load Store", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				dataBank.loadOnlyStoreData();
				storePanel.updatePanel();
			}
		}
		else if(e.getSource() == storeSaveStore)
		{
			if(JOptionPane.showConfirmDialog(frame, "Save store data?", "Save Store", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				if(storePanel.updateStore())
					dataBank.saveStoreData();
		}
		else if(e.getSource() == storeNewStore)
		{
			if(JOptionPane.showConfirmDialog(frame, "Create a new store?", "New Store", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				dataBank.newStore();
				storePanel.updatePanel();
			}
		}
		else if(e.getSource() == helpAbout)
		{
			JOptionPane.showMessageDialog(frame.getContentPane(), "ModorApplet Editor.\nVersion: " + version +"\nBy: August Junkala", "About", JOptionPane.PLAIN_MESSAGE);
		}
	}

	public void itemStateChanged(ItemEvent e)
	{
		if(e.getSource() == mapRoomView)
		{
			trueView.toggleRoomView(mapRoomView.isSelected());
		}
		else if(e.getSource() == mapVisitedView)
		{
			trueView.toggleVisitedView(mapVisitedView.isSelected());
		}
		
	}

	/**
	 * Activated whenever a tab is clicked.
	 */
	public void stateChanged(ChangeEvent e)
	{
		// TODO Auto-generated method stub
		if(e.getSource() == editors)
		{
			if(editors.getSelectedComponent() == trueView)
			{
				// Presently nothing to update.
			}
			else if(editors.getSelectedComponent() == racePanel)
				racePanel.updateLists();
			else if(editors.getSelectedComponent() == spellPanel)
				spellPanel.updateLists();
			else if(editors.getSelectedComponent() == guildPanel)
				guildPanel.updateLists();
			else if(editors.getSelectedComponent() == itemPanel)
				itemPanel.updateLists();
			else if(editors.getSelectedComponent() == monsterPanel)
				monsterPanel.updateLists();
			else if(editors.getSelectedComponent() == playerPanel)
				playerPanel.updateLists();
		}
	}
	

	
	public static final boolean validityCheckTf(JComponent parent, JTextField textField, long min, long max, String errText)
	{
		if(textField.getText() == null || textField.getText().trim().length() < 1)
			textField.setText("" + min);
		
		long temp;
		
		try
		{
			String text = textField.getText().trim();

			if(max < Byte.MAX_VALUE)
			{
				temp = Byte.parseByte(text);
				textField.setText("" + Util.FITBYTE((byte)temp, (byte)min, (byte)max));
			}
			else if(max < Short.MAX_VALUE)
			{
				temp = Short.parseShort(text);
				textField.setText("" + Util.FITSHORT((short)temp, (short)min, (short)max));
			}
			else if(max < Integer.MAX_VALUE)
			{
				temp = Integer.parseInt(text);
				textField.setText("" + Util.FITINT((int)temp, (int)min, (int)max));
			}
			else
			{
				temp = Long.parseLong(text);
				textField.setText("" + Util.FITLONG((long)temp, (long)min, (long)max));
			}
		}
		catch(NumberFormatException NFE)
		{
			JOptionPane.showMessageDialog(parent, "Invalid " + errText + ".");
			return false;
		}
		return true;
	}
}
