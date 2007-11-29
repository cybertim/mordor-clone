package mordorEditor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mordorData.DataBank;
import mordorData.MapLevel;


public class EditorLevelNewDialog extends JFrame implements ActionListener 
{
	private EditorTrueViewPanel mapView;
	private DataBank dataBank;
	
	private JButton done, cancel;
	private JTextField widthField, heightField, depthField;
	
	EditorLevelNewDialog(EditorTrueViewPanel nMapView, DataBank nDataBank)
	{
		mapView = nMapView;
		dataBank = nDataBank;
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4, 2));
		
		JLabel widthLabel = new JLabel("Width: ");
		JLabel heightLabel = new JLabel("Height: ");
		JLabel depthLabel = new JLabel("Depth: ");
		widthLabel.setToolTipText("The width of the new level.");
		heightLabel.setToolTipText("The height of the new level.");
		depthLabel.setToolTipText("What depth the new levels hould be at.");
		
		widthField = new JTextField("5", 3);
		heightField = new JTextField("5", 3);
		depthField = new JTextField("" + dataBank.getMap().getDepth(), 3);
		
		done = new JButton("Create");
		cancel = new JButton("Cancel");
		
		done.addActionListener(this);
		cancel.addActionListener(this);
		
		panel.add(widthLabel);
		panel.add(widthField);
		panel.add(heightLabel);
		panel.add(heightField);
		panel.add(depthLabel);
		panel.add(depthField);
		panel.add(done);
		panel.add(cancel);
		
		add(panel);
		pack();
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == done)
		{
			byte newWidth = 5;
			byte newHeight = 5;
			byte newDepth = dataBank.getMap().getDepth();
			
			try
			{
				newWidth = Byte.parseByte(widthField.getText());
				newHeight = Byte.parseByte(heightField.getText());
				newDepth = Byte.parseByte(depthField.getText());
			}
			catch(NumberFormatException NFE)
			{
				JOptionPane.showMessageDialog(this, "Not a valid integer.");
				return;
			}
			
			if(newWidth < 1 || newWidth > MapLevel.MAXWIDTH )
			{
				JOptionPane.showMessageDialog(this, "Width must be between 1 and " + MapLevel.MAXWIDTH);
				return;
			}
			
			if(newHeight < 1 || newHeight > MapLevel.MAXHEIGHT)
			{
				JOptionPane.showMessageDialog(this, "Height must be between 1 and " + MapLevel.MAXHEIGHT);
				return;
			}
			
			if(newDepth < 0 || newDepth > dataBank.getMap().getDepth())
			{
				JOptionPane.showMessageDialog(this, "Depth must be between 0 and " + dataBank.getMap().getDepth());
				return;
			}
			
			dataBank.getMap().addLevel(newDepth, newWidth, newHeight);
			mapView.changeLevelCount();
			dispose();
		}
		else if(e.getSource() == cancel)
		{
			dispose();
		}
	}

}
