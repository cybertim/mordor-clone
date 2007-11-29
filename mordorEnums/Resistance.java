package mordorEnums;

/**
 * Enum for resistances.
 * Note: has a second variable for stowing a percentage.
 * @author August Junkala (Sept 13, 2007)
 *
 */
public enum Resistance 
{
	Fire(0),
	Cold(1),
	Electrical(2),
	Mind(3),
	Disease(4),
	Poison(5),
	Magic(6),
	Stoning(7),
	Paralysis(8),
	Draining(9),
	Acid(10);
	
	private byte typeVal;
	Resistance(int nVal)
	{
		typeVal = (byte)nVal;
	}
	
	public byte value()
	{
		return typeVal;
	}
	
	public static Resistance type(int nVal)
	{
		for(Resistance re : Resistance.values())
			if(re.value() == nVal)
				return re;
		
		return Magic;
	}
}
