package mordorEnums;

public enum Direction
{
	North(0),
	East(1),
	South(2),
	West(3),
	Up(4),
	Down(5);
	
	public static final Direction MAXDIRECTION = West;
	
	private final byte dir;
	Direction(int nDir) { dir = (byte)nDir; }
	
	public byte value() { return dir; }
	
	/**
	 * Retrieve direction counter clockwise
	 * @return
	 */
	public Direction counter()
	{
		if(dir == 0)
			return MAXDIRECTION;
		
		return type(dir - 1);
		
	}
	
	/**
	 * Retrieve direction clockwise.
	 * @return
	 */
	public Direction clock()
	{
		return type((dir + 1) % (MAXDIRECTION.value() + 1));
	}
	
	/**
	 * Retrieve inverse direction
	 * @return
	 */
	public Direction inverse()
	{
		return type((dir + 2) % (MAXDIRECTION.value() + 1));
	}
	
	/**
	 * Retrieve a direction based on a value, else retrieve direction 0
	 * @param val
	 * @return
	 */
	public static Direction type(int val)
	{
		for(Direction di : Direction.values())
			if(di.value() == val)
				return di;
		
		return North;
	}
}
