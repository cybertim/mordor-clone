package mordorData;

import java.awt.image.BufferedImage;
import java.util.Random;

import mordorEnums.Direction;
import mordorEnums.PlayerState;
import mordorHelpers.Coord;
import mordorHelpers.Util;

/**
 * Class for map features.
 * @author August Junkala. April 8, 2007
 *
 */
public class SquareFeature
{
	// Note: features must be even since found is the 1 bit
	public static final byte TYPE_NONE = 0;
	public static final byte TYPE_WATER = 2;
	public static final byte TYPE_FOG = 4;
	public static final byte TYPE_SAND = 6;
	
	public static final byte TYPE_ANTIMAGIC = 8;
	public static final byte TYPE_STUD = 10;
	public static final byte TYPE_EXTINGUISH = 12;
	
	public static final byte TYPE_PIT = 14;
	public static final byte TYPE_ROTATOR = 16;
	public static final byte TYPE_TELEPORTSTATIC = 18;
	public static final byte TYPE_TELEPORTLEVEL = 20;
	public static final byte TYPE_TELEPORTDUNGEON = 22;
	public static final byte TYPE_CHUTE = 24;
	
	public static final byte TYPE_ONEWAYNORTH = 26;
	public static final byte TYPE_ONEWAYEAST = 28;
	public static final byte TYPE_ONEWAYSOUTH = 30;
	public static final byte TYPE_ONEWAYWEST = 32;
	
	public static final byte TYPE_STAIRSUP = 34;
	public static final byte TYPE_STAIRSDOWN = 36;
	public static final byte TYPE_EXIT = 38;
	
	public static final int FEATURE_X = 16;
	public static final int FEATURE_Y = 16;
	
	/*
	 * 1 in this value chance that if a new coordinate in solid
	 * rock is found, that this is where the player will go.
	 */
	private static final int CHANCETELEROCK = 1000;
	
	private byte type;
	private byte teleX, teleY, teleZ;
	private boolean detected;
	private ImageBank images;
	
	/**
	 * Default constructor. Not all parameters need to be set.
	 * @param newType		Type of feature
	 * @param destX			x destination for location features
	 * @param destY			y destination for location features
	 * @param destZ			z destination for location features
	 * @param newDirection	direction for one way features
	 * @param images		image bank (ImageBank)
	 * @param featureFound	if this feature has been detected (Boolean)
	 */
	SquareFeature(byte newType, byte destX, byte destY, byte destZ, ImageBank nImages, boolean featureFound)
	{
		type = newType;
		images = nImages;
		
		teleX = destX;
		teleY = destY;
		teleZ = destZ;
		detected = featureFound;
	}
	
	SquareFeature(byte newType, byte destX, byte destY, byte destZ, ImageBank nImages)
	{
		detected = ((newType & 1) == 1) ? true : false;
		type = (detected) ? (byte)(newType - 1) : newType;
		
		switch(newType)
		{
		case TYPE_NONE:
		case TYPE_WATER:
		case TYPE_FOG:
		case TYPE_SAND:
		case TYPE_EXIT:
			detected = true;
			break;
		}
		images = nImages;
		
		teleX = destX;
		teleY = destY;
		teleZ = destZ;
	}
	
	/**
	 * Method for modifying the player based on the nature of this
	 * square.
	 * @param player	Player to be modified (player)
	 * @param map		Game's map (Map)
	 */
	public void affectPlayer(Player player, Map map)
	{
		Random random = new Random(System.currentTimeMillis());
		byte newX, newY, newZ;
		Coord coords = player.getCoord();
		
		if(detected == false)
		{
			/* 
			 * Set something to detected based on if the player is lost (in addition to random chance)
			 * This means that they will get hints (i.e. messages) but will have to redetect things
			 * later when they are not lost.
			 */
			boolean nDetected = !player.isLost();
			switch(type)
			{
			case TYPE_NONE:
			case TYPE_WATER:
			case TYPE_FOG:
			case TYPE_SAND:
				detected = nDetected;
				break;
			case TYPE_PIT:
				detected = nDetected;
				player.postMessage("Pit detected");
				break;
			case TYPE_CHUTE:
				detected = nDetected;
				player.postMessage("Chute detected.");
				break;
			case TYPE_TELEPORTSTATIC:
			case TYPE_TELEPORTLEVEL:
			case TYPE_TELEPORTDUNGEON:
				detected = nDetected;
				player.postMessage("Teleporter detected.");
				break;
			case TYPE_STAIRSUP:
			case TYPE_STAIRSDOWN:
				detected = nDetected;
				player.postMessage("Stairs detected.");
				break;
			case TYPE_EXIT:
				detected = nDetected;
				player.postMessage("Exit detected.");
				break;
				// reset blind/drown/drop/antimagic flags
			case TYPE_STUD:
				detected = ((random.nextDouble() * 100) < player.chanceOfDetection()) ? nDetected : false;
				if(detected)
					player.postMessage("Stud square detected.");
				break;
			case TYPE_ANTIMAGIC:
				detected = ((random.nextDouble() * 100) < player.chanceOfDetection()) ? nDetected : false;
				if(detected)
					player.postMessage("Antimagic square detected.");
				break;
			case TYPE_EXTINGUISH:
				detected = ((random.nextDouble() * 100) < player.chanceOfDetection()) ? nDetected : false;
				if(detected)
					player.postMessage("Extinguish square detected.");
				break;
			case TYPE_ROTATOR:
				detected = ((random.nextDouble() * 100) < player.chanceOfDetection()) ? nDetected : false;
				if(detected)
					player.postMessage("Rotator detected.");
				break;
			case TYPE_ONEWAYNORTH:
			case TYPE_ONEWAYEAST:
			case TYPE_ONEWAYSOUTH:
			case TYPE_ONEWAYWEST:
				detected = ((random.nextDouble() * 100) < player.chanceOfDetection()) ? nDetected : false;
				if(detected)
					player.postMessage("One way square detected.");
				break;
			}
		}
		
		newZ = coords.getZ();
		
		switch(type)
		{
		case TYPE_STUD:
		case TYPE_NONE:
			// reset blind/drown/drop/antimagic flags
			player.setState(PlayerState.Drowning, false, Player.STATENATURAL);
			player.setState(PlayerState.Dropping, false, Player.STATENATURAL);
			player.setState(PlayerState.NoMagic, false, Player.STATENATURAL);
			player.setState(PlayerState.Fogged, false, Player.STATENATURAL);
			player.setState(PlayerState.NoDirectionChange, false, Player.STATENATURAL);
			break;
		case TYPE_WATER:
			if(!player.isFloating())
				player.setState(PlayerState.Drowning, true, Player.STATENATURAL);
			break;
		case TYPE_FOG:
			player.setState(PlayerState.Fogged, true, Player.STATENATURAL);
			break;
		case TYPE_SAND:
			if(!player.isFloating())
			{
				player.setState(PlayerState.Dropping, true, Player.STATENATURAL);
				player.postMessage(player.getName() + " is sinking!");
			}
			break;
		case TYPE_ANTIMAGIC:
			player.setState(PlayerState.NoMagic, true, Player.STATENATURAL);
			break;
		case TYPE_EXTINGUISH:
			// remove non permanent enchantments
			player.removeEffects();
			break;
		case TYPE_PIT:
			// remove random amount of health
			if(!player.isFloating())
			{
				int pain = ((random.nextInt(15) * coords.getZ()) + 5) * -1;
				player.changeHP(pain);
				player.postMessage(player.getName() + " fell in a pit for " + (~pain + 1) + " damage.");
			}
			break;
		case TYPE_ROTATOR:
			coords.setDirection(Direction.type(random.nextInt(4)));
			player.setLost();
			break;
		case TYPE_CHUTE:
			if(!player.isFloating() && player.recurseCount < Util.MAXLOOPS)
			{
				coords.setX(teleX);
				coords.setY(teleY);
				coords.setZ(teleZ);
				player.setLost();
				
				map.getMapSquare(coords).enterSquare(player, map);
			}
			break;
		case TYPE_TELEPORTSTATIC:
			if(player.recurseCount < Util.MAXLOOPS)
			{
				coords.setX(teleX);
				coords.setY(teleY);
				coords.setZ(teleZ);
				player.setLost();
			
				map.getMapSquare(coords).enterSquare(player, map);
			}
			break;
		case TYPE_TELEPORTDUNGEON:
			if(player.recurseCount < Util.MAXLOOPS)
				newZ = (byte)random.nextInt((map.getDepth() - 1) + 1);
		case TYPE_TELEPORTLEVEL:
			if(player.recurseCount < Util.MAXLOOPS)
			{
				// choose random x/y for level
				newX = (byte)random.nextInt(map.getMapLevel(newZ).getWidth());
				newY = (byte)random.nextInt(map.getMapLevel(newZ).getHeight());
				
				Coord newCoords = new Coord(newX, newY, newZ);
				while(map.getMapSquare(newCoords).isSolidRock())
				{
					// 1 in CHANCETELEROCK chance that the player will go to rock.
					if(random.nextInt(CHANCETELEROCK) < 1)
						break;
					newX = (byte)random.nextInt(map.getMapLevel(newZ).getWidth());
					newY = (byte)random.nextInt(map.getMapLevel(newZ).getHeight());
					newCoords = new Coord(newX, newY, newZ);
				}
					
			/*
				coords.setX(newX);
				coords.setY(newY);
				coords.setZ(newZ);*/
				player.setLost();
				
				map.getMapSquare(newCoords).enterSquare(player, map);
			}
			break;
		case TYPE_ONEWAYNORTH:
			// turn on no rotation/backwards flag
			coords.setDirection(Direction.North);
//			player.setDirection(MapSquare.NORTH);
			player.setState(PlayerState.NoDirectionChange, true, Player.STATENATURAL);
			break;
		case TYPE_ONEWAYEAST:
			// turn on no rotation/backwards flag
			coords.setDirection(Direction.East);
			//player.setDirection(MapSquare.EAST);
			player.setState(PlayerState.NoDirectionChange, true, Player.STATENATURAL);
			break;
		case TYPE_ONEWAYSOUTH:
			// turn on no rotation/backwards flag
			coords.setDirection(Direction.South);
			//player.setDirection(MapSquare.SOUTH);
			player.setState(PlayerState.NoDirectionChange, true, Player.STATENATURAL);
			break;
		case TYPE_ONEWAYWEST:
			// turn on no rotation/backwards flag
			coords.setDirection(Direction.West);
			//player.setDirection(MapSquare.WEST);
			player.setState(PlayerState.NoDirectionChange, true, Player.STATENATURAL);
			break;
		}
	}
	
	/**
	 * Retrieves the feature's types.
	 * @return int	Type
	 */
	public byte getType()
	{
		return type;
	}
	
	/**
	 * Retrieves the feature's image overlay.
	 * @return BufferedImage	overlay
	 */
	public BufferedImage getSquareImage()
	{
		switch(type)
		{
		case TYPE_WATER:
		case TYPE_FOG:
		case TYPE_SAND:
		case TYPE_ANTIMAGIC:
		case TYPE_STUD:
		case TYPE_EXTINGUISH:
		case TYPE_PIT:
		case TYPE_ROTATOR:
			return images.getTile(type >> 1);
		case TYPE_TELEPORTSTATIC:
		case TYPE_TELEPORTLEVEL:
		case TYPE_TELEPORTDUNGEON:
			return images.getTile(ImageBank.TILE_TELEPORT);
		case TYPE_CHUTE:
			return images.getTile(ImageBank.TILE_CHUTE);
		case TYPE_STAIRSUP:
			return images.getTile(ImageBank.TILE_STAIRSUP);
		case TYPE_EXIT:
			return images.getTile(ImageBank.TILE_EXIT);
		case TYPE_STAIRSDOWN:
			return images.getTile(ImageBank.TILE_STAIRSDOWN);
		case TYPE_ONEWAYNORTH:
			return images.getTile(ImageBank.TILE_ONEWAYNORTH);
		case TYPE_ONEWAYEAST:
			return images.getTile(ImageBank.TILE_ONEWAYEAST);
		case TYPE_ONEWAYSOUTH:
			return images.getTile(ImageBank.TILE_ONEWAYSOUTH);
		case TYPE_ONEWAYWEST:
			return images.getTile(ImageBank.TILE_ONEWAYWEST);
		}
		return images.getTile(ImageBank.TILE_INVISIBLE);
	}
	
	/**
	 * Determine if the player has detected this feature yet.
	 * @return
	 */
	public boolean featureDetected()
	{
		return detected;
	}

	public byte getDestX()
	{
		return teleX;
	}
	
	public byte getDestY()
	{
		return teleY;
	}
	
	public byte getDestZ()
	{
		return teleZ;
	}
	/*
	public int getDirection()
	{
		return direction;
	}*/
	
	public void setDestX(byte nX)
	{
		teleX = nX;
	}
	
	public void setDestY(byte nY)
	{
		teleY = nY;
	}
	
	public void setDestZ(byte nZ)
	{
		teleZ = nZ;
	}
	/*
	public void setDirection(int nDirection)
	{
		direction = nDirection;
	}*/
	
	public void setType(byte newType)
	{
		type = newType;
	}
	
	public SquareFeature copyFeature()
	{
		return new SquareFeature(type, teleX, teleY, teleZ, images, detected);
	}
	
	public void replicateFeature(SquareFeature oldFeature)
	{
		type = oldFeature.type;
		teleX = oldFeature.teleX;
		teleY = oldFeature.teleY;
		teleZ = oldFeature.teleZ;
	//	direction = oldFeature.direction;
		detected = oldFeature.detected;
		images = oldFeature.images;
	}
	
	public String toString()
	{
		String featureString;
		
		featureString = " " + type + " " + teleX + " " + teleY + " " + teleZ;
		featureString += (detected) ? " TRUE" : " FALSE";
		
		return featureString;
	}
	
	/**
	 * Retrieves the set of bytes representing this feature.
	 * 0 = type & found
	 * 1 = x
	 * 2 = y
	 * 3 = z
	 * @return byte[4]	The set of bytes.
	 */
	public byte[] toByte()
	{
		byte[] newByte = new byte[4];
		
		newByte[0] = type;
		newByte[0] |= (detected) ? 1 : 0;
		newByte[1] = teleX;
		newByte[2] = teleY;
		newByte[3] = teleZ;
		
		return newByte;
	}
	
	/**
	 * Retrieves a string array of the possible features on every floor
	 * except the first
	 * @return String[]
	 */
	public static final String[] getFeatureTypes()
	{
		String[] featureTypes = {"None", "Water", "Fog", "Sand",
								"Anti-magic", "Stud", "Extinguish", "Pit",
								"Rotator", "Static Teleport", 
								"Random Teleport: Same level", "Random Teleport: Any level",
								"Chute", "One way: North", "One way: East", "One way: South", "One way: West",
								"Stairs: Up", "Stairs: Down"};
		
		return featureTypes;
	}
	
	/**
	 * Retrieves a string array of the possible features on the first
	 * floor.
	 * @return String[]
	 */
	public static final String[] getFeatureTypesZero()
	{
		String[] featureTypes = {"None", "Water", "Fog", "Sand",
								"Anti-magic", "Stud", "Extinguish", "Pit",
								"Rotator", "Static Teleport", 
								"Random Teleport: Same level", "Random Teleport: Any level",
								"Chute", "One way: North", "One way: East", "One way: South", "One way: West",
								"Stairs: Up", "Stairs: Down", "Stairs: Exit"};
		
		return featureTypes;
	}
}
