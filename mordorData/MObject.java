package mordorData;

import mordorEnums.Stats;
import mordorHelpers.Util;

public abstract class MObject<TYPE>
{
	protected String name;
	protected short ID;
	protected TYPE type; // RACE in players, MonsterClass in monsters
	protected byte stats[];
	protected short level; // convert to byte for item/monster, useless in player?
	protected short attack, defense; // Unused in spells, usurp in player.
	protected String description;
	
	MObject(short nID)
	{
		name = Util.NOSTRING;
		description = "";
		ID = nID;
		type = null;
		stats = new byte[Stats.values().length];
		for(Stats st : Stats.values())
			stats[st.value()] = 0;
		
		level = Util.NOTHING;
		attack = 0;
		defense = 0;
	}
	
	public String toString()
	{
		return name;
	}
	
	/**
	 * Retrieve name.
	 * @return	String
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieve the description.
	 * @return	String
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Generate a description.
	 * @param html If true, generate an HTML formated description.
	 * @return	String
	 */
	public abstract String generateDescription(boolean html);
	
	/**
	 * Retrieve ID
	 * @return	short
	 */
	public short getID()
	{
		return ID;
	}
	
	/**
	 * Retrieve type/race
	 * @return TYPE
	 */
	public TYPE getType()
	{
		return type;
	}
	
	/**
	 * Retrieve stats.
	 * @param st	Stats enum
	 * @return	byte
	 */
	public byte getStat(Stats st)
	{
		return stats[st.value()];
	}
	
	/**
	 * Retrieve level.
	 * @return	short
	 */
	public short getLevel()
	{
		return level;
	}
	
	/**
	 * Retrieve attack
	 * @return	short
	 */
	public short getAttack()
	{
		return attack;
	}
	
	/**
	 * Retrieve defense
	 * @return	short
	 */
	public short getDefense()
	{
		return defense;
	}
	
	/**
	 * Set name.
	 * @param nName	String
	 */
	public void setName(String nName)
	{
		if(nName != null)
			name = nName;
		else
			name = Util.NOSTRING;
	}
	
	/**
	 * Set the description.
	 * @param nDescription
	 */
	public void setDescription(String nDescription)
	{
		description = nDescription;
	}
	
	/**
	 * Set ID
	 * @param nID	short
	 */
	public void setID(short nID)
	{
		ID = nID;
	}
	
	/**
	 * Set type.
	 * @param nType
	 */
	public void setType(TYPE nType)
	{
		if(nType != null)
			type = nType;
	}
	
	/**
	 * Set stat
	 * @param st stat to set	Stats enum
	 * @param newVal	value to give it	byte
	 */
	public void setStat(Stats st, byte newVal)
	{
		stats[st.value()] = newVal;
	}
	
	/**
	 * Set level
	 * @param nLevel	short
	 */
	public void setLevel(short nLevel)
	{
		level = nLevel;
	}
	
	/**
	 * Set attack
	 * @param nAttack	short
	 */
	public void setAttack(short nAttack)
	{
		attack = nAttack;
	}
	
	/**
	 * Set defense
	 * @param nDefense	short
	 */
	public void setDefense(short nDefense)
	{
		defense = nDefense;
	}
}