package mordorGame;
import javax.swing.JPanel;

import mordorData.Monster;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MonsterPanel extends JPanel
{
	private boolean slotEmpty;
	private Monster monsters[];
	private int monsterCount;
	private int xSize, ySize;
	
	public MonsterPanel(int nXSize, int nYSize)
	{
		xSize = nXSize;
		ySize = nYSize;
	}
	
	public void monsterKilled(Monster dMonster)
	{
		for(int i = 0; i < monsters.length; i++)
		{
			if(monsters[i].equals(dMonster))
			{
				monsters[i] = null;
				monsterCount -= 1;
			}
		}
	}
	
	public void addMonsters(Monster nMonsters[])
	{
		if(nMonsters == null)
		{
			slotEmpty = true;
			monsterCount = 0;
		}
		else
		{
			monsters = nMonsters;
			monsterCount = monsters.length;
		}
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		
		// use xSize/ySize so images are drawn right.
		// draw monster
		
		// draw monster count
	}
}
