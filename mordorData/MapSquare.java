package mordorData;

import mordorEnums.Direction;
import mordorEnums.PlayerState;
import mordorHelpers.Coord;

/**
 * Map Square class for Mordor Applet.
 * @author August Junkala. Pre April 2007
 *
 */
public class MapSquare
{	
/*	public static final byte NORTH = 0;
	public static final byte EAST = 1;
	public static final byte SOUTH = 2;
	public static final byte WEST = 3;
	public static final String[] DIR_STRINGS = {"North", "East", "South", "West"};*/
	
	public static final byte BYTE_WALLNONE = 0;
	public static final byte BYTE_WALLROCK = 2;
	public static final byte BYTE_WALLDOOR = 4;
	public static final byte BYTE_WALLFOUNDDOOR = 6;
	public static final byte BYTE_WALLHIDDENDOOR = 8;
	public static final byte MAXWALLTYPE = 8;
	
	// old style, retained for drawing routines
	public static final int NOWALL = -1;
	public static final int WALL_ROCK = 0;
	public static final int WALL_DOOR = 1;
	public static final int WALL_FOUNDDOOR = 2;
	public static final int WALL_HIDDENDOOR = 3;
	
	private Room room;
	private byte x, y, z;
	private boolean solidRock;
	private byte wall[] = new byte[4];
	private boolean visited;
	
	
	// ONLY featureB can hold a location changing feature
	private SquareFeature featureA, featureB;
	
	/**
	 * Default constructor. Creates a square with the given coordinates
	 * and sets it up as a block of stone.
	 * @param nX	X value of square
	 * @param nY	Y value of square
	 * @param nZ	Z value of square (level)
	 */
	MapSquare(byte nX, byte nY, byte nZ, boolean nVisited)
	{
		x = nX;
		y = nY;
		z = nZ;
		solidRock = true;
		
		wall[Direction.North.value()] = wall[Direction.East.value()] = wall[Direction.South.value()] = wall[Direction.West.value()] = BYTE_WALLROCK;
		
		visited = nVisited;
	}
	
	MapSquare(byte newX, byte newY, byte newZ, byte nNWall, byte nEWall, byte nSWall, byte nWWall, SquareFeature newFeatureA, SquareFeature newFeatureB, Room newRoom, boolean newVisited)
	{
		x = newX;
		y = newY;
		z = newZ;
		wall[Direction.North.value()] = nNWall;
		wall[Direction.East.value()] = nEWall;
		wall[Direction.South.value()] = nSWall;
		wall[Direction.West.value()] = nWWall;
		
		featureA = newFeatureA;
		featureB = newFeatureB;
		room = newRoom;
		visited = newVisited;
		
		solidRock = false;
	}
	
	MapSquare(byte[] newByte, Room newRoom, ImageBank images)
	{
		room = newRoom;
		
		x = newByte[0];
		y = newByte[1];
		z = newByte[2];
		
		wall[Direction.North.value()] = (byte)((newByte[5] & 112) >>> 3);
		wall[Direction.East.value()] = (byte)((newByte[6] & 112) >>> 3);
		wall[Direction.South.value()] = (byte)((newByte[5] & 14));
		wall[Direction.West.value()] = (byte)((newByte[6] & 14));
		
		solidRock = ((newByte[5] & 1) == 1) ? true : false;
		visited = ((newByte[6] & 1) == 1) ? true : false;
		
		featureA = new SquareFeature(newByte[7], newByte[8], newByte[9], newByte[10], images);
		featureB = new SquareFeature(newByte[11], newByte[12], newByte[13], newByte[14], images);
	}
	
	/**
	 * Determines the existence of any type of wall in the direction specified
	 * by wall number (see constants for directions)
	 * @param wallNum	int
	 * @return	boolean
	 */
	public boolean hasWall(byte wallNum)
	{
		if(wallNum > Direction.MAXDIRECTION.value() || wallNum < 0)
			return true;
		return (wall[wallNum] != BYTE_WALLNONE);
	}
	
	public boolean hasWall(Direction direction)
	{
		return (wall[direction.value()] != BYTE_WALLNONE);
	}
	
	/**
	 * Determines if the player can pass in the specified direction.
	 * @param wallNum	Direction of wall to check
	 * @return boolean	True if passable.
	 */
	/*public boolean directionPassable(byte wallNum)
	{DELETE
		if(wall[wallNum] == BYTE_WALLHIDDENDOOR)
			wall[wallNum] = BYTE_WALLFOUNDDOOR;
		return (wall[wallNum] != BYTE_WALLROCK);
	}*/
	public boolean directionPassable(Direction direction)
	{
		if(wall[direction.value()] == BYTE_WALLHIDDENDOOR)
			wall[direction.value()] = BYTE_WALLFOUNDDOOR;
		return (wall[direction.value()] != BYTE_WALLROCK);
	}
	
	/**
	 * Determines if this square contains stairs.
	 * @return boolean	True if contains stairs.
	 */
	public boolean areStairs()
	{
		return (areStairs(featureA) || areStairs(featureB));
	}
	
	private boolean areStairs(SquareFeature feature)
	{
		return (feature.getType() == SquareFeature.TYPE_STAIRSDOWN || 
				feature.getType() == SquareFeature.TYPE_STAIRSUP || 
				feature.getType() == SquareFeature.TYPE_EXIT);
	}
	
	public SquareFeature getStairs()
	{
		if(areStairs(featureA))
			return featureA;
		else if(areStairs(featureB))
			return featureB;
		else
			return null;
	}
	
	/**
	 * Retrieves the wall specified by the wall number (see constants for directions)
	 * Note: returns hidden doors as regular walls.
	 * @param wallNum	int
	 * @return	boolean
	 */
	public byte getWall(byte wallNum)
	{
		switch(wall[wallNum])
		{
		case BYTE_WALLROCK:
			return WALL_ROCK;
		case BYTE_WALLDOOR:
			return WALL_DOOR;
		}
		
		return NOWALL + 1;
	}
	
	/**
	 * Retrieves the wall type.
	 * @param wallNum	See direction constants
	 * @return int	Type of wall (wall constants)
	 */
	/*public byte getWallType(byte wallNum)
	{DELETE
		return wall[wallNum];
	}*/
	public byte getWallType(Direction direction)
	{
		return wall[direction.value()];
	}
	
	/**
	 * Retrieves the room this square is part of.
	 * @return	Room
	 */
	public Room getRoom()
	{
		return room;
	}
	
	/**
	 * Retrieves the x value of this square.
	 * @return	int
	 */
	public byte getXCoordinate()
	{
		return x;
	}
	
	/**
	 * Retrieves the y value of this square.
	 * @return	int
	 */
	public byte getYCoordinate()
	{
		return y;
	}
	
	/**
	 * Retrieves the z value of this square.
	 * @return
	 */
	public byte getZCoordinate()
	{
		return z;
	}
	
	public SquareFeature[] getSquareFeatures()
	{
		SquareFeature[] features = new SquareFeature[2];
		features[0] = featureA;
		features[1] = featureB;
		return features;
	}
	
	/**
	 * Sets a pointer to the room this square is in.
	 * @param nRoom Room
	 */
	public void updateRoom(Room nRoom)
	{
		room = nRoom;
	}
	
	/**
	 * Make any changes to the player/map when the player enters a square.
	 * @param player
	 * @param map
	 * @return If player is still here.
	 */
	public boolean enterSquare(Player player, Map map)
	{
		if(!player.isLost())
			visited = true;
		
		Coord coords = player.getCoord();
		coords.setX(x);
		coords.setY(y);
		coords.setZ(z);
		
		// If this square has a feature in A, cause it to affect the player.
		if(featureA.getType() != SquareFeature.TYPE_NONE)
			featureA.affectPlayer(player, map);
		
		// If the player has moved, this square is no longer affecting the player.
		if(player.getCoord().getX() != x || player.getCoord().getY() != y || player.getCoord().getZ() != z)
			return false;
		
		if(featureB.getType() != SquareFeature.TYPE_NONE)
			featureB.affectPlayer(player, map);

		if(player.getCoord().getX() != x || player.getCoord().getY() != y || player.getCoord().getZ() != z)
			return false;
		
		player.setState(PlayerState.Fogged, isFogSquare(), Player.STATENATURAL);
		
		// Ok, no more recursions, reset it.
		player.recurseCount = 0;
		return true;
	}
	
	/**
	 * Returns true if someone has visited this square.
	 * @return boolean
	 */
	public boolean isVisited()
	{
		return visited;
	}
	
	/**
	 * Checks if the room is simply solid rock.
	 * @return	boolean
	 */
	public boolean isSolidRock()
	{
		return solidRock;
	}
	
	public boolean isSandSquare()
	{
		return (featureA.getType() == SquareFeature.TYPE_SAND || featureB.getType() == SquareFeature.TYPE_SAND);
	}
	
	public boolean isWaterSquare()
	{
		return (featureA.getType() == SquareFeature.TYPE_WATER || featureB.getType() == SquareFeature.TYPE_WATER);
	}
	
	public boolean isFogSquare()
	{
		return (featureA.getType() == SquareFeature.TYPE_FOG || featureB.getType() == SquareFeature.TYPE_FOG);
	}
	
	public boolean isStudSquare()
	{
		return (featureA.getType() == SquareFeature.TYPE_STUD || featureB.getType() == SquareFeature.TYPE_STUD);
	}
	
	public boolean isExitSquare()
	{
		return (featureA.getType() == SquareFeature.TYPE_EXIT || featureB.getType() == SquareFeature.TYPE_EXIT);
	}
	
	public boolean isRotatorSquare()
	{
		return (featureA.getType() == SquareFeature.TYPE_ROTATOR|| featureB.getType() == SquareFeature.TYPE_ROTATOR);
	}
	
	public boolean hasEmptyFeature()
	{
		return (featureA.getType() == SquareFeature.TYPE_NONE || featureB.getType() == SquareFeature.TYPE_NONE);
	}
	
	
	public void setX(byte newX)
	{
		x = newX;
	}
	
	public void setY(byte newY)
	{
		y = newY;
	}
	
	public void setZ(byte newZ)
	{
		z = newZ;
	}
	
	public void setWall(Direction direction, byte newWallType)
	{
		wall[direction.value()] = newWallType;
	}
	
	/**
	 * Sets the visited status of this square.
	 * @param newVisited New visited status. (boolean)
	 */
	public void setVisited(boolean newVisited)
	{
		visited = newVisited;
	}
	
	/**
	 * Sets this squares solid rock status.
	 * @param newSolid Whether or not this square is solid rock (boolean)
	 */
	public void setSolidRock(boolean newSolid)
	{
		solidRock = newSolid;
	}
	
	/**
	 * Sets the room this square is a part of.
	 * @param newRoom The room (Room)
	 */
	public void setRoom(Room newRoom)
	{
		room = newRoom;
	}
	
	/**
	 * Creates a new instance of this square and returns.
	 * @return MapSquare The new copy
	 */
	public MapSquare copySquare()
	{
		if(solidRock)
			return new MapSquare(x, y, z, visited);
		else
			return new MapSquare(x, y, z, wall[Direction.North.value()], wall[Direction.East.value()], wall[Direction.South.value()], wall[Direction.West.value()], featureA.copyFeature(), featureB.copyFeature(), room, visited);
	}
	
	/**
	 * Adjust this square so it matches the square passed to it.
	 * @param oldSquare Square to be matched (MapSquare)
	 */
	public void replicateSquare(MapSquare oldSquare)
	{
		x = oldSquare.x;
		y = oldSquare.y;
		z = oldSquare.z;
		room = oldSquare.room;
		solidRock = oldSquare.solidRock;
		visited = oldSquare.visited;
		wall[Direction.North.value()] = oldSquare.wall[Direction.North.value()];
		wall[Direction.East.value()] = oldSquare.wall[Direction.East.value()];
		wall[Direction.South.value()] = oldSquare.wall[Direction.South.value()];
		wall[Direction.West.value()] = oldSquare.wall[Direction.West.value()];
		featureA.replicateFeature(oldSquare.featureA);
		featureB.replicateFeature(oldSquare.featureB);
	}
	
	/**
	 * Retrieves a string of the possible wall types in order of their
	 * byte representations.
	 * @return String[]
	 */
	public static final String[] getWallTypeStrings()
	{
		String[] wallTypes = {"None", "Solid", "Door", "Hidden Door: Found", "Hidden Door: Not found"};
		
		return wallTypes;
	}
	
	/**
	 * Retrieves the byte representation of this square. For Saving.
	 * @return byte[16]
	 */
	public byte[] toByte()
	{
		byte[] newByte = new byte[16];
		
		newByte[0] = DataBank.FLAG_SQUARE;
		newByte[1] = x;
		newByte[2] = y;
		newByte[3] = z;
		newByte[4] = (room != null) ? room.getRoomID()[0] : 0;
		newByte[5] = (room != null) ? room.getRoomID()[1] : 0;
		newByte[6] = (byte)(wall[Direction.North.value()] << 3);
		newByte[6] |= (byte)(wall[Direction.South.value()]);
		newByte[6] |= (solidRock) ? 1 : 0;
		newByte[7] = (byte)(wall[Direction.East.value()] << 3);
		newByte[7] |= (byte)(wall[Direction.West.value()]);
		newByte[7] |= (visited) ? 1 : 0;
		if(featureA != null)
		{
			newByte[8] = featureA.toByte()[0];
			newByte[9] = featureA.toByte()[1];
			newByte[10] = featureA.toByte()[2];
			newByte[11] = featureA.toByte()[3];
		}
		else
			newByte[8] = newByte[9] = newByte[10] = newByte[11] = 0;
		
		if(featureB != null)
		{
			newByte[12] = featureB.toByte()[0];
			newByte[13] = featureB.toByte()[1];
			newByte[14] = featureB.toByte()[2];
			newByte[15] = featureB.toByte()[3];
		}
		else
			newByte[12] = newByte[13] = newByte[14] = newByte[15] = 0;
		
		return newByte;
	}
	
	/**
	 * Retrieves the string representation of this square.
	 * @return String
	 */
	public String toString()
	{
		String squareString = "SQUARE";
		
		squareString += " " + z + " " + x + " " + y;
		
		if(solidRock)
		{
			squareString += " STONE";
			squareString += (visited) ? " TRUE" : " FALSE\n";
		}
		else
		{
			squareString += featureA.toString();
			squareString += featureB.toString();
			
			squareString += " " + wall[0] + " " + wall[1] + " " + wall[2] + " " + wall[3];
			
			squareString += " " + room.getRoomNumber();
			
			squareString += (visited) ? " TRUE\n" : " FALSE\n";
		}
		
		return squareString;
	}
}
