package mordorEnums;

/**
 * Enum for creature (Race & monster) size.
 * @author August Junkala (Sept. 13, 2007)
 *
 */
public enum Size
{
	VerySmall(0),
	Small(1),
	Normal(2),
	Big(3),
	VeryBig(4),
	Huge(5);
	
	private byte typeVal;
	Size(int nVal)
	{
		typeVal = (byte)nVal;
	}
	
	public byte value()
	{
		return typeVal;
	}
	
	public static Size type(int nVal)
	{
		for(Size sz : Size.values())
			if(sz.value() == nVal)
				return sz;
		
		return Normal;
	}
}
