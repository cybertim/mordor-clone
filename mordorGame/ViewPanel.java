package mordorGame;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import mordorData.DataBank;
import mordorData.ImageBank;
import mordorData.Player;

public class ViewPanel extends JPanel
{
	
	private Player player;
	private DataBank dataBank;
	
	private static final int L2OFFSET = 16;
	private static final int BUFFERWIDTH = ImageBank.IMAGEWIDTH + (L2OFFSET * 2); 
	private static final int BUFFERHEIGHT = ImageBank.IMAGEHEIGHT + (L2OFFSET * 2);
	
	private ViewMatrix viewMatrix;
	
	ViewPanel(Player nPlayer, DataBank nDataBank)
	{
		player = nPlayer;
		dataBank = nDataBank;
		
		viewMatrix = new ViewMatrix(player, dataBank, ImageBank.IMAGEWIDTH, ImageBank.IMAGEHEIGHT);
	}
	
	public void paintComponent(Graphics g)
	{	
		Graphics2D g2 = (Graphics2D)g;
		
		BufferedImage backBuffer = new BufferedImage(BUFFERWIDTH, BUFFERHEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D backBufferSurface = backBuffer.createGraphics();
		
		backBufferSurface.drawImage(viewMatrix.getView(), null, 0, 0);
		
		g2.drawImage(viewMatrix.getView(), null, 0, 0);
	}
	
	public void updateView()
	{
		viewMatrix.updateMatrix();
		repaint();
	}
}
