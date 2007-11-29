package mordorData;

import mordorEnums.PlayerState;
import mordorEnums.Resistance;
import mordorHelpers.Util;

/**
 * Helper class for handling item specials;
 * @author August Junkala, June 2, 2007
 *
 */
public class ItemSpecials
{
	public static final byte ITEMSPECIAL_NONE = 0;
	public static final byte ITEMSPECIAL_RESISTANCE = 1;
	public static final byte ITEMSPECIAL_SPELL = 2;
	public static final byte ITEMSPECIAL_STATE = 3;
	public static final byte ITEMSPECIAL_OTHER = 4;

	/**
	 * Maximum spell casts for any item special spell.
	 */
	public static final byte MAXSPELLCASTS = 120;
	
	public static final String[] ITEMSPECIAL_NAMES = {"None", "Resistance", "Spell", "State", "Other"};
	
	public static final byte ITEM_SPECIAL_OTHER_CH = 0; // Critical Hit
	public static final byte ITEM_SPECIAL_OTHER_BS = 1; // Backstabbing
	public static final byte ITEM_SPECIAL_OTHER_POY = 2; // Potion of youth - 1 year
	public static final byte ITEM_SPECIAL_OTHER_DB = 3; // Dragon's Blood - 10 year 
	public static final String[] ITEMSPECIAL_OTHERNAMES = {"Critical Hit", "Backstabbing", "1 Year Youth", "10 Years Youth"};
	
	byte type;	// Type of item special this is.
	short firstVal, secondVal, thirdVal; // Values stored for this special.
	
	ItemSpecials(byte newType, short newFirstValue, short newSecondValue, short newThirdValue)
	{
		type = newType;
		firstVal = secondVal = thirdVal = 0;
		
		switch(type)
		{
		case ITEMSPECIAL_NONE:
			break;
		case ITEMSPECIAL_RESISTANCE:
			firstVal = newFirstValue;	// type of resistance
			setResistanceAmount(newSecondValue); // amount of resistance
			break;
		case ITEMSPECIAL_SPELL:
			firstVal = newFirstValue; // spell ID;
			setSpellLevel(newSecondValue); // spell Level;
			setSpellCasts(newThirdValue); // spell casts;
			break;
		case ITEMSPECIAL_STATE:
			firstVal = newFirstValue; // state affected
			secondVal = Util.FITSHORT(newSecondValue, (short)0, (short)1); // on or off;
			break;
		case ITEMSPECIAL_OTHER:
			firstVal = newFirstValue; // Type of other
			break;
		}
	}
	
	/**
	 * Retrieves the type of item special.
	 * See ItemSpecials.ITEMSPECIAL_XXXX
	 * @return byte
	 */
	public byte getType()
	{
		return type;
	}
	
	/**
	 * Retrieves the first value for the item special.
	 * @return short
	 */
	public short getFirstVal()
	{
		return firstVal;
	}
	
	/**
	 * Retrieves the second value for the item special.
	 * @return short
	 */
	public short getSecondVal()
	{
		return secondVal;
	}
	
	/**
	 * Retrieves the third value for the item special.
	 * @return short
	 */
	public short getThirdVal()
	{
		return thirdVal;
	}
	
	/**
	 * Retrieves a spellReference for the spell.
	 * @param dataBank The databank (DataBank)
	 * @return SpellReference
	 */
	public SpellReference getSpell(DataBank dataBank)
	{
		if(type != ITEMSPECIAL_SPELL)
			return null;
		
		SpellReference tSpell = dataBank.getSpellBook().getSpell(firstVal);
		
		if(tSpell != null)
		{
			tSpell = tSpell.copyRef();
			tSpell.setLevel(secondVal);
		}
		
		return tSpell;
	}
	
	public short getSpellCasts()
	{
		return thirdVal;
	}
	
	/**
	 * Sets the spell. Uses the spell's ID.
	 * @param spellID Spell ID (short)
	 */
	public void setSpell(short spellID)
	{
		firstVal = spellID;
	}
	
	/**
	 * Sets the spell. Uses the spell object.
	 * @param spell The spell to assign (Spell)
	 */
	public void setSpell(Spell spell)
	{
		firstVal = spell.getID();
	}
	
	/**
	 * Sets the number of spell casts available for this spell.
	 * Limited by ItemSpecials.MAXSPELLCASTS
	 * @param newSpellCasts Number of casts (short)
	 */
	public void setSpellCasts(short newSpellCasts)
	{
		thirdVal = Util.FITSHORT(newSpellCasts, (short)0, MAXSPELLCASTS);
	}
	
	/**
	 * Sets the spell level at which the spell casts at.
	 * @param newSpellLevel Spell level (short)
	 */
	public void setSpellLevel(short newSpellLevel)
	{
		secondVal = Util.FITSHORT(newSpellLevel, (short)0, Short.MAX_VALUE);
	}
	/**
	 * Retrieves the form of resistance offered.
	 * See Race.RESIST_NAMES
	 * @return form of resistance (byte)
	 */
	public Resistance getResistanceType()
	{
		return Resistance.type(firstVal);
	}
	
	/**
	 * Retrieves the amount of resistance offered.
	 * @return Percentage of resistance offered (byte)
	 */
	public byte getResistanceAmount()
	{
		return (byte)secondVal;
	}
	
	/**
	 * Sets the form of resistance offered.
	 * See Race.RESIST_NAMES
	 * @param newResistance type of resistance (byte)
	 */
	public void setResistanceType(Resistance newResistance)
	{
		firstVal = newResistance.value();
	}
	
	/**
	 * Sets the amount of resistance offered.
	 * @param newAmount percentage (short)
	 */
	public void setResistanceAmount(short newAmount)
	{
		secondVal = Util.FITPERCENTAGE(newAmount);
	}
	
	/**
	 * Retrieves the type of state.
	 * @return
	 */
	public PlayerState getState()
	{
		return PlayerState.type(firstVal);
	}
	
	/**
	 * Retrieves the string of this special. Presently intended for build the item description.
	 * @param dataBank
	 * @return
	 */
	public String getString(DataBank dataBank)
	{
		switch(type)
		{
		case ITEMSPECIAL_RESISTANCE:
			return getResistanceAmount() + "% " + getResistanceType() + " resistance";
		case ITEMSPECIAL_SPELL:
			return " casts " + getSpell(dataBank).getSpell().getName() + " " + getSpellCasts() + " times at " +  getSecondVal() + " spell level";
		case ITEMSPECIAL_STATE:
			String t = (this.secondVal == 0) ? "removes " : "";
			t += getState();
			return t;
		case ITEMSPECIAL_OTHER:
			return ITEMSPECIAL_OTHERNAMES[firstVal];
		}
		
		return "nothing.";
	}
	
	/**
	 * Determines if the state is on.
	 * @return true if the state is set to on.
	 */
	public boolean isStateOn()
	{
		return (secondVal != 0);
	}
	
	/**
	 * Sets whether a state is on or off.
	 * @param newState state of uh... state (boolean)
	 */
	public void changeStateStatus(boolean newState)
	{
		secondVal = (newState) ? (short)1 : (short)0;
	}
	
	/**
	 * Sets the type of state.
	 * @param newState	The kind of state to set.
	 */
	public void changeState(PlayerState newState)
	{
		if(newState == null)
			type = ITEMSPECIAL_NONE;
		else
			firstVal = newState.value();
	}
	
	/**
	 * Is this special a critical hit enhancement.
	 * @return true if it is.
	 */
	public boolean isCriticalHit()
	{
		return (type == ITEMSPECIAL_OTHER && firstVal == ITEM_SPECIAL_OTHER_CH);
	}
	
	public boolean isBackStab()
	{
		return (type == ITEMSPECIAL_OTHER && firstVal == ITEM_SPECIAL_OTHER_BS);
	}
	
	public boolean isPOY()
	{
		return (type == ITEMSPECIAL_OTHER && firstVal == ITEM_SPECIAL_OTHER_POY);
	}
	
	public boolean isDragonsBlood()
	{
		return (type == ITEMSPECIAL_OTHER && firstVal == ITEM_SPECIAL_OTHER_DB);
	}
	
	/**
	 * Retrieves the type of other.
	 * @return
	 */
	public byte getOtherType()
	{
		return (byte)firstVal;
	}
	
	/**
	 * Sets the type of other this special is.
	 * @param newOtherType See ITEMSPECIAL_OTHER_XX. (short)
	 * @return false if special is not of type other or if the value is not valid.
	 */
	public boolean setOtherType(short newOtherType)
	{
		if(type != ITEMSPECIAL_OTHER || newOtherType < 0 || newOtherType > ITEMSPECIAL_OTHERNAMES.length)
			return false;
		
		firstVal = newOtherType;
		return true;
	}
	
	/**
	 * Set the type of item sepcial this is (see ITEMSPECIAL_NAMES)
	 * @param newType byte new item special type.
	 */
	public void setType(byte newType)
	{
		type = Util.FITBYTE(newType, (byte)0, (byte)ITEMSPECIAL_NAMES.length);
	}
	
	/**
	 * Set the first value.
	 * @param newVal new value for the first value (short).
	 */
	public void setFirstVal(short newVal)
	{
		firstVal = newVal;
	}
	
	/**
	 * Set the second value.
	 * @param newVal New value for second value (short).
	 */
	public void setSecondVal(short newVal)
	{
		secondVal = newVal;
	}
	
	/**
	 * Set the third value.
	 * @param newVal New value for third value (short);
	 */
	public void setThirdVal(short newVal)
	{
		thirdVal = newVal;
	}
}
