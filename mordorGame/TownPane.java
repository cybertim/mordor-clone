package mordorGame;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import mordorData.DataBank;
import mordorData.ImageBank;

public class TownPane extends JPanel implements ActionListener 
{
	private JButton jbDungeon, jbMorgue, jbExit, jbSeer, jbGuilds, jbStore, jbConfinement, jbBank;
	private Mordor parent;
	
	TownPane(Mordor nParent, DataBank dataBank)
	{
		parent = nParent;
		
		jbDungeon = new JButton("Dungeon", new ImageIcon(dataBank.getImages().getTownImage(ImageBank.TOWNICON_DUNGEON)));
		jbMorgue = new JButton("Morgue", new ImageIcon(dataBank.getImages().getTownImage(ImageBank.TOWNICON_MORGUE)));
		jbExit = new JButton("Exit Dejenol", new ImageIcon(dataBank.getImages().getTownImage(ImageBank.TOWNICON_EXIT)));
		jbSeer = new JButton("Seer", new ImageIcon(dataBank.getImages().getTownImage(ImageBank.TOWNICON_SEER)));
		jbGuilds = new JButton("Guilds", new ImageIcon(dataBank.getImages().getTownImage(ImageBank.TOWNICON_GUILDS)));
		jbStore = new JButton("Store", new ImageIcon(dataBank.getImages().getTownImage(ImageBank.TOWNICON_STORE)));
		jbConfinement = new JButton("Confinement", new ImageIcon(dataBank.getImages().getTownImage(ImageBank.TOWNICON_CONFINEMENT)));
		jbBank = new JButton("Bank", new ImageIcon(dataBank.getImages().getTownImage(ImageBank.TOWNICON_BANK)));
		
		jbDungeon.setToolTipText("Enter Dungeon");
		jbMorgue.setToolTipText("Visit Morgue");
		jbExit.setToolTipText("Exit Dejenol");
		jbSeer.setToolTipText("See Seer");
		jbGuilds.setToolTipText("Visit Guilds");
		jbStore.setToolTipText("Shop at the store.");
		jbConfinement.setToolTipText("Peruse confinement");
		jbBank.setToolTipText("Visit the bank");
		
		jbDungeon.addActionListener(this);
		jbMorgue.addActionListener(this);
		jbExit.addActionListener(this);
		jbSeer.addActionListener(this);
		jbGuilds.addActionListener(this);
		jbStore.addActionListener(this);
		jbConfinement.addActionListener(this);
		jbBank.addActionListener(this);
		
		setLayout(new GridLayout(2, 4));

		add(jbStore);
		add(jbMorgue);
		add(jbGuilds);
		add(jbDungeon);
		add(jbConfinement);
		add(jbSeer);
		add(jbBank);
		add(jbExit);
	}

	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		if(e.getSource() == jbStore)
		{
			parent.enterStore();
		}
		else if(e.getSource() == jbMorgue)
		{
			
		}
		else if(e.getSource() == jbGuilds)
		{
			
		}
		else if(e.getSource() == jbDungeon)
			parent.enterDungeon();
		else if(e.getSource() == jbConfinement)
		{
			
		}
		else if(e.getSource() == jbSeer)
		{
			
		}
		else if(e.getSource() == jbBank)
		{
			parent.enterBank();
		}
		else if(e.getSource() == jbExit)
			parent.exitMordorToIntro();
	}

}
