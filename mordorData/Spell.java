package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;

import mordorEnums.SpellType;
import mordorEnums.Stats;
import mordorHelpers.Util;

public class Spell extends MObject<SpellType>
{
	private String effectName; // e.g. dazzeled
	private byte strength, numGroups, subtype;
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
		super(newID);
		
		name = newName;
		effectName = "";
		type = SpellType.Damage;
		
		level = numGroups = strength = 1;
		subtype = 0;
	}
	
	public String getEffectString()
	{
		return effectName;
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
	
	public void setEffectString(String newEffectName)
	{
		effectName = newEffectName;
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
			dos.writeShort(ID);
			dos.writeByte(type.value());
			dos.writeByte(strength);
			dos.writeByte(numGroups);
			dos.writeShort(level);
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
			spell.ID = dis.readShort();
			spell.type = SpellType.type(dis.readByte());
			spell.setSpellStrength(dis.readByte());
			spell.setNumberGroups(dis.readByte());
			
			spell.level = dis.readShort();
			spell.setSubType(dis.readByte());
			for(Stats st : Stats.values())
				spell.stats[st.value()] = dis.readByte();
			spell.name = dis.readUTF();
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

	@Override
	public String generateDescription(boolean html)
	{
		// TODO Auto-generated method stub
		return description;
	}
}