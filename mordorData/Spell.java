package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;

import mordorEnums.SpellType;
import mordorEnums.Stats;
import mordorHelpers.Util;

public class Spell 
{
	private String name;
	private String description;
	private String effectName; // e.g. dazzeled
	private short spellID;
	private SpellType spellType;
	private byte stats[];
	private byte strength, numGroups, subtype;
	private short baseLevel;
	/*
	 * strength: How many monsters it can affect (by group) or change to resistance.
	 * numGroups: How many groups of targets it can affect.
	 * baseLevel: Spells base casting level.
	 * subtype: Value of the subtype. What this value signifies depends on spellType.
	 */
	
	public static final byte STRENGTH_MAXSIZE = 30;
	public static final byte STRENGTH_MINSIZE = 1;
	
	// For use with items. Indicates that the player's spell level should be used.
	public static final short VAL_USEPLAYERSLEVEL = 0; 
	public static final short VAL_MINBASELEVEL = 1; // Minimum base level for a spell
	public static final short VAL_MINLEVEL = 0;
	public static final short VAL_MAXLEVEL = Short.MAX_VALUE;
	
	public static final byte STATE_ADD = 1;
	public static final byte STATE_REMOVE = 0;
	
	
	Spell(String newName, short newID)
	{
		name = newName;
		spellID = newID;
		stats = new byte[Stats.values().length];
		description = "";
		effectName = "";
		spellType = SpellType.Damage;
		
		for(Stats st : Stats.values())
			stats[st.value()] = (byte)0;
		baseLevel = numGroups = strength = 1;
		subtype = 0;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getEffectString()
	{
		return effectName;
	}
	
	public short getID()
	{
		return spellID;
	}
	
	/**
	 * Retrieves the type of spell this is. See SpellType for the types
	 * Note: Classes != Type. Type is the kind of effect it has (does it
	 * heal? kill?). Class is groupings for the user.
	 * @return
	 */
	public SpellType getSpellType()
	{
		return spellType;
	}
	
	/**
	 * Retrieve the stat requirement for a stat.
	 * @param statsType
	 * @return
	 */
	public byte getStats(Stats statsType)
	{
		return stats[statsType.value()];
	}
	
	/**
	 * Retrieves the number of groups this spell affects.
	 * @return
	 */
	public byte getNumberGroupsAffect()
	{
		return numGroups;
	}
	
	/**
	 * Retrieves the base casting level for this spell.
	 * @return
	 */
	public short getBaseLevel()
	{
		return baseLevel;
	}
	
	/**
	 * Retrieves the strength of the spell. This can either
	 * be the number of monsters affected in a group or the
	 * size of the change in resistance.
	 * @return
	 */
	public byte getSpellStrength()
	{
		return strength;
	}
	
	/**
	 * Retrieves the value corresponding to the subtype of this
	 * spell. Note: What this value signifies depends on spellType
	 * @return
	 */
	public byte getSubType()
	{
		return subtype;
	}
	
	public void setName(String newName)
	{
		name = newName;
	}
	
	public void setDescription(String newDescription)
	{
		description = newDescription;
	}
	
	public void setEffectString(String newEffectName)
	{
		effectName = newEffectName;
	}
	
	public void setID(short newID)
	{
		spellID = newID;
	}
	
	/**
	 * Sets the type of spell this is.
	 * @param newSpellType
	 */
	public void setSpellType(SpellType newSpellType)
	{
		spellType = newSpellType;
	}
	
	/**
	 * Sets the stat requirement for a stat
	 * @param statsType
	 * @param statsValue
	 */
	public void setStats(Stats statsType, byte statsValue)
	{
		stats[statsType.value()] = Util.FITBYTE(statsValue, Stats.MINIMUMVALUE, Stats.MAXIMUMVALUE);
	}
	
	/**
	 * Sets the base casting level for this spell.
	 * @param newBaseLevel
	 */
	public void setBaseLevel(short newBaseLevel)
	{
		baseLevel = newBaseLevel;
	}
	
	/**
	 * Sets the number of groups that this spell affects
	 * @param newNumberGroups
	 */
	public void setNumberGroups(byte newNumberGroups)
	{
		numGroups = newNumberGroups;
	}
	
	/**
	 * Sets the number of monsters or the amount of resistance
	 * this spell effects.
	 * @param newStrength
	 */
	public void setSpellStrength(byte newStrength)
	{
		strength = newStrength;
	}
	
	/**
	 * Sets the value to signify the subtype of this spell.
	 * Note: What this value signifies depends on the spellType
	 * @param newSubType
	 */
	public void setSubType(byte newSubType)
	{
		subtype = newSubType;
	}
	
	/**
	 * Casts the spell on a group of monsters. Monsters should be ordered
	 * before being sent to player (E.g. selected group should be in
	 * monsters[0][x], others in monsters[1to3][x]
	 * Certain types of spells don't affect monsters and should simply
	 * post the message 'non-combat spell'
	 * @param monsters	Two dimensional array of monsters it is used on
	 * @param castingPlayer	Player that casted the spell
	 * @return
	 */
	public boolean affectMonsters(Monster[][] monsters, Player castingPlayer)
	{
		// TODO: code spells that affect monsters
		// kick out if spell doesn't affect monsters
		return true;
	}
	
	public boolean affectPlayers(Player[] players, Monster[][] companions, Player castingPlayer)
	{
		// TODO: code spells that affect players
		// kick out if spell doesn't affect players
		return true;
	}
	
	/**
	 * Determines if the owning player can use the spell (i.e. high enough stats)
	 * @param owningPlayer	Player owning the spell
	 * @return boolean	True if owning player can cast the spell.
	 */
	public boolean usable(Player owningPlayer)
	{
		return true;
	}
	
	public void writeSpell(DataOutputStream dos)
	{
		try
		{
			dos.writeShort(spellID);
			dos.writeByte(spellType.value());
			dos.writeByte(strength);
			dos.writeByte(numGroups);
			dos.writeShort(baseLevel);
			dos.writeByte(subtype);
			for(byte i = 0; i < stats.length; i++)
				dos.writeByte(stats[i]);
			dos.writeUTF(name);
			dos.writeUTF(effectName);
			dos.writeUTF(description);
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}
	
	/**
	 * Reads a spell from a stream.
	 * @param dis	The input stream
	 * @return	Spell	The new spell.
	 */
	public static final Spell readSpell(DataInputStream dis)
	{
		Spell spell = new Spell(Util.NOSTRING, (short)0);
		
		try
		{
			spell.setID(dis.readShort());
			spell.setSpellType(SpellType.type(dis.readByte()));
			spell.setSpellStrength(dis.readByte());
			spell.setNumberGroups(dis.readByte());
			
			spell.setBaseLevel(dis.readShort());
			spell.setSubType(dis.readByte());
			for(Stats st : Stats.values())
				spell.setStats(st, dis.readByte());
			spell.setName(dis.readUTF());
			spell.setEffectString(dis.readUTF());
			spell.setDescription(dis.readUTF());
		}
		catch(EOFException e)
		{
			return null;
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
		
		return spell;
	}
}