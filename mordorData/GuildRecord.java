package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;

import mordorEnums.GuildSkill;
import mordorHelpers.Util;


/**
 * Guild record object. Maintains information about each player's membership 
 * in a guild. Includes player reference, current quest (if any) experience
 * amount (and function to get level), learned skills. Also performs all functions
 * relevant to a player's interaction with his/her guild.
 * @author August Junkala. April 18, 2007
 *
 */
public class GuildRecord
{
	private byte guildID;
	private short questItemID;
	private short questMonsterID;
	private Guild guild;
	private long experience;
	private Item questItem;
	private Monster questMonster;
	private short level;
	private boolean pinned;
	private short attackPoints, defensePoints;
	private short[] guildSkills;
	
	private static final byte QUEST_MONSTERMINLEVEL = 5;
	private static final byte QUEST_ITEMMINLEVEL = 15;
	private static final byte QUEST_RAREREROLLPERCENT = 74;
	private static final byte QUEST_LEVELDIVISOR = 25;
	private static final long QUEST_ITEMMINVALUE = 100;
	// TODO Can level divisor be adjusted based on deepst possible level
	// and maximum possible player level?
	// the deepest level something can be randomly selected is 10..
	// so 10 * 25 = level 250, the selected monster doesn't get harder.
	
	public static final short MAXGUILDDEFENSE = Short.MAX_VALUE;
	public static final short MAXGUILDATTACk = Short.MAX_VALUE;
	public static final short MAXGUILDLEVEL = Short.MAX_VALUE;
	public static final long MAXGUILDXP = Long.MAX_VALUE;
	public static final short MAXGUILDSKILL = Short.MAX_VALUE;
	
	/**
	 * Default constructor.
	 * @param newGuild	The guild the player is joining.
	 * @param newExperience	The starting experience.
	 */
	public GuildRecord(Guild newGuild)
	{
		guild = newGuild;
		// newGuild will be null when we are loading from file. The guild id will come next.
		if(newGuild != null)
			guildID = (byte)newGuild.getID();
		experience = 0;
		level = 0;
		pinned = false;
		questItem = null;
		questMonster = null;
		attackPoints = 0;
		defensePoints = 0;
		guildSkills = new short[GuildSkill.values().length];
		for(GuildSkill gs : GuildSkill.values())
			guildSkills[gs.value()] = 0;
			
	}
	
	/**
	 * If possible, level the player.
	 * @param levelPlayer	Player to level
	 * @param dataBank		Data bank (used for questing)
	 * @return
	 */
	public boolean levelPlayer(Player player, DataBank dataBank)
	{
		// check if player meets XP requirements
		if(!canLevel())
		{
			player.postMessage("Not enough experience.");
			return false;
		}

		// check if player meets gold requirements
		if(player.getGoldOnHand() < goldToLevel((short)(level + 1)))
		{
			player.postMessage("Not enough gold to level.");
			return false;
		}
		
		// check if the quests have been completed
		if(isQuested())
		{
			if(questMonster != null)
				player.postMessage("Have not completed quest to kill a " + questMonster.getName());
			else
				player.postMessage("Have not completed quest to find a " + questItem.getName());
		}
		// if so, increase level of player
		level += 1;
		pinned = false;
		
		// TODO : add skill additions to player.
		// TODO : add spells (or lower level of spells) to player
		// TODO : add attack/defense additions to player
		
		// if requirements are met, run newQuest to see if a new quest should happen
		newQuest(player, dataBank);

		return true;
	}
	
	/**
	 * Determine if the player has a new quest.
	 * @param levelPlayer	Player to be quested.
	 * @param dataBank		DataBank (for choosing item/monster)
	 */
	private void newQuest(Player player, DataBank dataBank)
	{
		if(level < QUEST_MONSTERMINLEVEL)
			return;
		
		Random random = new Random(System.nanoTime());
		
		// roll dice to see if there even will be a quest based on the guilds
		// quest percentage
		if(random.nextInt(100) > guild.getQP())
			return;
		
		// determine if the player has a new monster or item quest
		boolean monsterQuest = (level < QUEST_ITEMMINLEVEL) ? true : (random.nextInt(100) > 25);
		

		if(random.nextInt(100) < 6)
		{
			player.postMessage("Questmaster will left you off the hook.");
			return;
		}
		

		byte maxLevel = (byte)(level / QUEST_LEVELDIVISOR);

		if(maxLevel > 10)
			maxLevel = 10;
		
		if(monsterQuest)
		{
			// monster quest
			Monster monster;
			monster = dataBank.getRandomMonster(maxLevel);
			
			if(monster.isRare() && random.nextInt(100) < QUEST_RAREREROLLPERCENT)
				monster = dataBank.getRandomMonster(maxLevel);
			
			questMonster = monster;
			questMonsterID = monster.getID();
		}
		else
		{
			// item quest
			Item item;
			maxLevel = (byte)((maxLevel / 1.5) + 1);
			
			do
			{
				//TODO fix
				item = dataBank.getRandomItem(maxLevel);
			
				// if the item's value is less than 100, is cursed or from level 0
				// re-roll.
				if(item.isRare() && random.nextInt(100) < QUEST_RAREREROLLPERCENT)
					item = dataBank.getRandomItem(maxLevel);
			} while(item.getItemBaseValue() <= QUEST_ITEMMINVALUE || item.getLevel() == 0 || item.isCursed());
			
			questItem = item;
			questItemID = item.getID();
		}
	}
	
	/**
	 * Determine if the monster completes the quest.
	 * @param questMonster	Monster killed
	 * @return boolean	True if the monster is the correct one.
	 */
	public boolean questCompleted(Monster killedMonster)
	{
		if(questMonster.getID() == killedMonster.getID())
		{
			questMonster = null;
			questMonsterID = 0;
			return true;
		}
		
		return false;
		// determine if monster defeated was quested
	}
	
	/**
	 * Determine if the item completes the quest.
	 * @param questItem	Item acquired.
	 * @return boolean	True if the item is the quested one.
	 */
	public boolean questCompleted(Item foundItem)
	{
		if(questItem.getID() == foundItem.getID())
		{
			questItem = null;
			questItemID = 0;
			return true;
		}
		return false;
		// determine if item found was quested
	}
	
	/**
	 * Determine if this record currently holds an outstanding quest.
	 * @return
	 */
	public boolean isQuested()
	{
		return (questItem != null || questMonster != null);
	}
	
	/**
	 * Retrieve the guild this record is for
	 * @return Guild	Guild this record is for.
	 */
	public Guild getGuild()
	{
		return guild;
	}
	
	public byte getGuildID()
	{
		return guildID;
	}
	
	/**
	 * Retrieve the current experience held in this record.
	 * @return long	Current amount of experience
	 */
	public long getExperience()
	{
		return experience;
	}
	
	/**
	 * Retrieve the current quested monster
	 * @return Monster	Current quested Monster
	 */
	public Monster getQuestMonster()
	{
		return questMonster;
	}
	
	public short getQuestMonsterID()
	{
		return questMonsterID;
	}
	
	/**
	 * Retrieve the current quested item
	 * @return Item	Current quested item.
	 */
	public Item getQuestItem()
	{
		return questItem;
	}
	
	public short getQuestItemID()
	{
		return questItemID;
	}
	
	/**
	 * Retrieve the current level the player is at in this level
	 * @return short	Current level
	 */
	public short getLevel()
	{
		return level;
	}
	
	public short getGuildAttack()
	{
		return attackPoints;
	}
	
	public short getGuildDefense()
	{
		return defensePoints;
	}
	
	public short getGuildSkill(GuildSkill skill)
	{
		return guildSkills[skill.value()];
	}
	
	/**
	 * Adds experience based on a hit on a monster.
	 * @param hitsInflicted		Amount of health the monster lost. (int)
	 * @param playerDamage		Player did the damage (vs. companion) (boolean)
	 * @param partySize			Size of the player's party (byte)
	 * @param monsterGuildLevel	Monster guild level (short)
	 */
	public void addMonsterExperience(int hitsInflicted, boolean playerDamage, byte partySize, short monsterGuildLevel, Player player)
	{
		if(!pinned)
		{
			double companionFactor = (playerDamage) ? 1.0 : 0.25; 
			long xpAwarded = (long)(hitsInflicted * (9 - Math.log((double) hitsInflicted + 1)) * (20 - Math.log((double)hitsInflicted)) * (monsterGuildLevel + 50) * companionFactor * getLevel() / 1000);
			double partySizeFactor = 1 - ((partySize - 1) * 0.04);
			
			experience += (long)(partySizeFactor * xpAwarded);
			
			if(canLevel())
				player.postMessage(player.getName() + " has enough experience to level.");
			
			if(isPinned())
			{
				experience = getLevelXP((short)(level + 2)) - 1;
				player.postMessage(player.getName() + " is pinned.");
			}
		}
	}
	
	/**
	 * Adds experience based on opening a chest.
	 * @param chanceOfDisarming	Player's chance of disarming a chest (byte)
	 * @param partySize			Size of player's party (byte)
	 * @param monsterGuildLevel	Monster guild level (short)
	 */
	public void addChestExperience(byte chanceOfDisarming, byte partySize, short monsterGuildLevel, Player player)
	{
		if(!pinned)
		{
			boolean canLevel = canLevel();
			long xpAwarded = (long)(chanceOfDisarming * monsterGuildLevel * 0.5 * getLevel());
			double partySizeFactor = 1 - ((partySize - 1) * 0.04);
		
			experience += (long)(partySizeFactor * xpAwarded);
		
			if(!canLevel && canLevel())
				player.postMessage(player.getName() + " has enough experience to level.");
			else if(isPinned())
			{
				experience = getLevelXP((short)(level + 2)) - 1;
				player.postMessage(player.getName() + " is pinned.");
			}
		}
	}
	
	/**
	 * Determines if the player is pinned.
	 * @return boolean	True if pinned.
	 */
	public boolean isPinned()
	{
		return ((getLevelXP((short)(level + 2)) - 1 - experience) <= 0);
	}
	
	/**
	 * Determines how much experience is needed to level, or until pinned.
	 * @return long	Experience still necessary.
	 */
	public long getXPNeeded()
	{
		long xpNeeded = getLevelXP((short)(level + 1)) - experience;
		
		return (xpNeeded > 0) ? xpNeeded : getLevelXP((short)(level + 2)) - experience - 1;
	}
	
	/**
	 * Sets the level of the player based on how much experience she has.
	 * 
	 */
	private void setLevelFromXP()
	{
		while((experience - getLevelXP(level)) > 0)
			level += 1;
		
		level -= 1;
	}
	
	public void setLevel(short newLevel)
	{
		level = newLevel;
	}
	
	/**
	 * Gets the amount of experience required for a certain level.
	 * @param nLevel	Level whose experience is wanted.
	 * @return long	Amount of experience necessary
	 */
	public long getLevelXP(short nLevel)
	{
		return (long)((88.09020776 * (nLevel - 1) * (nLevel - 1) * (guild.getEP() / 8)) + (45.45454 * (((nLevel - 1) * 2) - 1)) - 0.264);
	}
	
	/**
	 * Gets the amount of experience required for a certain level.
	 * Independed of this actual record (should probably be moved to Util
	 * @param nLevel
	 * @param nGuild
	 * @param nRace
	 * @return
	 */
	public long getLevelXP(short nLevel, Guild nGuild, Race nRace)
	{
		// TODO move to Util
		return (long)(nRace.getTrueXPRate() * ((88.09020776 * (nLevel - 1) * (nLevel - 1) * (nGuild.getEP() / 8)) + (45.45454 * (((nLevel - 1) * 2) - 1)) - 0.264));
	}
	
	/**
	 * Determine if the player is able to level.
	 * @return
	 */
	private boolean canLevel()
	{
		return (experience > getLevelXP((short)(level + 1)));
	}
	
	/**
	 * Calculates and returns the amount of golded needed for the specified level.
	 * @param nLevel	Level the cost is sought for. (short)
	 * @return long	Cose for the level specified
	 */
	private long goldToLevel(short nLevel)
	{
		long baseLevelCost = (long)(((nLevel << 1) - 1) * 30.1); 
		return (long)(baseLevelCost * ((1 + guild.getGGF()) / 10));
	}
	
	public short getSpellLevel()
	{
		return (short)(level >>> 1);
	}
	
	public void setExperience(long newExperience)
	{
		experience = newExperience;
	}
    
    public boolean setGuild(Guild newGuild)
    {
        if(newGuild == null)
            return false;
        
        guild = newGuild;
        guildID = (byte)newGuild.getID();
        return true;
    }
	
	public void setGuildID(byte nGuildID)
	{
		guildID = nGuildID;
	}
	
	public void setMonsterID(short nQuestMonsterID)
	{
		questMonsterID = nQuestMonsterID;
	}
	
	public void setItemID(short nQuestItemID)
	{
		questItemID = nQuestItemID;
	}
	
	public boolean setQuestMonster(Monster nQuestMonster)
	{
        if(nQuestMonster == null)
            return false;
        
		questMonster = nQuestMonster;
        questMonsterID = nQuestMonster.getID();
        questItem = null;
        questItemID = Util.NOTHING;
        return true;
	}
	
	public boolean setQuestItem(Item nQuestItem)
	{
        if(nQuestItem == null)
            return false;
        
		questItem = nQuestItem;
        questItemID = nQuestItem.getID();
        questMonster = null;
        questMonsterID = Util.NOTHING;
        return true;
	}
	
	public void setGuildAttack(short newAttack)
	{
		attackPoints = newAttack;
	}
	
	public void setGuildDefense(short newDefense)
	{
		defensePoints = newDefense;
	}
	
	public void setGuildSkill(GuildSkill skill, short skillValue)
	{
		guildSkills[skill.value()] = skillValue;
	}
	
	public boolean writeGuildRecord(DataOutputStream dos)
	{
		try
		{
			dos.writeByte(guildID);
			if(questMonster != null)
				dos.writeShort(questMonsterID);
			else
				dos.writeShort(Util.NOTHING);
		
			if(questItem != null)
				dos.writeShort(questItemID);
			else
				dos.writeShort(Util.NOTHING);
			
			dos.writeShort(level);
			dos.writeLong(experience);
		}
		catch(Exception e)
		{
			System.err.println("GuildRecord - writeGuildRecord : " + e);
			return false;
		}
		
		return true;
	}
	
	public static final GuildRecord readGuildRecord(DataInputStream dis)
	{
		GuildRecord tGuild = null;
		try
		{
			tGuild = new GuildRecord(null);
			tGuild.setGuildID(dis.readByte());

			tGuild.setMonsterID(dis.readShort());
			tGuild.setItemID(dis.readShort());
			
			tGuild.setLevel(dis.readShort());
			tGuild.setExperience(dis.readLong());
		}
		catch(Exception e)
		{
			
			System.err.println("GuildRecord - readGuildRecord : " + e);
			return null;
		}
		
		return tGuild;
	}
}
