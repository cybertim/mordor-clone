package mordorEnums;

/**
 * Enum for the different types of drops that monsters can make.
 * @author August Junkala (Sept 18, 2007)
 *
 */
public enum DropTypes
{
	None(0),
	OnHand(1),
	Box(2),
	Chest(3);
	
	private byte typeVal;
	DropTypes(int nVal) { typeVal = (byte)nVal; }
	public byte value() { return typeVal; }
	public static final DropTypes type(byte nVal)
	{
		for(DropTypes dt : DropTypes.values())
			if(dt.value() == nVal)
				return dt;
		
		return None;
	}

}
