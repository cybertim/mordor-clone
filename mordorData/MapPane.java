package mordorData;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

import mordorEnums.Direction;
import mordorHelpers.Coord;



public class MapPane extends JPanel implements Scrollable
{
	private MapLevel mapLevel;
	private JViewport viewPort;
	private BufferedImage currentScreen;
	
	public MapPane(MapLevel nMapLevel, Player nPlayer)
	{
		mapLevel = nMapLevel;
		updateMapImage(nPlayer.getCoord());
	}
	
	public void setViewport(JViewport nViewPort)
	{
		viewPort = nViewPort;
	//	viewPort.setBackground(Color.WHITE);
	//	viewPort.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
	}
	
	/**
	 * Retrieves the depth of this map.
	 * @return byte
	 */
	public byte getLevel()
	{
		return mapLevel.getLevel();
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		
		g2.drawImage(currentScreen, null, 0, 0);
	}
	
	/**
	 * Updates a specific square on this level.
	 * @param x
	 * @param y
	 */
	public void updateSquare(byte x, byte y)
	{
		Graphics2D surface = currentScreen.createGraphics();
		int mapHeight = ((mapLevel.getHeight() - 1) * ImageBank.FEATUREHEIGHT);
		
		surface.drawImage(createSquare(mapLevel.getMapSquare(x, y)), null, x * ImageBank.FEATUREWIDTH, mapHeight - (y * ImageBank.FEATUREHEIGHT));
	
		surface.dispose();
	}
	
	/**
	 * Updates the arrow.
	 * @param x
	 * @param y
	 * @param dir
	 */
	public void updateArrow(Coord coords)
	{
		Graphics2D surface = currentScreen.createGraphics();
		Polygon arrow = getArrow(coords);
		int mapHeight = ((mapLevel.getHeight() - 1) * ImageBank.FEATUREHEIGHT);
		
		// Redraw this square.
		surface.drawImage(createSquare(mapLevel.getMapSquare(coords)), null, coords.getX() * ImageBank.FEATUREWIDTH, mapHeight - (coords.getY() * ImageBank.FEATUREHEIGHT));
		
		// Overlay the arrow.
		surface.setColor(Color.RED);
		surface.fillPolygon(arrow);
		surface.setColor(Color.WHITE);
		surface.drawPolygon(arrow);
		
		surface.dispose();
	}
	
	/**
	 * Update the square the player is based on the passed coordinates.
	 * Repaints
	 * @param coords  Coord of player
	 */
	public void updatePlayerSquare(Coord coords)
	{
		if(coords.getZ() != mapLevel.getLevel())
			return; // This square is not on this level...
		
		updateArrow(coords);
		repaint();
	}
	
	/**
	 * Updates another square and repaints.
	 * @param coords Coord to update.
	 */
	public void updateNonPlayerSquare(Coord coords)
	{
		if(coords.getZ() != mapLevel.getLevel())
			return; // This square is not on this level...
		
		updateSquare(coords.getX(), coords.getY());
		repaint();
	}
	
	/**
	 * Updates the whole map image.
	 * @param playerCoords 	Coordinates of the player.
	 *
	 */
	private void updateMapImage(Coord playerCoords)
	{
		currentScreen = new BufferedImage(mapLevel.getWidth() * ImageBank.FEATUREWIDTH, mapLevel.getHeight() * ImageBank.FEATUREHEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D surface = currentScreen.createGraphics();
		int mapHeight = ((mapLevel.getHeight() - 1) * ImageBank.FEATUREHEIGHT);
		
		surface.setColor(Color.GRAY);
		surface.fillRect(0, 0, currentScreen.getWidth(), currentScreen.getHeight());
		
		for(byte x = 0; x < mapLevel.getWidth(); x++)
		{
			for(byte y = 0; y < mapLevel.getHeight(); y++)
			{
				if(mapLevel.getMapSquare(x, y).isVisited())
				{
					surface.drawImage(createSquare(mapLevel.getMapSquare(x, y)), null, x * ImageBank.FEATUREWIDTH, mapHeight - (y * ImageBank.FEATUREHEIGHT));
				}
			}
		}
		
		// draw player arrow.
		if(playerCoords.getZ() == mapLevel.getLevel())
		{
			Polygon arrow = getArrow(playerCoords);
			surface.setColor(Color.RED);
			surface.fillPolygon(arrow);
			surface.setColor(Color.WHITE);
			surface.drawPolygon(arrow);
		}
		
		setPreferredSize(new Dimension(mapLevel.getWidth() * ImageBank.FEATUREWIDTH, mapLevel.getHeight() * ImageBank.FEATUREHEIGHT));
		revalidate();
	}
	
	/**
	 * Creates an image for the requested map square
	 * @param mapSquare	The square an image is requested for.
	 * @return BufferedImage	The image requested.
	 */
	private BufferedImage createSquare(MapSquare mapSquare)
	{
		BufferedImage newImage = new BufferedImage(ImageBank.FEATUREWIDTH, ImageBank.FEATUREHEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bSurface = newImage.createGraphics();
		
		int left = 0;
		int right = ImageBank.FEATUREWIDTH - 1;
		int top = 0;
		int bottom = ImageBank.FEATUREHEIGHT - 1;
		
		if(mapSquare.isSolidRock())
		{
			bSurface.setColor(Color.WHITE);
			bSurface.fillRect(left, top, ImageBank.FEATUREWIDTH, ImageBank.FEATUREHEIGHT);
			return newImage;
		}
		
		bSurface.setColor(Color.BLACK);
		bSurface.fillRect(left, top, ImageBank.FEATUREWIDTH, ImageBank.FEATUREHEIGHT);
		
		// draw the features. do so regardless of existence
		SquareFeature[] features = mapSquare.getSquareFeatures();
		if(features[0].featureDetected())
			bSurface.drawImage(features[0].getSquareImage(), null, 0, 0);
		if(features[1].featureDetected())
			bSurface.drawImage(features[1].getSquareImage(), null, 0, 0);
		
		//	 draw walls
		bSurface.setColor(Color.WHITE);
		switch(mapSquare.getWallType(Direction.North))
		{
		case MapSquare.BYTE_WALLDOOR:
			bSurface.drawLine(left + 4, top, left + 4, top + 1);
			bSurface.drawLine(right - 4, top, right - 4, top + 1);
			bSurface.drawLine(left, top, left + 4, top);
			bSurface.drawLine(right - 4, top, right, top);
			break;
		case MapSquare.BYTE_WALLHIDDENDOOR:
		case MapSquare.BYTE_WALLROCK:
			bSurface.drawLine(left, top, right, top);
			break;
		case MapSquare.BYTE_WALLFOUNDDOOR:
			bSurface.drawLine(left, top, left + 5, top);
			bSurface.drawLine(right - 5, top, right, top);
			bSurface.drawLine(left + 4, top + 1, right - 4, top + 1);
			break;
		}
		
		switch(mapSquare.getWallType(Direction.East))
		{
		case MapSquare.BYTE_WALLDOOR:
			bSurface.drawLine(right, top, right, top + 4);
			bSurface.drawLine(right, bottom - 4, right, bottom);
			bSurface.drawLine(right, top + 4, right - 1, top + 4);
			bSurface.drawLine(right, bottom - 4, right - 1, bottom - 4);
			break;
		case MapSquare.BYTE_WALLHIDDENDOOR:
		case MapSquare.BYTE_WALLROCK:
			bSurface.drawLine(right, top, right, bottom);
			break;
		case MapSquare.BYTE_WALLFOUNDDOOR:
			bSurface.drawLine(right, top, right, top + 5);
			bSurface.drawLine(right, bottom - 5, right, bottom);
			bSurface.drawLine(right - 1, top + 4, right - 1, bottom - 4);
			break;
			
		}
		switch(mapSquare.getWallType(Direction.South))
		{
		case MapSquare.BYTE_WALLDOOR:
			bSurface.drawLine(left, bottom, left + 4, bottom);
			bSurface.drawLine(right - 4, bottom, right, bottom);
			bSurface.drawLine(left + 4, bottom, left + 4, bottom - 1);
			bSurface.drawLine(right - 4, bottom, right - 4, bottom - 1);
			break;
		case MapSquare.BYTE_WALLHIDDENDOOR:
		case MapSquare.BYTE_WALLROCK:
			bSurface.drawLine(left, bottom, right, bottom);
			break;
		case MapSquare.BYTE_WALLFOUNDDOOR:
			bSurface.drawLine(left, bottom, left + 5, bottom);
			bSurface.drawLine(right - 5, bottom, right, bottom);
			bSurface.drawLine(left + 4, bottom - 1, right - 4, bottom - 1);
			break;
		}
		switch(mapSquare.getWallType(Direction.West))
		{
		case MapSquare.BYTE_WALLDOOR:
			bSurface.drawLine(left, top, left, top + 4);
			bSurface.drawLine(left, bottom - 4, left, bottom);
			bSurface.drawLine(left, top + 4, left + 1, top + 4);
			bSurface.drawLine(left, bottom - 4, left + 1, bottom - 4);
			break;
		case MapSquare.BYTE_WALLHIDDENDOOR:
		case MapSquare.BYTE_WALLROCK:
			bSurface.drawLine(left, top, left, bottom);
			break;
		case MapSquare.BYTE_WALLFOUNDDOOR:
			bSurface.drawLine(left, top, left, top + 5);
			bSurface.drawLine(left, bottom - 5, left, bottom);
			bSurface.drawLine(left + 1, top + 4, left + 1, bottom - 4);
			break;
		}
		
		return newImage;
	}
	
	/**
	 * Creates a polygon with the correct corners representing the player's arrow
	 * @param playerCoords	Player's coordinates 
	 * @return Polygon	The player arrow.
	 */
	private Polygon getArrow(Coord playerCoords)
	{
		Polygon arrow = new Polygon();
		int mapHeight = 5 + ((mapLevel.getHeight() - 1) * ImageBank.FEATUREHEIGHT);
		int left = (playerCoords.getX() * ImageBank.FEATUREWIDTH);
		int top = mapHeight - ((playerCoords.getY() * ImageBank.FEATUREHEIGHT) + 5);
		
		switch(playerCoords.getDirection())
		{
		case North:
			arrow.addPoint(left + 7, top);
			arrow.addPoint(left + 14, top + 7);
			arrow.addPoint(left + 10, top + 7);
			arrow.addPoint(left + 10, top + 14);
			arrow.addPoint(left + 4, top + 14);
			arrow.addPoint(left + 4, top + 7);
			arrow.addPoint(left, top + 7);
			break;
		case East:
			arrow.addPoint(left + 14, top + 7);
			arrow.addPoint(left + 7, top + 14);
			arrow.addPoint(left + 7, top + 10);
			arrow.addPoint(left, top + 10);
			arrow.addPoint(left, top + 4);
			arrow.addPoint(left + 7, top + 4);
			arrow.addPoint(left + 7, top);
			break;
		case South:
			arrow.addPoint(left + 7, top + 14);
			arrow.addPoint(left + 14, top + 7);
			arrow.addPoint(left + 10, top + 7);
			arrow.addPoint(left + 10, top);
			arrow.addPoint(left + 4, top);
			arrow.addPoint(left + 4, top + 7);
			arrow.addPoint(left, top + 7);
			break;
		case West:
			arrow.addPoint(left, top + 7);
			arrow.addPoint(left + 7, top + 14);
			arrow.addPoint(left + 7, top + 10);
			arrow.addPoint(left + 14, top + 10);
			arrow.addPoint(left + 14, top + 4);
			arrow.addPoint(left + 7, top + 4);
			arrow.addPoint(left + 7, top);
			break;
		}
		
		return arrow;
	}
	
	/**
	 * Updates the map's viewport's viewing rectangle
	 * @param playerCoords Player's coordinates
	 */
	public void updateCurrentView(Coord playerCoords)
	{
		int x = (playerCoords.getX() * ImageBank.FEATUREWIDTH) - (viewPort.getWidth() >>> 1);
		int y = ((mapLevel.getHeight() - 1) * ImageBank.FEATUREHEIGHT) - ((playerCoords.getY() * ImageBank.FEATUREHEIGHT) + (viewPort.getHeight() >>> 1));
		
		if(x < 0)
			x = 0;
		if(x > (currentScreen.getWidth() - viewPort.getWidth()))
			x = (currentScreen.getWidth() - viewPort.getWidth());
		if(y < 0)
			y = 0;
		if(y > (currentScreen.getHeight() - viewPort.getHeight()))
			y = (currentScreen.getHeight() - viewPort.getHeight());
		
		viewPort.setViewPosition(new Point(x, y));
	}
	
	/**
	 * Sets the maps current viewport view.
	 * @param playerCoords	Player's coordinates
	 *
	 */
	public void setCurrentView(Coord playerCoords)
	{
		int x = (playerCoords.getX() * ImageBank.FEATUREWIDTH) - 75;
		int y = ((mapLevel.getHeight() - 1) * ImageBank.FEATUREHEIGHT) - ((playerCoords.getY() * ImageBank.FEATUREHEIGHT) + 50);
		
		viewPort.setViewPosition(new Point(x, y));
	}

	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		// TODO Auto-generated method stub
		return 5 * ImageBank.FEATUREWIDTH;
	}

	public boolean getScrollableTracksViewportHeight() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		// TODO Auto-generated method stub
		return ImageBank.FEATUREWIDTH;
	}
}
