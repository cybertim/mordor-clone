package mordorData;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import mordorEnums.Direction;
import mordorHelpers.Coord;


public class Map
{
	private MapLevel[] mapLevels;
	private ImageBank imageBank;
	private MapSquare exitSquare;
	
	private static final String mapFileName = "default.map";
	
	public static final byte MAXDEPTH = 127;
	
	Map(ImageBank nImageBank)
	{
		imageBank = nImageBank;
		newMap();
	}
	
	Map(byte numLevels, ImageBank nImageBank)
	{
		imageBank = nImageBank;
		mapLevels = new MapLevel[numLevels];
	}
	
	public void reloadLevels(byte numLevels)
	{
		mapLevels = new MapLevel[numLevels];
	}
	
	public void newMap()
	{
		mapLevels = new MapLevel[1];
		mapLevels[0] = new MapLevel(imageBank, (byte)0, (byte)1, (byte)1);
		
		exitSquare = mapLevels[0].getMapSquare(0, 0);
		mapLevels[0].getMapSquare(0, 0).getSquareFeatures()[0].setType(SquareFeature.TYPE_EXIT);
	}
	
	/**
	 * Retrieves a specific map square.
	 * @param x	x Coordinate
	 * @param y	y Coordinate
	 * @param z	z Coordinate
	 * @return	MapSquare
	 */
	public MapSquare getMapSquare(int x, int y, int z)
	{
		if(z >= 0 && z < mapLevels.length)
			return mapLevels[z].getMapSquare(x, y);
		else
			return null;
	}
	
	/**
	 * Retrieve a specific map square based on a coord object.
	 * @param coord
	 * @return
	 */
	public MapSquare getMapSquare(Coord coord)
	{
		if(coord == null)
			return null;
		
		int x = coord.getX();
		int y = coord.getY();
		int z = coord.getZ();
		if(z >= 0 && z < mapLevels.length)
			return mapLevels[z].getMapSquare(x, y);
		else
			return null;
	}
	
	/**
	 * Retrieves a specific map level
	 * @param z	depth of level
	 * @return	MapLevel
	 */
	public MapLevel getMapLevel(int z)
	{
		if(z >= mapLevels.length)
			return null;
		
		return mapLevels[z];
	}
	
	/**
	 * Retrieves the depth of the dungeon.
	 * @return	int
	 */
	public byte getDepth()
	{
		return (byte)mapLevels.length;
	}
	
	/**
	 * Adds a new, empty, level to the map. The index provided is scaled
	 * if invalid.
	 * @param newLevelIndex	Index for the new value.
	 */
	public void addLevel(byte newIndex, byte newWidth, byte newHeight)
	{
		if(newIndex < 0)
			newIndex = 0;
		if(newIndex > mapLevels.length)
			newIndex = (byte)mapLevels.length;
		
		MapLevel[] newArray = new MapLevel[mapLevels.length + 1];
		
		for(int i = 0; i < newIndex; i++)
		{
			newArray[i] = mapLevels[i];
		}
		
		newArray[newIndex] = new MapLevel(imageBank, newIndex, newWidth, newHeight);
		
		for(byte i = (byte)(newIndex + 1); i < newArray.length; i++)
		{
			mapLevels[i - 1].setMapLevel(i);
			newArray[i] = mapLevels[i - 1];
		}
		
		mapLevels = newArray;
		
		if(newIndex == 0)
			setExitSquare(getMapSquare(0, 0, 0));
	}
	
	/**
	 * Removes a level. Note, does nothing if the index is invalid.
	 * @param oldIndex	Index to remove. (int)
	 */
	public void removeLevel(byte oldIndex)
	{
		if(oldIndex < 0 || oldIndex >= mapLevels.length)
			return;
		
		MapLevel[] newArray = new MapLevel[mapLevels.length - 1];
		
		for(byte i = 0; i < oldIndex; i++)
		{
			newArray[i] = mapLevels[i];
		}
		
		for(byte i = (byte)(oldIndex + 1); i < mapLevels.length; i++)
		{
			mapLevels[i].setMapLevel((byte)(i - 1));
			newArray[i - 1] = mapLevels[i];
		}
		
		mapLevels = newArray;
		
		for(byte x = 0; x < mapLevels[0].getWidth(); x++)
			for(byte y = 0; y < mapLevels[0].getHeight(); y++)
				if(!mapLevels[0].getMapSquare(x, y).isSolidRock())
				{
					setExitSquare(mapLevels[0].getMapSquare(x, y));
					return;
				}
	}
	
	/**
	 * Changes a level to the specified level
	 * @param newLevel The level it will now be
	 */
	public void setLevel(MapLevel newLevel)
	{
		mapLevels[newLevel.getLevel()] = newLevel;
	}
	
	public void setExitSquare(MapSquare newExitSquare)
	{
		if(exitSquare != null)
		{
			exitSquare.getSquareFeatures()[0].setType(SquareFeature.TYPE_NONE);
			exitSquare.getRoom().setExit(false);
		}
		exitSquare = newExitSquare;
		exitSquare.getRoom().setExit(true);
		exitSquare.getSquareFeatures()[0].setType(SquareFeature.TYPE_EXIT);
	}
	
	/**
	 * Retrieve a Coord object of the coordinates for the exit square.
	 * @return	Coord
	 */
	public Coord getExitCoords()
	{
		byte x = exitSquare.getXCoordinate();
		byte y = exitSquare.getYCoordinate();
		byte z = exitSquare.getZCoordinate();
		Direction t;
		return new Coord(x, y, z, Direction.North);
	}
	
	public MapSquare getExitSquare()
	{
		return exitSquare;
	}
}
