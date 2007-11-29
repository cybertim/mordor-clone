package mordorEditor;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import mordorData.DataBank;
import mordorData.ImageBank;


public class ImageChooser extends JFrame
{
	private DataBank dataBank;
	private EditorMonsterPanel parent;
	
	ImageChooser(DataBank newDataBank, EditorMonsterPanel newParent)
	{
		dataBank = newDataBank;
		parent = newParent;
		
		ImageChoosePanel temp = new ImageChoosePanel(dataBank, this);
		add(new JScrollPane(temp));
		
		int cols = (ImageBank.MONSTERFILES.length > (ImageChoosePanel.IMAGECOLS + 1)) ? (ImageChoosePanel.IMAGECOLS + 1) : ImageBank.MONSTERFILES.length;
		int rows = ImageBank.MONSTERFILES.length / ImageChoosePanel.IMAGECOLS;
		rows = (rows > 5) ? 5 : rows;
		
		setSize((cols * (ImageBank.MONSTERIMAGESIZE + 13)) + 14 , (rows  * (ImageBank.MONSTERIMAGESIZE + 16)) + 10);		
	}
	
	public void imageChosen(short imageID)
	{
		parent.setImageID(imageID);
		this.dispose();
	}
}
