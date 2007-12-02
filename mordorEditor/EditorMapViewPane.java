package mordorEditor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.Scrollable;

import mordorData.DataBank;
import mordorData.ImageBank;
import mordorData.MapLevel;
import mordorData.MapSquare;
import mordorData.SquareFeature;
import mordorEnums.Direction;

import structures.ListIter;
import structures.ListNode;


public class EditorMapViewPane extends JPanel implements MouseListener, MouseMotionListener, Scrollable
{
	private boolean drawRooms, visitedView;
	private BufferedImage currentView, roomViewImage;
	private MapLevel mapLevel;
	private DataBank dataBank;
	private EditorTrueViewPanel parent;
	
	EditorMapViewPane(EditorTrueViewPanel nParent, MapLevel currentMapLevel, DataBank nDataBank)
	{
		drawRooms = false;
		visitedView = false;
		mapLevel = currentMapLevel;
		dataBank = nDataBank;
		parent = nParent;
		
		updateMapLevel(mapLevel);
		
		this.addMouseListener(this);
	}
	
	public void paintComponent(Graphics g)
	{	
		Graphics2D g2 = (Graphics2D)g;
		
		if(drawRooms)
			g2.drawImage(roomViewImage, null, 0, 0);
		else
			g2.drawImage(currentView, null, 0, 0);
	}
	
	public void updateMapLevel(MapLevel newMapLevel)
	{
		mapLevel = newMapLevel;
		createCurrentView();
		this.setPreferredSize(new Dimension(mapLevel.getWidth() * ImageBank.FEATUREWIDTH, mapLevel.getHeight() * ImageBank.FEATUREHEIGHT));
		repaint();
	}
	
	public void updateSquare(MapSquare fixSquare)
	{
		if(fixSquare == null)
			return;
		
		Graphics2D surface = currentView.createGraphics();
		Graphics2D rvsurface = roomViewImage.createGraphics();

		int imageBottom = mapLevel.getHeight() * ImageBank.FEATUREHEIGHT;
		surface.drawImage(createSquareImage(fixSquare, false), null, fixSquare.getXCoordinate() * ImageBank.FEATUREWIDTH,  (imageBottom - ((fixSquare.getYCoordinate() + 1) * ImageBank.FEATUREHEIGHT)));
		rvsurface.drawImage(createSquareImage(fixSquare, true), null, fixSquare.getXCoordinate() * ImageBank.FEATUREWIDTH,  (imageBottom - ((fixSquare.getYCoordinate() + 1) * ImageBank.FEATUREHEIGHT)));
		
		surface.dispose();
		rvsurface.dispose();
		
		this.repaint();
	}
	
	public void updateSquare(int x, int y)
	{
		updateSquare(mapLevel.getMapSquare(x, y));
	}
	
	public void reloadCurrentView()
	{
		mapLevel = dataBank.getMap().getMapLevel(mapLevel.getLevel());
		createCurrentView();
		this.setPreferredSize(new Dimension(mapLevel.getWidth() * ImageBank.FEATUREWIDTH, mapLevel.getHeight() * ImageBank.FEATUREHEIGHT));
		this.revalidate();
		repaint();
	}
	
	private void createCurrentView()
	{
		int imageBottom = mapLevel.getHeight() * ImageBank.FEATUREHEIGHT;
		currentView = new BufferedImage(mapLevel.getWidth() * ImageBank.FEATUREWIDTH, imageBottom, BufferedImage.TYPE_INT_ARGB);
		roomViewImage = new BufferedImage(mapLevel.getWidth() * ImageBank.FEATUREWIDTH, imageBottom, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D surface = currentView.createGraphics();
		Graphics2D rvsurface = roomViewImage.createGraphics();
		
		for(int x = 0; x < mapLevel.getWidth(); x++)
		{
			for(int y = 0; y < mapLevel.getHeight(); y++)
			{
				surface.drawImage(createSquareImage(mapLevel.getMapSquare(x, y), false), null, x * ImageBank.FEATUREWIDTH, (imageBottom - ((y + 1) * ImageBank.FEATUREHEIGHT)));
				rvsurface.drawImage(createSquareImage(mapLevel.getMapSquare(x, y), true), null, x * ImageBank.FEATUREWIDTH, (imageBottom - ((y + 1) * ImageBank.FEATUREHEIGHT)));
			}
		}
		
		surface.dispose();
		rvsurface.dispose();
	}
	
	public BufferedImage createSquareImage(MapSquare mapSquare, boolean roomView)
	{
		BufferedImage newImage = new BufferedImage(ImageBank.FEATUREWIDTH, ImageBank.FEATUREHEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bSurface = newImage.createGraphics();
		
	//	 draw floor
		int left = 0;
		int right = ImageBank.FEATUREWIDTH - 1;
		int top = 0;
		int bottom = ImageBank.FEATUREHEIGHT - 1;
		
		if(mapSquare == null)
		{
			bSurface.setColor(Color.YELLOW);
			bSurface.fillRect(left, top, ImageBank.FEATUREWIDTH, ImageBank.FEATUREHEIGHT);
			return newImage;
		}
		else if(mapSquare.isSolidRock())
		{
			bSurface.setColor(Color.WHITE);
			bSurface.fillRect(left, top, ImageBank.FEATUREWIDTH, ImageBank.FEATUREHEIGHT);
			return newImage;
		}
		
		bSurface.setColor(Color.BLACK);
		bSurface.fillRect(left, top, ImageBank.FEATUREWIDTH, ImageBank.FEATUREHEIGHT);
		
		// draw the features. do so regardless of existence
		SquareFeature[] features = mapSquare.getSquareFeatures();
		bSurface.drawImage(features[0].getSquareImage(), null, 0, 0);
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
		case MapSquare.BYTE_WALLROCK:
			bSurface.drawLine(left, top, right, top);
			break;
		case MapSquare.BYTE_WALLHIDDENDOOR:
			bSurface.setColor(Color.RED);
			bSurface.drawLine(left, top, left + 5, top);
			bSurface.drawLine(right - 5, top, right, top);
			bSurface.drawLine(left + 4, top + 1, right - 4, top + 1);
			bSurface.setColor(Color.WHITE);
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
		case MapSquare.BYTE_WALLROCK:
			bSurface.drawLine(right, top, right, bottom);
			break;
		case MapSquare.BYTE_WALLHIDDENDOOR:
			bSurface.setColor(Color.RED);
			bSurface.drawLine(right, top, right, top + 5);
			bSurface.drawLine(right, bottom - 5, right, bottom);
			bSurface.drawLine(right - 1, top + 4, right - 1, bottom - 4);
			bSurface.setColor(Color.WHITE);
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
		case MapSquare.BYTE_WALLROCK:
			bSurface.drawLine(left, bottom, right, bottom);
			break;
		case MapSquare.BYTE_WALLHIDDENDOOR:
			bSurface.setColor(Color.RED);
			bSurface.drawLine(left, bottom, left + 5, bottom);
			bSurface.drawLine(right - 5, bottom, right, bottom);
			bSurface.drawLine(left + 4, bottom - 1, right - 4, bottom - 1);
			bSurface.setColor(Color.WHITE);
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
		case MapSquare.BYTE_WALLROCK:
			bSurface.drawLine(left, top, left, bottom);
			break;
		case MapSquare.BYTE_WALLHIDDENDOOR:
			bSurface.setColor(Color.RED);
			bSurface.drawLine(left, top, left, top + 5);
			bSurface.drawLine(left, bottom - 5, left, bottom);
			bSurface.drawLine(left + 1, top + 4, left + 1, bottom - 4);
			bSurface.setColor(Color.WHITE);
			break;
		case MapSquare.BYTE_WALLFOUNDDOOR:
			bSurface.drawLine(left, top, left, top + 5);
			bSurface.drawLine(left, bottom - 5, left, bottom);
			bSurface.drawLine(left + 1, top + 4, left + 1, bottom - 4);
			break;
		}
		
		if(visitedView && !mapSquare.isVisited())
		{
			bSurface.setColor(Color.ORANGE);
			bSurface.fillRect(right - 3, top + 3, 2, 2);
		}
		
		if(roomView)
		{
			bSurface.setColor(Color.YELLOW);
			bSurface.setFont(new Font(bSurface.getFont().getFontName(), Font.PLAIN, bSurface.getFont().getSize() - 4));
			bSurface.drawString("" + mapSquare.getRoom().getRoomNumber(), left + 2, bottom - 2);
		}
		
		return newImage;
	}
	
	public void toggleRoomView(boolean newRoomView)
	{
		drawRooms = newRoomView;
	//	createCurrentView();
		repaint();
	}
	
	public void toggleVisitedView(boolean newVisitedView)
	{
		visitedView = newVisitedView;
		createCurrentView();
		repaint();
	}
	
	/**
	 * Removes all features from a square.
	 * @param tSquare	Square to be modified.
	 */
	private void setClear(MapSquare tSquare)
	{
		byte squareX = tSquare.getXCoordinate();
		byte squareY = tSquare.getYCoordinate();
		
		if(tSquare.isSolidRock())
		{
			tSquare.setSolidRock(false);
			tSquare.setWall(Direction.North, MapSquare.BYTE_WALLROCK);
			tSquare.setWall(Direction.East, MapSquare.BYTE_WALLROCK);
			tSquare.setWall(Direction.South, MapSquare.BYTE_WALLROCK);
			tSquare.setWall(Direction.West, MapSquare.BYTE_WALLROCK);
		}
		if(tSquare.isStudSquare())
		{
			tSquare.getRoom().setStud(false);
			updateRoom(tSquare);
		}
		if(tSquare.areStairs())
		{
			if(dataBank.getMap().getMapSquare(squareX, squareY, mapLevel.getLevel() + 1) != null && dataBank.getMap().getMapSquare(squareX, squareY, mapLevel.getLevel() + 1).areStairs())
			{
				if(dataBank.getMap().getMapSquare(squareX, squareY, mapLevel.getLevel() + 1).getSquareFeatures()[0].getType() == SquareFeature.TYPE_STAIRSDOWN)
					dataBank.getMap().getMapSquare(squareX, squareY, mapLevel.getLevel() + 1).getSquareFeatures()[0].setType(SquareFeature.TYPE_NONE);
				else
					dataBank.getMap().getMapSquare(squareX, squareY, mapLevel.getLevel() + 1).getSquareFeatures()[1].setType(SquareFeature.TYPE_NONE);
				parent.updateSquare(dataBank.getMap().getMapSquare(squareX, squareY, mapLevel.getLevel() + 1));
			}
			else if(dataBank.getMap().getMapSquare(squareX, squareY, mapLevel.getLevel() - 1) != null && dataBank.getMap().getMapSquare(squareX, squareY, mapLevel.getLevel() - 1).areStairs())
			{
				if(dataBank.getMap().getMapSquare(squareX, squareY, mapLevel.getLevel() - 1).getSquareFeatures()[0].getType() == SquareFeature.TYPE_STAIRSUP)
					dataBank.getMap().getMapSquare(squareX, squareY, mapLevel.getLevel() - 1).getSquareFeatures()[0].setType(SquareFeature.TYPE_NONE);
				else
					dataBank.getMap().getMapSquare(squareX, squareY, mapLevel.getLevel() - 1).getSquareFeatures()[1].setType(SquareFeature.TYPE_NONE);
				parent.updateSquare(dataBank.getMap().getMapSquare(squareX, squareY, mapLevel.getLevel() - 1));
			}
		}
		if(tSquare.isExitSquare())
			return;
		

		tSquare.getSquareFeatures()[0].setType(SquareFeature.TYPE_NONE);
		tSquare.getSquareFeatures()[1].setType(SquareFeature.TYPE_NONE);
	}
	
	/**
	 * Removes all features from a square and sets it to solid rock.
	 * @param stonedSquare
	 */
	private void setStone(MapSquare stonedSquare)
	{
		setClear(stonedSquare);
		
		stonedSquare.setSolidRock(true);
		if(mapLevel.getMapSquare(stonedSquare.getXCoordinate(), stonedSquare.getYCoordinate() + 1) != null)
		{
			mapLevel.getMapSquare(stonedSquare.getXCoordinate(), stonedSquare.getYCoordinate() + 1).setWall(Direction.South, MapSquare.BYTE_WALLROCK);
			updateSquare(mapLevel.getMapSquare(stonedSquare.getXCoordinate(), stonedSquare.getYCoordinate() + 1));
		}
		if(mapLevel.getMapSquare(stonedSquare.getXCoordinate(), stonedSquare.getYCoordinate() - 1) != null)
		{
			mapLevel.getMapSquare(stonedSquare.getXCoordinate(), stonedSquare.getYCoordinate() - 1).setWall(Direction.North, MapSquare.BYTE_WALLROCK);
			updateSquare(mapLevel.getMapSquare(stonedSquare.getXCoordinate(), stonedSquare.getYCoordinate() - 1));
		}
		if(mapLevel.getMapSquare(stonedSquare.getXCoordinate() - 1, stonedSquare.getYCoordinate()) != null)
		{
			mapLevel.getMapSquare(stonedSquare.getXCoordinate() - 1, stonedSquare.getYCoordinate()).setWall(Direction.East, MapSquare.BYTE_WALLROCK);
			updateSquare(mapLevel.getMapSquare(stonedSquare.getXCoordinate() - 1, stonedSquare.getYCoordinate()));
		}
		if(mapLevel.getMapSquare(stonedSquare.getXCoordinate() + 1, stonedSquare.getYCoordinate()) != null)
		{
			mapLevel.getMapSquare(stonedSquare.getXCoordinate() + 1, stonedSquare.getYCoordinate()).setWall(Direction.West, MapSquare.BYTE_WALLROCK);
			updateSquare(mapLevel.getMapSquare(stonedSquare.getXCoordinate() + 1, stonedSquare.getYCoordinate()));
		}
	}
	
	private void setStud(MapSquare tSquare)
	{
		ListIter<MapSquare> tNode = tSquare.getRoom().getSquares().getIterator();
		
		
		while(tNode.next())
			if(!tNode.element().hasEmptyFeature())
				return;
		
		tSquare.getRoom().setStud(true);
		updateRoom(tSquare);
	}
	
	/**
	 * Performs updateSquare on all squares in a room.
	 * For use with stud on/off.
	 * @param tSquare
	 */
	private void updateRoom(MapSquare tSquare)
	{
		ListIter<MapSquare> tNode = tSquare.getRoom().getSquares().getIterator();
		
		while(tNode.next())
			if(tNode.element() != tSquare)
				updateSquare(tNode.element());

		tSquare.getRoom().setStud(true);
		updateSquare(tSquare);
	}
	
	/**
	 * Sets the specified wall to the specified square.
	 * Will cycle through values.
	 * @param square
	 * @param wallNum
	 */
	/*private void setWall(MapSquare square, byte wallNum)
	{DELETE
		if(square.getWallType(wallNum) != MapSquare.MAXWALLTYPE)
			square.setWall(wallNum, ((byte)(square.getWallType(wallNum) + 2)));
		else
			square.setWall(wallNum, MapSquare.BYTE_WALLNONE);
	}*/
	private void setWall(MapSquare square, Direction direction)
	{
		if(square.getWallType(direction) != MapSquare.MAXWALLTYPE)
			square.setWall(direction, ((byte)(square.getWallType(direction) + 2)));
		else
			square.setWall(direction, MapSquare.BYTE_WALLNONE);
	}

	public void mouseClicked(MouseEvent e)
	{
		// get mouse's location and open corresponding square
		// or, if it is room, geet the corresponding square, get
		// its room, and opne the corresponding room dialogue
		
		int squareX, squareY;
		int mouseX, mouseY;
		int mapWidth, mapHeight;
		
		mouseX = e.getX() - 2;
		mouseY = e.getY() - 2;
		
		mapWidth = mapLevel.getWidth() * ImageBank.FEATUREWIDTH;
		mapHeight = mapLevel.getHeight() * ImageBank.FEATUREHEIGHT;
		
		// note in the mape
		if(mouseX > mapWidth || mouseY > mapHeight)
		{
			JOptionPane.showMessageDialog(null, "Out of map.");
			return;
		}

		squareX = mouseX / ImageBank.FEATUREWIDTH;
		squareY = (mapLevel.getHeight() - 1) - (mouseY / ImageBank.FEATUREHEIGHT);
		
		MapSquare tSquare = mapLevel.getMapSquare(squareX, squareY);
		
		if(parent.getTileType() == SquareFeature.TYPE_NONE)
		{
			setClear(tSquare);
			updateSquare(tSquare);
		}
		else if(parent.getTileType() == EditorTrueViewPanel.BUTTON_PICKER)
		{
			JFrame roomView = new JFrame();
			
			roomView.add(new MapRoomPanel(tSquare.getRoom(), dataBank, roomView));
			roomView.pack();
			roomView.setVisible(true);
			
			//JOptionPane.showMessageDialog(this, "Room # " + tSquare.getRoom().getRoomNumber() + "\n(" + tSquare.getXCoordinate() + ", " + tSquare.getYCoordinate() + ", " + tSquare.getZCoordinate() + ")");
		}
		else if(parent.getTileType() < 0)
		{
			switch(parent.getTileType())
			{
			case EditorTrueViewPanel.BUTTON_STONE:
				setStone(tSquare);
				break;
			case EditorTrueViewPanel.BUTTON_WALLNORTH:
				setWall(tSquare, Direction.North);
				if(mapLevel.getMapSquare(squareX, squareY + 1) != null)
				{
					setWall(mapLevel.getMapSquare(squareX, squareY + 1), Direction.South);
					updateSquare(mapLevel.getMapSquare(squareX, squareY + 1));
				}
				break;
			case EditorTrueViewPanel.BUTTON_WALLEAST:
				setWall(tSquare, Direction.East);
				if(mapLevel.getMapSquare(squareX + 1, squareY) != null)
				{
					setWall(mapLevel.getMapSquare(squareX + 1, squareY), Direction.West);
					updateSquare(mapLevel.getMapSquare(squareX + 1, squareY));
				}
				break;
			case EditorTrueViewPanel.BUTTON_WALLSOUTH:
				setWall(tSquare, Direction.South);
				if(mapLevel.getMapSquare(squareX, squareY - 1) != null)
				{
					setWall(mapLevel.getMapSquare(squareX, squareY - 1), Direction.North);
					updateSquare(mapLevel.getMapSquare(squareX, squareY - 1));
				}
				break;
			case EditorTrueViewPanel.BUTTON_WALLWEST:
				setWall(tSquare, Direction.West);
				if(mapLevel.getMapSquare(squareX - 1, squareY) != null)
				{
					setWall(mapLevel.getMapSquare(squareX - 1, squareY), Direction.East);
					updateSquare(mapLevel.getMapSquare(squareX - 1, squareY));
				}
				break;
			case EditorTrueViewPanel.BUTTON_VISITED:
				tSquare.setVisited(!tSquare.isVisited());
				break;
			case EditorTrueViewPanel.BUTTON_ROOM:
				tSquare.getRoom().removeSquare(tSquare);
				mapLevel.getRoom(parent.getSelectedRoom()).addSquare(tSquare);
				if(tSquare.isStudSquare() && !tSquare.getRoom().isStudRoom())
					setClear(tSquare);
				if(!tSquare.isStudSquare() && tSquare.getRoom().isStudRoom())
				{
					if(tSquare.getSquareFeatures()[0].getType() == SquareFeature.TYPE_NONE)
						tSquare.getSquareFeatures()[0].setType(SquareFeature.TYPE_STUD);
					else
						tSquare.getSquareFeatures()[1].setType(SquareFeature.TYPE_STUD);
				}
				break;
			}
			updateSquare(tSquare);
		}
		else if(tSquare.hasEmptyFeature())
		{
			if(parent.getTileType() == SquareFeature.TYPE_STUD && !tSquare.getRoom().isStudRoom())
			{
				setStud(tSquare);
			}
			else if(parent.getTileType() > 0 && parent.getTileType() != SquareFeature.TYPE_STUD && tSquare.getSquareFeatures()[0].getType() != parent.getTileType() && tSquare.getSquareFeatures()[1].getType() != parent.getTileType())
			{
				if(parent.getTileType() == SquareFeature.TYPE_TELEPORTSTATIC || parent.getTileType() == SquareFeature.TYPE_CHUTE)
				{
					EditorMapDestDialog temp = new EditorMapDestDialog(parent, tSquare, dataBank, parent.getTileType());
					temp.setVisible(true);
					return;
				}
				else if(parent.getTileType() == SquareFeature.TYPE_STAIRSDOWN)
				{
					if(dataBank.getMap().getMapSquare(tSquare.getXCoordinate(), tSquare.getYCoordinate(), tSquare.getZCoordinate() + 1) != null && !dataBank.getMap().getMapSquare(tSquare.getXCoordinate(), tSquare.getYCoordinate(), tSquare.getZCoordinate() + 1).isSolidRock())
					{
						setClear(dataBank.getMap().getMapSquare(tSquare.getXCoordinate(), tSquare.getYCoordinate(), tSquare.getZCoordinate() + 1));
						setClear(tSquare);
						
						dataBank.getMap().getMapSquare(tSquare.getXCoordinate(), tSquare.getYCoordinate(), tSquare.getZCoordinate() + 1).getSquareFeatures()[0].setType(SquareFeature.TYPE_STAIRSUP);
						tSquare.getSquareFeatures()[0].setType(SquareFeature.TYPE_STAIRSDOWN);
						
						parent.updateSquare(dataBank.getMap().getMapSquare(tSquare.getXCoordinate(), tSquare.getYCoordinate(), tSquare.getZCoordinate() + 1));
						updateSquare(tSquare);
						return;
					}
				}
				else if(parent.getTileType() == SquareFeature.TYPE_STAIRSUP)
				{
					if(dataBank.getMap().getMapSquare(tSquare.getXCoordinate(), tSquare.getYCoordinate(), tSquare.getZCoordinate() - 1) != null && !dataBank.getMap().getMapSquare(tSquare.getXCoordinate(), tSquare.getYCoordinate(), tSquare.getZCoordinate() - 1).isSolidRock())
					{
						setClear(dataBank.getMap().getMapSquare(tSquare.getXCoordinate(), tSquare.getYCoordinate(), tSquare.getZCoordinate() - 1));
						setClear(tSquare);
						
						dataBank.getMap().getMapSquare(tSquare.getXCoordinate(), tSquare.getYCoordinate(), tSquare.getZCoordinate() - 1).getSquareFeatures()[0].setType(SquareFeature.TYPE_STAIRSDOWN);
						tSquare.getSquareFeatures()[0].setType(SquareFeature.TYPE_STAIRSUP);
						
						parent.updateSquare(dataBank.getMap().getMapSquare(tSquare.getXCoordinate(), tSquare.getYCoordinate(), tSquare.getZCoordinate() - 1));
						updateSquare(tSquare);
						return;
					}
				}
				else if(parent.getTileType() == SquareFeature.TYPE_EXIT)
				{
					setClear(tSquare);
					tSquare.getSquareFeatures()[0].setType(SquareFeature.TYPE_EXIT);
					MapSquare bSquare = dataBank.getMap().getExitSquare();
					dataBank.getMap().setExitSquare(tSquare);
					
					parent.updateSquare(bSquare);
					updateSquare(tSquare);
				}
				else if(tSquare.getSquareFeatures()[0].getType() == SquareFeature.TYPE_NONE)
				{
					tSquare.getSquareFeatures()[0].setType(parent.getTileType());
					updateSquare(tSquare);
				}
				else
				{
					tSquare.getSquareFeatures()[1].setType(parent.getTileType());
					updateSquare(tSquare);
				}
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseMoved(MouseEvent e)
	{
		int squareX, squareY;
		int mouseX, mouseY;
		int mapWidth, mapHeight;
		
		mouseX = e.getX() - 2;
		mouseY = e.getY() - 2;
		
		mapWidth = mapLevel.getWidth() * ImageBank.FEATUREWIDTH;
		mapHeight = mapLevel.getHeight() * ImageBank.FEATUREHEIGHT;
		

		if(mouseX < mapWidth && mouseY < mapHeight)
		{
			squareX = mouseX / ImageBank.FEATUREWIDTH;
			squareY = (mapLevel.getHeight() - 1) - (mouseY / ImageBank.FEATUREHEIGHT);
			this.setToolTipText("(" + squareX + ", " + squareY + ")");
			return;
		}
	}

	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		// TODO Auto-generated method stub
		return 0;
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
		return 0;
	}
}
