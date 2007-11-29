package mordorEnums;

/**
 * Enum for alignments
 * @author August Junkala Sept 13, 2007
 *
 */
public enum Alignment
{
	Good(0),
	Neutral(1),
	Evil(2);
	
	private byte typeVal;
	Alignment(int nVal)
	{
		typeVal = (byte)nVal;
	}
	
	public byte value()
	{
		return typeVal;
	}
	
	public static Alignment type(byte nVal)
	{
		for(Alignment al : Alignment.values())
			if(al.value() == nVal)
				return al;
		
		return Neutral;
	}
}
