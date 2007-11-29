package mordorEnums;

/**
 * Enum for different monster group sizes.
 * @author August Junkala (Sept 18, 2007)
 *
 */
public enum MonsterGroupSize
{
	One(0, 1, 1),
	Few(1, 2, 3),
	Several(2, 4, 6),
	Many(3, 7, 12),
	Lots(4, 13, 20);
	
	private byte typeVal, lowVal, highVal;
	MonsterGroupSize(int nVal, int nLVal, int nHVal)
	{
		typeVal = (byte)nVal;
		lowVal = (nLVal < nHVal) ? (byte)nLVal : (byte)nHVal;
		highVal = (nHVal > nLVal) ? (byte)nHVal : (byte)nLVal;
	}
	public byte value() { return typeVal; }
	public int range() { return (highVal - lowVal); }
	public int lowBound() { return lowVal; }
	public int highBound() { return highVal; }
	public static final MonsterGroupSize type(byte nVal)
	{
		for(MonsterGroupSize mg : MonsterGroupSize.values())
			if(mg.value() == nVal)
				return mg;
		
		return One;
	}
	public static final byte MAXMONSTERCOUNT() { return Lots.highVal; }
	
}
