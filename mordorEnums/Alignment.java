package mordorEnums;

/**
 * Enum for alignments
 * @author August Junkala Sept 13, 2007
 *
 */
public enum Alignment
{
	Good(0, 'G'),
	Neutral(1, 'N'),
	Evil(2, 'E');
	
	private byte typeVal;
	private char shortName;
	Alignment(int nVal, char newShortName)
	{
		typeVal = (byte)nVal;
		shortName = newShortName;
	}
	
	public byte value()
	{
		return typeVal;
	}
	
	public char shortName()
	{
		return shortName;
	}
	
	public static Alignment type(byte nVal)
	{
		for(Alignment al : Alignment.values())
			if(al.value() == nVal)
				return al;
		
		return Neutral;
	}
}
