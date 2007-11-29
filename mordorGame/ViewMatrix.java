package mordorGame;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import mordorData.DataBank;
import mordorData.ImageBank;
import mordorData.Map;
import mordorData.MapSquare;
import mordorData.Player;
import mordorEnums.Direction;

public class ViewMatrix
{
	private Player player;
	private DataBank dataBank;
	private Map map;
	private ImageBank images;
	
	private BufferedImage currentView;
	private int viewWidth, viewHeight;
	
	private MapSquare[] currentSquares;
	
	public ViewMatrix(Player nPlayer, DataBank nDataBank, int nViewWidth, int nViewHeight)
	{
		dataBank = nDataBank;
		player = nPlayer;
		map = dataBank.getMap();
		images = dataBank.getImages();
		currentSquares = new MapSquare[6];
		
		viewWidth = nViewWidth;
		viewHeight = nViewHeight;
		
		currentView = new BufferedImage(viewWidth, viewHeight, BufferedImage.TYPE_INT_ARGB);
		
		updateMatrix();
	}
	
	public void updateMatrix()
	{
		// Update squares in matrix.
		int x = player.getCoord().getX();
		int y = player.getCoord().getY();
		
		int correction = (player.getCoord().getDirection() == Direction.North || player.getCoord().getDirection() == Direction.East) ? 1 : -1;
		boolean vertical = (player.getCoord().getDirection() == Direction.North || player.getCoord().getDirection() == Direction.South);
		
		if(vertical)
		{
			updateSquare(0, x - (1 * correction), y + (1 * correction));
			updateSquare(1, x, y + (1 * correction));
			updateSquare(2, x + (1 * correction), y + (1 * correction));
			
			updateSquare(3, x - (1 * correction), y);
			updateSquare(5, x + (1 * correction), y);
		}
		else
		{
			updateSquare(0, x + (1 * correction), y + (1 * correction));
			updateSquare(1, x + (1 * correction), y);
			updateSquare(2, x + (1 * correction), y - (1 * correction));
			
			updateSquare(3, x, y + (1 * correction));
			updateSquare(5, x, y - (1 * correction));
		}

		currentSquares[4] = map.getMapSquare(x, y, player.getCoord().getZ());
		updateCurrentView();
	}
	
	public void updateSquare(int squareNumber, int x, int y)
	{
		if(x >= 0 && x < map.getMapLevel(player.getCoord().getZ()).getWidth() && y >= 0 && y < map.getMapLevel(player.getCoord().getZ()).getHeight())
			currentSquares[squareNumber] = map.getMapSquare(x, y, player.getCoord().getZ());
		else 
			currentSquares[squareNumber] = null;
	}
	
	public BufferedImage getView()
	{
		return currentView;
	}
	
	private void updateCurrentView()
	{
		Graphics2D viewSurface = currentView.createGraphics();
		byte left = (player.getCoord().getDirection().value() > 0) ? (byte)(player.getCoord().getDirection().value() - 1) : 3;
		byte far = player.getCoord().getDirection().value();
		byte right = (byte)((player.getCoord().getDirection().value() + 1) % 4);
		
		viewSurface.setColor(Color.BLACK);
		viewSurface.fillRect(0, 0, viewWidth, viewHeight);
		
		// ceilings
		if(currentSquares[0] != null && !currentSquares[0].isSolidRock())
		{
			viewSurface.drawImage(images.getShapedWall(1, -1, ImageBank.SURAFCE_CEILING, ImageBank.WALL_ROCK), null, 0, ImageBank.CEILINGFARY);
			
			if(currentSquares[0].isWaterSquare())
				viewSurface.drawImage(images.getShapedWall(1, -1, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_WATER), null, 0,ImageBank.FLOORFARY);
			else if(currentSquares[0].isSandSquare())
				viewSurface.drawImage(images.getShapedWall(1, -1, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_SAND), null, 0,ImageBank.FLOORFARY);
			else
				viewSurface.drawImage(images.getShapedWall(1, -1, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_STONE), null, 0,ImageBank.FLOORFARY);
			
			if(currentSquares[0].hasWall(far))
				viewSurface.drawImage(images.getShapedWall(1, -1, ImageBank.SURFACE_FAR, currentSquares[0].getWall(far)), null, 0, 48);
			
		}
		
		if(currentSquares[2] != null && !currentSquares[2].isSolidRock())
		{
			viewSurface.drawImage(images.getShapedWall(1, 1, ImageBank.SURAFCE_CEILING, ImageBank.WALL_ROCK), null, 86, ImageBank.CEILINGFARY);

			if(currentSquares[2].isWaterSquare())
				viewSurface.drawImage(images.getShapedWall(1, 1, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_WATER), null, 86, ImageBank.FLOORFARY);
			else if(currentSquares[2].isSandSquare())
				viewSurface.drawImage(images.getShapedWall(1, 1, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_SAND), null, 86, ImageBank.FLOORFARY);
			else
				viewSurface.drawImage(images.getShapedWall(1, 1, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_STONE), null, 86, ImageBank.FLOORFARY);
			
			if(currentSquares[2].hasWall(far))
				viewSurface.drawImage(images.getShapedWall(1, 1, ImageBank.SURFACE_FAR, currentSquares[2].getWall(far)), null, 86, 48);
				
		}
		
		if(currentSquares[1] != null && !currentSquares[1].isSolidRock())
		{
			viewSurface.drawImage(images.getShapedWall(1, 0, ImageBank.SURAFCE_CEILING, ImageBank.WALL_ROCK), null, 24, ImageBank.CEILINGFARY);

			if(currentSquares[1].isWaterSquare())
				viewSurface.drawImage(images.getShapedWall(1, 0, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_WATER), null, 24, ImageBank.FLOORFARY);
			else if(currentSquares[1].isSandSquare())
				viewSurface.drawImage(images.getShapedWall(1, 0, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_SAND), null, 24, ImageBank.FLOORFARY);
			else
				viewSurface.drawImage(images.getShapedWall(1, 0, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_STONE), null, 24, ImageBank.FLOORFARY);
			
			if(currentSquares[1].hasWall(far))
				viewSurface.drawImage(images.getShapedWall(1, 0, ImageBank.SURFACE_FAR, currentSquares[1].getWall(far)), null, 42, 48);
			
			if(currentSquares[1].hasWall(left))
				viewSurface.drawImage(images.getShapedWall(1, 0, ImageBank.SURFACE_LEFT, currentSquares[1].getWall(left)), null, 24, 32);
			
			if(currentSquares[1].hasWall(right))
				viewSurface.drawImage(images.getShapedWall(1, 0, ImageBank.SURFACE_RIGHT, currentSquares[1].getWall(right)), null, 86, 32);
				
		}
		
		if(currentSquares[3] != null && !currentSquares[3].isSolidRock())
		{
			viewSurface.drawImage(images.getShapedWall(0, -1, ImageBank.SURAFCE_CEILING, ImageBank.WALL_ROCK), null, -128, 0);

			if(currentSquares[3].isWaterSquare())
				viewSurface.drawImage(images.getShapedWall(0, -1, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_WATER), null, -128, ImageBank.FLOORNEARY);
			else if(currentSquares[3].isSandSquare())
				viewSurface.drawImage(images.getShapedWall(0, -1, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_SAND), null, -128, ImageBank.FLOORNEARY);
			else
				viewSurface.drawImage(images.getShapedWall(0, -1, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_STONE), null, -128, ImageBank.FLOORNEARY);
			
			if(currentSquares[3].hasWall(far))
				viewSurface.drawImage(images.getShapedWall(0, 0, ImageBank.SURFACE_FAR, currentSquares[3].getWall(far)), null, -56, 32);
				
		}
		
		if(currentSquares[5] != null && !currentSquares[5].isSolidRock())
		{
			viewSurface.drawImage(images.getShapedWall(0, 1, ImageBank.SURAFCE_CEILING, ImageBank.WALL_ROCK), null, 104, 0);

			if(currentSquares[5].isWaterSquare())
				viewSurface.drawImage(images.getShapedWall(0, 1, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_WATER), null, 104, ImageBank.FLOORNEARY);
			else if(currentSquares[5].isSandSquare())
				viewSurface.drawImage(images.getShapedWall(0, 1, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_SAND), null, 104, ImageBank.FLOORNEARY);
			else
				viewSurface.drawImage(images.getShapedWall(0, 1, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_STONE), null, 104, ImageBank.FLOORNEARY);
			
			if(currentSquares[5].hasWall(far))
				viewSurface.drawImage(images.getShapedWall(0, 0, ImageBank.SURFACE_FAR, currentSquares[5].getWall(far)), null, 104, 32);
		}
		
		if(!currentSquares[4].isSolidRock())
		{
			viewSurface.drawImage(images.getShapedWall(0, 0, ImageBank.SURAFCE_CEILING, ImageBank.WALL_ROCK), null, 0, 0);

			if(currentSquares[4].isWaterSquare())
				viewSurface.drawImage(images.getShapedWall(0, 0, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_WATER), null, 0, ImageBank.FLOORNEARY);
			else if(currentSquares[4].isSandSquare())
				viewSurface.drawImage(images.getShapedWall(0, 0, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_SAND), null, 0, ImageBank.FLOORNEARY);
			else
				viewSurface.drawImage(images.getShapedWall(0, 0, ImageBank.SURFACE_FLOOR, ImageBank.FLOOR_STONE), null, 0, ImageBank.FLOORNEARY);

			if(currentSquares[4].hasWall(far))
				viewSurface.drawImage(images.getShapedWall(0, 0, ImageBank.SURFACE_FAR, currentSquares[4].getWall(far)), null, 24, 32);
			
			if(currentSquares[4].hasWall(left))
				viewSurface.drawImage(images.getShapedWall(0, 0, ImageBank.SURFACE_LEFT, currentSquares[4].getWall(left)), null, 0, 0);
			
			if(currentSquares[4].hasWall(right))
				viewSurface.drawImage(images.getShapedWall(0, 0, ImageBank.SURFACE_RIGHT, currentSquares[4].getWall(right)), null, 104, 0);
		}

		
		if(currentSquares[4].isFogSquare())
			viewSurface.drawImage(images.getFogFilter(), null, 0, 0);
		
		viewSurface.dispose();
	}
}
