package mordorEditor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import mordorData.DataBank;
import mordorData.Guild;
import mordorData.GuildReference;
import mordorData.Item;
import mordorHelpers.Util;

import structures.SkipIter;

public class EditorItemPanel_GuildsPane extends JPanel implements Scrollable, ActionListener
{
	private Item item;
	private DataBank dataBank;
	private byte[] guildIDs;
	private JCheckBox[] guildNames;
	private JTextField[] guildLevel;
	
	private JCheckBox jbSelectAll;
	
	private short gViewWidth, gViewHeight;
	
	EditorItemPanel_GuildsPane(Item nItem, DataBank nDataBank)
	{
		dataBank = nDataBank;
		gViewWidth = 250;
		gViewHeight = 150;
		
		setupPane();
	}
	
	private void setupPane()
	{
		this.removeAll();
		
		guildIDs = new byte[dataBank.getGuildCount()];
		guildNames = new JCheckBox[dataBank.getGuildCount()];
		guildLevel = new JTextField[dataBank.getGuildCount()];
		
		JPanel checks = new JPanel();
		JPanel fields = new JPanel();
		
		checks.setLayout(new GridLayout(guildIDs.length + 2, 1));
		fields.setLayout(new GridLayout(guildIDs.length + 2, 1));
		
		jbSelectAll = new JCheckBox("Invert");
		jbSelectAll.setToolTipText("Invert Selection");
		jbSelectAll.addActionListener(this);
		checks.add(jbSelectAll);
		fields.add(new JLabel(""));
		checks.add(new JLabel("Allow"));
		fields.add(new JLabel("Lvl"));
		for(byte i = 0; i < guildIDs.length; i++)
		{
			guildNames[i] = new JCheckBox("spam");
			guildLevel[i] = new JTextField(2);
			checks.add(guildNames[i]);
			fields.add(guildLevel[i]);
		}
		
		add(checks);
		add(fields);
	}
	
	/**
	 * Takes an items guild information and updates the panel with it.
	 * @param nItem
	 */
	public void updatePane(Item nItem)
	{
		item = nItem;
		
		SkipIter<Guild> tNode = dataBank.getGuilds().getIterator();
		byte count = 0;
		
		while(tNode.next())
		{
			guildIDs[count] = (byte)tNode.element().getID();
			guildNames[count].setText(tNode.element().getName());
			guildNames[count].setSelected(item.getGuild(tNode.element()) != null);
			if(guildNames[count].isSelected())
				guildLevel[count].setText("" + item.getGuild(tNode.element()).getLevel());
			else
				guildLevel[count].setText("0");
			
			count++;
		}
	}
	
	public boolean updateItem()
	{
		if(!validGuilds())
			return false;
		
		GuildReference tGuild;
		
		// use guildIDs to quickly find the guild
		for(byte i = 0; i < guildIDs.length; i++)
		{
			if(guildNames[i].isSelected())
			{
				tGuild = item.addGuild(dataBank.getGuild(guildIDs[i]));
				if(tGuild == null)
					tGuild = item.getGuild(dataBank.getGuild(guildIDs[i]));
				tGuild.setLevel(getLevel(guildLevel[i].getText().trim(), i));
				guildLevel[i].setText("" + tGuild.getLevel());
			}
		}
		return true;
	}
	
	private short getLevel(String string, byte index)
	{
		if(string == null || string.length() < 1)
			return 0;
		
		try
		{
			return Util.FITSHORT(Short.parseShort(string), (short)0, Short.MAX_VALUE);
		}
		catch(NumberFormatException NFE)
		{
			return 0;
		}
	}
	
	public boolean validGuilds()
	{
		for(byte i = 0; i < guildIDs.length; i++)
		{
			String temp = guildLevel[i].getText();
			if(!guildNames[i].isSelected());
			else if(temp == null || temp.length() < 1)
				guildLevel[i].setText("" + 0);
			else
			{
				try
				{
					guildLevel[i].setText("" + Util.FITSHORT(Short.parseShort(guildLevel[i].getText().trim()), (short)0, Short.MAX_VALUE));
				}
				catch(NumberFormatException NFE)
				{
					JOptionPane.showMessageDialog(this, "Invalid level for " + guildNames[i].getText());
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Rebuilds the pane in case the race count has changed.
	 */
	public void updateLists()
	{
		setupPane();
		updatePane(item);
	}

	public Dimension getPreferredScrollableViewportSize()
	{
		// TODO Auto-generated method stub
		return new Dimension(((this.getWidth() < gViewWidth) ? this.getWidth() : gViewWidth), gViewHeight);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
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

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == jbSelectAll)
		{
			for(int i = 0; i < guildNames.length; i++)
				guildNames[i].setSelected(!guildNames[i].isSelected());
		}
	}

}
