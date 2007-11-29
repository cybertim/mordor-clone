package mordorEnums;

public enum Trap
{
	None(0, 0),
	Flame(1, 1),
	Fear(2, 4),
	Withering(3, 4),
	Teleport(4, 4),
	Slime(5, 7),
	Blackout(6, 8),
	Fate(7, 8);

	private final byte trapType, minLevel;
	Trap(int nTrapType, int nMinLevel)
	{
		trapType = (byte)nTrapType;
		minLevel = (byte)nMinLevel;
	}
	
	public byte value()
	{
		return trapType;
	}
	
	public byte minimumLevel()
	{
		return minLevel;
	}
	
	/**
	 * Retrieve an array of all possible traps upto the specified level.
	 * @param level
	 * @return
	 */
	public static Trap[] allowedTraps(byte level)
	{
		int count = 0;
		for(Trap it : Trap.values())
			if(it.minLevel <= level)
				count++;

		Trap[] traps;
		
		if(count == 0)
		{
			traps = new Trap[1];
			traps[1] = None;
			return traps;
		}
		else
			traps = new Trap[count];
		
		count = 0;
		for(Trap it : Trap.values())
			if(it.minLevel <= level)
			{
				traps[count] = it;
				count++;
			}
		
		return traps;
	}
	
	/**
	 * Get the type from a value.
	 * @param val int.
	 * @return
	 */
	public static Trap type(int val)
	{
		for(Trap it : Trap.values())
			if(it.value() == val)
				return it;
		
		return None;
	}
}
