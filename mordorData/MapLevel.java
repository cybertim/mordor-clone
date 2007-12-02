package mordorData;
import mordorEnums.Direction;
import mordorHelpers.Coord;
import structures.LinkedList;
import structures.ListIter;
import structures.ListNode;


public class MapLevel
{
	private MapSquare[][] levelSquares;
	private LinkedList<Room> rooms;
	private byte level;
	
	public static final int MAXWIDTH = 127;
	public static final int MAXHEIGHT = 127;
	
	/**
	 * Default constructor. Use for new maps.
	 */
	public MapLevel(ImageBank imageBank, byte newLevel, byte newWidth, byte newHeight)
	{
		levelSquares = new MapSquare[newWidth][newHeight];
		rooms = new LinkedList<Room>();
		level = newLevel;
		
		Room newRoom = new Room(level, 0);
		rooms.insert(newRoom);
		
		setAllSquaresEmpty(imageBank);
	}
	
	/**
	 * Loading constructor.
	 * @param newByte The byte representation of the level.
	 */
	MapLevel(byte[] newByte)
	{
		levelSquares = new MapSquare[newByte[0]][newByte[1]];
		level = newByte[2];
		
		rooms = new LinkedList<Room>();
	}
	
	/**
	 * Method for repopulating any empty rooms
	 *
	 */
	public void repopulate()
	{
		
	}
	
	/**
	 * Retrieves a specific Mapsquare using its coordinates
	 * @param x	int
	 * @param y	int
	 * @return	MapSquare
	 */
	public MapSquare getMapSquare(int x, int y)
	{
		if(x >= 0 && y >= 0 && x < levelSquares.length && y < levelSquares[0].length)
			return levelSquares[x][y];
		else
			return null;
	}
	
	/**
	 * Retrieves a specific map square using a coord object.
	 * @param coords	Coord
	 * @return	MapSquare
	 */
	public MapSquare getMapSquare(Coord coords)
	{
		if(coords != null && coords.getX() >= 0 && coords.getY() >= 0 && coords.getX() < levelSquares.length && coords.getY() < levelSquares[0].length)
			return levelSquares[coords.getX()][coords.getY()];
		else
			return null;
	}
	
	/**
	 * Retrieves the number of MapSquares on the X axis
	 * @return	in
	 */
	public byte getWidth()
	{
		return (byte)levelSquares.length;
	}
	
	/**
	 * Retrieves the number of MapSquares on the Y axis.
	 * @return	in
	 */
	public byte getHeight()
	{
		return (byte)levelSquares[0].length;
	}
	
	/**
	 * Retrieves the level number of this level
	 * @return	int
	 */
	public byte getLevel()
	{
		return level;
	}
	
	public int getNumRooms()
	{
		return rooms.getSize();
	}
	
	/**
	 * Sets a map square. Note: will not set if the square is invalid.
	 * @param newSquare	Square being set. 
	 */
	public void setMapSquare(MapSquare newSquare)
	{
		if(newSquare == null)
			return;
		
		if(newSquare.getZCoordinate() != level || newSquare.getXCoordinate() >= levelSquares.length || newSquare.getYCoordinate() >= levelSquares[0].length)
			return;
		
		levelSquares[newSquare.getXCoordinate()][newSquare.getYCoordinate()] = newSquare;
	}
	
	/**
	 * Changes the depth of the level.
	 * @param newDepth
	 */
	public void setMapLevel(byte newDepth)
	{
		level = newDepth;
		
		for(int x = 0; x < levelSquares.length; x++)
		{
			for(int y = 0; y < levelSquares[0].length; y++)
			{
				levelSquares[x][y].setZ(newDepth);
			}
		}
		
		ListIter<Room> tNode = rooms.getIterator();
		
		while(tNode.next())
			tNode.element().setLevel(newDepth);
	}
	
	/**
	 * Retrieve the room specified by the provided room number.
	 * @param roomNumber
	 * @return Room or null if no room has the requested room number
	 */
	public Room getRoom(int roomNumber)
	{
		ListIter<Room> tNode = rooms.getIterator();
		
		while(tNode.next())
			if(tNode.element().getRoomNumber() == roomNumber)
				return tNode.element();
		
		return null;
	}
	
	public void addRoom()
	{
		Room newRoom = new Room(level, rooms.getSize());
		rooms.insert(newRoom);
	}
	
	public void addRoom(Room newRoom)
	{
		rooms.insert(newRoom);
	}
	
	public boolean removeRoom(Room newRoom)
	{
		if(rooms.getFirst() == newRoom)
			return false;
		
		newRoom.clearRoom(rooms.getFirst());
		rooms.remove(newRoom);
		
		return true;
	}
	
	public Integer[] getRoomNumbers()
	{
		Integer[] roomNumbers;
		if(rooms.isEmpty())
		{
			roomNumbers = new Integer[1];
			roomNumbers[0] = 0;
		}
		else
		{
			roomNumbers = new Integer[rooms.getSize()];
			
			ListIter<Room> tNode = rooms.getIterator();
			int count = 0;
				
			while(tNode.next())
			{
					roomNumbers[count] = tNode.element().getRoomNumber();
					count += 1;
			}
		}
		
		return roomNumbers;
	}
	
	public LinkedList<Room> getRooms()
	{
		return rooms;
	}
	
	/**
	 * Resizes the level to the provided width/height. If the new width
	 * or height is smaller than present, all squares beyond will be
	 * disconnected then deleted. Return false if room size is invalid.
	 * 
	 * @param newWidth	New level width
	 * @param newHeight	new level height
	 * @return boolean	True if map was resized.
	 */
	public boolean resizeLevel(int newWidth, int newHeight, ImageBank images)
	{
		if(newWidth < 1 || newHeight < 1)
			return false;
		
		// if smaller width, remove extra squares.
		if(newWidth < levelSquares.length)
		{
			for(int x = newWidth; x < levelSquares.length; x++)
			{
				for(int y = 0; y < levelSquares[0].length; y++)
				{
					levelSquares[x][y].getRoom().removeSquare(levelSquares[x][y]);
				}
			}
		}
		
		if(newHeight < levelSquares.length)
		{
			for(int x = 0; x < levelSquares.length; x++)
			{
				for(int y = newHeight; y < levelSquares[0].length; y++)
				{
					levelSquares[x][y].getRoom().removeSquare(levelSquares[x][y]);
				}
			}
		}
		
		clearEmptyRooms();
		
		MapSquare[][] newArray = new MapSquare[newWidth][newHeight];
		setAllSquaresEmpty(newArray, images);
		
		for(byte x = 0; x < newWidth; x++)
		{
			for(byte y = 0; y < newHeight; y++)
			{
				if(x < levelSquares.length && y < levelSquares[0].length)
					newArray[x][y] = levelSquares[x][y];
			}
		}
		

		if(newHeight > levelSquares[0].length)
		{
			for(byte x = 0; x < levelSquares.length; x++)
			{
				newArray[x][levelSquares[0].length].setWall(Direction.South, MapSquare.BYTE_WALLROCK);
			}
		}
		
		if(newWidth > levelSquares.length)
		{
			for(byte y = 0; y < levelSquares[0].length; y++)
			{
				newArray[levelSquares.length][y].setWall(Direction.West, MapSquare.BYTE_WALLROCK);
			}
		}
		
		levelSquares = newArray;
		fixRoomlessSquares();
		
		return true;
	}
	
	/**
	 * Parses the room list and searches for 'dead' rooms.
	 * That is, rooms without any squares, then deletes them.
	 * Note: if there is only 1 room, it will exit immediately.
	 *
	 */
	public void clearEmptyRooms()
	{
		if(rooms.getSize() <= 1)
			return;
		
		ListIter<Room> tNode = rooms.getIterator();
		
		while(tNode.next())
		{
			if(tNode.element().getNumberSquares() <= 0 && tNode.element().getRoomNumber() != 0)
			{
				Room dRoom = tNode.element();
				rooms.remove(dRoom);
			}
		}
	}
	
	/**
	 * Parses the squares on this level for ones that aren't
	 * assigned a room. If so, they are assigned the first room.
	 *
	 */
	public void fixRoomlessSquares()
	{
		Room zeroRoom = rooms.getFirst();
		
		for(int x = 0; x < levelSquares.length; x++)
		{
			for(int y = 0; y < levelSquares[0].length; y++)
			{
				if(levelSquares[x][y].getRoom() == null)
				{
					levelSquares[x][y].setRoom(zeroRoom);
					zeroRoom.addSquare(levelSquares[x][y]);
				}
			}
		}
	}
	
	/**
	 * Sets all the squares on this level to empty. If it is level 0,
	 * the square 0, 0 becomes the exit square.
	 * @param images
	 */
	public void setAllSquaresEmpty(ImageBank images)
	{
		setAllSquaresEmpty(levelSquares, images);

		if(level == 0)
			levelSquares[0][0].getSquareFeatures()[0].setType(SquareFeature.TYPE_EXIT);
	}
	
	private void setAllSquaresEmpty(MapSquare[][] newArray, ImageBank images)
	{
		Room firstRoom = rooms.getFirst();
		
		for(byte x = 0; x < newArray.length; x++)
		{
			for(byte y = 0; y < newArray[0].length; y++)
			{
				newArray[x][y] = new MapSquare(x, y, level, MapSquare.BYTE_WALLNONE, MapSquare.BYTE_WALLNONE, MapSquare.BYTE_WALLNONE, MapSquare.BYTE_WALLNONE, new SquareFeature(SquareFeature.TYPE_NONE, (byte)0, (byte)0, (byte)0, images), new SquareFeature(SquareFeature.TYPE_NONE, (byte)0, (byte)0, (byte)0, images), firstRoom, false);
				firstRoom.addSquare(newArray[x][y]);
				
				if(x == 0)
					newArray[x][y].setWall(Direction.West, MapSquare.BYTE_WALLROCK);
				if(x == newArray.length - 1)
					newArray[x][y].setWall(Direction.East, MapSquare.BYTE_WALLROCK);
				if(y == 0)
					newArray[x][y].setWall(Direction.South, MapSquare.BYTE_WALLROCK);
				if(y == newArray[0].length - 1)
					newArray[x][y].setWall(Direction.North, MapSquare.BYTE_WALLROCK);
			}
		}
		
	}
	
	public void setAllSquaresVisited(boolean areVisited)
	{
		for(byte x = 0; x < levelSquares.length; x++)
		{
			for(byte y = 0; y < levelSquares[0].length; y++)
			{
				levelSquares[x][y].setVisited(areVisited);
			}
		}
	}
	
	public void fixWaterRooms()
	{
		
	}
	
	public byte[] toByte()
	{
		byte[] newByte = new byte[4];
		
		newByte[0] = DataBank.FLAG_LEVEL;
		newByte[1] = (byte)levelSquares.length;
		newByte[2] = (byte)levelSquares[0].length;
		newByte[3] = level;
		
		return newByte;
	}
	
	public String toString()
	{
		return ("LEVEL " + levelSquares.length + " " + levelSquares[0].length + " " + level + "\n");
	}
}
