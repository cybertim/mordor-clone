package mordorEditor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mordorData.DataBank;
import mordorData.ImageBank;
import mordorData.MapSquare;
import mordorData.SquareFeature;

public class EditorTrueViewPanel extends JPanel implements ActionListener, ChangeListener
{	
	private JTabbedPane mapsPane;
	private JScrollPane[] viewScroll;
	
	private JButton tbEmpty, tbWater, tbFog, tbStone, tbSand;
	private JButton tbWallN, tbWallE, tbWallS, tbWallW;
	private JButton tbAnti, tbStud, tbExting, tbPit, tbRot;
	private JButton tbTeleS, tbTeleL, tbTeleD, tbChute;
	private JButton tbStairsU, tbStairsD, tbExit;
	private JButton tbVisited, tbRoom;
	private JButton roomPicker;
	private JComboBox cbRooms;
	
	JPanel toolBarB = new JPanel();
	
	private EditorMapViewPane[] viewPanel;
//	private BufferedImage[] viewRuler;
	private DataBank dataBank;
	
	private static final int VIEWWIDTH = 600;
	private static final int VIEWHEIGHT = 400;
	
	private byte activeType = SquareFeature.TYPE_NONE;
	
	public static final byte BUTTON_STONE = -1;
	public static final byte BUTTON_WALLNORTH = -2;
	public static final byte BUTTON_WALLEAST = -3;
	public static final byte BUTTON_WALLSOUTH = -4;
	public static final byte BUTTON_WALLWEST = -5;
	public static final byte BUTTON_ROOM = -6;
	public static final byte BUTTON_TELESTATIC = -7;
	public static final byte BUTTON_TELELEVEL = -8;
	public static final byte BUTTON_TELEDUNG = -9;
	public static final byte BUTTON_VISITED = -10;
	public static final byte BUTTON_PICKER = -11;
	
	EditorTrueViewPanel(DataBank nDataBank)
	{
		dataBank = nDataBank;
		
		mapsPane = new JTabbedPane();
		createMaps();
		mapsPane.setPreferredSize(new Dimension(VIEWWIDTH, VIEWHEIGHT));
		mapsPane.addChangeListener(this);
		
		JPanel toolBar = new JPanel();
		toolBarB = new JPanel();
		tbEmpty = new JButton(getToolBarImage(ImageBank.TILE_NONE));
		tbWater = new JButton(getToolBarImage(ImageBank.TILE_WATER));
		tbFog = new JButton(getToolBarImage(ImageBank.TILE_FOG));
		tbSand = new JButton(getToolBarImage(ImageBank.TILE_SAND));
		tbStone = new JButton(getToolBarImage(BUTTON_STONE));
		
		tbWallN = new JButton("North");
		tbWallE = new JButton("East");
		tbWallS = new JButton("South");
		tbWallW = new JButton("West");

		tbAnti = new JButton(getToolBarImage(ImageBank.TILE_ANTIMAGIC));
		tbStud = new JButton(getToolBarImage(ImageBank.TILE_STUD));
		tbExting = new JButton(getToolBarImage(ImageBank.TILE_EXTINGUISH));
		tbPit = new JButton(getToolBarImage(ImageBank.TILE_PIT));
		tbRot = new JButton(getToolBarImage(ImageBank.TILE_ROTATOR));
		
		tbTeleS = new JButton(getToolBarImage(BUTTON_TELESTATIC));
		tbTeleL = new JButton(getToolBarImage(BUTTON_TELELEVEL));
		tbTeleD = new JButton(getToolBarImage(BUTTON_TELEDUNG));
		tbStairsU = new JButton(getToolBarImage(ImageBank.TILE_STAIRSUP));
		tbStairsD = new JButton(getToolBarImage(ImageBank.TILE_STAIRSDOWN));
		tbExit = new JButton(getToolBarImage(ImageBank.TILE_EXIT));
		tbChute = new JButton(getToolBarImage(ImageBank.TILE_CHUTE));
		
		tbVisited = new JButton("Visited");
		tbRoom = new JButton("Room 0");
		JLabel cbRoomsLabel =  new JLabel("Rooms");
		cbRooms = new JComboBox(dataBank.getMap().getMapLevel(mapsPane.getSelectedIndex()).getRoomNumbers());
		cbRooms.setToolTipText("Select the room to paint");
		roomPicker = new JButton("?");
		roomPicker.setToolTipText("Room info.");
		
		tbEmpty.addActionListener(this);
		tbWater.addActionListener(this);
		tbFog.addActionListener(this);
		tbSand.addActionListener(this);
		tbStone.addActionListener(this);
		tbWallN.addActionListener(this);
		tbWallE.addActionListener(this);
		tbWallS.addActionListener(this);
		tbWallW.addActionListener(this);
		tbAnti.addActionListener(this);
		tbStud.addActionListener(this);
		tbExting.addActionListener(this);
		tbPit.addActionListener(this);
		tbRot.addActionListener(this);
		tbTeleS.addActionListener(this); 
		tbTeleL.addActionListener(this); 
		tbTeleD.addActionListener(this);
		tbStairsU.addActionListener(this); 
		tbStairsD.addActionListener(this); 
		tbExit.addActionListener(this);
		tbChute.addActionListener(this);
		tbVisited.addActionListener(this);
		tbRoom.addActionListener(this);
		cbRooms.addActionListener(this);
		roomPicker.addActionListener(this);
		
		toolBar.setLayout(new FlowLayout());
		toolBar.add(tbEmpty);
		toolBar.add(tbWater);
		toolBar.add(tbFog);
		toolBar.add(tbSand);
		toolBar.add(tbStone);
		toolBar.add(tbAnti);
		toolBar.add(tbStud);
		toolBar.add(tbExting);
		toolBar.add(tbPit);
		toolBar.add(tbRot);
		toolBar.add(tbTeleS); 
		toolBar.add(tbTeleL); 
		toolBar.add(tbTeleD);
		toolBar.add(tbStairsU); 
		toolBar.add(tbStairsD); 
		toolBar.add(tbExit); 
		toolBar.add(tbChute);
		toolBar.add(roomPicker);
		
		toolBarB.setLayout(new FlowLayout());
		toolBarB.add(new JLabel("Walls"));
		toolBarB.add(tbWallN);
		toolBarB.add(tbWallE);
		toolBarB.add(tbWallS);
		toolBarB.add(tbWallW);
		toolBarB.add(tbVisited);
		toolBarB.add(tbRoom);
		toolBarB.add(cbRoomsLabel);
		toolBarB.add(cbRooms);

		this.add(toolBar);
		this.add(toolBarB);
		this.add(mapsPane);
	}
	
	/**
	 * Creates the tool bar images.
	 * @param tileType	The identification for the tool to use.
	 * @return
	 */
	public ImageIcon getToolBarImage(int tileType)
	{
		BufferedImage temp = new BufferedImage(ImageBank.FEATUREWIDTH, ImageBank.FEATUREHEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D surface = temp.createGraphics();
		
		if(tileType >= 0)
		{
			surface.drawImage(dataBank.getImages().getTile(ImageBank.TILE_NONE), null, 0, 0);
			
			if(tileType > 0)
				surface.drawImage(dataBank.getImages().getTile(tileType), null, 0, 0);
		}
		else
		{
			switch(tileType)
			{
			case BUTTON_STONE:
				surface.fillRect(0, 0, ImageBank.FEATUREWIDTH, ImageBank.FEATUREHEIGHT);
				break;
			case BUTTON_TELESTATIC:
				surface.setColor(Color.RED);
				surface.drawImage(dataBank.getImages().getTile(ImageBank.TILE_NONE), null, 0, 0);
				surface.drawImage(dataBank.getImages().getTile(ImageBank.TILE_TELEPORT), null, 0, 0);
				surface.drawString("S", 2, ImageBank.FEATUREHEIGHT - 2);
				break;
			case BUTTON_TELELEVEL:
				surface.setColor(Color.RED);
				surface.drawImage(dataBank.getImages().getTile(ImageBank.TILE_NONE), null, 0, 0);
				surface.drawImage(dataBank.getImages().getTile(ImageBank.TILE_TELEPORT), null, 0, 0);
				surface.drawString("L", 2, ImageBank.FEATUREHEIGHT - 2);
				break;
			case BUTTON_TELEDUNG:
				surface.setColor(Color.RED);
				surface.drawImage(dataBank.getImages().getTile(ImageBank.TILE_NONE), null, 0, 0);
				surface.drawImage(dataBank.getImages().getTile(ImageBank.TILE_TELEPORT), null, 0, 0);
				surface.drawString("D", 2, ImageBank.FEATUREHEIGHT - 2);
				break;
			}
		}
		
		surface.dispose();
		return new ImageIcon(temp.getScaledInstance(ImageBank.FEATUREWIDTH, ImageBank.FEATUREHEIGHT, BufferedImage.SCALE_FAST));
	}
	
/*	private ImageIcon getRowHeader(MapLevel mapLevel)
	{
		BufferedImage temp = new BufferedImage(ImageBank.FEATUREWIDTH, (mapLevel.getHeight() * ImageBank.FEATUREHEIGHT), BufferedImage.TYPE_INT_ARGB);
		Graphics2D surface = temp.createGraphics();
		
		for(int y = 0; y < mapLevel.getHeight(); y++)
		{
			surface.drawLine(0, y * ImageBank.FEATUREHEIGHT, ImageBank.FEATUREWIDTH, y * ImageBank.FEATUREHEIGHT);
			surface.drawString("" + ((mapLevel.getHeight() - 1) - y), 0, (y + 1) * ImageBank.FEATUREHEIGHT);
		}
		
		return new ImageIcon(temp);
	}
	
	private ImageIcon getColumnHeader(MapLevel mapLevel)
	{
		BufferedImage temp = new BufferedImage(mapLevel.getWidth() * ImageBank.FEATUREWIDTH, ImageBank.FEATUREHEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D surface = temp.createGraphics();
		
		surface.setColor(Color.BLACK);
		surface.fillRect(0, 0, temp.getWidth(), temp.getHeight());
		surface.setColor(Color.RED);
		
		for(int x = 0; x < mapLevel.getWidth(); x++)
		{
			surface.drawLine(x * ImageBank.FEATUREWIDTH, 0, x * ImageBank.FEATUREWIDTH, ImageBank.FEATUREHEIGHT);
			surface.drawString("" + x, (x * ImageBank.FEATUREWIDTH) + 2, ImageBank.FEATUREHEIGHT - 2);
		}
		
		return new ImageIcon(temp);
	}*/
	
	/**
	 * Creates the maps and tabs.
	 *
	 */
	public void createMaps()
	{
		viewScroll = new JScrollPane[dataBank.getMap().getDepth()];
		viewPanel = new EditorMapViewPane[dataBank.getMap().getDepth()];
		
		for(int i = 0; i < dataBank.getMap().getDepth(); i++)
		{
			viewPanel[i] = new EditorMapViewPane(this, dataBank.getMap().getMapLevel(i), dataBank);
			viewScroll[i] = new JScrollPane(viewPanel[i]);
			mapsPane.addTab("" + i, viewScroll[i]);
		}
	}
	
	/**
	 * Toggles room view on/off.
	 * @param roomView
	 */
	public void toggleRoomView(boolean roomView)
	{
		for(int i = 0; i < viewPanel.length; i++)
		{
			viewPanel[i].toggleRoomView(roomView);
		}
	}
	
	/**
	 * Toggles visited view on/off
	 * @param visitedView
	 */
	public void toggleVisitedView(boolean visitedView)
	{
		for(int i = 0; i < viewPanel.length; i++)
		{
			viewPanel[i].toggleVisitedView(visitedView);
		}
	}
	
	/**
	 * Runs reLoadCurrent view on every map and revalidates the scroll
	 * pane.
	 *
	 */
	public void reloadView()
	{
		for(int i = 0; i < viewPanel.length; i++)
		{
			viewPanel[i].reloadCurrentView();
			viewScroll[i].revalidate();
		}
	}
	
	public void reLoadCurrentView()
	{	
		viewPanel[mapsPane.getSelectedIndex()].reloadCurrentView();
	}
	
	public int getCurrentLevelIndex()
	{
		return mapsPane.getSelectedIndex();
	}
	
	public void changeLevelCount()
	{
		this.remove(mapsPane);
		mapsPane = new JTabbedPane();
		createMaps();
		mapsPane.setPreferredSize(new Dimension(VIEWWIDTH, VIEWHEIGHT));
		mapsPane.addChangeListener(this);
		this.add(mapsPane);
		this.revalidate();
	}
	
	public byte getTileType()
	{
		return activeType;
	}
	
	public int getSelectedRoom()
	{
		return cbRooms.getSelectedIndex();
	}
	
	/**
	 * Updates the squares image on the appropriate map
	 * @param square	Square to be edited.
	 */
	public void updateSquare(MapSquare square)
	{
		viewPanel[square.getZCoordinate()].updateSquare(square);
	}
	
	public void updateRoomsBox()
	{
		// TODO Update to modify model
		toolBarB.remove(cbRooms);
		cbRooms = new JComboBox(dataBank.getMap().getMapLevel(mapsPane.getSelectedIndex()).getRoomNumbers());
		cbRooms.setToolTipText("Select the room to paint");
		cbRooms.addActionListener(this);
		toolBarB.add(cbRooms);
		tbRoom.setText("Room " + cbRooms.getSelectedIndex());
		
		toolBarB.revalidate();
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == tbEmpty)
			activeType = SquareFeature.TYPE_NONE;
		if(e.getSource() == tbWater)
			activeType = SquareFeature.TYPE_WATER;
		if(e.getSource() == tbFog)
			activeType = SquareFeature.TYPE_FOG;
		if(e.getSource() == tbSand)
			activeType = SquareFeature.TYPE_SAND;
		if(e.getSource() == tbAnti)
			activeType = SquareFeature.TYPE_ANTIMAGIC;
		if(e.getSource() == tbStud)
			activeType = SquareFeature.TYPE_STUD;
		if(e.getSource() == tbExting)
			activeType = SquareFeature.TYPE_EXTINGUISH;
		if(e.getSource() == tbPit)
			activeType = SquareFeature.TYPE_PIT;
		if(e.getSource() == tbRot)
			activeType = SquareFeature.TYPE_ROTATOR;
		

		if(e.getSource() == tbTeleS)
			activeType = SquareFeature.TYPE_TELEPORTSTATIC;
		if(e.getSource() == tbTeleL)
			activeType = SquareFeature.TYPE_TELEPORTLEVEL;
		if(e.getSource() == tbTeleD)
			activeType = SquareFeature.TYPE_TELEPORTDUNGEON;
		if(e.getSource() == tbStairsU)
			activeType = SquareFeature.TYPE_STAIRSUP;
		if(e.getSource() == tbStairsD)
			activeType = SquareFeature.TYPE_STAIRSDOWN;
		if(e.getSource() == tbExit)
			activeType = SquareFeature.TYPE_EXIT;
		if(e.getSource() == tbChute)
			activeType = SquareFeature.TYPE_CHUTE;
		
		if(e.getSource() == tbStone)
			activeType = BUTTON_STONE;
		if(e.getSource() == tbWallN)
			activeType = BUTTON_WALLNORTH;
		if(e.getSource() == tbWallE)
			activeType = BUTTON_WALLEAST;
		if(e.getSource() == tbWallS)
			activeType = BUTTON_WALLSOUTH;
		if(e.getSource() == tbWallW)
			activeType = BUTTON_WALLWEST;
		
		if(e.getSource() == tbVisited)
			activeType = BUTTON_VISITED;
		if(e.getSource() == tbRoom)
			activeType = BUTTON_ROOM;
		if(e.getSource() == roomPicker)
			activeType = BUTTON_PICKER;
		
		if(e.getSource() == cbRooms)
		{
			tbRoom.setText("Room " + cbRooms.getSelectedIndex());
			tbRoom.doClick();
		}
		
	}

	public void stateChanged(ChangeEvent e)
	{
		if(e.getSource() == mapsPane)
		{
			updateRoomsBox();
			
			// TODO: Add in code so that the image is update when the
			// tab is accessed, may greatly speed up a lot of updates
			// the only time all will need to be updated are on new/remove
			// levels (?even then?) and loading a whole map
		}
	}
}
