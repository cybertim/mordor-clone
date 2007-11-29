package mordorGame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import mordorData.Map;
import mordorData.MapPane;
import mordorData.Player;
import mordorHelpers.Coord;


public class DungeonMapFrame extends JInternalFrame implements InternalFrameListener, ActionListener
{

	private MapPane[] mapPanes;
	private JScrollPane[] mapScrolls;
	private JTabbedPane mapTabs;
	private Player player;
	private Map map;
	
	DungeonMapFrame(Player nPlayer, Map nMap)
	{
		super("Dungeon Map", true, false, true, true);	
		player = nPlayer;
		map = nMap;
		
		mapPanes = new MapPane[map.getDepth()];
		mapScrolls = new JScrollPane[map.getDepth()];
		
		mapTabs = new JTabbedPane();
		for(int i = 0; i < mapPanes.length; i++)
		{
			mapPanes[i] = new MapPane(map.getMapLevel(i), player);
			mapPanes[i].setDoubleBuffered(true);
			mapScrolls[i] = new JScrollPane(mapPanes[i]);
			mapScrolls[i].setBackground(Color.WHITE);
			mapPanes[i].setViewport(mapScrolls[i].getViewport());
			mapTabs.addTab("" + i, mapScrolls[i]);
			
		}
		mapTabs.setPreferredSize(new Dimension(100, 100));
		mapTabs.setSelectedIndex(player.getCoord().getZ());
		
		add(mapTabs);
	}
	
	public void updateSquare(Coord coords)
	{
		mapPanes[coords.getZ()].updateNonPlayerSquare(coords);
	}
	
	public void updatePlayerSquare(Coord playerCoords)
	{
		mapPanes[playerCoords.getZ()].updatePlayerSquare(playerCoords);
		mapPanes[playerCoords.getZ()].updateCurrentView(playerCoords);
		mapTabs.setSelectedIndex(playerCoords.getZ());
	}

	public void internalFrameActivated(InternalFrameEvent e)
	{
		mapTabs.setSelectedComponent(mapPanes[player.getCoord().getZ()]);
		mapPanes[player.getCoord().getZ()].updatePlayerSquare(player.getCoord());
	}

	public void internalFrameClosed(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void internalFrameClosing(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void internalFrameDeactivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void internalFrameDeiconified(InternalFrameEvent e)
	{
	}

	public void internalFrameIconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void internalFrameOpened(InternalFrameEvent e)
	{
		
	}

	public void actionPerformed(ActionEvent e)
	{
		
	}

}
