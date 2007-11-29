package mordorHelpers;

import mordorData.MapSquare;
import mordorEnums.Direction;
import mordorEnums.PlayerState;

/**
 * Class to store coordinates as a single unit.
 * @author August Junkala, Nov 4, 2007
 *
 */
public class Coord
{
	private byte x, y, z;
	private Direction direction;
	
	public Coord(byte nX, byte nY, byte nZ)
	{
		x = nX;
		y = nY;
		z = nZ;
		direction = Direction.type(0);
	}
	
	public Coord(byte nX, byte nY, byte nZ, Direction nDir)
	{
		x = nX;
		y = nY;
		z = nZ;
		direction = nDir;
	}
	
	public Coord(Coord nCoord)
	{
		x = nCoord.x;
		y = nCoord.y;
		z = nCoord.z;
		direction = nCoord.direction;
	}
	
	public byte getX()
	{
		return x;
	}
	
	public byte getY()
	{
		return y;
	}
	
	public byte getZ()
	{
		return z;
	}
	
	public Direction getDirection()
	{
		return direction;
	}
	
	public void setX(byte nX)
	{
		x = nX;
	}
	
	public void setY(byte nY)
	{
		y = nY;
	}
	
	public void setZ(byte nZ)
	{
		z = nZ;
	}
	
	public void setDirection(Direction nDir)
	{
		if(nDir != null)
			direction = nDir;
		else
			direction = Direction.type(0);
	}
	
	/**
	 * Retrieves the neighbour of the this coordinate based on a direction.
	 * Note: the dungeon increase down/east/north.
	 * @param dir	Direction
	 * @return Coord
	 */
	public Coord getNeighbour(Direction dir)
	{
		byte nX = x, nY = y, nZ = z;
		switch(dir)
		{
		case North:
			nY = Util.FITBYTE(y + 1, y, Byte.MAX_VALUE);
			break;
		case East:
			nX = Util.FITBYTE(x + 1, x, Byte.MAX_VALUE);
			break;
		case South:
			nY = Util.FITBYTE(y - 1, 0, y);
			break;
		case West:
			nX = Util.FITBYTE(x - 1, 0, x);
			break;
		case Up:
			nZ = Util.FITBYTE(z - 1, 0, z);
			break;
		case Down:
			nZ = Util.FITBYTE(z + 1, z, Byte.MAX_VALUE);
			break;
		}
		
		return new Coord(nX, nY, nZ, direction);
	}
	
	public String toString()
	{
		String coordinateString = "";
		coordinateString += x + ", " + y + ", ";
		coordinateString += z;
		switch(direction)
		{
		case North:
			coordinateString += ", NORTH";
			break;
		case East:
			coordinateString += ", EAST";
			break;
		case South:
			coordinateString += ", SOUTH";
			break;
		case West:
			coordinateString += ", WEST";
			break;
		}
		
		return coordinateString;
	}
}
