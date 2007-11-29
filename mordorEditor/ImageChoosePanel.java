package mordorEditor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import mordorData.DataBank;
import mordorData.ImageBank;


public class ImageChoosePanel extends JPanel implements Scrollable, ActionListener
{
	public static final byte IMAGECOLS = 4;
	
	private DataBank dataBank;
	private ImageChooser parent;
	
	private JButton[] monImages;
	
	ImageChoosePanel(DataBank newDataBank, ImageChooser newParent)
	{
		dataBank = newDataBank;
		parent = newParent;
		
		GridLayout newLayout = null;
		
		if(ImageBank.MONSTERFILES.length < IMAGECOLS)
			newLayout = new GridLayout(1, ImageBank.MONSTERFILES.length);
		else
			newLayout = new GridLayout(ImageBank.MONSTERFILES.length / IMAGECOLS, IMAGECOLS);
		
		setLayout(newLayout);
		
		monImages = new JButton[ImageBank.MONSTERFILES.length];
		
		for(int i = 0; i < ImageBank.MONSTERFILES.length; i++)
		{
			monImages[i] = new JButton(new ImageIcon(dataBank.getImages().getMonsterImage((short)i).getScaledInstance(ImageBank.MONSTERIMAGESIZE, ImageBank.MONSTERIMAGESIZE, 0)));
			monImages[i].addActionListener(this);
			add(monImages[i]);
		}
	}

	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		// TODO Auto-generated method stub
		return ImageBank.MONSTERIMAGESIZE + 16;
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

	public void actionPerformed(ActionEvent e)
	{
		for(short i = 0; i < monImages.length; i++)
		{
			if(e.getSource() == monImages[i])
				parent.imageChosen(i);
		}
	}

}
