package mordorEditor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import mordorData.DataBank;
import mordorData.MapSquare;
import mordorData.SquareFeature;


public class EditorMapDestDialog extends JFrame implements ActionListener 
{
	private JButton done, cancel;
	private JTextField xField, yField, zField;
	
	private EditorTrueViewPanel parent;
	private MapSquare square;
	private DataBank dataBank;
	byte type;
	
	EditorMapDestDialog(EditorTrueViewPanel nParent, MapSquare nSquare, DataBank nDataBank, byte nType)
	{
		parent = nParent;
		square = nSquare;
		dataBank = nDataBank;
		type = nType;
		
		xField = new JTextField("" + square.getXCoordinate(), 3);
		yField = new JTextField("" + square.getYCoordinate(), 3);
		zField = new JTextField("" + square.getZCoordinate(), 3);
		
		done = new JButton("Create");
		cancel = new JButton("Cancel");
		
		done.addActionListener(this);
		cancel.addActionListener(this);
		
		setLayout(new GridLayout(4, 2));
		add(new JLabel("X destination: "));
		add(xField);
		add(new JLabel("Y destination: "));
		add(yField);
		add(new JLabel("Level: "));
		add(zField);
		add(done);
		add(cancel);
		
		pack();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == done)
		{
			byte x = (byte)(square.getXCoordinate() + 1);
			byte y = square.getYCoordinate();
			byte z = square.getZCoordinate();
			
			try
			{
				x = Byte.parseByte(xField.getText());
				y = Byte.parseByte(yField.getText());
				z = Byte.parseByte(zField.getText());
			}
			catch(NumberFormatException NFE)
			{
				JOptionPane.showMessageDialog(this, "Not a valid integer.");
				return;
			}
			
			if(x < 0 || x >= dataBank.getMap().getMapLevel(square.getZCoordinate()).getWidth())
			{
				JOptionPane.showMessageDialog(this, "X must be between 0 and " + (dataBank.getMap().getMapLevel(square.getZCoordinate()).getWidth() - 1));
				return;
			}
			
			if(y < 0 || y >= dataBank.getMap().getMapLevel(square.getZCoordinate()).getHeight())
			{
				JOptionPane.showMessageDialog(this, "Z must be between 0 and " + (dataBank.getMap().getMapLevel(square.getZCoordinate()).getHeight() - 1));
				return;
			}
			
			if(type == SquareFeature.TYPE_TELEPORTSTATIC && (z < 0 || z > dataBank.getMap().getDepth()))
			{
				JOptionPane.showMessageDialog(this, "Depth must be between 0 and " + dataBank.getMap().getDepth());
				return;
			}
			else if(type == SquareFeature.TYPE_CHUTE && (z <= square.getZCoordinate() || z > dataBank.getMap().getDepth()))
			{
				JOptionPane.showMessageDialog(this, "Depth must be between " + (square.getZCoordinate() + 1) + " and " + dataBank.getMap().getDepth());
				return;
			}
			
			if(type == SquareFeature.TYPE_TELEPORTSTATIC)
			{
				if(dataBank.getMap().getMapSquare(x, y, z) == null || dataBank.getMap().getMapSquare(x, y, z) == square)
				{
					JOptionPane.showMessageDialog(this, "Invalid destination square.");
					return;
				}
			}
			else if(type == SquareFeature.TYPE_CHUTE)
			{
				if(dataBank.getMap().getMapSquare(x, y, z) == null || dataBank.getMap().getMapSquare(x, y, z).isSolidRock())
				{
					JOptionPane.showMessageDialog(this, "Invalid destination square.");
					return;
				}
			}
			
			if(square.getSquareFeatures()[0].getType() == SquareFeature.TYPE_NONE)
			{
				square.getSquareFeatures()[0].setType(parent.getTileType());
				square.getSquareFeatures()[0].setDestX(x);
				square.getSquareFeatures()[0].setDestY(y);
				square.getSquareFeatures()[0].setDestZ(z);
			}
			else
			{
				square.getSquareFeatures()[1].setType(parent.getTileType());
				square.getSquareFeatures()[1].setDestX(x);
				square.getSquareFeatures()[1].setDestY(y);
				square.getSquareFeatures()[1].setDestZ(z);
			}

			parent.updateSquare(square);
			dispose();
		}
		else if(e.getSource() == cancel)
		{
			dispose();
		}
	}

}
