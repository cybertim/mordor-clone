package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import mordorEnums.Alignment;
import mordorEnums.GuildSkill;
import mordorEnums.SpellClass;
import mordorEnums.Stats;
import mordorHelpers.Util;


import structures.LinkedList;
import structures.ListIter;

/**
 * Guild object for holding guilds.
 * @author August Junkala. April 8, 2007
 *
 */
public class Guild extends MObject<Byte>
{
	private byte averageHP, standardHP, experiencePenalty, questPercentage;
	private boolean[] alignment;
	private String guildMasterName;
	private short guildMasterLevel;
	private short[] extraSwings;
	private short maximumLevel;//maxAttack, maxDefense, , maxLevelAD;
	private short crestID;
	private long joinCost;
	private float[] skills;
	private LinkedList<Race> allowedRaces;
	private LinkedList<SpellReference> learnedSpells;
	private Player guildMaster;
	private Item guildCrest;
	
	// ggf = GGF is GuildGoldFactor, the number displayed in Wabbit's Editor as "GoldPerLev?" (click on "Show Spell Data" on the Guilds form). Its value is 1 for Warrior, 2 for Paladin, 3 for Nomad, Seeker, Mage, and Sorcerer, 4 for Wizard and Healer, 5 for Ninja and Villain, 7 for Scavenger, and 9 for Thief.\
	
	public static final byte SWINGS_MAX = 4;
	
	public static final byte NOFEEGUILDS = 2; // How many guilds are the fees waived for.
	
	Guild(byte newID)
	{
		super(newID);
		
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
		
		attack = defense = 250;
		level = 1000;
		
		crestID = -1;
		
		type = 1;
		joinCost = 1;
		
		skills = new float[GuildSkill.values().length];
		for(GuildSkill gs : GuildSkill.values())
			skills[gs.value()] = 0.0f;
		
		allowedRaces = new LinkedList<Race>();
		learnedSpells = new LinkedList<SpellReference>();
		
		guildMaster = null;
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
		return (guildMaster == null) ? guildMasterLevel : guildMaster.getGuildRecord((byte)ID).getLevel();
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
		ListIter<Race> tRace = allowedRaces.getIterator();
		while(tRace.next())
			if(tRace.element().getRaceID() == nRace.getRaceID())
				return true;
		
		return false;
	}
	
	public short getExtraSwingLevel(byte swingNum)
	{
		return extraSwings[swingNum];
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
		return type;
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
		ListIter<SpellReference> tSpell = learnedSpells.getIterator();
		
		while(tSpell.next())
			if(tSpell.element().getLevel() > spellLevel[tSpell.element().getSpellClass().value()])
				spellLevel[tSpell.element().getSpellClass().value()] = tSpell.element().getLevel();
		
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
			if(guildMaster.getGuildRecord((byte)ID) != null)
			{
				guildMasterName = guildMaster.getName();
				guildMasterLevel = guildMaster.getGuildRecord((byte)ID).getLevel();
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
	 * Set the level the specified swing number is aquired at.
	 * @param swingNum
	 * @param swingLevel
	 */
	public void setExtraSwings(byte swingNum, short swingLevel)
	{
		if(swingNum < extraSwings.length)
			extraSwings[swingNum] = swingLevel;
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
		type = newGGF;
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
		ListIter<SpellReference> tNode = learnedSpells.getIterator();
		
		while(tNode.next())
			if(tNode.element().getSpell() == newSpell.getSpell())
				return;
		
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
		if(newPlayer.getGuildRecord((byte)ID) != null)
			return false;
		
		// Is the player properly aligned
		if(!allowedAlignment(newPlayer.getAlignment()))
			return false;
		
		// Is the player of the right race
		if(!allowedRace(newPlayer.getRace()))
			return false;
		
		// Are all the player's stats high enough
		for(Stats st : Stats.values())
			if(stats[st.value()] > newPlayer.getNaturalStat(st))
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
			dos.writeByte((byte)ID);
			
			dos.writeBoolean(alignment[Alignment.Good.value()]);
			dos.writeBoolean(alignment[Alignment.Neutral.value()]);
			dos.writeBoolean(alignment[Alignment.Evil.value()]);
			
			dos.writeByte(averageHP);
			dos.writeByte(standardHP);
			dos.writeByte(experiencePenalty);
			dos.writeByte(questPercentage);
			dos.writeByte(type);
			
			for(int i = 0; i < stats.length; i++)
				dos.writeByte(stats[i]);
			
			dos.writeShort(guildMasterLevel);
			dos.writeShort(attack);
			dos.writeShort(defense);
			dos.writeShort(level);
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
				ListIter<Race> tNode = allowedRaces.getIterator();
				
				dos.writeInt((int)allowedRaces.getSize());
				
				while(tNode.next())
					dos.writeByte(tNode.element().getRaceID());
			}
			
			{
				ListIter<SpellReference> tNode = learnedSpells.getIterator();
				
				dos.writeInt((int)learnedSpells.getSize());
				
				while(tNode.next())
				{
					dos.writeShort(tNode.element().getSpell().getID());
					dos.writeShort(tNode.element().getLevel());
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
				newGuild.stats[st.value()] = dis.readByte();
			
			short gmLevel = dis.readShort();
			newGuild.attack = dis.readShort();
			newGuild.defense = dis.readShort();
			newGuild.level = dis.readShort();
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
			
			newGuild.name = dis.readUTF();
			if(newGuild.getGuildMaster() == null)
				newGuild.setGuildMaster(dis.readUTF(), gmLevel);
			newGuild.description = dis.readUTF();
		}
		catch(Exception e)
		{
			System.err.println("Guild load error: " + e);
			return null;
		}
		
		return newGuild;
	}

	@Override
	public String generateDescription(boolean html) {
		// TODO Auto-generated method stub
		return null;
	}
}
