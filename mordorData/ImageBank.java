package mordorData;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;


public class ImageBank
{
	public static final int IMAGEHEIGHT = 128;
	public static final int IMAGEWIDTH = 128;
	public static final int FEATUREWIDTH = 16;
	public static final int FEATUREHEIGHT = 16;
	
	private BufferedImage[] floors;
	private BufferedImage[] walls;
	private BufferedImage[] mapTiles;
	private BufferedImage[] monsterImages;
	private BufferedImage[] townImages;
	private BufferedImage[] otherImages;
	private BufferedImage fogFilter;
	
	// stores the shaped walls
	// first value is the level, second value is the square
	// thrid value is ceiling, floor, lwall, rwall, far wall,
	// fourth value is for wall and door (could be expanded for more types I guess
	// or stone, water, sand (if floor)
	private BufferedImage[][][][] shapedWalls;
	
	private static final int TYPESOFWALLS = 3;
	public static final int WALL_ROCK = 0;
	public static final int WALL_DOOR = 1;
	public static final byte FLOOR_STONE = 0;
	public static final byte FLOOR_WATER = 1;
	public static final byte FLOOR_SAND = 2;
	
	private static final String[] FLOOR_FILENAMES = {"floor_stone", "floor_water", "floor_sand"};
	
	public static final int SURFACE_LEFT = 0;
	public static final int SURFACE_RIGHT = 1;
	public static final int SURAFCE_CEILING = 2;
	public static final int SURFACE_FLOOR = 3;
	public static final int SURFACE_FAR = 4;
	
	public static final int CEILINGNEARY = 0;
	public static final int CEILINGFARY = 32;
	public static final int FLOORNEARY = 96;
	public static final int FLOORFARY = 80;
	
	public static final int LWALLFARX = 24;
	public static final int AWALLFARY = 32;
	public static final int RWALLFARX = 86;
	
	private static final int TYPESOFTILES = 18;
	public static final int TILE_NONE = 0;
	public static final int TILE_WATER = 1;
	public static final int TILE_FOG = 2;
	public static final int TILE_SAND = 3;
	public static final int TILE_ANTIMAGIC = 4;
	public static final int TILE_STUD = 5;
	public static final int TILE_EXTINGUISH = 6;
	public static final int TILE_PIT = 7;
	public static final int TILE_ROTATOR = 8;
	public static final int TILE_TELEPORT = 9;
	public static final int TILE_CHUTE = 10;
	public static final int TILE_STAIRSUP = 11;
	public static final int TILE_STAIRSDOWN = 12;
	public static final int TILE_ONEWAYNORTH = 13;
	public static final int TILE_ONEWAYEAST = 14;
	public static final int TILE_ONEWAYSOUTH = 15;
	public static final int TILE_ONEWAYWEST = 16;
	public static final int TILE_EXIT = 17;
	public static final int TILE_INVISIBLE = 18;
	
	public static final String[] MONSTERFILES = 
	{"MON1.jpg", "MON2.jpg", "MON3.jpg", "MON4.jpg", "MON5.jpg", 
	"MON6.jpg", "MON7.jpg", "MON8.jpg", "MON9.jpg", "MON10.jpg", 
	"MON11.jpg", "MON12.jpg", "MON13.jpg", "MON14.jpg", "MON15.jpg", 
	"MON16.jpg", "MON17.jpg", "MON18.jpg", "MON20.jpg", 
	"MON21.jpg", "MON22.jpg", "MON23.jpg", "MON24.jpg", "MON25.jpg", 
	"MON26.jpg", "MON27.jpg", "MON28.jpg", "MON29.jpg", "MON30.jpg", 
	"MON31.jpg", "MON32.jpg", "MON33.jpg", "MON34.jpg", "MON35.jpg", 
	"MON36.jpg", "MON37.jpg", "MON38.jpg", "MON39.jpg", "MON40.jpg", 
	"MON41.jpg", "MON42.jpg", "MON43.jpg", "MON44.jpg", "MON45.jpg", 
	"MON46.jpg", "MON47.jpg", "MON48.jpg", "MON49.jpg", "MON50.jpg", 
	"MON51.jpg", "MON52.jpg", "MON53.jpg", "MON54.jpg", "MON55.jpg", 
	"MON56.jpg", "MON57.jpg", "MON58.jpg", "MON59.jpg", "MON60.jpg", 
	"MON61.jpg", "MON62.jpg", "MON63.jpg", "MON64.jpg", "MON65.jpg", 
	"MON66.jpg", "MON67.jpg", "MON68.jpg", "MON69.jpg", "MON70.jpg", 
	"MON71.jpg", "MON72.jpg", "MON73.jpg", "MON74.jpg", 
	"MON76.jpg", "MON77.jpg", "MON78.jpg", "MON79.jpg", "MON80.jpg", 
	"MON81.jpg", "MON82.jpg", "MON83.jpg",  "MON100.jpg"};

	public static final int MONSTERIMAGESIZE = 100; // 100 px square
	
	private static final String[] TILENAMES = {"tile_none", "tile_water",
			"tile_fog", "tile_sand", "tile_antimagic", "tile_stud", 
			"tile_extinguish", "tile_pit", "tile_rotator", "tile_teleport", 
			"tile_chute", "tile_stairsup", "tile_stairsdown", 
			"tile_onewayn", "tile_onewaye", "tile_oneways", "tile_onewayw",
			"tile_exit"};
	
	public static final String[] TOWNFILES = 
	{
		"town_confinebutton.png", "town_storebutton.png", "town_bankbutton.png",
		"town_guildsbutton.png", "town_dungbutton.png", "town_seerbutton.png",
		"town_morguebutton.png", "town_exitbutton.png"
	};
	
	public static final int TOWNICON_CONFINEMENT = 0;
	public static final int TOWNICON_STORE = 1;
	public static final int TOWNICON_BANK = 2;
	public static final int TOWNICON_GUILDS = 3;
	public static final int TOWNICON_DUNGEON = 4;
	public static final int TOWNICON_SEER = 5;
	public static final int TOWNICON_MORGUE = 6;
	public static final int TOWNICON_EXIT = 7;
	
	public static final String IMAGEDIR = "images/";

	public static String[] OTHERFILES = 
	{
		"introtitle.png", "peace.png", "hostile.png", "box.png", "chest.png"
	};
	
	public static final int OTHER_INTROTITLE = 0;
	public static final int OTHER_PEACE = 1;
	public static final int OTHER_HOSTILE = 2;
	public static final int OTHER_BOX = 3;
	public static final int OTHER_CHEST = 4;
	
	ImageBank()
	{
		long time = System.nanoTime() / 1000;
		walls = new BufferedImage[TYPESOFWALLS];
		mapTiles = new BufferedImage[TYPESOFTILES + 1];
		floors = new BufferedImage[FLOOR_FILENAMES.length];
		monsterImages = new BufferedImage[MONSTERFILES.length];
		townImages = new BufferedImage[TOWNFILES.length];
		otherImages = new BufferedImage[OTHERFILES.length];
		URL imgUrl;
		
		time = (System.nanoTime() / 1000) - time;
		System.out.println("Prep: " + time);
		time = System.nanoTime() / 1000;
		
		try
		{
			imgUrl = Thread.currentThread().getContextClassLoader().getResource(IMAGEDIR + "wall.png");
			walls[WALL_ROCK] = ImageIO.read(imgUrl);
			//walls[WALL_ROCK] = ImageIO.read(getClass().getResource(IMAGEDIR + "wall.png"));
			imgUrl = Thread.currentThread().getContextClassLoader().getResource(IMAGEDIR + "door.png");
			walls[WALL_DOOR] = ImageIO.read(imgUrl);
			//walls[WALL_DOOR] = ImageIO.read(getClass().getResource(IMAGEDIR + "door.png"));
			
			for(byte i = 0; i < FLOOR_FILENAMES.length; i++)
			{
				imgUrl = Thread.currentThread().getContextClassLoader().getResource(IMAGEDIR + FLOOR_FILENAMES[i] + ".png");
				floors[i] = ImageIO.read(imgUrl);
				//floors[i] = ImageIO.read(getClass().getResource(IMAGEDIR + FLOOR_FILENAMES[i] + ".png"));
			}
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
		
		time = (System.nanoTime() / 1000) - time;
		System.out.println("Walls: " + time);
		time = System.nanoTime() / 1000;
		
		for(int i = 0; i < mapTiles.length - 1; i++)
		{
			try
			{
				imgUrl = Thread.currentThread().getContextClassLoader().getResource(IMAGEDIR + TILENAMES[i] + ".png");
				mapTiles[i] = ImageIO.read(imgUrl);
				//mapTiles[i] = ImageIO.read(getClass().getResource(IMAGEDIR + TILENAMES[i] + ".png"));
			
			}
			catch(Exception e)
			{
				System.err.println(e + TILENAMES[i]);
			}
		}
		

		time = (System.nanoTime() / 1000) - time;
		System.out.println("Map Tiles: " + time);
		time = System.nanoTime() / 1000;
		
		mapTiles[TILE_INVISIBLE] = new BufferedImage(FEATUREWIDTH, FEATUREHEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D surface = mapTiles[TILE_INVISIBLE].createGraphics();
		surface.setColor(new Color(0x00FF00FF));
		
		fogFilter = new BufferedImage(IMAGEWIDTH, IMAGEHEIGHT, BufferedImage.TYPE_INT_ARGB);
		createFogFilter(fogFilter);
		
		time = (System.nanoTime() / 1000) - time;
		System.out.println("Extras: " + time);
		time = System.nanoTime() / 1000;
		
		for(int i = 0; i < monsterImages.length; i++)
		{
			try
			{
				imgUrl = Thread.currentThread().getContextClassLoader().getResource(IMAGEDIR + MONSTERFILES[i]);
				monsterImages[i] = ImageIO.read(imgUrl);
				
				//monsterImages[i] = ImageIO.read(getClass().getResource(IMAGEDIR + MONSTERFILES[i]));
			}
			catch(Exception e)
			{
				System.err.println(e + MONSTERFILES[i]);
			}
		}

		time = (System.nanoTime() / 1000) - time;
		System.out.println("Monsters Images: " + time);
		time = System.nanoTime() / 1000;
		
		for(int i = 0; i < townImages.length; i++)
		{
			try
			{

				imgUrl = Thread.currentThread().getContextClassLoader().getResource(IMAGEDIR + TOWNFILES[i]);
				townImages[i] = ImageIO.read(imgUrl);
			//	townImages[i] = ImageIO.read(getClass().getResource(IMAGEDIR + TOWNFILES[i]));
			}
			catch(Exception e)
			{
				System.err.println(e + TOWNFILES[i]);
			}
		}
		
		for(int i = 0; i < otherImages.length; i++)
		{
			try
			{
				imgUrl = Thread.currentThread().getContextClassLoader().getResource(IMAGEDIR + OTHERFILES[i]);
				otherImages[i] = ImageIO.read(imgUrl);
			//	otherImages[i] = ImageIO.read(getClass().getResource(IMAGEDIR + OTHERFILES[i]));
			}
			catch(Exception e)
			{
				System.err.println(e + OTHERFILES[i]);
			}
		}
		
		time = (System.nanoTime() / 1000) - time;
		System.out.println("Other Images: " + time);
		time = System.nanoTime() / 1000;

		createShapedWalls();
		

		time = (System.nanoTime() / 1000) - time;
		System.out.println("Reshape: " + time);
	}

	/**
	 * Generates the image used as a fog filter.
	 * @param nFogFilter
	 */
	private void createFogFilter(BufferedImage nFogFilter)
	{
		for(int x = 0; x < nFogFilter.getWidth(); x++)
		{
			for(int y = 0; y < nFogFilter.getHeight(); y++)
			{
				nFogFilter.setRGB(x, y, 0xCCFFFFFF);
			}
		}
	}
	
	/**
	 * Scales an image so it fits in x, y. Very simple routine, basically just draws far
	 * right/bottom pixel for each. Also, doesn't presently work with making an image bigger
	 * although it won't error out (you'll just get blank lines through the picture)
	 * @param startImage	Image to be scaled	(BufferedImage)
	 * @param x				new width	(double)
	 * @param y				new height	(double)
	 * @return				new Image (BufferedImage)
	 */
	public BufferedImage scaleImage(BufferedImage startImage, double x, double y)
	{
		Double tVal;
		
		tVal = startImage.getWidth() / x;
		int tX = tVal.intValue();
		tVal = startImage.getHeight() / y;
		int tY = tVal.intValue(); 
			
		BufferedImage temp = new BufferedImage(tX, tY, BufferedImage.TYPE_INT_ARGB);
		
		// x is presently the width to adjust by, y the height)
		
		// how much to move in the final image for each increment in start imate
		double xFactor = x / startImage.getWidth();
		double yFactor = y / startImage.getHeight();
		
		// present location in the final image
		double xLoc = 0;
		double yLoc = 0;
		
		int intX = 0;
		int intY = 1;

		Graphics2D tempSurface = temp.createGraphics();
		tempSurface.setColor(new Color(0x00FFFFFF, true));
		tempSurface.fillRect(0, 0, temp.getWidth(), temp.getHeight());
		
		for(int i = 0; i < startImage.getWidth(); i++)
		{
			for(int j = 0; j < startImage.getHeight(); j++)
			{
				tVal = (double)Math.floor((double) xLoc);
				intX = tVal.intValue();
				tVal = (double)Math.floor((double) yLoc);
				intY = tVal.intValue();
				
				temp.setRGB(intX, intY, startImage.getRGB(i, j));
				yLoc += yFactor;
			}
			yLoc = 0;
			xLoc += xFactor;
		}
		return temp;
	}
	
	public BufferedImage reshape2(BufferedImage startImage, Integer[][] corners)
	{
		// this needs to be replaced and based on the absolute square (so negative values can be
		// allowed
		// section it out into a method.
	
		// get the image size for the new image
		Integer imageWidth, imageHeight;
		if(corners[0][0] < corners[3][0])
			imageWidth = (corners[1][0] < corners[2][0]) ? (corners[2][0] - corners[0][0]) : (corners[1][0] - corners[0][0]);
		else
			imageWidth = (corners[1][0] < corners[2][0]) ? (corners[2][0] - corners[3][0]) : (corners[1][0] - corners[3][0]);
			
		imageWidth = Math.abs(imageWidth);
			
		if(corners[0][1] < corners[1][1])
			imageHeight = (corners[2][1] < corners[3][1]) ? (corners[3][1] - corners[0][1]) : (corners[2][1] - corners[0][1]);
		else
			imageHeight = (corners[2][1] < corners[3][1]) ? (corners[3][1] - corners[1][1]) : (corners[2][1] - corners[1][1]);
			
		imageHeight = Math.abs(imageHeight);
			
		// create the temporary image where the start image will be drawn to
		// and fill it with clear pixels.
		BufferedImage newImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D newImageSurface = newImage.createGraphics();
		
		newImageSurface.setColor(new Color(0x00FF00FF, true));
		newImageSurface.fillRect(0, 0, imageWidth, imageHeight);
		
		// figure out the rate of change for the left lines x and y values.
		Double leftXROC = (corners[3][0] - corners[0][0]) / ((Integer)startImage.getHeight()).doubleValue();
		Double leftYROC = (corners[3][1] - corners[0][1]) / ((Integer)startImage.getHeight()).doubleValue();
		
		// rate of change (ROC) for right line along x & y axis
		Double rightXROC = (corners[2][0] - corners[1][0]) / ((Integer)startImage.getHeight()).doubleValue();
		Double rightYROC = (corners[2][1] - corners[1][1]) / ((Integer)startImage.getHeight()).doubleValue();
		
		// rate of change for the top line. This is going to be used (the YROC) to figure out the starting
		// y value for each line, and the starting x value
		Double topXROC = (corners[1][0] - corners[0][0]) / ((Integer)startImage.getWidth()).doubleValue();
		Double topYROC = (corners[1][1] - corners[0][1]) / ((Integer)startImage.getWidth()).doubleValue();
		
		// now for the rate of change in x and y over the image
		// width since we are scanning left to right
		Double wholeXROC = (rightXROC - leftXROC) / ((Integer)startImage.getWidth()).doubleValue();
		Double wholeYROC = (rightYROC - leftYROC) / ((Integer)startImage.getWidth()).doubleValue();
		
		// where in new image we are drawing.
		Double presentX = (corners[0][0] < corners[3][0]) ? 0 : (corners[0][0].doubleValue() - corners[3][0]);
		Double presentY = (corners[0][1] < corners[1][1]) ? 0 : (corners[0][1].doubleValue() - corners[1][1]);
		Double lastX = presentX;
		Double lastY = presentY;
		
		Double presentWholeX = leftXROC;
		Double presentWholeY = leftYROC;
		Double lastWholeX = presentWholeX;
		Double lastWholeY = presentWholeY;
		
		Integer integerX, integerY;
		
		// now loop through the start image drawing each pixel to the
		for(int x = 0; x < startImage.getWidth(); x++)
		{
			for(int y = 0; y < startImage.getHeight(); y++)
			{
				// get an
				integerX = ((Double)Math.floor(presentX)).intValue();
				integerY = ((Double)Math.floor(presentY)).intValue();
				
				newImage.setRGB(integerX, integerY, startImage.getRGB(x, y));
				
				presentX += presentWholeX;
				presentY += presentWholeY;
			}
			
			// now get the next x & y values
			presentX = lastX + topXROC;
			presentY = lastY + topYROC;
			lastX = presentX;
			lastY = presentY;
			
			presentWholeX = lastWholeX + wholeXROC;
			presentWholeY = lastWholeY + wholeYROC;
			lastWholeX = presentWholeX;
			lastWholeY = presentWholeY;
		}

		// create the image object that will be exported (making sure it is in vid. mem.)
		BufferedImage finalImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D finalImageSurface = finalImage.createGraphics();
		
		finalImageSurface.drawImage(newImage, null, 0, 0);
			
		
		return finalImage;
	}
	
	public BufferedImage getWall(int wall)
	{
		return walls[wall];
	}
	
	/**
	 * Using the door & wall types creates shaped walls for all possible views.
	 *
	 */
	private void createShapedWalls()
	{
		// near/far, left, middle, right, l, r, c, fl, far, 
		shapedWalls = new BufferedImage[2][3][5][TYPESOFWALLS];
		
		boolean isNear = true;
		
		for(int square = 0; square < 3; square++)
		{
			if(square == 1)
			{
				shapedWalls[0][square][SURFACE_LEFT][WALL_ROCK] = reshape2(walls[WALL_ROCK], getWallCorners(isNear, square - 1, -1));
				shapedWalls[0][square][SURFACE_LEFT][WALL_DOOR] = reshape2(walls[WALL_DOOR], getWallCorners(isNear, square - 1, -1));
				
				shapedWalls[0][square][SURFACE_RIGHT][WALL_ROCK] = reshape2(walls[WALL_ROCK], getWallCorners(isNear, square - 1, 1));
				shapedWalls[0][square][SURFACE_RIGHT][WALL_DOOR] = reshape2(walls[WALL_DOOR], getWallCorners(isNear, square - 1, 1));
			}
			
			shapedWalls[0][square][SURAFCE_CEILING][WALL_ROCK] = reshape2(floors[FLOOR_STONE], getFloorCorners(isNear, square - 1, false));
			shapedWalls[0][square][SURFACE_FAR][WALL_ROCK] = reshape2(walls[WALL_ROCK], getWallCorners(isNear, square - 1, 0));

			shapedWalls[0][square][SURAFCE_CEILING][WALL_DOOR] = shapedWalls[0][square][SURAFCE_CEILING][WALL_ROCK];// reshape2(floors[FLOOR_STONE], getFloorCorners(isNear, square - 1, false));
			shapedWalls[0][square][SURFACE_FAR][WALL_DOOR] = reshape2(walls[WALL_DOOR], getWallCorners(isNear, square - 1, 0));
			

			shapedWalls[0][square][SURFACE_FLOOR][FLOOR_STONE] = reshape2(floors[FLOOR_STONE], getFloorCorners(isNear, square - 1, true));
			shapedWalls[0][square][SURFACE_FLOOR][FLOOR_WATER] = reshape2(floors[FLOOR_WATER], getFloorCorners(isNear, square - 1, true));
			shapedWalls[0][square][SURFACE_FLOOR][FLOOR_SAND] = reshape2(floors[FLOOR_SAND], getFloorCorners(isNear, square - 1, true));
		}
		
		isNear = false;
		
		for(int square = 0; square < 3; square++)
		{
			if(square == 1)
			{
				shapedWalls[1][square][SURFACE_LEFT][WALL_ROCK] = reshape2(walls[WALL_ROCK], getWallCorners(isNear, square - 1, -1));
				shapedWalls[1][square][SURFACE_RIGHT][WALL_ROCK] = reshape2(walls[WALL_ROCK], getWallCorners(isNear, square - 1, 1));
				
				shapedWalls[1][square][SURFACE_LEFT][WALL_DOOR] = reshape2(walls[WALL_DOOR], getWallCorners(isNear, square - 1, -1));
				shapedWalls[1][square][SURFACE_RIGHT][WALL_DOOR] = reshape2(walls[WALL_DOOR], getWallCorners(isNear, square - 1, 1));
			}
			shapedWalls[1][square][SURAFCE_CEILING][WALL_ROCK] = reshape2(floors[FLOOR_STONE], getFloorCorners(isNear, square - 1, false));
			shapedWalls[1][square][SURFACE_FAR][WALL_ROCK] = reshape2(walls[WALL_ROCK], getWallCorners(isNear, square - 1, 0));
			
			shapedWalls[1][square][SURAFCE_CEILING][WALL_DOOR] = shapedWalls[1][square][SURAFCE_CEILING][WALL_ROCK]; //reshape2(floor, getFloorCorners(isNear, square - 1, false));
			shapedWalls[1][square][SURFACE_FAR][WALL_DOOR] = reshape2(walls[WALL_DOOR], getWallCorners(isNear, square - 1, 0));
		
			shapedWalls[1][square][SURFACE_FLOOR][FLOOR_STONE] = reshape2(floors[FLOOR_STONE], getFloorCorners(isNear, square - 1, true));
			shapedWalls[1][square][SURFACE_FLOOR][FLOOR_WATER] = reshape2(floors[FLOOR_WATER], getFloorCorners(isNear, square - 1, true));
			shapedWalls[1][square][SURFACE_FLOOR][FLOOR_SAND] = reshape2(floors[FLOOR_SAND], getFloorCorners(isNear, square - 1, true));
		}
	}
	
	/**
	 * Gets the corners of a wall.
	 * @param isNear	is it the nearest row of squares?
	 * @param floorX	which square is it (-1 left, 0 mid, 1 right)
	 * @param surfaceType which wall is it (-1 left, 0 far, 1 right)
	 * @return	int[4][2]
	 */
	private Integer[][] getWallCorners(boolean isNear, int floorX, int surfaceType)
	{
		Integer[][] newCorners = new Integer[4][2];
		
		Integer[][] floorCorners = getFloorCorners(isNear, floorX, true);
		Integer[][] ceilingCorners = getFloorCorners(isNear, floorX, false);
		
		switch(surfaceType)
		{
		case -1:
			newCorners[0][0] = ceilingCorners[0][0];
			newCorners[0][1] = ceilingCorners[0][1];
			
			newCorners[1][0] = ceilingCorners[3][0];
			newCorners[1][1] = ceilingCorners[3][1];
			
			newCorners[2][0] = floorCorners[0][0];
			newCorners[2][1] = floorCorners[0][1];
			
			newCorners[3][0] = floorCorners[3][0];
			newCorners[3][1] = floorCorners[3][1];
			break;
		case 0:
			newCorners[0][0] = ceilingCorners[3][0];
			newCorners[0][1] = ceilingCorners[3][1];
			
			newCorners[1][0] = ceilingCorners[2][0];
			newCorners[1][1] = ceilingCorners[2][1];
			
			newCorners[2][0] = floorCorners[1][0];
			newCorners[2][1] = floorCorners[1][1];
			
			newCorners[3][0] = floorCorners[0][0];
			newCorners[3][1] = floorCorners[0][1];
			break;
		case 1:
			newCorners[0][0] = ceilingCorners[2][0];
			newCorners[0][1] = ceilingCorners[2][1];
			
			newCorners[1][0] = ceilingCorners[1][0];
			newCorners[1][1] = ceilingCorners[1][1];
			
			newCorners[2][0] = floorCorners[2][0];
			newCorners[2][1] = floorCorners[2][1];
			
			newCorners[3][0] = floorCorners[1][0];
			newCorners[3][1] = floorCorners[1][1];
			break;
		}
		
		return newCorners;
	}
	
	/**
	 * Gets the corners for floors and ceilings.
	 * @param isNear	is it the closest square or furthest?
	 * @param floorX	-1 for left, 0 for middle, 1 for right
	 * @param isFloor	is it the floor? (else gives ceiling values)
	 * @return	int[4][2]
	 */
	private Integer[][] getFloorCorners(boolean isNear, int floorX, boolean isFloor)
	{
		// groups
		// dimension 1: {top line}, {middle line}, {bottom line}, so the y 
		// dimension 2: {far left, left, right, far right} so the x
		int[][] floorXValues = {{-128, 0, 128, 256}, {0, 24, 104, 128}, {0, 42, 86, 128}};
		
		Integer[][] newCorners = new Integer[4][2];
		
		int floorY = (isNear) ? 0 : 1;
		
		int topY, bottomY;
		if(!isFloor)
		{
			topY = (isNear) ? 0 : 32;
			bottomY = (isNear) ? 32 : 48;
		}
		else
		{
			topY = (isNear) ? 96 : 80;
			bottomY = (isNear) ? 128 : 96;
		}

		floorX += 1;
		
		if(!isFloor)
		{
			newCorners[0][0] = floorXValues[floorY][floorX];
			newCorners[1][0] = floorXValues[floorY][floorX + 1];
			newCorners[2][0] = floorXValues[floorY + 1][floorX + 1];
			newCorners[3][0] = floorXValues[floorY + 1][floorX];
		}
		else
		{
			newCorners[0][0] = floorXValues[floorY + 1][floorX];
			newCorners[1][0] = floorXValues[floorY + 1][floorX + 1];
			newCorners[2][0] = floorXValues[floorY][floorX + 1];
			newCorners[3][0] = floorXValues[floorY][floorX];
		}
		
		newCorners[0][1] = newCorners[1][1] = topY;
		newCorners[2][1] = newCorners[3][1] = bottomY;
		
		return newCorners;
	}
	
	public BufferedImage getShapedWall(int level, int square, int surface, int type)
	{
		return shapedWalls[level][square + 1][surface][type];
	}
	
	public BufferedImage getTile(int tileNumber)
	{
		return mapTiles[tileNumber];
	}

	public BufferedImage getFogFilter()
	{
		return fogFilter;
	}
	
	public BufferedImage getMonsterImage(short monsterImage)
	{
		return monsterImages[monsterImage];
	}
	
	public BufferedImage getTownImage(int townImage)
	{
		return townImages[townImage];
	}
	
	public BufferedImage getOtherImage(int otherImage)
	{
		return otherImages[otherImage];
	}
}
