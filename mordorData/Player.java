package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import mordorEnums.Alignment;
import mordorEnums.BodyParts;
import mordorEnums.Direction;
import mordorEnums.GuildSkill;
import mordorEnums.MonsterAbility;
import mordorEnums.PlayerSkill;
import mordorEnums.PlayerState;
import mordorEnums.Resistance;
import mordorEnums.Stats;
import mordorHelpers.Coord;
import mordorHelpers.Util;

import structures.LinkedList;
import structures.ListIter;
import structures.ListNode;

public class Player extends MObject<Race>
{	
	private Coord coords;
	private Alignment alignment;
	
	private byte[][] acquiredResistance;
	private byte[] acquiredStats;
	private boolean[][] states;
	
	private int hp, mp;
	private int maxHP;
	
//	private short itemAttack, itemDefense;
	
	private long goldOnHand;
	// IDs
	private byte raceID;
	
	// info
	private int kills, deaths, finishedItemQuests, finishedMonQuests;
	private int age;
	private short[] skills;
	
	private BankAccount bankAccount;
	private Companion[] companions;
	private ItemInstance[] items;
	private short[] equipment;
	private LinkedList<GuildRecord> guildRecords;
	private GuildRecord activeGuild;
	private SpellReference[] spellBuffers;
	private SpellBook spellBook;
	
	private double timePlayed;
	private long dateCreated, lastStartTime;
	private byte[] sanctuary;
	
	/* 
	 * It is technically possible for someone to build a loop of teleporters.
	 * This is allows for the loop to be broken before there is a stack
	 * overflow.
	 * 
	 * 10 should be plenty.
	 */
	public byte recurseCount; 
	
	private DataBank dataBank;
	
	public static final byte SANCTUARY_X = 0;
	public static final byte SANCTUARY_Y = 1;
	public static final byte SANCTUARY_Z = 2;
	private static final byte SANCTUARY_DIMS = 3; // Number of dimensions.
	
	public static final byte NOITEMEQUIPPED = -1;
	public static final byte HANDSEQUIPPED = -1;
	
	public static final byte MAXITEMSONHAND = 40;
	public static final byte MAXCOMPANIONS = 4;
	public static final byte MAXSPELLBUFFERS = 10;
    public static final byte MAXSTATESTACK = 3;
    public static final byte MAXRESISTSTACK = 5;
    // if full, ignore
    // to remove, look for a matching one. 
    // update from items when leave dungeon.
    
    public static final byte STATENATURAL = 0; // necessary?
    public static final byte STATEITEM = 1; // equipped item
    public static final byte STATESPELL = 2; // spells & monsters
    // if 1 or 2 differs from natural, then, differs.
    
    public static final byte MINBIND = 1;
    public static final byte MAXBIND = 30;
	
	private static final int INITIALAGE = 365 << 4; // 16 years
	private static final int DEFAULTHEALTH = 15;
	private static final short INITIALATTACK = 0;
	private static final short INITIALDEFENSE = 0;
	private static final long INITIALGOLD = 1500;

	private static final double DEFAULTNATDEATH = 0.5; // Default chance of natural death at maxage.
	private static final int DEFAULTMAXHEALTIME = 28; // Default maximum age time in days.
	
	protected Player(short newPlayerID, DataBank nDataBank)
	{
		super(newPlayerID);
		dataBank = nDataBank;
		
		coords = dataBank.getMap().getExitCoords();
		
		type = dataBank.getRaces().first();
		
		acquiredStats = new byte[Stats.values().length];
		for(byte i = 0; i < stats.length; i++)
		{
			stats[i] = Stats.MINIMUMVALUE;
			acquiredStats[i] = (byte)0;
		}
		
		acquiredResistance = new byte[Resistance.values().length][MAXRESISTSTACK];
		for(Resistance al : Resistance.values())
            for(byte j = 0; j < MAXRESISTSTACK; j++)
                acquiredResistance[al.value()][j] = (byte)0;
                
		states = new boolean[PlayerState.values().length][MAXSTATESTACK];
		for(byte i = 0; i < PlayerState.values().length; i++)
        {
            for(byte j = 0; j < MAXSTATESTACK; j++)
                states[i][j] = false;
        }
		
		states[PlayerState.InDejenol.value()][0] = true;
		
		hp = maxHP = DEFAULTHEALTH;
		mp = getMaxMP();
		
		attack = INITIALATTACK;
		defense = INITIALDEFENSE;
		goldOnHand = INITIALGOLD;
		
		kills = 0;
		deaths = 0;
		finishedItemQuests = 0;
		finishedMonQuests = 0;
		age = INITIALAGE;
		
		skills = new short[PlayerSkill.values().length];
		for(byte i = 0; i < skills.length; i++)
			skills[i] = 0;
		
		bankAccount = new BankAccount();
		
		companions = new Companion[MAXCOMPANIONS];
		for(byte i = 0; i < MAXCOMPANIONS; i++)
		{
			companions[i] = null;
		}
		
		items = new ItemInstance[MAXITEMSONHAND];
		for(byte i = 0; i < MAXITEMSONHAND; i++)
			items[i] = null;
		
		equipment = new short[BodyParts.values().length];
		for(byte i = 0; i < BodyParts.values().length; i++)
			equipment[i] = Util.NOTHING;
		
		guildRecords = new LinkedList<GuildRecord>();
		activeGuild = null;
		
		spellBuffers = new SpellReference[MAXSPELLBUFFERS];
		for(byte i = 0; i < MAXSPELLBUFFERS; i++)
			spellBuffers[i] = null;
		
		spellBook = new SpellBook();
		
		timePlayed = 0.0;
		dateCreated = 0;
		lastStartTime = System.currentTimeMillis();
		sanctuary = new byte[SANCTUARY_DIMS];
		for(byte i = 0; i < sanctuary.length; i++)
			sanctuary[i] = Util.NOTHING;
		
		recurseCount = 0;
	}
	
	/**
	 * Retrieve the coordinates of the player.
	 * @return	Coord
	 */
	public Coord getCoord()
	{
		return coords;
	}
	
	/**
	 * Retrieves the sanctuary coodrinates (x, y, z)
	 * Defines exist for coords
	 * @return null if any value has not been set.
	 */
	public byte[] getSanctuaryCoords()
	{
		for(byte i = 0; i < sanctuary.length; i++)
			if(sanctuary[i] == 0)
				return null;
		
		return sanctuary;
	}
	
	/**
	 * Retrieves the amount of time played.
	 * @return
	 */
	public double getTimePlayed()
	{
		return timePlayed + (System.currentTimeMillis() - lastStartTime);
	}
	
	/**
	 * Retrieves the amount of time this character has played in hours.
	 * @return
	 */
	public double getTimePlayerHours()
	{
		return (getTimePlayed() / (1000 * 60 * 60));
	}
	
	/**
	 * Get the date this character was created in milliseconds.
	 * @return
	 */
	public long getDateCreated()
	{
		return dateCreated;
	}
	
	/**
	 * Get the dd/mm/yyyy String of when this character was created.
	 * @return
	 */
	public String getDateCreatedString()
	{
		GregorianCalendar tDate = new GregorianCalendar();
		tDate.setTime(new Date(dateCreated));
		return new String((tDate.get(GregorianCalendar.MONTH) + 1) + "/" + tDate.get(GregorianCalendar.DAY_OF_MONTH) + "/" + tDate.get(GregorianCalendar.YEAR));
	}
	
	/**
	 * Set the date this player was created, in milliseconds
	 * @param newDateMillis
	 */
	public void setDateCreated(long newDateMillis)
	{
		dateCreated = newDateMillis;
	}
	
	/**
	 * Set the most recent time (in ms) that this player was started.
	 */
	public void setLastStartTime()
	{
		lastStartTime = System.currentTimeMillis();
	}
	
	/**
	 * Retrieves the alignment
	 * @return Player's alignment
	 */
	public Alignment getAlignment()
	{
		return alignment;
	}
	
	/**
	 * Retrieve the race
	 * @return Player's Race
	 */
	public Race getRace()
	{
		return type;
	}
    
    public byte getRaceID()
    {
        return raceID;
    }
	
    /**
     * Retrieve the player's natural stat level. I.e. w/o equipment.
     * @param statType Stat type to retrieve
     * @return byte
     */
	public byte getNaturalStat(Stats statType)
	{
		return stats[statType.value()];
	}
	
	/**
	 * Retrieve the player's acquired stat. I.e. anything gained
	 * from equipment.
	 * @param statType
	 * @return
	 */
	public byte getAcquiredStat(Stats statType)
	{
		return acquiredStats[statType.value()];
	}
	
	/**
	 * Get the total value (base value + anything acquired by items) for a specific stat.
	 * @param statType The stat type.
	 * @return byte
	 */
	public byte getTotalStat(Stats statType)
	{
		return Util.FITBYTE((getNaturalStat(statType) + getAcquiredStat(statType)), Stats.MINIMUMVALUE, Stats.MAXIMUMVALUE);
	}
	
	/**
	 * Gets the natural resistance (i.e. Race resitance) of the player.
	 * @param resistType
	 * @return
	 */
	public byte getNaturalResistance(Resistance resistType)
	{
		return type.getResistance(resistType);
	}
	
	/**
	 * Gets acquired resistance of the player. I.e. items, spell, etc.
	 * @param resistType
	 * @return
	 */
	public byte getAcquiredResistance(Resistance resistType)
	{
        byte sum = 0;
        for(byte i = 0; i < acquiredResistance[resistType.value()].length; i++)
            sum += acquiredResistance[resistType.value()][i];
        
		return Util.FITBYTE(sum, -100, 100);
	}
	
	/**
	 * Retrieve the total (natural + acquired) resistance of the player.
	 * @param resistType
	 * @return
	 */
	public byte getTotalResistance(Resistance resistType)
	{
		return Util.FITBYTE(getNaturalResistance(resistType) + getAcquiredResistance(resistType), 0, 100);
	}
	
    /**
     * Will parse the state stack. Whatever state is more common, will
     * be returned.
     * @param state     The state to test.
     * @return
     */
	public boolean isInState(PlayerState state)
	{
        switch(state)
        {
            case Female:
            case Dead:
            case InDejenol:
            case Drowning:
            case Dropping:
            case Fogged:
            case Invisible:
            case NoMagic:
            case NoDirectionChange:
            case LostLocation:
            case LostDirection:
            case LostLevel:
            case Protected:
                return states[state.value()][0]; // all of these are based on the first entry.
        }
        
        // if 1 or 2 differs from 0, then use it, otherwise use 0
        return (states[state.value()][STATEITEM] != states[state.value()][STATENATURAL]) ?
        		states[state.value()][STATEITEM] :
        			(states[state.value()][STATESPELL] != states[state.value()][STATENATURAL]) ?
        					states[state.value()][STATESPELL] : 
        						states[state.value()][STATENATURAL];
	}
	
	public int getHP()
	{
		return hp;
	}
	
	public int getMP()
	{
		return mp;
	}
	
	public int getMaxHP()
	{
		return maxHP;
	}
	
	/**
	 * Calculates the player's current maximum mp.
	 * @return This player's current maximum mp.
	 */
	public int getMaxMP()
	{
		return ((getTotalStat(Stats.Intelligence) + getTotalStat(Stats.Wisdom)) * 5);
	}
	
	public short getItemAttack()
	{
		return attack;
	}
	
	public short getGuildAttack()
	{
		return (activeGuild == null) ? 0 : activeGuild.getGuildAttack();
	}
	
	public short getStatAttack()
	{
		// TODO : Figure out what is with stat attack
		// Sum up stat points
		return 3;
	}
	
	public short getItemDefense()
	{
		return defense;
	}
	
	public short getGuildDefense()
	{
		return (activeGuild == null) ? 0 : activeGuild.getGuildDefense();
	}
	
	public short getStatDefense()
	{
		return 3;
	}
	
	public short getAttack()
	{
		return Util.FITSHORT((getItemAttack() + getGuildAttack() + getStatAttack()), 0, Short.MAX_VALUE);
	}
	
	public short getDefense()
	{
		return Util.FITSHORT((getItemDefense() + getGuildDefense() + getStatDefense()), 0, Short.MAX_VALUE);
	}
	
	public long getGoldOnHand()
	{
		return goldOnHand;
	}
	
	public long getTotalGold()
	{
		return goldOnHand + ((bankAccount == null) ? 0 : bankAccount.getGold());
	}
	
	public int getKills()
	{
		return kills;
	}
	
	public int getDeaths()
	{
		return deaths;
	}
	
	public int finishedItemQuests()
	{
		return finishedItemQuests;
	}
	
	public int finishedMonQuests()
	{
		return finishedMonQuests;
	}
	
	/**
	 * Retrieve age in days
	 * @return
	 */
	public int getAge()
	{
		return age;
	}
	
	/**
	 * Retrieve age in years.
	 * @return
	 */
	public short getAgeYears()
	{
		return (short)(age / 364);
	}
	
	/**
	 * Retrieves how high in one of the PlayerSkills a player is.
	 * @param skill PlayerSkill to retrieve
	 * @return short amount of the skill
	 */
	public short getPlayerSkill(PlayerSkill skill)
	{
		return skills[skill.value()];
	}
	
	/**
	 * Sets the amount for a specific PlayerSkill
	 * @param skill
	 * @param amount
	 */
	public void setPlayerSkill(PlayerSkill skill, short amount)
	{
		skills[skill.value()] = amount;
	}
	
	/**
	 * Adjust the amount of a specific PlayerSkill
	 * @param skill
	 * @param amount
	 */
	public void changePlayerSkill(PlayerSkill skill, short amount)
	{
		skills[skill.value()] = Util.FITSHORT(skills[skill.value() + amount], 0, Short.MAX_VALUE);
	}
	
	/**
	 * Get highest skill in any guild.
	 * @param skill	GuildSkill to use
	 * @return short
	 */
	public short getGuildSkillTop(GuildSkill skill)
	{	
		short topSkillVal = 0;
		
		ListIter<GuildRecord> tNode = guildRecords.getIterator();
		
		while(tNode.next())
			if(tNode.element().getGuildSkill(skill) > topSkillVal)
				topSkillVal = tNode.element().getGuildSkill(skill);
		
		return topSkillVal;
	}
	
	public BankAccount getBankAccount()
	{
		return bankAccount;
	}
	
	/**
	 * Retrieve a companion based on its index
	 * @param companionIndex
	 * @return
	 */
	public MonsterInstance getCompanion(byte companionIndex)
	{
		return (companionIndex < 0 || companionIndex >= companions.length) ? null : companions[companionIndex];
	}
	
	/**
	 * Retrieve all the companions.
	 * @return
	 */
	public Companion[] getCompanions()
	{
		return companions;
	}
	
	public long getTotalExperience()
	{
		ListIter<GuildRecord> tGuild = guildRecords.getIterator();
		long xp = 0;
		while(tGuild.next())
			xp = Util.FITLONG(xp + tGuild.element().getExperience(), 0, Long.MAX_VALUE);
		
		return xp;
	}
	
	/**
	 * Retrieves the array holding all items the player is carrying.
	 * @return ItemInstance[]
	 */
	public ItemInstance[] getItems()
	{
		return items;
	}
	
	/**
	 * Retrieve the number of items the player is currently carrying.
	 * @return int
	 */
	public int getItemCount()
	{
		int count = 0;
		for(int i = 0; i < Player.MAXITEMSONHAND; i++)
			if(items[i] != null)
				count++;
		
		return count;
	}
	
	/**
	 * Retrieve a specific item based on its index.
	 * @param itemIndex	The index to retrieve
	 * @return ItemInstance or null if doesn't exist/invalid index
	 */
	public ItemInstance getItem(byte itemIndex)
	{
		return (itemIndex < 0 || itemIndex >= items.length) ? null : items[itemIndex];
	}
	
	/**
	 * Retrieves the index of a specific item instance.
	 * @param item ItemInstance to find
	 * @return byte  either the index or Util.Nothing if not found/valid.
	 */
	public byte getItemIndex(ItemInstance item)
	{
		if(item == null)
			return Util.NOTHING;
		
		for(byte i = 0; i < items.length; i++)
			if(items[i] == item)
				return i;
		
		return Util.NOTHING;
	}
	
	/**
	 * Get the item equipped on a specific body part.
	 * @param nBodyPart
	 * @return
	 */
	public ItemInstance getEquipment(BodyParts nBodyPart)
	{
		return items[equipment[nBodyPart.value()]];
	}

	public LinkedList<GuildRecord> getGuildRecords()
	{
		return guildRecords;
	}
    
    public GuildRecord getGuildRecord(byte guildID)
    {
        ListIter<GuildRecord> tNode = guildRecords.getIterator();
        while(tNode.next())
            if(tNode.element().getGuildID() == guildID)
                return tNode.element();
        
        return null;
    }
	
	public GuildRecord getActiveGuild()
	{
		return activeGuild;
	}
	
	public SpellReference getSpellBuffers(byte bufferNum)
	{
		return (bufferNum < 0 || bufferNum >= spellBuffers.length) ? null : spellBuffers[bufferNum];
	}
	
	public SpellBook getSpellBook()
	{
		return spellBook;
	}
	
	public boolean isFloating()
	{
		Random random = new Random(System.currentTimeMillis());

		if(isInState(PlayerState.Levitating))
			return (random.nextInt(100) < 5) ? false : true;
		return false;
	}
	
	public boolean isLost()
	{
		return (states[PlayerState.LostLocation.value()][STATENATURAL] || 
				states[PlayerState.LostDirection.value()][STATENATURAL] || 
				states[PlayerState.LostLevel.value()][STATENATURAL]);
	}
	
	/**
	 * Sets all sanctuary points at once.
	 * @param nSanctuary
	 * @return True if succesfully copied.
	 */
	public boolean setSanctuaryCoords(byte[] nSanctuary)
	{
		if(nSanctuary.length != SANCTUARY_DIMS)
			return false;
		
		sanctuary = nSanctuary;
		return true;
	}
	
	/**
	 * Removes the sanctuary point.
	 */
	public void setSanctuaryCoords()
	{
		for(byte i = 0; i < sanctuary.length; i++)
			sanctuary[i] = Util.NOTHING;
	}
	
	/**
	 * Sets the alignment.
	 * @param nAlignment New alignment (Alignment)
	 */
	public void setAlignment(Alignment nAlignment)
	{
		alignment = nAlignment;
	}

	/**
	 * Set the Race
	 * @param newRace new Race (Race)
	 */
	public void setRace(Race newRace)
	{
		if(newRace == null)
			return;
		
		type = newRace;
		raceID = newRace.getRaceID();
	}
    
	/**
	 * Set the race ID
	 * @param newRaceID new race ID (byte)
	 */
    public void setRaceID(byte newRaceID)
    {
        raceID = newRaceID;
    }
	
    /**
     * Sets the natural stat for the player.
     * @param statType
     * @param newStatVal
     */
	public void setNaturalStat(Stats statType, byte newStatVal)
	{
		byte min = (type == null) ? Stats.MINIMUMVALUE : type.getBaseStat(statType, true);
		byte max = (type == null) ? Stats.MAXIMUMVALUE : (byte)(type.getBaseStat(statType, false) + Stats.MAXIMUMEXTENDED);
		stats[statType.value()] = Util.FITBYTE(newStatVal, min, max);
	}
	
	/**
	 * Adjusts a specific natural stat. If the value falls below the minimum
	 * or above the maximum allowed stat value, it will adjust to the limit
	 * and return false so that the problem can be handled. 
	 * @param statType		Type of stat being modified (byte)
	 * @param newStatVal	Amount to change specified stat (byte)
	 * @return boolean	true if change is w/in range.
	 */
	public boolean changeNaturalStats(Stats statType, byte newStatVal)
	{
		boolean success;
		if(stats[statType.value()] + newStatVal < type.getBaseStat(statType, true) || stats[statType.value()] + newStatVal > type.getBaseStat(statType, false) + Stats.MAXIMUMEXTENDED)
			success = false;
		else
			success = true;
        
		stats[statType.value()] = Util.FITBYTE(stats[statType.value()] + newStatVal, type.getBaseStat(statType, true), type.getBaseStat(statType, false) + Stats.MAXIMUMEXTENDED);
        return success;
	}
	
	/**
	 * Finds the first empty valu on the acquiredResist stack and
     * sets it to the new value. If no empty spaces, nothing is changed.
	 * @param resistType		Type of resistance (byte) see defs
	 * @param newResistVal		New value
	 */
	public void addAcquiredResistance(Resistance resistType, byte newResistVal)
	{
        for(byte i = 0; i < acquiredResistance[resistType.value()].length; i++)
            if(acquiredResistance[resistType.value()][i] == 0)
            {
                acquiredResistance[resistType.value()][i] = Util.FITPERCENTAGE(newResistVal);
                return;
            }
    }
    
    /**
     * Empties the first instance of the Resist value pasted to it.
     * @param resistType
     * @param oldResistVal
     */
    public void removeAcquiredResistance(Resistance resistType, byte oldResistVal)
    {
        for(byte i = 0; i < acquiredResistance[resistType.value()].length; i++)
            if(acquiredResistance[resistType.value()][i] == oldResistVal)
            {
                acquiredResistance[resistType.value()][i] = (byte)0;
                return;
            }
    }
	
    /**
     * Directly set the value of an acquired stat.
     * @param statType	Type to stat (byte)
     * @param statVal	New value for stat (byte)
     */
	public void setAcquiredStats(Stats statType, byte statVal)
	{
		acquiredStats[statType.value()] = Util.FITBYTE(statVal, Stats.MAXIMUMNEGADJUSTMENT, Stats.MAXIMUMPOSADJUSTMENT);
	}
	
	/**
	 * Changes a specific stat. For acquired stats.
	 * @param statType	Type of stat (byte)
	 * @param statAdj	How much to adjust by (byte)
	 */
	public void changeAcquiredStats(Stats statType, byte statAdj)
	{
		acquiredStats[statType.value()] = Util.FITBYTE(acquiredStats[statType.value()] + statAdj, Stats.MAXIMUMNEGADJUSTMENT, Stats.MAXIMUMPOSADJUSTMENT);
	}
	
	/**
	 * Sets the guild records to a new linked list of guild records.
	 * @param nGuildRecords LinkedList<GuildReocrd>
	 */
	public void setGuildRecords(LinkedList<GuildRecord> nGuildRecords)
	{
		guildRecords = nGuildRecords;
	}
	
    /**
     * Changes a state.
     * @param stateType
     *      State being changed.
     * @param isStateOn
     * @param source
     *      STATEITEM (if item is changing state) or STATESPELL (if spell or monster is changing state)
     */
	public void setState(PlayerState stateType, boolean isStateOn, byte source)
	{
		if(stateType.isNaturalState())
			source = STATENATURAL; // all of these are based on the first entry.

		states[stateType.value()][source] = isStateOn;
		
		switch(stateType)
		{
		case Drowning:
			if(isStateOn)
				postMessage(name + "is drowning!");
			break;
		case Dead:
			if(isStateOn)
				postMessage(name + "has died.");
			else
				postMessage(name + "has been resurrected.");
			break;
		case Levitating:
			if(isStateOn)
				postMessage(name + "is floating.");
			break;
		case Poisoned:
			if(isStateOn)
				postMessage(name + "is poisoned.");
			break;
		case Stoned:
			if(isStateOn)
				postMessage(name + "is stoned.");
			break;
		case Paralyzed:
			if(isStateOn)
				postMessage(name + "can't move!");
			break;
		case Diseased:
			if(isStateOn)
				postMessage(name + "is diseased.");
			break;
		case InDejenol:
			if(isStateOn)
			{
				// Remove any temporary effects.
				removeEffects();
				// Calculate the proportion the player will heal.
				double healing = (maxHP - hp) / maxHP;
				hp = maxHP; // Refill health
				mp = this.getMaxMP(); // Refill MP 
				if(healing >= 1)
					changeAge((int)(DEFAULTMAXHEALTIME * healing)); // Age the player.
			}
			break;
		}
	}
    
    public void setAbsoluteState(byte stateType, boolean isStateOn, byte source)
    {
        states[stateType][source] = isStateOn;
    }
    
    /**
     * Updates aspects of the player due to timer calls.
     */
    public void timerUpdate()
    {
    	// Player should loose health based on state
    	// Player should die if... well... dead.
    	
    	// TODO
    }
	
    /**
     * Set the player's health points
     * @param newHP
     */
	public void setHP(int newHP)
	{
		hp = Util.FITINT(newHP, 0, maxHP);
	}
	
	/**
	 * Set the players magic points.
	 * @param newMP
	 */
	public void setMP(int newMP)
	{
		mp = Util.FITINT(newMP, 0, getMaxMP());
	}
	
	/**
	 * Set the maximum number of health points the player can presently have.
	 * @param newMaxHP
	 */
	public void setMaxHP(int newMaxHP)
	{
		maxHP = Util.FITINT(newMaxHP, DEFAULTHEALTH, Integer.MAX_VALUE);
	}
	
	/**
	 * Adjust the player's current health points by a specified number.
	 * @param HPAdj
	 */
	public void changeHP(int HPAdj)
	{
		hp = Util.FITINT(hp + HPAdj, 0, maxHP);
	}
	
	/**
	 * Adjust the player's current magic points by a specified number.
	 * @param MPAdj
	 */
	public void changeMP(int MPAdj)
	{
		mp = Util.FITINT(mp + MPAdj, 0, getMaxMP());
	}
	
	/**
	 * Adjust the maximum number of health points the player can have by a specific number.
	 * @param maxHPAdj
	 */
	public void changeMaxHP(int maxHPAdj)
	{
		maxHP = Util.FITINT(maxHP + maxHPAdj, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Set the current amount of attack the player has from items.
	 * @param newItemAttack
	 */
	public void setItemAttack(short newItemAttack)
	{
		attack = Util.FITSHORT(newItemAttack, 0, Short.MAX_VALUE);
	}
	
	/**
	 * Set the current amount of defense the player has from items.
	 * @param newItemDefense
	 */
	public void setItemDefense(short newItemDefense)
	{
		defense = Util.FITSHORT(newItemDefense, 0, Short.MAX_VALUE);
	}
	
	/**
	 * Adjust the current amount of attack the player has from items.
	 * @param itemAttackAdj
	 */
	public void changeItemAttack(short itemAttackAdj)
	{
		attack = Util.FITSHORT(attack + itemAttackAdj, 0, Short.MAX_VALUE);
	}
	
	/**
	 * Adjust the current amount of defense the player has from items
	 * @param itemDefenseAdj
	 */
	public void changeItemDefense(short itemDefenseAdj)
	{
		defense = Util.FITSHORT(defense + itemDefenseAdj, 0, Short.MAX_VALUE);
	}
	
	/**
	 * Set the total amount of gold the player has on hand.
	 * @param newGold
	 */
	public void setGoldOnHand(long newGold)
	{
		goldOnHand = Util.FITLONG(newGold, 0, Long.MAX_VALUE);
	}
	
	/**
	 * Adjust the amount of gold the player has on hand.
	 * @param goldOnHandAdj
	 */
	public void changeGoldOnHand(long goldOnHandAdj)
	{
		goldOnHand = Util.FITLONG(goldOnHand + goldOnHandAdj, 0, Long.MAX_VALUE);
	}
	
	/**
	 * If the player has enough gold, it will spend the amount
	 * provided. Money will be removed from goldOnHand before gold in
	 * the bank.
	 * @param goldAdjustment amount of gold to remove.
	 * @return true if player had enough gold
	 */
	public boolean spendGold(long goldAdjustment)
	{
		if(getTotalGold() < goldAdjustment)
			return false;
		
		if(goldAdjustment >= goldOnHand)
		{
			goldAdjustment -= goldOnHand;
			goldOnHand = 0;
		}
		else
		{
			goldOnHand -= goldAdjustment;
			return true;
		}
		
		bankAccount.changeGold(goldAdjustment);
		return true;
	}
	
	/**
	 * Spends gold from players on hand gold, then (if not enough) from the bank account.
	 * @param goldAdj Positive amount of gold to spend.
	 * @return False if the player doesn't have enough, or if the adjustment is invalid.
	 */
	public boolean spendTotalGold(long goldAdj)
	{
		if(getTotalGold() < goldAdj || goldAdj < 0)
			return false;
		if(goldAdj > goldOnHand)
		{
			goldAdj -= goldOnHand;
			goldOnHand = 0;
			
			bankAccount.changeGold(goldAdj);
		}
		else
			goldOnHand -= goldAdj;
		
		return true;
	}
	
	/**
	 * Set the total number of kills the player has
	 * @param newKills
	 */
	public void setKills(int newKills)
	{
		kills = Util.FITINT(newKills, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Set the total number of times the player has died.
	 * @param newDeaths
	 */
	public void setDeaths(int newDeaths)
	{
		deaths = Util.FITINT(newDeaths, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Set the total number of item quests the player has completed.
	 * @param newFinishedItemQuests
	 */
	public void setFinishedItemQuests(int newFinishedItemQuests)
	{
		finishedItemQuests = Util.FITINT(newFinishedItemQuests, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Set the total number of monster quests the player has completed.
	 * @param newFinishedMonQuests
	 */
	public void setFinishedMonQuests(int newFinishedMonQuests)
	{
		finishedMonQuests = Util.FITINT(newFinishedMonQuests, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Set the players current age.
	 * @param newAge
	 */
	public void setAge(int newAge)
	{
		age = Util.FITINT(newAge, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Adjust the number of kills the player has done.
	 * @param newKills
	 */
	public void changeKills(int newKills)
	{
		kills = Util.FITINT(kills + newKills, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Adjust the number of times the player has died.
	 * @param newDeaths
	 */
	public void changeDeaths(int newDeaths)
	{
		deaths = Util.FITINT(deaths + newDeaths, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Adjust the number of times a player has completed item quests.
	 * @param newFinishedItemQuests
	 */
	public void changeFinishedItemQuests(int newFinishedItemQuests)
	{
		finishedItemQuests = Util.FITINT(finishedItemQuests +newFinishedItemQuests, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Adjust the number of times the player has completed monster quests.
	 * @param newFinishedMonQuests
	 */
	public void changeFinishedMonQuests(int newFinishedMonQuests)
	{
		finishedMonQuests = Util.FITINT(finishedMonQuests + newFinishedMonQuests, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Adjust the age of the player.
	 * @param newAge Change in age in days
	 */
	public void changeAge(int newAge)
	{
		age = Util.FITINT(age + newAge, 0, Integer.MAX_VALUE);
		
		short weeks, years;
		
		years = (short)(newAge / 365);
		newAge %= 365;
		weeks = (short)(newAge / 7);
		newAge %= 7;
		
		String ageString = name + " has aged ";
		if(years != 0)
			ageString += years + " years ";
		if(weeks != 0)
			ageString += weeks + " weeks ";
		if(newAge != 0)
			ageString += newAge + " days";
		
		ageString += ".";
		
		// Check and see if the player is dead.
		if(newAge > 0)
			checkNaturalDeath();
		
		postMessage(ageString);
	}
	
	/**
	 * Determines if the player should die of natural causes.
	 */
	private void checkNaturalDeath()
	{
		short maxAge = type.getMaxAge();
		
		int diff = maxAge - (age / 365);
		
		Random rand = new Random(System.nanoTime());
		double chance = rand.nextDouble();
		boolean died = false;
		
		died = (diff < 1) ? (chance < DEFAULTNATDEATH) : (chance < DEFAULTNATDEATH * Math.pow(2, diff));
		
		if(died)
			this.setState(PlayerState.Dead, true, STATENATURAL);
	}
	
	public void setBankAccount(BankAccount nBankAccount)
	{
        if(nBankAccount == null)
            return;
		bankAccount = nBankAccount;
	}
	
	/**
	 * Add a specific companion
	 * @param companionIndex	Index of the companion
	 * @param nCompanion		The companion
	 * @param bind				Bind level of companion
	 * @return	True if done (not done if index already has a companion)
	 */
	public boolean addCompanion(byte companionIndex, Companion nCompanion)//, byte bind)
	{
		if(companions[companionIndex] != null)
			return false;
		// TODO
		companions[companionIndex] = nCompanion;
	//	companionBind[companionIndex] = Util.FITBYTE(bind, MINBIND, MAXBIND);
		return true;
	}
	
	/**
	 * Set a specific companion
	 * @param companionIndex	Index of the companion
	 * @param nCompanion		The companion
	 * @param bind				Bind level of companion
	 */
	public void setCompanion(byte companionIndex, Companion nCompanion)//, byte bind)
	{
		companions[companionIndex] = nCompanion;
	//	companionBind[companionIndex] = Util.FITBYTE(bind, MINBIND, MAXBIND);
	}
	
	/**
	 * Set the companions
	 * @param nCompanions
	 * @return true if successful
	 */
	public boolean setCompanions(Companion[] nCompanions)
	{
		if(nCompanions.length > Player.MAXCOMPANIONS)
			return false;
		
		companions = nCompanions;
		return true;
	}
	
	/**
	 * Sets the companion bindings
	 * @param nBinds
	 * @return
	 */
/*	public void setCompanionBind(byte[] nBinds)
	{
		byte i = 0;
		for(; i < nBinds.length; i++)
		{
			if(i < companionBind.length)
				companionBind[i] = nBinds[i];
		}
		
		for(;i < companionBind.length; i++)
			companionBind[i] = MINBIND;
	}*/
	
	/**
	 * Sets a companion in the next available slot
	 * @param nCompanion
	 * @param bind
	 * @return True if an empty slot was found.
	 */
	public boolean setNextCompanion(Companion nCompanion)
	{
		for(byte i = 0; i < companions.length; i++)
			if(addCompanion(i, nCompanion))
				return true;
		
		return false;
	}
	
	/**
	 * Sets an index to a specific item.
	 * @param itemIndex
	 * @param nItem
	 * @return
	 */
	public boolean setItem(byte itemIndex, ItemInstance nItem)
	{
//		if(items[itemIndex] != null)
//			return false;
		
		items[itemIndex] = nItem;
		return true;
	}
	
	public boolean setItems(ItemInstance[] nItems)
	{
		items = nItems;
		return true;
	}
	
	public boolean setNextItem(ItemInstance nItem)
	{
		for(byte i = 0; i < items.length; i++)
			if(setItem(i, nItem))
				return true;
		
		return false;
	}
	
	/**
	 * Can this player equip the item?
	 * Check to see if the player meets the guild, stats, free slot requirements
	 * of the item and if the item is of the kind that is equippable.
	 * @param tItem
	 * @return true if equippable
	 */
	public boolean canPlayerEquip(Item tItem)
	{
		// Is it even possible to equip the item?
		// Can't equip if it doesn't exist.
		if(tItem == null)
			return false;
		
		// Can't equip, if it is not equipment
		if(tItem.getItemType().getEquippingPart() == BodyParts.Objects)
			return false;
		
		// Where does it go?
		BodyParts bodyPart = tItem.getItemType().getEquippingPart();
		
		// Can't equip if already have something equipped there.
		if(equipment[bodyPart.value()] != Util.NOTHING)
			return false;
		
		// Can't equip if the player is incapable of equipping.
		for(Stats st : Stats.values())
			if(tItem.getStat(st) > this.getTotalStat(st))
				return false;
		
		// Can't equip if guild doesn't allow it.
		if(activeGuild == null)
			return false;
		
		GuildReference tGuild = tItem.getGuild(activeGuild.getGuild());
		if(tGuild == null)
			return false;
		
		// Can't equip if not high enough level in guild.
		if(tGuild.getLevel() > activeGuild.getLevel())
			return false;
		
		return true;
	}
	
	/**
	 * Equips a specific item instance, if possible. See equipItem(byte itemIndex)
	 * and canPlayerEquip(Item item) for more info.
	 * @param item ItemInstance to equip
	 * @return true if successful
	 */
	public boolean equipItem(ItemInstance item)
	{
		for(byte i = 0; i < MAXITEMSONHAND; i++)
			if(items[i] == item)
				return equipItem(i);
		
		return false;
	}
	
	/**
	 * Equips an item based on its index in the players' item list.
	 * @param itemIndex
	 * @return
	 */
	public boolean equipItem(byte itemIndex)
	{
		if(itemIndex < 0 || itemIndex > items.length || items[itemIndex].getItem().getItemType().getEquippingPart() == BodyParts.Objects) 
            return false;
        
        // check each items stat requirement against the players natural stat.
        for(byte i = 0; i < stats.length; i++)
        {
            if(items[itemIndex].getItem().getStatAdjustment(Stats.type(i)) > stats[i])
                return false;
        }
        
        ListIter<GuildReference> tGuild = items[itemIndex].getItem().getGuilds().getIterator();
        while(tGuild.next())
            if(tGuild.element().getGuild() == activeGuild.getGuild())
                break; // Guild was found in allowed list.
        
        if(tGuild.element().getGuild() != activeGuild.getGuild()) // Guild not allowed for item.
            return false;
        
        if(tGuild.element().getLevel() > activeGuild.getLevel())
            return false; // Level required by item is too high!
        
        equipment[items[itemIndex].getItem().getItemType().getEquippingPart().value()] = itemIndex;
        
        updateItemStates();
        updateItemResist();
        changeItemAttack(items[itemIndex].getItem().getAttackModifier());
        changeItemDefense(items[itemIndex].getItem().getDefenseModifier());
        return true;
	}
	
	/**
	 * Parses everything that is currently equipped to checks if it can still be equipped.
	 * If not, unequips it.
	 */
	public void updateEquipment()
	{
		boolean canEquip;
		for(byte i = 0; i < equipment.length; i++)
		{
			if(equipment[i] >= 0 && equipment[i] < items.length)
			{
				ItemInstance tItem = items[equipment[i]];
				// Is it even possible to equip the item?
				// Can't equip if it doesn't exist.
				canEquip = true;
				if(tItem == null)
					canEquip = false;
				
				// Can't equip, if it is not equipment
				if(tItem.getItem().getItemType().getEquippingPart() == BodyParts.Objects)
					canEquip = false;
				
				// Can't equip if the player is incapable of equipping.
				for(Stats st : Stats.values())
					if(tItem.getItem().getStat(st) > this.getTotalStat(st))
						canEquip = false;
				
				// Can't equip if guild doesn't allow it.
				GuildReference tGuild = tItem.getItem().getGuild(activeGuild.getGuild());
				if(tGuild == null)
					canEquip = false;
				
				// Can't equip if not high enough level in guild.
				if(tGuild.getLevel() > activeGuild.getLevel())
					canEquip = false;
				
				if(!canEquip)
					this.unequipItem(i);
				
			}
		}
	}
    
    public void setEquipmentIndex(byte equipIndex, byte itemIndex)
    {
        if(equipIndex < 0 || equipIndex > BodyParts.values().length)
            return;
        
        if(itemIndex != Util.NOTHING && (itemIndex < 0 || itemIndex >= MAXITEMSONHAND))
            equipment[equipIndex] = Util.NOTHING;
        
        equipment[equipIndex] = itemIndex;
    }
    
    /**
     * Un equips a specific item instance if it exists.
     * @param item
     */
    public void unequipItem(ItemInstance item)
    {
    	if(item != null && this.isItemEquipped(item))
    		unequipItem(item.getItem().getItemType().getEquippingPart().value());
    }
	
    /**
     * Unequip an item.
     * @param equipIndex Index in equipment.
     */
	public void unequipItem(byte equipIndex)
	{
		if(equipIndex < 0 || equipIndex >= equipment.length || equipment[equipIndex] == Util.NOTHING) // invalid index
			return;
		else if(items[equipment[equipIndex]].isCursed()) // item is cursed
			postMessage(items[equipment[equipIndex]].getItem().getName() + " can't be unequipped.");
		else // can unequip item
		{
            Item tItem = items[equipment[equipIndex]].getItem();
			equipment[equipIndex] = Util.NOTHING;
            
            for(Stats st : Stats.values())
                changeAcquiredStats(st, (byte)((~tItem.getStatAdjustment(st)) + 1));
            
            updateItemStates(); // update item states
            updateItemResist(); // update item resistance bonuses
            
            changeItemAttack((short)((~tItem.getAttackModifier()) + 1));
            changeItemDefense((short)((~tItem.getDefenseModifier()) + 1));
		}
	}
	
	public void swapItems(byte indexA, byte indexB)
	{
		ItemInstance tItem = items[indexA];
		
		boolean aEquip = isItemEquipped(indexA);
		boolean bEquip = isItemEquipped(indexB);
		
		items[indexA] = items[indexB];
		items[indexB] = tItem;
		
		if(aEquip)
			equipment[items[indexB].getItem().getItemType().getEquippingPart().value()] = indexB;
		if(bEquip)
			equipment[items[indexA].getItem().getItemType().getEquippingPart().value()] = indexA;
	}
	
	/**
	 * Determines if a particular item is equipped.
	 * @param itemIndex	The index of the item for the player.
	 * @return	true if the item is equipped.
	 */
	public boolean isItemEquipped(byte itemIndex)
	{
		if(items[itemIndex] == null)
			return false;
		
		return (equipment[items[itemIndex].getItem().getItemType().getEquippingPart().value()] == itemIndex);
	}
	
	/**
	 * Determines if a particular itemInstance is equipped.
	 * @param oldItem An itemInstance for an item the player has on hand.
	 * @return True if the item is equipped.
	 */
	public boolean isItemEquipped(ItemInstance oldItem)
	{
		if(oldItem == null)
			return false;
		
		for(byte i = 0; i < items.length; i++)
			if(items[i] == oldItem && equipment[oldItem.getItem().getItemType().getEquippingPart().value()] == i)
				return true;
		
		return false;
	}
    
    /**
     * Parses the items and changes the states accordingly.
     * Primarily used when the player leaves the dungeon or changes
     * equipment.
     *
     */
    public void updateItemStates()
    {
        for(byte i = 0; i < states.length; i++)
        {
            states[i][STATEITEM] = states[i][STATENATURAL];
        }
        
        for(byte i = 0; i < equipment.length; i++)
        {
            if(equipment[i] != Util.NOTHING)
            {
                for(byte j = 0; j < Item.ITEMSPECIAL_MAX; j++)
                {
                    if(items[equipment[i]].getItem().getSpecials()[j].getType() == ItemSpecials.ITEMSPECIAL_STATE)
                        states[items[equipment[i]].getItem().getSpecials()[j].getState().value()][STATEITEM] = items[equipment[i]].getItem().getSpecials()[j].isStateOn(); 
                }
            }
        }
    }
    
    /**
     * Parses the items and changes the acquired resistances accordingly.
     *
     */
    public void updateItemResist()
    {
    	// TODO: Maybe something should be done about this.
    	
        // First clear all present item resistances.
        for(byte i = 0; i < Resistance.values().length; i++)
        {
            for(byte j = 0; j < acquiredResistance[i].length; j++)
            {
                acquiredResistance[i][j] = (byte)0;
            }
        }
        
        for(byte i = 0; i < equipment.length; i++)
        {
            if(equipment[i] != Util.NOTHING)
            {
                for(byte j = 0; j < Item.ITEMSPECIAL_MAX; j++)
                {
                    if(items[equipment[i]].getItem().getSpecials()[j].getType() == ItemSpecials.ITEMSPECIAL_RESISTANCE)
                    {
                        for(byte k = 0; k < acquiredResistance[0].length; k++)
                        {
                            if(acquiredResistance[items[equipment[i]].getItem().getSpecials()[j].getResistanceType().value()][k] == 0)
                            {
                                acquiredResistance[items[equipment[i]].getItem().getSpecials()[j].getResistanceType().value()][k] = items[equipment[i]].getItem().getSpecials()[j].getResistanceAmount();
                                k = (byte)acquiredResistance[0].length;
                            }
                        }
                    } 
                }
            }
        }
    }
    
    /**
     * Removes all temporary effects on the player.
     *
     */
    public void removeEffects()
    {
        for(byte i = 0; i < states.length; i++)
        {
            states[i][STATESPELL] = states[i][STATENATURAL];
        }
        
        removeAcquiredResist();
        
        updateItemResist();
    }
    
    /**
     * Remove all acquired resistances.
     */
    public void removeAcquiredResist()
    {
        for(byte i = 0; i < acquiredResistance.length; i++)
        {
            for(byte j = 0; j < acquiredResistance[i].length; j++)
            {
                acquiredResistance[i][j] = (byte)0;
            }
        }
    }
    
    /**
     * Adds a new guild record to this player.
     * @param newGuild
     * @return False if not successful, that is, the guild record already exists.
     */
    public boolean addGuildRecord(GuildRecord newGuild)
    {
        ListIter<GuildRecord> tNode = guildRecords.getIterator();
        
        while(tNode.next())
            if(tNode.element().getGuild() == newGuild.getGuild())
                return false;
        
        guildRecords.insert(newGuild);
        return true;
    }
    
    public boolean removeGuildRecord(GuildRecord oldGuild)
    {
        if(guildRecords.remove(oldGuild) == null)
            return false;
        else
            return true;
            
    }
    
    /**
     * Adds an item to the player
     * @param newItem
     * @return True if the player could hold the item.
     */
    public boolean addItem(ItemInstance newItem)
    {
    	for(byte i = 0; i < items.length; i++)
    		if(items[i] == null)
    		{
    			items[i] = newItem;
    			return true;
    		}
    	return false;
    }
    
    /**
     * Removes an item from the player. Also unequips, if the item is equiped.
     * @param oldItem
     * @return  boolean True if the item was found and removed.
     */
    public boolean removeItem(ItemInstance oldItem)
    {
        for(byte i = 0; i < items.length; i++)
        {
            if(items[i] == oldItem)
            {
            	// Eliminate the item from inventory.
                items[i] = null;
                
                // If the item is equippable (that is, not an 'objects' body part type)
                // Then unequip it.
                BodyParts bodyPart = oldItem.getItem().getItemType().getEquippingPart();
                if(bodyPart != BodyParts.Objects && equipment[bodyPart.value()] == i)
                    equipment[bodyPart.value()] = Util.NOTHING;
                return true;                
            }
        }
        
        return false;
    }
    
    /**
     * Remove the specified crest from the player entirely.
     * @param crest Crest to remove (Item)
     */
    public void removeCrest(Item crest)
    {
    	// Can't remove nothing.
    	if(crest == null)
    		return;
    	
    	// Check if it is on the player.
    	for(byte i = 0; i < items.length; i++)
    		if(items[i].getItemID() == crest.getID())
    			removeItem(items[i]);
    	
    	// Check if it is in the player's bank account
    	ListIter<ItemInstance> tItem = bankAccount.getItems().getIterator();
    	while(tItem.next())
    		if(tItem.element().getItemID() == crest.getID())
    			bankAccount.removeItem(tItem.element());
    }
    
    /**
     * Sets the player's current active guild.
     * Also checks to ensure only allowed equipment is equipped.
     * @param nActiveGuild
     * @return True if successful
     */
    public boolean setActiveGuild(GuildRecord nActiveGuild)
    {
        if(nActiveGuild == null)
            return false;
        
        activeGuild = nActiveGuild;
        updateEquipment();
        return true;
    }
    
    public void setSpellBuffer(byte bufferIndex, SpellReference spell)
    {
        spellBuffers[bufferIndex] = spell;
    }
    
    public void setSpellBook(SpellBook newSpellBook)
    {
        spellBook = newSpellBook;
    }
	
	/**
	 * Attempts to lose the player. This will roll the dice a maxium of three times
	 * to going through the levels of lost the player can be. A player won't move to
	 * a higher level unless they are at a lower level.
	 *
	 */
	public void setLost()
	{
		// last is based on: players perception + depth + current square is visited
		// note: this method is called before the player enters a new square
		// note: shouldn't change lost state ifplayer is already lost.
		// note: location -> direction -> depth (priority)
		
		Random random = new Random(System.currentTimeMillis());
		int visited = (dataBank.getMap().getMapSquare(coords).isVisited()) ? 1 : 2;
		
		if(!states[PlayerState.LostLocation.value()][STATENATURAL])
		{
			states[PlayerState.LostLocation.value()][STATENATURAL] = (random.nextDouble() * 100) > (chanceOfDetection() / visited / (coords.getZ() / 2));
			if(states[PlayerState.LostLocation.value()][STATENATURAL])
				postMessage(name + " is lost!");
		}
		
		if(states[PlayerState.LostLocation.value()][STATENATURAL])
		{
			if(!states[PlayerState.LostDirection.value()][STATENATURAL])
				states[PlayerState.LostDirection.value()][STATENATURAL] = (random.nextDouble() * 100) > (chanceOfDetection() / visited / (coords.getZ() / 2));
			
			if(states[PlayerState.LostDirection.value()][STATENATURAL])
				states[PlayerState.LostLevel.value()][STATENATURAL] = (random.nextDouble() * 100) > (chanceOfDetection() / visited / (coords.getZ() / 2));
		}
	}
	
	/**
	 * Tries to find the coordinates of the player. Goes through the different
	 * types of 'lost' the player can be and rolls the dice to see if she is
	 * still that type. Note, will only lower the player down one notch per call.
	 *
	 */
	public void setUnlost()
	{
		Random random = new Random(System.currentTimeMillis());
		
		if(states[PlayerState.LostLevel.value()][STATENATURAL])
		{
			states[PlayerState.LostLevel.value()][STATENATURAL] = ((random.nextDouble() * 100) < chanceOfDetection());
			if(!states[PlayerState.LostLevel.value()][STATENATURAL])
				postMessage(name + " is on level " + coords.getZ());
		}
		else if(states[PlayerState.LostDirection.value()][STATENATURAL])
		{
			states[PlayerState.LostDirection.value()][STATENATURAL] = ((random.nextDouble() * 100) < chanceOfDetection());
			if(!states[PlayerState.LostDirection.value()][STATENATURAL])
			{
				switch(coords.getDirection())
				{
				case North:
					postMessage(name + " is facing north.");
					break;
				case East:
					postMessage(name + " is facing east.");
					break;
				case South:
					postMessage(name + " is facing south.");
					break;
				case West:
					postMessage(name + " is facing west.");
					break;
				}
			}
		}
		else if(states[PlayerState.LostLocation.value()][STATENATURAL])
		{
			states[PlayerState.LostLocation.value()][STATENATURAL] = ((random.nextDouble() * 100) < chanceOfDetection());
			if(!states[PlayerState.LostLocation.value()][STATENATURAL])
				postMessage(name + " is at " + coords.getX() + ", " + coords.getY());
		}
	}
	
	public double chanceOfDetection()
	{
		// note: values should exceed 2 * map depth
		// this is so that it is possible for the character to be very deep
		// and still never get lost?
		// TODO: Should be affected by wisdom, intelligence and relevant player skills
		// Such that 'maxed out' = ~98%
		return 50.0;
	}
	
	/**
	 * Says hello, unless this player is dead, then notes that.
	 */
	public void sayHello()
	{
		if(isInState(PlayerState.Dead))
			postMessage(name + " is dead...");
		else
			postMessage(name + ": Hello!");
		
		// TODO Play appropriate sounds.
	}
	
	/**
	 * Post a message through the player.
	 * @param nMessage	The message to be posted (String)
	 */
	public void postMessage(String nMessage)
	{
		dataBank.getMessenger().postMessage(nMessage);
	}
	
	/**
	 * Setup player for Dungeon.
	 */
	public void enterDungeon()
	{
		states[PlayerState.InDejenol.value()][STATENATURAL] = false;
		coords = dataBank.getMap().getExitCoords();
	}
	
	/**
	 * Takes a monster and attacks it. Returns the amount of damage done.
	 * Based on http://www.mordor.uni.cc/index.php?title=Character_Attack_Formulas
	 * @param monster MonsterInstance
	 * @return int
	 */
	public int attackMonster(MonsterInstance monster)
	{
		int GLvl = getGuildSkillTop(GuildSkill.Fighting);
		float FightingMod = activeGuild.getGuild().getSkillFactor(GuildSkill.Fighting);
		Item weapon = getEquipment(BodyParts.Weapon).getItem();
		Random random = new Random(System.currentTimeMillis());
		
		double DamMod = 0.6 + (((Math.log(100 + (GLvl / 1.75)) - 2.3) * (FightingMod * FightingMod)) / 2);
		DamMod *= weapon.getDamageModifier();
		
		int criticalHitCount = getGuildSkillTop(GuildSkill.CriticalHit);
		if(weapon.hasCriticalHit())
			criticalHitCount++;
		
		int backstabCount = getGuildSkillTop(GuildSkill.BackStabbing);
		if(weapon.hasBackStabbing())
			backstabCount++;
		
		if(criticalHitCount > 0 && random.nextBoolean())
			DamMod += 5;
		
		if(backstabCount > 0 && random.nextInt(100) < 75)
			DamMod += 2;
		
		if(!isInState(PlayerState.SeeInvisible) && monster.getMonster().hasAbility(MonsterAbility.Invisible))
			DamMod /= 2;
		
		DamMod += (type.getSize().value() - monster.getMonster().getSize().value()) * 0.1;
		
		if(DamMod > 1)
			DamMod = DamMod - (Math.pow(Math.log(DamMod), 2.0));
		
		int str = getStat(Stats.Strength);
		int BSV = (int)(str - (Math.log(str) * ((Math.log(str) - 2.4) * 2.25))) + 1;
		int StrMod1 = (int)((random.nextInt() * ((str + 10) / 2)) + ((str + 10) / 4)) / 10;
		double StrMod2 = (BSV / 30);
		
		double LvlMod = (Math.log(GLvl + 5) + 1) / Math.log(1.2);
		
		double baseDamage = LvlMod * StrMod1 * StrMod2 * DamMod;
		int finalDamage = (int)(baseDamage + random.nextInt((getAttack() / 8)) + 1);
		
		// The following is the correct formula if monster Defense wasn't 0
		// int finalDamage = (int)(baseDamage + random.nextInt(((monster.getMonster().getDefense() - getAttack()) / 8)) + 1);
		
		// Roll the 
		if(getAttack() == 0)
		{
			if(random.nextBoolean())
			{
				monster.changeHits(finalDamage);
				return finalDamage;
			}
		
			return 0;
		}
		else
		{
			if(random.nextDouble() < ((0.5 * getAttack()) / getAttack()))
			{
				monster.changeHits(finalDamage);
				return finalDamage;
			}
			return 0;
		}
		
		/*
		Chance of Hit
		if Atk = 0 and Def = 0 then 50
		if Attk > Def -> .5 + ((.5 * (atk - def)) / atk)
		if( atk < def -> 0.5 - ((.5 * (def - atk)) / def)
		
		note: values should range 1 to 97
		note: the def bug means def always = 0
		*/
	}
	
	/**
	 * Retrieves a string representation of the players current location.
	 * @return String
	 */
	public String getCoordString()
	{
		String coordinateString = "";
		coordinateString += (states[PlayerState.LostLocation.value()][STATENATURAL]) ? "?" : "" + coords.getX() + ", " + coords.getY() + ", ";
		coordinateString += (states[PlayerState.LostLevel.value()][STATENATURAL]) ? "?" : "" + coords.getZ();
		switch(coords.getDirection())
		{
		case North:
			coordinateString += (states[PlayerState.LostDirection.value()][STATENATURAL]) ? "?" : ", NORTH";
			break;
		case East:
			coordinateString += (states[PlayerState.LostDirection.value()][STATENATURAL]) ? "?" : ", EAST";
			break;
		case South:
			coordinateString += (states[PlayerState.LostDirection.value()][STATENATURAL]) ? "?" : ", SOUTH";
			break;
		case West:
			coordinateString += (states[PlayerState.LostDirection.value()][STATENATURAL]) ? "?" : ", WEST";
			break;
		}
		
		return coordinateString;
	}
	
	public boolean writePlayer(DataOutputStream dos)
	{
        try
        {
        	// Write ID size: 0
            dos.writeShort(ID);
            
            // create a 1 dimensional boolean array the same size
            // as the entire 2D states array.
            boolean[] tBool = new boolean[states.length * states[0].length];
            long[] tLong; // long array to compress into
            
            // fill tBool w/ all the values in states, flattened into 1D
            for(byte i = 0, k = 0; i < states.length; i++)
            {
                for(byte j = 0; j < states[i].length; j++, k++)
                {
                    tBool[k] = states[i][j];
                }
            }
            
            // Compress tBool
            tLong = Util.COMPRESSBOOLTOLONG(tBool);
            
            // Write first part of stats; size = 2
            dos.writeInt(states.length); // write size of first dim. of states
            dos.writeInt(states[0].length); // write size of sec. dim. of states
            dos.writeInt(tLong.length); // write size of long array
            
            // write longs, size = 14
            for(int i = 0; i < tLong.length; i++)
                dos.writeLong(tLong[i]);

            // Write Coordinates & alignment : size = 22 (for states = 21, max state stacks = 3) 
            dos.writeByte(coords.getX());
            dos.writeByte(coords.getY());
            dos.writeByte(coords.getZ());
            dos.writeByte(coords.getDirection().value());
            dos.writeByte(alignment.value());
            
            // Write natural stats. size = 27
            dos.writeInt(stats.length);
            for(byte i = 0; i < stats.length; i++)
                dos.writeByte(stats[i]);
            
            dos.writeInt(acquiredResistance.length); 
            dos.writeInt(acquiredResistance[0].length);
            for(byte i = 0; i < acquiredResistance.length; i++)
                for(byte j = 0; j < acquiredResistance[0].length; j++)
                    dos.writeByte(acquiredResistance[i][j]);
            
            dos.writeInt(acquiredStats.length);
            for(byte i = 0; i < acquiredStats.length; i++)
                dos.writeByte(acquiredStats[i]);

            dos.writeByte(raceID);
            
            dos.writeInt(equipment.length);
            for(byte i = 0; i < equipment.length; i++)
            {
            	if(equipment[i] < 0)
            		dos.writeByte(Util.NOTHING);
            	else
            		dos.writeByte(equipment[i]);
            }
            
            dos.writeShort(attack);
            dos.writeShort(defense);
            
            dos.writeInt(items.length);
            for(byte i = 0; i < items.length; i++)
            {
            	if(items[i] == null)
            		dos.writeShort(Util.NOTHING);
            	else
                    items[i].writeItemInstance(dos);
            }
            
            // Write companions
            dos.writeInt(companions.length);
            for(byte i = 0; i < companions.length; i++)
            {
            	if(companions[i] == null)
            		dos.writeShort(Util.NOTHING);
            	else
                    companions[i].writeCompanion(dos);
            }

            dos.writeShort(age);
            
            dos.writeInt(skills.length);
            for(byte i = 0; i < skills.length; i++)
                dos.writeShort(skills[i]);
            
            dos.writeInt(hp);
            dos.writeInt(mp);
            dos.writeInt(maxHP);
            
            dos.writeInt(kills);
            dos.writeInt(deaths);
            dos.writeInt(finishedItemQuests);
            dos.writeInt(finishedMonQuests);

            dos.writeLong(goldOnHand);
            
            bankAccount.writeBankAccount(dos);
            
            dos.writeInt(guildRecords.getSize());
            ListIter<GuildRecord> tGuild = guildRecords.getIterator();
            while(tGuild.next())
                tGuild.element().writeGuildRecord(dos);

            if(activeGuild == null)
            	dos.writeByte(Util.NOTHING);
            else
            	dos.writeByte(activeGuild.getGuildID());
            
            spellBook.writeSpellBook(dos);
            
            dos.writeInt(spellBuffers.length);
            for(byte i = 0; i < spellBuffers.length; i++)
            {
                if(spellBuffers[i] == null)
                    dos.writeShort(Util.NOTHING);
                else
                    dos.writeShort(spellBuffers[i].getSpellID()); // when loading, parse personal spell book for this reference
            }
            
            // Write time played and date created.
            dos.writeDouble(this.getTimePlayerHours());
            dos.writeLong(dateCreated);
            
            // Write sanctuary point.
            dos.writeInt(sanctuary.length);
            for(int i = 0; i < sanctuary.length; i++)
            	dos.writeByte(sanctuary[i]);
            
            dos.writeUTF(name);
        }
        catch(Exception e)
        {
            System.err.println("Player write error. PlayerID : " + ID + " Error: " +  e);
            return false;
        }
		return true;
	}
	
	public static final Player readPlayer(DataInputStream dis, DataBank dataBank, LinkedList<GuildRecord> guildRecordLoadList, LinkedList<ItemInstance> itemInstLoadList, LinkedList<MonsterInstance> monInstLoadList, LinkedList<SpellReference> spellRefLoadList)
	{
        Player newPlayer = null;
        try
        {
            newPlayer = new Player(dis.readShort(), dataBank);
            
            int dim1, dim2, numLongs;
            byte i;
            
            dim1 = dis.readInt();
            dim2 = dis.readInt();
            numLongs = dis.readInt();
            
            long[] longs = new long[numLongs];
            for(i = 0; i < numLongs; i++)
                longs[i] = dis.readLong();
            
            boolean[] tBool = Util.UNCOMPRESSLONGTOBOOL(longs, dim1 * dim2);
            
            i = 0;
            for(byte k = 0; i < PlayerState.values().length; i++)
            {
                if(i < dim1)
                {
                    for(byte j = 0; j < MAXSTATESTACK; j++, k++)
                    {
                        if(j < dim2)
                            newPlayer.setAbsoluteState(i, tBool[k], j);
                        else // For some reason dim2 is too small
                            newPlayer.setAbsoluteState(i, tBool[STATENATURAL], j);
                    }
                    
                    if(MAXSTATESTACK < dim2)
                        k += dim2 - MAXSTATESTACK; // For some reason dim2 is too big
                }
                else
                {
                    for(byte j = 0; j < MAXSTATESTACK; j++, k++)
                    {
                        newPlayer.setAbsoluteState(i, false, j);
                    }
                }
            }
            
            newPlayer.coords = new Coord(dis.readByte(), dis.readByte(), dis.readByte(), Direction.type(dis.readByte()));
            
            newPlayer.setAlignment(Alignment.type(dis.readByte()));
            
            dim1 = dis.readInt();
            
            for(Stats st : Stats.values())
            {
            	i = st.value();
                if(st.value() < dim1)
                    newPlayer.setNaturalStat(st, dis.readByte());
                else
                    newPlayer.setNaturalStat(st, Stats.MINIMUMVALUE);
            }
            
            if(Stats.values().length < dim1)
                for(;i < dim1; i++)
                    dis.readByte();
            
            dim1 = dis.readInt();
            dim2 = dis.readInt();
            
            for(i = 0; i < Resistance.values().length; i++)
            {
                byte j;
                for(j = 0; j < MAXRESISTSTACK; j++)
                {
                    if(j < dim2)
                        newPlayer.addAcquiredResistance(Resistance.type(i), dis.readByte());
                    else
                        newPlayer.addAcquiredResistance(Resistance.type(i), (byte)0);
                }
                
                if(MAXRESISTSTACK < dim2)
                    for(; j < dim2; j++)
                        dis.readByte();
            }
            
            if(Resistance.values().length < dim1)
                for(; i < dim1; i++)
                    for(byte j = 0; j < dim2; j++)
                        dis.readByte();
            
            dim1 = dis.readInt();
            for(Stats st : Stats.values())
            {
            	i = st.value();
                if(st.value() < dim1)
                    newPlayer.setAcquiredStats(st, dis.readByte());
                else
                    newPlayer.setAcquiredStats(st, (byte)0);
            }
            
            if(Stats.values().length < dim1)
                for(;i < dim1; i++)
                    dis.readByte();
            
            newPlayer.setRaceID(dis.readByte());
            
            dim1 = dis.readInt();
            for(i = 0; i < BodyParts.values().length; i++)
            {
                if(i < dim1)
                    newPlayer.setEquipmentIndex(i, dis.readByte());
                else
                    newPlayer.equipItem(Util.NOTHING);
            }
            
            if(BodyParts.values().length < dim1)
                for(; i < dim1; i++)
                    dis.readByte();
            
            newPlayer.setItemAttack(dis.readShort());
            newPlayer.setItemDefense(dis.readShort());
            
            dim1 = dis.readInt();
            ItemInstance nItem = null;
            for(i = 0; i < MAXITEMSONHAND; i++)
            {
                if(i < dim1)
                {
                    nItem = ItemInstance.readItemInstance(dis);
                    newPlayer.setItem(i, nItem);
                    if(nItem != null)
                    	itemInstLoadList.insert(nItem);
                }
                else
                    newPlayer.setItem(i, null);
            }
            
            for(; i < dim1; i++)
                ItemInstance.readItemInstance(dis);
            
            // Read companions
            dim1 = dis.readInt();
            Companion nMon = null;
            for(i = 0; i < MAXCOMPANIONS; i++)
            {
                if(i < dim1)
                {
                    nMon = Companion.readCompanion(dis);
                    newPlayer.setCompanion(i, nMon);
                    monInstLoadList.insert(nMon);
                }
                else
                    newPlayer.setCompanion(i, null);
            }
            
            for(; i < dim1; i++)
            	Companion.readCompanion(dis);
            
            newPlayer.setAge(dis.readShort());
            
            dim1 = dis.readInt();
            for(i = 0; i < PlayerSkill.values().length; i++)
            {
                if(i < dim1)
                    newPlayer.setPlayerSkill(PlayerSkill.type(i), dis.readShort());
                else
                    newPlayer.setPlayerSkill(PlayerSkill.type(i), (short)0);
            }
            
            for(; i < dim1; i++)
                dis.readShort();
            
            newPlayer.setHP(dis.readInt());
            newPlayer.setMP(dis.readInt());
            newPlayer.setMaxHP(dis.readInt());

            newPlayer.setKills(dis.readInt());
            newPlayer.setDeaths(dis.readInt());
            newPlayer.setFinishedItemQuests(dis.readInt());
            newPlayer.setFinishedMonQuests(dis.readInt());
            
            newPlayer.setGoldOnHand(dis.readLong());
            
            BankAccount tBank = BankAccount.readBankAccount(dis, itemInstLoadList);
            if(tBank != null)
                newPlayer.setBankAccount(tBank);
            
            dim1 = dis.readInt();
            GuildRecord tGuild;
            for(i = 0; i < dim1; i++)
            {
                tGuild = GuildRecord.readGuildRecord(dis);
                if(tGuild != null)
                {
                    newPlayer.addGuildRecord(tGuild);
                    guildRecordLoadList.insert(tGuild);
                }
            }
            
            tGuild = newPlayer.getGuildRecord(dis.readByte());
            if(tGuild != null)
                newPlayer.setActiveGuild(tGuild);
            else
                newPlayer.setActiveGuild(newPlayer.getGuildRecords().getFirst());
            
            SpellBook tSpellBook = SpellBook.readSpellBook(dis, spellRefLoadList);
            if(tSpellBook != null)
                newPlayer.setSpellBook(tSpellBook);
            
            dim1 = dis.readInt();
            SpellReference tSpellRef;
            for(i = 0; i < MAXSPELLBUFFERS; i++)
            {
                if(i < dim1)
                {
                    short tShort = dis.readShort();
                    if(tShort != Util.NOTHING || tShort < 0)
                    {
                        tSpellRef = newPlayer.getSpellBook().getSpell(tShort);
                        newPlayer.setSpellBuffer(i, tSpellRef);
                    }
                }
                else
                    newPlayer.setSpellBuffer(i, null);
            }
            
            for(; i < dim1; i++)
                dis.readShort();
            
            newPlayer.timePlayed = dis.readDouble();
            newPlayer.dateCreated = dis.readLong();

            // Write sanctuary point.
            dim1 = dis.readInt();
            if(dim1 < 1)
            	newPlayer.setSanctuaryCoords();
            else
            {
            	byte[] newSanc = new byte[dim1];
            	for(i = 0; i < newSanc.length; i++)
            		newSanc[i] = dis.readByte();
            	
            	if(!newPlayer.setSanctuaryCoords(newSanc))
            		newPlayer.setSanctuaryCoords();
            }
            
            newPlayer.setName(dis.readUTF());
        }
        catch(Exception e)
        {
            System.err.println("Error reading player. Error: " + e);
            return null;
        }
		return newPlayer;
	}
    
    public boolean postLoadUpdate(DataBank dataBank)
    {
    	Race tRace = dataBank.getRace(raceID);
    	
    	if(tRace == null)
    	{
    		System.err.println("Error: Player " + ID + " race no longer exists.");
    		return false;
    	}
    	
    	setRace(tRace);
    	return true;
    }

	@Override
	public String generateDescription(boolean html)
	{
		// TODO Auto-generated method stub
		return description;
	}	
}