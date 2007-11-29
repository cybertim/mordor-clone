package mordorData;

import mordorEnums.Alignment;
import mordorEnums.Stats;
import mordorHelpers.Util;

public abstract class MObject<TYPE>
{
	protected String mName;
	protected short mID;
	protected TYPE mType; // RACE in players, MonsterClass in monsters
	protected byte mStats[];
	protected short mLevel; // convert to byte for item/monster, useless in player?
	protected short mAttack, mDefense; // Unused in spells, usurp in player.
	
	MObject(short nID)
	{
		mName = Util.NOSTRING;
		mID = nID;
		mType = null;
		mStats = new byte[Stats.values().length];
		mLevel = Util.NOTHING;
		mAttack = 0;
		mDefense = 0;
	}
	
	public String toString()
	{
		return mName;
	}
	
	/**
	 * Retrieve name.
	 * @return	String
	 */
	public String getName()
	{
		return mName;
	}
	
	/**
	 * Retrieve ID
	 * @return	short
	 */
	public short getID()
	{
		return mID;
	}
	
	/**
	 * Retrieve type/race
	 * @return TYPE
	 */
	public TYPE getType()
	{
		return mType;
	}
	
	/**
	 * Retrieve stats.
	 * @param st	Stats enum
	 * @return	byte
	 */
	public byte getStat(Stats st)
	{
		return mStats[st.value()];
	}
	
	/**
	 * Retrieve level.
	 * @return	short
	 */
	public short getLevel()
	{
		return mLevel;
	}
	
	/**
	 * Retrieve attack
	 * @return	short
	 */
	public short getAttack()
	{
		return mAttack;
	}
	
	/**
	 * Retrieve defense
	 * @return	short
	 */
	public short getDefense()
	{
		return mDefense;
	}
	
	/**
	 * Set name.
	 * @param nName	String
	 */
	public void setName(String nName)
	{
		if(nName != null)
			mName = nName;
		else
			mName = Util.NOSTRING;
	}
	
	/**
	 * Set ID
	 * @param nID	short
	 */
	public void setID(short nID)
	{
		mID = nID;
	}
	
	/**
	 * Set type.
	 * @param nType
	 */
	public void setType(TYPE nType)
	{
		if(nType != null)
			mType = nType;
	}
	
	/**
	 * Set stat
	 * @param st stat to set	Stats enum
	 * @param newVal	value to give it	byte
	 */
	public void setStat(Stats st, byte newVal)
	{
		mStats[st.value()] = newVal;
	}
	
	/**
	 * Set level
	 * @param nLevel	short
	 */
	public void setLevel(short nLevel)
	{
		mLevel = nLevel;
	}
	
	/**
	 * Set attack
	 * @param nAttack	short
	 */
	public void setAttack(short nAttack)
	{
		mAttack = nAttack;
	}
	
	/**
	 * Set defense
	 * @param nDefense	short
	 */
	public void setDefense(short nDefense)
	{
		mDefense = nDefense;
	}
}