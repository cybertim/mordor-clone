package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import mordorEnums.ItemTypes;
import mordorHelpers.Util;
import mordorMessenger.MordorMessenger;


import structures.LinkedList;
import structures.ListIter;
import structures.ListNode;
import structures.QuadNode;
import structures.SkipIter;
import structures.SkipList;

/**
 * Class for the storage of all the default data.
 * I.e.: Race types, item types, monster types, etc.
 * @author August Junkala
 *
 */
public class DataBank
{
	// remember: ID values of 0 for this is not allowed.
	private MonsterEden monsterEden;
	private ItemCloset itemCloset;
	private SkipList<Race> raceTypes;
	private SkipList<Guild> guildTypes;
	private SpellBook spellBook;
	private SkipList<Player> players;
	private Map map;
	private ImageBank images;
	
	private Store store;
	
	private byte defaultGuild;
	
	public static final byte FLAG_SQUARE = -1;
	public static final byte FLAG_LEVEL = -2;
	public static final byte FLAG_ROOM = -3;
	
	private static final byte BYTESIZE_FLAG = 1;
	private static final byte BYTESIZE_ROOM = 17;
	private static final byte BYTESIZE_SQUARE = 16;
	private static final byte BYTESIZE_LEVEL = 4;
	private static final byte BYTESIZE_RACE = 25;
	
	private static final String DATFILENAME = "db.dat";
	private static final String MAPFILENAME = "map.dat";
	private static final String RACEFILENAME = "race.data";
	private static final String SPELLFILENAME = "spell.dat";
	private static final String GUILDFILENAME = "guild.dat";
	private static final String ITEMFILENAME = "item.dat";
	private static final String MONSTERFILENAME = "monster.dat";
    private static final String PLAYERFILENAME = "players.dat";
    private static final String IMAGEFILENAME = "images.dat";
    private static final String UTILFILENAME = "util.dat";
    
    private static final String STOREFILENAME = "store.dat";
	
	private static final byte MONSTERFILEVERSION = 1;
    private static final byte PLAYERFILEVERSION = 1;
    private static final byte STOREFILEVERSION = 1;
	
	private static final byte MAXGUILDCOUNT = 127;
	private static final byte MAXRACECOUNT = 127;
	
	private static final byte DATABANK_CURRENTVERSION = 1;
    
    //public static final byte NOTHING = -1;
	
	private MordorMessenger messenger; 
	
	public DataBank(MordorMessenger nMessenger)
	{
		messenger = nMessenger;
		
		raceTypes = new SkipList<Race>();
		monsterEden = new MonsterEden();
		itemCloset = new ItemCloset();
		guildTypes = new SkipList<Guild>();
		spellBook = new SpellBook();
		players = new SkipList<Player>();
		store = new Store();
	}
	
	public void loadData()
	{
        LinkedList<GuildRecord> guildRecordLoadList = new LinkedList<GuildRecord>();
        LinkedList<ItemInstance> itemInstLoadList = new LinkedList<ItemInstance>();
        LinkedList<MonsterInstance> monInstLoadList = new LinkedList<MonsterInstance>();
        LinkedList<SpellReference> spellRefLoadList = new LinkedList<SpellReference>();
		LinkedList<StoreRecord> storeRecordLoadList = new LinkedList<StoreRecord>();
        
		messenger.postMessage("Loading images...");
		loadImageData();
		messenger.postMessage("Loading spell data...");
		loadSpellData();
		messenger.postMessage("Loading race data...");
		loadRaceData();
		messenger.postMessage("Loading guild data...");
		loadGuildData();
		messenger.postMessage("Loading item data...");
		loadItemData();
		messenger.postMessage("Loading monster data...");
		loadMonsterData();
		messenger.postMessage("Loading map data...");
		loadMapData(itemInstLoadList, monInstLoadList);
		messenger.postMessage("Loading player data...");
		loadPlayerData(guildRecordLoadList, itemInstLoadList, monInstLoadList, spellRefLoadList);
		messenger.postMessage("Loading city data...");
		loadCityData(storeRecordLoadList);
        messenger.postMessage("Finalizing load data...");
		loadFinalData(guildRecordLoadList, itemInstLoadList, monInstLoadList, spellRefLoadList, storeRecordLoadList);
	}
	
	public void saveData()
	{
		saveImageData();
		saveMonsterData();
		saveItemData();
		saveRaceData();
		saveGuildData();
		saveSpellData();
		saveMapData();
		savePlayerData();
		saveCityData();
	}
	
	/**
	 * Saves all the data relevant to on going play.
	 * I.e., saves the state of all the players, the state of the dungeon, and
	 * everything in the city (e.g. the store).
	 */
	public void saveGameData()
	{
		saveMapData();
		savePlayerData();
		saveCityData();
	}
	
	public Map loadMapData(LinkedList<ItemInstance> itemInstLoadList, LinkedList<MonsterInstance> monInstLoadList)
	{
		try
		{
			FileInputStream fis = new FileInputStream(MAPFILENAME);
			DataInputStream dis = new DataInputStream(fis);
			
			// read map data
			{
				byte[] mapData = new byte[1];
				
				fis.read(mapData);
				
				map = new Map(mapData[0], images);
			}
			// read level data
			for(byte i = 0; i < map.getDepth(); i++)
			{
				byte[] flagByte = new byte[BYTESIZE_FLAG];
				byte[] mapLevelByte = new byte[BYTESIZE_LEVEL - 1];
				byte[] squareByte = new byte[BYTESIZE_SQUARE - 1];
				
				MapLevel mapLevel;
				Room room;
				MapSquare mapSquare;
				
				// read rooms
				while(fis.read(flagByte) != -1)
				{
					switch(flagByte[0])
					{
					case FLAG_LEVEL:
						fis.read(mapLevelByte);
						map.setLevel(new MapLevel(mapLevelByte));
						break;
					case FLAG_ROOM:
						Room nRoom = Room.readRoom(map, dis, itemInstLoadList, monInstLoadList);
						map.getMapLevel(nRoom.getLevel()).addRoom(nRoom);
						break;
					case FLAG_SQUARE:
						fis.read(squareByte);
						// get room
						mapLevel = map.getMapLevel(squareByte[2]);
						room = mapLevel.getRoom((squareByte[3] << 7) + squareByte[4]);
						mapSquare = new MapSquare(squareByte, room, images);
						if(mapSquare.getSquareFeatures()[0].getType() == SquareFeature.TYPE_EXIT)
							map.setExitSquare(mapSquare);
						mapLevel.setMapSquare(mapSquare);
						room.addSquare(mapSquare);
					}
				}
			}
			
			for(int i = 0; i < map.getDepth(); i++)
			{
				map.getMapLevel(i).fixWaterRooms();
			}
			
			fis.close();
		}
		catch (FileNotFoundException e)
		{
			map = new Map(images);
		}
		catch(IOException e)
		{
			System.err.println(e);
		}
		
		return map;
	}
	
	public boolean loadMonsterData()
	{
		monsterEden = new MonsterEden();
		try
		{
			FileInputStream fis = new FileInputStream(MONSTERFILENAME);
			DataInputStream dis = new DataInputStream(fis);

			byte version = dis.readByte();
			if(version != MONSTERFILEVERSION)
			{
				System.err.println("Invalid monster file version.");
				return false;
			}
			
			int count = dis.readInt();
			Monster tMonster;
			
			for(int i = 0; i < count; i++)
			{
				tMonster = Monster.loadMonster(this, dis);
				if(tMonster != null)
				{
					if(!monsterEden.addMonster(tMonster))
						System.err.println("Monster load failed. Monster #" + i);
				}
				else
					System.err.println("Monster load failed. Monster #" + i);
			}
			
			dis.close();
			fis.close();
		}
		catch (FileNotFoundException e)
		{
			monsterEden.newMonster();
			System.err.println(MONSTERFILENAME + " not found. Creating empty monster list.");
			return false;
		}
		catch(Exception e)
		{
			monsterEden.newMonster();
			System.err.println("Monster load error: " + e);
			return false;
		}
		
		return true;
	}
	
	public void loadItemData()
	{
		itemCloset = new ItemCloset();
		SkipList<Item> itemTypes = itemCloset.getItems();
		
		try
		{
		//	FileInputStream fis = new FileInputStream(getClass().getResourceAsStream(ITEMFILENAME));//new FileInputStream(ITEMFILENAME);
			InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream(ITEMFILENAME);
			DataInputStream dis = new DataInputStream(fis);
			
			int count = dis.readInt();
			Item tItem;
			
			for(int i = 0; i < count; i++)
			{
				tItem = Item.readItem(dis, this);
				if(tItem != null)
					itemTypes.insert(tItem, (int)tItem.getID());
				else
					System.err.println("Item load failed. Item #" + i);
			}
			
			dis.close();
			fis.close();
		}
		catch (FileNotFoundException e)
		{
			itemTypes.insert(new Item((byte)0), 0);
			System.err.println("dataBank - loadItemData " + e);
		}
		catch(Exception e)
		{
			itemTypes.insert(new Item((byte)0), 0);
			System.err.println("dataBank - loadItemData " + e);
		}
	}
	
	public void loadRaceData()
	{
		raceTypes = new SkipList<Race>();
		
		try
		{
			FileInputStream fis = new FileInputStream(RACEFILENAME);
			DataInputStream dis = new DataInputStream(fis);
			
			int numberRaces = dis.readByte();
			for(byte i = 0; i < numberRaces; i++)
			{
				Race newRace = Race.readRace(dis);
				if(newRace != null && raceTypes.find((int)newRace.getRaceID()) == null)
					raceTypes.insert(newRace, (int)newRace.getRaceID());
			}
			
			dis.close();
			fis.close();
		}
		catch (FileNotFoundException e)
		{
			raceTypes.insert(new Race((byte)0), 0);
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}
	
	public void loadGuildData()
	{
		guildTypes = new SkipList<Guild>();
		
		try
		{
			FileInputStream fis = new FileInputStream(GUILDFILENAME);
			DataInputStream dis = new DataInputStream(fis);
			
			int tInt;
			Guild newGuild;
			
			tInt = dis.readInt();
			defaultGuild = 0;
			
			for(int i = 0; i < tInt; i++)
			{
				newGuild = Guild.readGuild(this, dis);
				if(newGuild == null)
					System.err.println("Guild load error.");
				else
				{
					if(newGuild.getName().equalsIgnoreCase("Nomad"))
						defaultGuild = (byte)newGuild.getID();
					guildTypes.insert(newGuild, (int)newGuild.getID());
				}
			}
			
			dis.close();
			fis.close();
		}
		catch (FileNotFoundException e)
		{
			guildTypes.insert(new Guild((byte)0), 0);
			System.err.println("dataBank - loadGuildData " + e);
		}
		catch (EOFException e)
		{
			guildTypes.insert(new Guild((byte)0), 0);
			System.err.println("dataBank - loadGuildData " + e);
			return;
		}
		catch(Exception e)
		{
			System.err.println("dataBank - loadGuildData " + e);
		}
	}
	
	public void loadSpellData()
	{
		spellBook = new SpellBook();
		
		try
		{
			FileInputStream fis = new FileInputStream(SPELLFILENAME);
			DataInputStream dis = new DataInputStream(fis);

			short spellCount = dis.readShort();
			for(byte i = 0; i < spellCount; i++)
			{
				Spell nSpell = Spell.readSpell(dis);
				SpellReference nSpellRef = SpellReference.readSpellRef(dis);
				nSpellRef.setSpell(nSpell);
				spellBook.insertSpell(nSpellRef);
			}
			
			dis.close();
			fis.close();
		}
		catch (FileNotFoundException e)
		{
			spellBook = new SpellBook();
		}
		catch (EOFException e)
		{
			return;
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}
	
	public void loadImageData()
	{
		images = new ImageBank();
	}
    
    public void loadOnlyPlayerData()
    {
    	LinkedList<GuildRecord> guildRecordLoadList = new LinkedList<GuildRecord>();
    	LinkedList<ItemInstance> itemInstLoadList = new LinkedList<ItemInstance>();
    	LinkedList<MonsterInstance> monInstLoadList = new LinkedList<MonsterInstance>();
    	LinkedList<SpellReference> spellRefLoadList = new LinkedList<SpellReference>();
		LinkedList<StoreRecord> storeRecordLoadList = new LinkedList<StoreRecord>();
    	
    	loadPlayerData(guildRecordLoadList, itemInstLoadList, monInstLoadList, spellRefLoadList);
    	loadFinalData(guildRecordLoadList, itemInstLoadList, monInstLoadList, spellRefLoadList, storeRecordLoadList);
    }
    
    public void loadOnlyMapData()
    {
    	LinkedList<GuildRecord> guildRecordLoadList = new LinkedList<GuildRecord>();
    	LinkedList<ItemInstance> itemInstLoadList = new LinkedList<ItemInstance>();
    	LinkedList<MonsterInstance> monInstLoadList = new LinkedList<MonsterInstance>();
    	LinkedList<SpellReference> spellRefLoadList = new LinkedList<SpellReference>();
		LinkedList<StoreRecord> storeRecordLoadList = new LinkedList<StoreRecord>();
    	
    	loadMapData(itemInstLoadList, monInstLoadList);
    	loadFinalData(guildRecordLoadList, itemInstLoadList, monInstLoadList, spellRefLoadList, storeRecordLoadList);
    }
    
    public void loadOnlyStoreData()
    {
		LinkedList<StoreRecord> storeRecordLoadList = new LinkedList<StoreRecord>();
    	loadStoreData(storeRecordLoadList);
    	store.finishLoadingStore(itemCloset.getItems(), storeRecordLoadList);
    }
	
	public void loadPlayerData(LinkedList<GuildRecord> guildRecordLoadList, LinkedList<ItemInstance> itemInstLoadList, LinkedList<MonsterInstance> monInstLoadList, LinkedList<SpellReference> spellRefLoadList)
	{        
		players = new SkipList<Player>();
        
        try
        {
            FileInputStream fis = new FileInputStream(PLAYERFILENAME);
            DataInputStream dis = new DataInputStream(fis);
            
            byte version = dis.readByte();
            if(version != PLAYERFILEVERSION)
            {
                System.err.println("");
            }
            int count = dis.readInt();
            Player tPlayer;
            
            for(int i = 0; i < count; i++)
            {
                tPlayer = Player.readPlayer(dis, this, guildRecordLoadList, itemInstLoadList, monInstLoadList, spellRefLoadList);
                if(tPlayer != null)
                    players.insert(tPlayer, (int)tPlayer.getID());
                else
                    System.err.println("Player load failed. Player #" + i);
            }
            
            dis.readInt(); // Flag for extension
            // TODO: Player team loading
            
            dis.close();
            fis.close();
        }
        catch (FileNotFoundException e)
        {
            players.insert(new Player((short)0, this), 0);
            System.err.println("dataBank - loadPlayerData " + e);
        }
        catch(Exception e)
        {
            players.insert(new Player((short)0, this), 0);
            System.err.println("dataBank - loadPlayerData " + e);
        }
	}
	
	public void loadCityData(LinkedList<StoreRecord> storeRecordLoadList)
	{	
		loadStoreData(storeRecordLoadList);
	}
	
	public void loadStoreData(LinkedList<StoreRecord> storeRecordLoadList)
	{
		store = new Store();
        
        try
        {
            FileInputStream fis = new FileInputStream(STOREFILENAME);
            DataInputStream dis = new DataInputStream(fis);
            
            byte version = dis.readByte();
            if(version != STOREFILEVERSION)
            {
                System.err.println("");
            }
            
            store.readStoreRecord(dis, storeRecordLoadList);
            
            dis.close();
            fis.close();
        }
        catch (FileNotFoundException e)
        {
            System.err.println("No store data found.");
        }
        catch(Exception e)
        {
            System.err.println("dataBank - loadStoreData " + e);
        }
	}
	
	/**
	 * Function to load stuff in odd spots.
	 * For example: guilds have crests. however, items have guild restrictions
	 * since there are more restrictions then quests, guild load first
	 * and then get their crests after items have loaded.
	 * 
	 * Could this be changed so that Guilds don't keep a copy of the item, just
	 * the ID. That is all they really need.
	 *
	 */
	public void loadFinalData(LinkedList<GuildRecord> guildRecordLoadList, LinkedList<ItemInstance> itemInstLoadList, LinkedList<MonsterInstance> monInstLoadList, LinkedList<SpellReference> spellRefLoadList, LinkedList<StoreRecord> storeRecordLoadList)
	{
		// Could this be changed to simply parse every item, guildrecord, etc.
        // in each monster/item/guildrecord/player/bankaccount?
        // Would save the list space & building
        
        LinkedList<GuildRecord> deadGuilds = new LinkedList<GuildRecord>();
        
        ListIter<GuildRecord> tGuildRecord = guildRecordLoadList.getIterator();
        while(tGuildRecord.next())
            if(!tGuildRecord.element().setGuild(getGuild(tGuildRecord.element().getGuildID())))
                deadGuilds.insert(tGuildRecord.element()); // guild doesn't exist. add to dead guilds to be removed.
        
        removeDeadGuildRecords(deadGuilds);
        
        
        SkipIter<Guild> tGuild = guildTypes.getIterator();
	    while(tGuild.next())
	       	tGuild.element().setCrest(this.getItem(tGuild.element().getCrestID()));
        
        // Parses every player and ensures that and quest items/monsters
        // still exist.
        SkipIter<Player> tPlayer = players.getIterator();
        while(tPlayer.next())
        {
            tGuildRecord = tPlayer.element().getGuildRecords().getIterator();
            while(tGuildRecord.next())
            {
                if(tGuildRecord.element().getQuestItemID() >= 0)
                	tGuildRecord.element().setQuestItem(getItem(tGuildRecord.element().getQuestItemID()));
                if(tGuildRecord.element().getQuestMonsterID() >= 0)
                	tGuildRecord.element().setQuestMonster(getMonsterEden().getMonster(tGuildRecord.element().getQuestMonsterID()));
            }
        }
        
        // Cleans up dead itemInstances
        ListIter<ItemInstance> tItem = itemInstLoadList.getIterator();
        LinkedList<ItemInstance> deadItems = new LinkedList<ItemInstance>();
        
        while(tItem.next())
            if(!tItem.element().setItem(getItem(tItem.element().getItemID())))
                deadItems.insert(tItem.element());

        removeDeadItemInstances(deadItems);
        
        ListIter<MonsterInstance> tMon = monInstLoadList.getIterator();
        LinkedList<MonsterInstance> deadMonsters = new LinkedList<MonsterInstance>();
        
        while(tMon.next())
            if(!tMon.element().setMonster(getMonsterEden().getMonster(tMon.element().getMonsterID())))
                deadMonsters.insert(tMon.element());

        removeDeadMonsterInstances(deadMonsters);
        
        ListIter<SpellReference> tSpell = spellRefLoadList.getIterator();
        LinkedList<SpellReference> deadSpells = new LinkedList<SpellReference>();
        while(tSpell.next())
            if(!tSpell.element().setSpell(getSpellBook().getSpell(tSpell.element().getSpellID()).getSpell()))
                deadSpells.insert(tSpell.element());

        removeDeadSpellReferences(deadSpells);
        
        
        // Load player's race data.
        // Load players into rooms.
		tPlayer = players.getIterator();
		while(tPlayer.next())
		{
			if(!tPlayer.element().postLoadUpdate(this))
				players.remove(tPlayer.key());
			else
				map.getMapSquare(tPlayer.element().getCoord().getX(), tPlayer.element().getCoord().getY(), tPlayer.element().getCoord().getZ()).getRoom().addPlayer(tPlayer.element());
		}
		
		/* Finalize store data. */
		store.finishLoadingStore(itemCloset.getItems(), storeRecordLoadList);
	}
    
    private void removeDeadGuildRecords(LinkedList<GuildRecord> deadGuildRecords)
    {
        // parse anywhere where guildRecords are stored and removed dead ones.
        // Only players hold guild records.
        
        SkipIter<Player> tNode = players.getIterator();
        ListIter<GuildRecord> qNode;
        while(deadGuildRecords.getSize() > 0 && tNode.next())
        {
            qNode = deadGuildRecords.getIterator();
            while(qNode.next())
                if(tNode.element().removeGuildRecord(qNode.element()))
                    deadGuildRecords.remove(qNode.element());
        }
    }
    
    private void removeDeadItemInstances(LinkedList<ItemInstance> deadItems)
    {
        // Parse anywhere where items may be.
        // Locations: Players (items & bankaccounts), Monsters, chests(?)
        // store
        // at present, only players use finalization
        
        SkipIter<Player> tNode = players.getIterator();
        ListIter<ItemInstance> iNode;
        while(deadItems.getSize() > 0 && tNode.next())
        {
            iNode = deadItems.getIterator();
            while(iNode.next())
            {
                if(tNode.element().removeItem(iNode.element()))
                    deadItems.remove(iNode.element());
                else if(tNode.element().getBankAccount().removeItem(iNode.element()) != null)
                    deadItems.remove(iNode.element());
            }
        }
        
        // TODO: Add in monster, when monster switches.
    }
    
    /**
     * Remove any dead monsters instances.
     * @param deadMonsters
     */
    private void removeDeadMonsterInstances(LinkedList<MonsterInstance> deadMonsters)
    {
        // Parse anywhere where monsterInstances may be.
        // Locations: Players (companions), Dungeons, confinement
        // at present only players use finalization
        
        SkipIter<Player> tNode = players.getIterator();
        ListIter<MonsterInstance> mNode;
        while(deadMonsters.getSize() > 0 && tNode.next())
        {
            mNode = deadMonsters.getIterator();
            while(mNode.next())
            {
                for(byte i = 0; i < Player.MAXCOMPANIONS; i++)
                    if(tNode.element().getCompanion(i) == mNode.element())
                    {
                        tNode.element().setCompanion(i, null);//, Player.MINBIND);
                        deadMonsters.remove(mNode.element());
                    }
            }
        }
    }
    
    private void removeDeadSpellReferences(LinkedList<SpellReference> deadSpells)
    {
        // Parse anywhere where spellReferences may be saved
        // note: Due to the structure of the primary spellBook, it doesn't
        // save the spell reference.
        // Locations: Player's spell book, items, guild spellBook
        // Presently implemented: player
        
        SkipIter<Player> pNode = players.getIterator();
        ListIter<SpellReference> sNode;
        while(deadSpells.getSize() > 0 && pNode.next())
        {
            sNode = deadSpells.getIterator();
            while(sNode.next())
            {
                if(!pNode.element().getSpellBook().removeSpell(sNode.element()))
                    deadSpells.remove(sNode.element());
            }
        }
    }
	
	public void saveMapData()
	{
		try
		{
			FileOutputStream fos = new FileOutputStream (MAPFILENAME);
			DataOutputStream dos = new DataOutputStream(fos);
			MapLevel mapLevel;
			//write map data
			fos.write(map.getDepth());
			// write level data
			for(byte i = 0; i < map.getDepth(); i++)
			{
				mapLevel = map.getMapLevel(i);
				fos.write(mapLevel.toByte());
				
				dos.writeInt(mapLevel.getNumRooms());

				// write room data
				ListIter<Room> tNode = mapLevel.getRooms().getIterator();

				while(tNode.next())
					tNode.element().writeRoom(dos);

				// write square data
				for(byte x = 0; x < mapLevel.getWidth(); x++)
				{
					for(byte y = 0; y < mapLevel.getHeight(); y++)
					{
						fos.write(mapLevel.getMapSquare(x, y).toByte());
					}
				}
			}
		
			fos.flush();
			fos.close();
		}
		catch(IOException e)
		{
			System.err.println(e);
		}
	}
	
	public boolean saveMonsterData()
	{
		boolean success = true;
		
		try
		{
			FileOutputStream fos = new FileOutputStream(MONSTERFILENAME);
			DataOutputStream dos = new DataOutputStream(fos);
			
			monsterEden.removeBlankMonsters();
			
			dos.writeByte(MONSTERFILEVERSION);
			dos.writeInt((int)monsterEden.getMonsters().getSize());
			
			SkipIter<Monster> tNode = monsterEden.getMonsters().getIterator();
			
			while(tNode.next())
				if(!tNode.element().getName().contains(Util.NOSTRING))
					success = tNode.element().writeMonster(dos);
			
			dos.close();
			fos.close();
		}
		catch(Exception e)
		{
			System.err.println("Monster saving error: " + e);
			success = false;
		}
		
		return success;
	}
	
	public boolean saveItemData()
	{
		boolean success = true;
		SkipList<Item> itemTypes = itemCloset.getItems();
		
		try
		{
			FileOutputStream fos = new FileOutputStream(ITEMFILENAME);
			DataOutputStream dos = new DataOutputStream(fos);
			
			dos.writeInt((int)itemTypes.getSize());
			
			SkipIter<Item> tNode = itemTypes.getIterator();
			
			while(tNode.next())
				success = tNode.element().writeItem(dos);
			
			dos.close();
			fos.close();
		}
		catch(Exception e)
		{
			System.err.println(e);
			success = false;
		}
		
		return success;
	}
	
	public void saveRaceData()
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(RACEFILENAME);
			DataOutputStream dos = new DataOutputStream(fos);
			
			dos.writeByte(raceTypes.getSize());
			SkipIter<Race> tNode = raceTypes.getIterator();
			while(tNode.next())
				tNode.element().writeRace(dos);
			
			dos.close();
			fos.close();
		}
		catch(Exception e)
		{
			System.err.println("dataBank - save race :" + e);
		}
	}
	
	public boolean saveGuildData()
	{
		boolean success = true;
		
		try
		{
			FileOutputStream fos = new FileOutputStream(GUILDFILENAME);
			DataOutputStream dos = new DataOutputStream(fos);
			
			dos.writeInt((int)guildTypes.getSize());
			
			SkipIter<Guild> tNode = guildTypes.getIterator();
			
			while(tNode.next())
				success = tNode.element().writeGuild(dos);
			
			dos.close();
			fos.close();
		}
		catch(Exception e)
		{
			System.err.println(e);
			success = false;
		}
		
		return success;
	}
	
	public void saveSpellData()
	{
		// Eliminate invalid spells.
		spellBook.clearEmptySpells();
		
		try
		{
			FileOutputStream fos = new FileOutputStream(SPELLFILENAME);
			DataOutputStream dos = new DataOutputStream(fos);
			
			dos.writeShort(spellBook.getNumberSpells());
			
			SkipIter<SpellReference> sNode = spellBook.getAllSpells().getIterator();
			while(sNode.next())
			{
				sNode.element().getSpell().writeSpell(dos);
				sNode.element().writeSpellRef(dos);
			}
			
			dos.close();
			fos.close();
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}
	
	public void saveImageData()
	{
		
	}
	
	public boolean savePlayerData()
	{
	    boolean success = true;
        if(players.getSize() == 0)
        {
            System.err.println("No players to save.");
        }
        
        try
        {
            FileOutputStream fos = new FileOutputStream(PLAYERFILENAME);
            DataOutputStream dos = new DataOutputStream(fos);
            
            dos.writeByte(PLAYERFILEVERSION);
            dos.writeInt((int)players.getSize());
            
            SkipIter<Player> tNode = players.getIterator();
            
            while(tNode.next())
                success = tNode.element().writePlayer(dos);

            dos.writeInt(0); // Flag for extension.
            // TODO: Player team saving
            
            dos.close();
            fos.close();
        }
        catch(Exception e)
        {
            System.err.println("Error saving player. " + e);
            success = false;
        }
        
        return success;
	}
	
	public void saveCityData()
	{
		saveStoreData();
	}
	
	public boolean saveStoreData()
	{
		boolean success = true;
        
        try
        {
            FileOutputStream fos = new FileOutputStream(STOREFILENAME);
            DataOutputStream dos = new DataOutputStream(fos);
            
            dos.writeByte(STOREFILEVERSION);
            
            store.writeStoreRecord(dos);
            
            dos.close();
            fos.close();
        }
        catch(Exception e)
        {
            System.err.println("Error saving store. " + e);
            success = false;
        }
        
        return success;
	}
    
    public void exportPlayer()
    {
        
    }
    
    public boolean importPlayer()
    {
        return false;
    }
	
	/**
	 * Retrieves the monster eden structure.
	 * @return	MonsterEden
	 */
	public MonsterEden getMonsterEden()
	{
		return monsterEden;
	}
	
	public void newMonsterEden()
	{
		monsterEden = new MonsterEden();
	}
	
	/**
	 * Retrieve the map.
	 * @return Map
	 */
	public Map getMap()
	{
		return map;
	}
	
	/**
	 * Retrieve the store
	 * @return Store
	 */
	public Store getStore()
	{
		return store;
	}
	
	/**
	 * Retrieves the currently loaded images.
	 * @return ImageBank
	 */
	public ImageBank getImages()
	{
		return images;
	}
	
	/**
	 * Creates a new map.
	 *
	 */
	public void newMap()
	{
		map.newMap();
	}
	
	/**
	 * Retrieves the messenger.
	 * @return MordorMessenger
	 */
	public MordorMessenger getMessenger()
	{
		return messenger;
	}
	
	/**
	 * Retrieves the list of races.
	 * @return LinkedList<Race>
	 */
	public SkipList<Race> getRaces()
	{
		return raceTypes;
	}
	
	/**
	 * Retives an array of Strings containing the names of all existent races.
	 * @return	String[]
	 */
	public String[] getRaceNames()
	{
		String[] raceNames = new String[raceTypes.getSize()];
		int count = 0;
		SkipIter<Race> tNode = raceTypes.getIterator();
		
		while(tNode.next())
		{
			raceNames[count] = tNode.element().getName();
			count++;
		}
		
		return raceNames;
	}
	
	/**
	 * Retrieve a race based on its name.
	 * @param raceName
	 * @return
	 */
	public Race getRace(String raceName)
	{
		SkipIter<Race> tNode = raceTypes.getIterator();
		
		while(tNode.next())
			if(tNode.element().getName().equalsIgnoreCase(raceName))
				return tNode.element();
		
		return null;
	}
	
	/**
	 * Retrieves a race based on its raceID
	 * @param raceID
	 * @return
	 */
	public Race getRace(byte raceID)
	{	
		return raceTypes.find((int)raceID);
	}
	
	/**
	 * Creates a new race.
	 * @return The new race or null
	 */
	public Race newRace()
	{
		if(raceTypes.getSize() > MAXRACECOUNT)
			return null;

		SkipIter<Race> tRace = raceTypes.getIterator();
		byte newRaceID = 0;
			
		while(tRace.next())
		{
			if(tRace.key() > newRaceID)
				break;
			
			newRaceID++;
		}
		
		Race newRace = new Race(newRaceID);
		raceTypes.insert(newRace, (int)newRaceID);
		return newRace;
	}
	
	public void deleteRace(Race dRace)
	{
		raceTypes.remove((int)dRace.getRaceID());
	}
	
	/**
	 * Retrieves a random monster from levels 1 to maxLevel
	 * @param maxLevel	The maximum level of the monster (byte)
	 * @return	Monster	the randomly selected monster.
	 */
	public Monster getRandomMonster(byte maxLevel)
	{
		// TODO Fix this.
		return monsterEden.getRandomMonster(maxLevel, 1).getFirst().monster;
	}
	
	/**
	 * Retrieves a random item from levels 1 to maxLevel. Will return null
	 * if no items of max level or lower exist.
	 * @param maxlevel	The maximum level of the item. (byte)
	 * @return Item	The randomly selected Item
	 */
	public Item getRandomItem(byte maxLevel)
	{
		return itemCloset.getItems().first();
	}
	
	
	/**
	 * Retrieve the itemCloset. This holds all the items and related functions.
	 * @return ItemCloset
	 */
	public ItemCloset getItemCloset()
	{
		return itemCloset;
	}
	
	/**
	 * Retrieve the spell book
	 * @return SpellBook
	 */
	public SpellBook getSpellBook()
	{
		return spellBook;
	}
	
	public void newSpellBook()
	{
		spellBook = new SpellBook();
	}
	
	public void newStore()
	{
		store = new Store();
	}
	
	/**
	 * Retrieve the number of guilds in existence.
	 * @return byte
	 */
	public byte getGuildCount()
	{
		return (byte)guildTypes.getSize();
	}
	
	/**
	 * Retrieves a SkipList containing all the guilds.
	 * @return
	 */
	public SkipList<Guild> getGuilds()
	{
		return guildTypes;
	}
	
	public String[] getGuildNames()
	{
		if(guildTypes.getSize() == 0)
			return null;
		
		String[] guildNames = new String[guildTypes.getSize()];
		short count = 0;
		SkipIter<Guild> tNode = guildTypes.getIterator();
		
		while(tNode.next())
		{
			guildNames[count] = tNode.element().getName();
			count++;
		}
		
		return guildNames;
	}
	
	public Guild getGuild(String guildName)
	{
		SkipIter<Guild> tNode = guildTypes.getIterator();
		
		while(tNode.next())
			if(tNode.element().getName().equalsIgnoreCase(guildName))
				return tNode.element();

		return null;
	}
	
	public Guild getGuild(byte guildID)
	{
		return guildTypes.find((int)guildID);
	}
	
	public Guild newGuild()
	{
		Guild newGuild;
		
		if(guildTypes.getSize() == 0)
		{
			newGuild = new Guild((byte)0);
			guildTypes.insert(newGuild, 0);
			return newGuild;
		}
		
		SkipIter<Guild> tNode = guildTypes.getIterator();
		
		// New guild code
		byte lastKey = 0;
		while(tNode.next() && tNode.key() <= MAXGUILDCOUNT)
		{
			if(tNode.key() > lastKey)
				break;
			
			lastKey++;
		}
		
		if(tNode.key() != MAXGUILDCOUNT)
		{
			newGuild = new Guild(lastKey);
			guildTypes.insert(newGuild, (int)newGuild.getID());
			return newGuild;
		}
		
		return null;
	}
	
	public void deleteGuild(String name)
	{
		SkipIter<Guild> tNode = guildTypes.getIterator();
		
		while(tNode.next())
			if(tNode.element().getName().equalsIgnoreCase(name))
				guildTypes.remove(tNode.key());
	}
	
	public void deleteGuild(Guild guild)
	{
		deleteGuild(guild.getName());
	}
	
	public boolean validGuildName(String newName)
	{
		SkipIter<Guild> tNode = guildTypes.getIterator();
		
		if(newName.equalsIgnoreCase(Util.NOSTRING))
			return false;
		
		while(tNode.next())
			if(tNode.element().getName().equalsIgnoreCase(newName))
				return false;
		
		return true;
	}
	
	/**
	 * Checks to ensure that a provide name is not forbidden either
	 * because it is a reserved word or because it is already used.
	 * @param newName
	 * @return true if the name is allowed.
	 */
	public boolean validSpellName(String newName)
	{
		// Is it the "default" name?
		if(newName.equalsIgnoreCase(Util.NOSTRING))
			return false;
		
		// Is it the name of a spell that already exists?
		if(spellBook.getSpell(newName) != null)
			return false;
		
		// Nope, it is a good name.
		return true;
	}
	
	/**
	 * Checks to see if a player name is valid. It is valid if it isn't
	 * already used.
	 * @param newName	New name (String)
	 * @return	boolean	True if name is valid
	 */
	public boolean validPlayerName(String newName)
	{
		return true;
	}
	
	/**
	 * Retrieves a player based on her name
	 * @param playerName	Name of player (String)
	 * @return	Player
	 */
	public Player getPlayer(String playerName)
	{
		SkipIter<Player> tNode = players.getIterator();
		while(tNode.next())
			if(tNode.element().getName().equalsIgnoreCase(playerName))
				return tNode.element();
		
		return null;
	}
	
	/**
	 * Retrieves a Player based on her key.
	 * @param playerID	short
	 * @return
	 */
	public Player getPlayer(short playerID)
	{
		return players.find((int)playerID);
	}
    
	/**
	 * Retrieves a SkipList<Player> of all players.
	 * @return
	 */
    public SkipList<Player> getPlayers()
    {
        return players;
    }
    
    public Player newPlayer()
    {
    	short newID = getNextFreeShortID(players);
    	if(newID == Short.MAX_VALUE)
    		return null;
    	
    	Player newPlayer = new Player(newID, this);
    	
    	players.insert(newPlayer, (int)newID);
    	return newPlayer;
    }
    
    public void newPlayers()
    {
    	players = new SkipList<Player>();
    	
    	players.insert(new Player((short)0, this), 0);
    }
    
    /**
     * Removes the provided Player. Returns false if it fails.
     * @param deletedPlayer Player to remove
     * @return boolean
     */
    public boolean removePlayer(Player deletedPlayer)
    {
    	return (players.remove((int)deletedPlayer.getID()) != null);
    }
    
    /**
     * Finds the first free ID between 0 and Short.MAX_VALUE
     * @param tSkipList	Skiplist to parse
     * @return	first free ID, or Short.MAX_VALUE if no free entries
     */
    public short getNextFreeShortID(SkipList tSkipList)
    {
    	short freeID = 0;
    	
    	if(tSkipList.isEmpty())
    		return freeID;
    	
    	SkipIter tNode = tSkipList.getIterator();
    	
    	short lastID = 0;
    	while(tNode.next())
    	{
    		if(tNode.key() > lastID)
    			break;
    		
    		lastID++;
    	}
    	
    	if(lastID < Short.MAX_VALUE)
    		return (lastID);
    	else
    		return Short.MAX_VALUE;
    }
    
    /**
     * Retrieve a String array of the names of all players
     * organized based on increasing key.
     * @return
     */
    public String[] getPlayerNames()
    {
    	String[] playerNames = new String[players.getSize()];
    	SkipIter<Player> tNode = players.getIterator();
    	
    	if(tNode.last())
    		return null;
    	
    	for(int i = 0; tNode.next(); i++)
    		playerNames[i] = tNode.element().getName();
    	
    	return playerNames;
    }
	
	/**
	 * Retrieves an item based on its name
	 * @param itemName	Name of the item (String)
	 * @return	Item
	 */
	public Item getItem(String itemName)
	{
		SkipList<Item> itemTypes = itemCloset.getItems();
		
		if(itemTypes.getSize() <= 0)
			return null;
		
		SkipIter<Item> tNode = itemTypes.getIterator();
		
		while(tNode.next())
			if(tNode.element().getName().equalsIgnoreCase(itemName))
				return tNode.element();
		
		return null;
	}
	
	/**
	 * Retrieve all existing kinds of items.
	 * @return	SkipList<Item>
	 */
	public SkipList<Item> getItems()
	{
		return itemCloset.getItems();//itemTypes;
	}
	
	/**
	 * Retrieve an item based on its ID.
	 * @param itemID	ItemId (short)
	 * @return	Item
	 */
	public Item getItem(short itemID)
	{
		return itemCloset.getItems().find((int)itemID);
	}
	
	/**
	 * Retrieve a linked list of all the items of a specific type.
	 * @param itemType	ItemTypes
	 * @return	LinkedList<Item> or null if size = 0
	 */
	public LinkedList<Item> getItemsOfType(ItemTypes itemType)
	{
		SkipList<Item> itemTypes = itemCloset.getItems();
		
		if(itemTypes.getSize() == 0)
			return null;
		
		LinkedList<Item> itemList = new LinkedList<Item>();
		SkipIter<Item> tNode = itemTypes.getIterator();
		
		while(tNode.next())
			if(tNode.element().getItemType() == itemType)
				itemList.insert(tNode.element());
		
		return itemList;
	}
	
	public String[] getItemNamesInClass(ItemTypes itemType)
	{
		LinkedList<Item> itemList = getItemsOfType(itemType);
		
		if(itemList == null || itemList.getSize() <= 0)
			return null;
		
		String[] itemNames = new String[itemList.getSize()];
		int count = 0;
		ListIter<Item> tNode = itemList.getIterator();
		
		while(tNode.next())
		{
			itemNames[count] = tNode.element().getName();
			count += 1;
		}
	
		return itemNames;
	}
	
	public Item newItem()
	{
		SkipList<Item> itemTypes = itemCloset.getItems();
		
		if(itemCloset.getItems().getSize() >= Short.MAX_VALUE)
			return null;
		
		short newID = 0;
		
		SkipIter<Item> tNode = itemTypes.getIterator();
		while(tNode.next())
		{
			if(newID < tNode.key())
				break;
			
			newID++;
		}
		
		Item newItem = new Item(newID);
		itemTypes.insert(newItem, (int)newID);
		return newItem;
	}
	
	public Item newItem(ItemTypes itemType)
	{
		Item tItem = newItem();
		tItem.setItemType(itemType);
		return tItem;
	}
	
	public void deleteItem(Item item)
	{
		itemCloset.getItems().remove((int)item.getID());
	}
	
	public void deleteItem(String name)
	{
		SkipList<Item> itemTypes = itemCloset.getItems();
		SkipIter<Item> tNode = itemTypes.getIterator();
		
		while(tNode.next())
			if(tNode.element().getName().equalsIgnoreCase(name))
				itemTypes.remove((int)tNode.element().getID());
	}
	
	public boolean validItemName(String name)
	{
		if(name.equalsIgnoreCase(Util.NOSTRING))
			return false;
		
		SkipIter<Item> tNode = itemCloset.getItems().getIterator();
		
		while(tNode.next())
			if(tNode.element().getName().equalsIgnoreCase(name))
				return false;
		
		return true;
	}
	
	/**
	 * Retrieve the default guild.
	 * @return Guild
	 */
	public Guild getDefaultGuild()
	{
		return getGuild(defaultGuild);
	}
}
