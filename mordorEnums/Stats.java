package mordorEnums;

/**
 * Enum for the different base statistics for things in Mordor.
 * @author August Junkala (Sept 15, 2007)
 *
 */
public enum Stats
{
	/* As in all enums, these should be successive */
	
	Strength(0),
	Intelligence(1),
	Wisdom(2),
	Constitution(3),
	Charisma(4),
	Dexterity(5);

	public static final byte MINIMUMVALUE = 0;
	public static final byte MAXIMUMVALUE = 64;
	public static final byte MAXIMUMNEGADJUSTMENT = -64;
	public static final byte MAXIMUMPOSADJUSTMENT = 64;
	public static final byte MAXIMUMEXTENDED = 5; /** The amount beyond a race's maximum a stat can go. */
	
	public static final byte DEFAULTMINSTAT = 4;
	public static final byte DEFAULTMAXSTAT = 18;
	
	private byte typeVal;
	Stats(int nVal) { typeVal = (byte)nVal; }
	public byte value() { return typeVal; }
	public static Stats type(byte nVal)
	{
		for(Stats st : Stats.values())
			if(st.value() == nVal)
				return st;
		return Strength;
	}
}
