package mordorEditor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import mordorData.DataBank;
import mordorData.MapLevel;
import mordorData.SquareFeature;


public class EditorLevelResizeFrame extends JFrame implements ActionListener 
{
	private JTextField widthField, heightField;
	private JButton done, cancel;
	private MapLevel mapLevel;
	private EditorTrueViewPanel mapView;
	private DataBank dataBank;
	
	EditorLevelResizeFrame(EditorTrueViewPanel nMapView, MapLevel nMapLevel, DataBank nDataBank)
	{
		dataBank = nDataBank;
		mapLevel = nMapLevel;
		mapView = nMapView;
		
		JLabel widthLabel = new JLabel("New Width: ");
		JLabel heightLabel = new JLabel("New Height: ");
		
		widthField = new JTextField("" + mapLevel.getWidth(), 3);
		heightField = new JTextField("" + mapLevel.getHeight(), 3);
		
		done = new JButton("Resize");
		done.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		
		getContentPane().add(widthLabel);
		getContentPane().add(widthField);
		getContentPane().add(heightLabel);
		getContentPane().add(heightField);
		getContentPane().add(done);
		getContentPane().add(cancel);
		
		getContentPane().setLayout(new GridLayout(3, 2));
		
		pack();
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == done)
		{
			byte newWidth = mapLevel.getWidth();
			byte newHeight = mapLevel.getHeight();
			
			try
			{
				newWidth = Byte.parseByte(widthField.getText());
				newHeight = Byte.parseByte(heightField.getText());
			}
			catch(NumberFormatException NFE)
			{
				JOptionPane.showMessageDialog(this, "Invalid input. Must be between 1 and 127");
				return;
			}
			
			if(newWidth > MapLevel.MAXWIDTH || newWidth < 1 || newHeight < 1 || newHeight > MapLevel.MAXHEIGHT)
			{
				JOptionPane.showMessageDialog(this, "Invalid input. Must be between 1 and 127");
			}
			else
			{
				mapLevel.resizeLevel(newWidth, newHeight, dataBank.getImages());
				if(mapLevel.getLevel() == 0 && (dataBank.getMap().getExitSquare().getXCoordinate() >= newWidth || dataBank.getMap().getExitSquare().getYCoordinate() >= newHeight))
				{
					if(mapLevel.getMapSquare(0, 0).getSquareFeatures()[0].getType() == SquareFeature.TYPE_STUD)
						mapLevel.getMapSquare(0, 0).getSquareFeatures()[1].setType(SquareFeature.TYPE_STUD);

					mapLevel.getMapSquare(0, 0).getSquareFeatures()[0].setType(SquareFeature.TYPE_EXIT);
					dataBank.getMap().setExitSquare(mapLevel.getMapSquare(0, 0));
				}
				mapView.reloadView();
				this.dispose();
			}
		}
		else if(e.getSource() == cancel)
		{
			this.dispose();
		}

	}

}
