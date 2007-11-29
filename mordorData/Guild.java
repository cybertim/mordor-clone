package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import mordorEnums.Alignment;
import mordorEnums.GuildSkill;
import mordorEnums.SpellClass;
import mordorEnums.Stats;
import mordorHelpers.Util;


import structures.LinkedList;
import structures.ListNode;

/**
 * Guild object for holding guilds.
 * @author August Junkala. April 8, 2007
 *
 */
public class Guild
{
	private byte requiredStats[];
	private byte averageHP, standardHP, experiencePenalty, questPercentage;
	private String name;
	private String description;
	private byte guildID;
	private boolean[] alignment;
	private String guildMasterName;
	private short guildMasterLevel;
	private short[] extraSwings;
	private short maxAttack, maxDefense, maxLevelAD, maximumLevel;
	private short crestID;
	private byte goldFactor;
	private long joinCost;
	private float[] skills;
	private LinkedList<Race> allowedRaces;
	private LinkedList<SpellReference> learnedSpells;
	private Player guildMaster;
	private Item guildCrest;
	
	// ggf = GGF is GuildGoldFactor, the number displayed in Wabbit's Editor as "GoldPerLev?" (click on "Show Spell Data" on the Guilds form). Its value is 1 for Warrior, 2 for Paladin, 3 for Nomad, Seeker, Mage, and Sorcerer, 4 for Wizard and Healer, 5 for Ninja and Villain, 7 for Scavenger, and 9 for Thief.\
	
	public static final byte SWINGS_MAX = 4;

/*	public static final byte SKILL_TYPES = 6;
	public static final String[] SKILL_NAMES = {"Thieving", "Back-Stabbing", "Critical Hits",
								"Multiple Swings", "Fighting", "Perception"};
	public static final byte SKILL_THIEVING = 0;
	public static final byte SKILL_BACKSTABBING = 1;
	public static final byte SKILL_CRITICALHITS = 2;
	public static final byte SKILL_EXTRASWINGS = 3;
	public static final byte SKILL_FIGHTING = 4;
	public static final byte SKILL_PERCEPTION = 5;*/
	
	public static final byte NOFEEGUILDS = 2; // How many guilds are the fees waived for.
	
	Guild(byte newID)
	{
		name = Util.NOSTRING;
		guildID = newID;
		
		requiredStats = new byte[Stats.values().length];
		for(byte i = 0; i < requiredStats.length; i++)
			requiredStats[i] = (byte)0;
		
		alignment = new boolean[Alignment.values().length];
		for(byte i = 0; i < alignment.length; i++)
			alignment[i] = true;
		
		averageHP = 5;
		standardHP = 2;
		maximumLevel = 30;
		experiencePenalty = 1;
		questPercentage = 5;
		
		guildMasterName = "August the Awesome";
		
		extraSwings = new short[SWINGS_MAX];
		for(byte i = 0; i < SWINGS_MAX; i++)
			extraSwings[i] = -1;
		
		maxAttack = maxDefense = 250;
		maxLevelAD = 1000;
		
		crestID = -1;
		
		goldFactor = 1;
		joinCost = 1;
		
		skills = new float[GuildSkill.values().length];
		for(GuildSkill gs : GuildSkill.values())
			skills[gs.value()] = 0.0f;
		
		allowedRaces = new LinkedList<Race>();
		learnedSpells = new LinkedList<SpellReference>();
		
		guildMaster = null;
	}
	
	public byte getGuildID()
	{
		return guildID;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Retrieves the name of the guild master.
	 * @return
	 */
	public String getGuildMasterName()
	{
		return (guildMaster == null) ? guildMasterName : guildMaster.getName();
	}
	
	/**
	 * Retrieves the level of the guild master.
	 * @return
	 */
	public short getGuildMasterLevel()
	{
		return (guildMaster == null) ? guildMasterLevel : guildMaster.getGuildRecord(guildID).getLevel();
	}
	
	/**
	 * Retrieve the guild master. Note, if a non-existent guild master is in use (i.e. only a name and level)
	 * then this will return null. Go to getGuildMasterName.
	 * @return Player
	 */
	public Player getGuildMaster()
	{
		return guildMaster;
	}
	
	/**
	 * Retrieve the value of a specific required stat.
	 * @param statType
	 * @return
	 */
	public byte getStatRequired(Stats statType)
	{
		return requiredStats[statType.value()];
	}
	
	public byte getAH()
	{
		return averageHP;
	}
	
	public byte getMH()
	{
		return standardHP;
	}
	
	public short getML()
	{
		return maximumLevel;
	}
	
	public byte getEP()
	{
		return experiencePenalty;
	}
	
	public byte getQP()
	{
		return questPercentage;
	}
	
	/**
	 * Checks if an alignment is allowed.
	 * @param nAlignment
	 * @return True if the alignment is allowed for this guild.
	 */
	public boolean allowedAlignment(Alignment nAlignment)
	{
		return (alignment[nAlignment.value()]);
	}
	
	/**
	 * Checks if a race is allowed.
	 * @param nRace Race to check
	 * @return True if the race is allowed for this guild.
	 */
	public boolean allowedRace(Race nRace)
	{
		ListNode<Race> tRace = allowedRaces.getFirstNode();
		while(tRace != null)
		{
			if(tRace.getElement().getRaceID() == nRace.getRaceID())
				return true;
			
			tRace = tRace.getNext();
		}
		
		return false;
	}
	
	public short getExtraSwingLevel(byte swingNum)
	{
		return extraSwings[swingNum];
	}
	
	public short getMaxAttack()
	{
		return maxAttack;
	}
	
	public short getMaxDefense()
	{
		return maxDefense;
	}
	
	public short getMaxLevelAD()
	{
		return maxLevelAD;
	}
	
	public Item getCrest()
	{
		return guildCrest;
	}
	
	public short getCrestID()
	{
		return crestID;
	}
	
	public byte getGGF()
	{
		return goldFactor;
	}
	
	/**
	 * Calculates and returns the cost to join this guild (assuming it isn't a freebie)
	 * @return long cost
	 */
	public long getJoinCost()
	{
		int skillCount = 0;
		for(int i = 0; i < skills.length; i++)
			if(skills[i] > -0.001 && skills[i] < 0.001)
				skillCount++;
		
		skillCount++;
		int[] spellLevel = new int[SpellClass.values().length];
		for(byte i = 0; i < spellLevel.length; i++) spellLevel[i] = 0;
		ListNode<SpellReference> tSpell = learnedSpells.getFirstNode();
		
		while(tSpell != null)
		{
			if(tSpell.getElement().getLevel() > spellLevel[tSpell.getElement().getSpellClass().value()])
				spellLevel[tSpell.getElement().getSpellClass().value()] = tSpell.getElement().getLevel();
			tSpell = tSpell.getNext();
		}
		
		double spellCount = 0;
		for(byte i = 0; i < spellLevel.length; i++)
			spellCount += spellLevel[i] / 3;
		
		double countVal = ((skillCount * 1.25) + spellCount);
		
		return (long)(countVal * countVal * 5536);
	}
	
	public float getSkillFactor(GuildSkill skillType)
	{
		return skills[skillType.value()];
	}
	
	public LinkedList<SpellReference> getLearnedSpells()
	{
		return learnedSpells;
	}
	
	public LinkedList<Race> getAllowedRaces()
	{
		return allowedRaces;
	}
	
	public void setName(String newName)
	{
		name = newName;
	}
	
	public void setDescription(String newDesc)
	{
		description = newDesc;
	}
	
	public void setAlignment(Alignment nAlignment, boolean allowed)
	{
		alignment[nAlignment.value()] = allowed;
	}
	
	/**
	 * Sets the guild master, if possible. A player that is not part of the guild cannot
	 * be the guild  master.
	 * @param newGuildMaster
	 * @return
	 */
	public boolean setGuildMaster(Player newGuildMaster)
	{
		if(guildMaster != null)
		{
			if(guildMaster.getGuildRecord(guildID) != null)
			{
				guildMasterName = guildMaster.getName();
				guildMasterLevel = guildMaster.getGuildRecord(guildID).getLevel();
			}
			else
				return false;
		}
		
		guildMaster = newGuildMaster;
		
		return true;
	}
	
	/**
	 * Set a non existent guild master.
	 * @param newName	Name of the guild master.
	 * @param newLevel	Level of the guild master.
	 */
	public void setGuildMaster(String newName, short newLevel)
	{
		guildMasterName = newName;
		guildMasterLevel = newLevel;
		guildMaster = null;
	}
	
	public void setAH(byte newAvgHP)
	{
		averageHP = newAvgHP;
	}
	
	public void setMH(byte newMaxHP)
	{
		standardHP = newMaxHP;
	}
	
	public void setML(short newMaxLevel)
	{
		maximumLevel = newMaxLevel;
	}
	
	public void setEP(byte newXPPenalty)
	{
		experiencePenalty = newXPPenalty;
	}
	
	public void setQP(byte newQuestPercent)
	{
		questPercentage = newQuestPercent;
	}
	
	/**
	 * Sets the required value for the specified stat.
	 * @param statType
	 * @param statValue
	 */
	public void setRequiredStats(Stats statType, byte statValue)
	{
		requiredStats[statType.value()] = statValue;
	}
	
	/**
	 * Set the level the specified swing number is aquired at.
	 * @param swingNum
	 * @param swingLevel
	 */
	public void setExtraSwings(byte swingNum, short swingLevel)
	{
		if(swingNum < extraSwings.length)
			extraSwings[swingNum] = swingLevel;
	}
	
	public void setMaxAttack(short newMAttack)
	{
		maxAttack = newMAttack;
	}
	
	public void setMaxDefense(short newMDefense)
	{
		maxDefense = newMDefense;
	}
	
	public void setMaxLevelAD(short newMLAD)
	{
		maxLevelAD = newMLAD;
	}
	
	/**
	 * Set the crest for this guild.
	 * @param newCrest Item
	 */
	public void setCrest(Item newCrest)
	{
		crestID = (newCrest == null) ? Util.NOTHING : newCrest.getID();
		guildCrest = newCrest;
	}
	
	public void setCrest(short newCrestID)
	{
		crestID = newCrestID;
	}
	
	public void setGuildGoldFactor(byte newGGF)
	{
		goldFactor = newGGF;
	}
	
	public void setJoinCost(long newJoinCost)
	{
		joinCost = newJoinCost;
	}
	
	public void setSkillFactor(GuildSkill skillType, float skillFactor)
	{
		skills[skillType.value()] = skillFactor;
	}
	
	public void addAllowedRace(Race newRace)
	{
		if(!allowedRaces.containsElement(newRace))
			allowedRaces.insert(newRace);
	}
	
	public void addSpell(SpellReference newSpell)
	{
		ListNode<SpellReference> tNode = learnedSpells.getFirstNode();
		
		while(tNode != null)
		{
			if(tNode.getElement().getSpell() == newSpell.getSpell())
				return;
			tNode = tNode.getNext();
		}
		
		learnedSpells.insert(newSpell);
	}
	
	/**
	 * If the provided player can become the new guild master, add her as it
	 * and pass the item offer to her.
	 * @param newOwner
	 * @return
	 */
	public Item newCrestOwner(Player newOwner, DataBank dataBank)
	{
		if(guildMaster != null)
		{
			guildMaster.removeCrest(getCrest());
		}
		
		if(newOwner != null)
			newOwner.addItem(new ItemInstance(getCrest()));
		return null;
	}
	
	/**
	 * Determines if a player can join a guild.
	 * @param newPlayer
	 * @return
	 */
	public boolean joinGuild(Player newPlayer)
	{
		// Is the player a member already?
		if(newPlayer.getGuildRecord(guildID) != null)
			return false;
		
		// Is the player properly aligned
		if(!allowedAlignment(newPlayer.getAlignment()))
			return false;
		
		// Is the player of the right race
		if(!allowedRace(newPlayer.getRace()))
			return false;
		
		// Are all the player's stats high enough
		for(Stats st : Stats.values())
			if(requiredStats[st.value()] > newPlayer.getNaturalStat(st))
				return false;
		
		// Is this guild join counted as a freebie
		boolean guildFeeWaiver = (newPlayer.getGuildRecords().getSize() <= NOFEEGUILDS);
		
		// If there is no freebie, does the player have enough gold.
		if(!guildFeeWaiver && newPlayer.getTotalGold() < getJoinCost())
			return false;
		
		GuildRecord newGuild = new GuildRecord(this); // Create a record
		newPlayer.addGuildRecord(newGuild); // Add it to the player
		newPlayer.setActiveGuild(newGuild); // Make it the player's active guild.
		newPlayer.spendTotalGold(getJoinCost()); // Deduct fees from the player.
		return true;
	}
	
	public boolean writeGuild(DataOutputStream dos)
	{
		try
		{
			dos.writeByte(guildID);
			
			dos.writeBoolean(alignment[Alignment.Good.value()]);
			dos.writeBoolean(alignment[Alignment.Neutral.value()]);
			dos.writeBoolean(alignment[Alignment.Evil.value()]);
			
			dos.writeByte(averageHP);
			dos.writeByte(standardHP);
			dos.writeByte(experiencePenalty);
			dos.writeByte(questPercentage);
			dos.writeByte(goldFactor);
			
			for(int i = 0; i < requiredStats.length; i++)
				dos.writeByte(requiredStats[i]);
			
			dos.writeShort(guildMasterLevel);
			dos.writeShort(maxAttack);
			dos.writeShort(maxDefense);
			dos.writeShort(maxLevelAD);
			dos.writeShort(maximumLevel);
			
			for(int i = 0; i < SWINGS_MAX; i++)
				dos.writeShort(extraSwings[i]);
			
			dos.writeLong(joinCost);
			
			for(int i = 0; i < GuildSkill.values().length; i++)
				dos.writeFloat(skills[i]);
			
			dos.writeShort(crestID);
			
			if(guildMaster != null)
				dos.writeShort(guildMaster.getID());
			else
				dos.writeShort(-1);
			
			{
				ListNode<Race> tNode = allowedRaces.getFirstNode();
				
				dos.writeInt((int)allowedRaces.getSize());
				
				while(tNode != null)
				{
					dos.writeByte(tNode.getElement().getRaceID());
					tNode = tNode.getNext();
				}
			}
			
			{
				ListNode<SpellReference> tNode = learnedSpells.getFirstNode();
				
				dos.writeInt((int)learnedSpells.getSize());
				
				while(tNode != null)
				{
					dos.writeShort(tNode.getElement().getSpell().getID());
					dos.writeShort(tNode.getElement().getLevel());
					tNode = tNode.getNext();
				}
			}
			
			dos.writeUTF(name);
			dos.writeUTF(guildMasterName);
			dos.writeUTF(description);
		}
		catch(Exception e)
		{
			System.err.println(e);
			return false;
		}
		
		return true;
	}

	/**
	 * Read a guild from the data stream.
	 * @param dataBank
	 * @param dis		DataInputStream being read from.
	 * @return	Guild - the new guild.
	 */
	public static final Guild readGuild(DataBank dataBank, DataInputStream dis)
	{
		Guild newGuild = null;
		
		try
		{
			newGuild = new Guild(dis.readByte());
			
			newGuild.setAlignment(Alignment.Good, dis.readBoolean());
			newGuild.setAlignment(Alignment.Neutral, dis.readBoolean());
			newGuild.setAlignment(Alignment.Evil, dis.readBoolean());
			
			newGuild.setAH(dis.readByte());
			newGuild.setMH(dis.readByte());
			newGuild.setEP(dis.readByte());
			newGuild.setQP(dis.readByte());
			newGuild.setGuildGoldFactor(dis.readByte());
			
			for(Stats st : Stats.values())
				newGuild.setRequiredStats(st, dis.readByte());
			
			short gmLevel = dis.readShort();
			newGuild.setMaxAttack(dis.readShort());
			newGuild.setMaxDefense(dis.readShort());
			newGuild.setMaxLevelAD(dis.readShort());
			newGuild.setML(dis.readShort());
			
			for(byte i = 0; i < SWINGS_MAX; i++)
				newGuild.setExtraSwings(i, dis.readShort());
			
			newGuild.setJoinCost(dis.readLong());
			
			for(byte i = 0; i < GuildSkill.values().length; i++)
				newGuild.setSkillFactor(GuildSkill.type(i), dis.readFloat());
			
			newGuild.setCrest(dis.readShort());
			
			short temp = dis.readShort();
			
			if(temp == -1)
				newGuild.setGuildMaster(null);
			else
				newGuild.setGuildMaster(dataBank.getPlayer(temp));
			
			int tInt;
			Race tRace;
			
			tInt = dis.readInt();
			
			for(int i = 0; i < tInt; i++)
			{
				tRace = dataBank.getRace(dis.readByte());
				if(tRace != null)
					newGuild.addAllowedRace(tRace);
			}
			
			SpellReference tSpell;
			tInt = dis.readInt();
			
			for(int i = 0; i < tInt; i++)
			{
				tSpell = dataBank.getSpellBook().getSpell(dis.readShort());
				if(tSpell != null)
				{
					tSpell.setLevel(dis.readShort());
					newGuild.addSpell(tSpell);
				}
			}
			
			newGuild.setName(dis.readUTF());
			if(newGuild.getGuildMaster() == null)
				newGuild.setGuildMaster(dis.readUTF(), gmLevel);
			newGuild.setDescription(dis.readUTF());
		}
		catch(Exception e)
		{
			System.err.println("Guild load error: " + e);
			return null;
		}
		
		return newGuild;
	}
}
