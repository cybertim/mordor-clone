package mordorGame;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mordorData.Player;
import mordorEnums.PlayerSkill;

import structures.SkipIter;
import structures.SkipList;

public class HallOfRecords extends JPanel implements ActionListener
{
	private JButton jbDone;
	private JDialog parent;
	
	HallOfRecords(JDialog nParent, SkipList<Player> players)
	{
		parent = nParent;
		
		jbDone = new JButton("Done");
		jbDone.setToolTipText("Exit the hall of records.");
		jbDone.addActionListener(this);
		
		
		GridLayout layout = new GridLayout(3 + PlayerSkill.values().length, 1);
		layout.setVgap(10);
		setLayout(layout);

		Player tPlayer = getMostGold(players.getIterator());
		add(new JLabel(tPlayer.getName() + " has the most gold with " + tPlayer.getTotalGold()));
		tPlayer = getHighestExperience(players.getIterator());
		add(new JLabel(tPlayer.getName() + " has the most experience with " + tPlayer.getTotalExperience()));
		for(PlayerSkill ps : PlayerSkill.values())
		{
			tPlayer = getHighestPlayerSkill(ps, players.getIterator());
			add(new JLabel(tPlayer.getName() + " is the bast at " + ps.name() + "  with " + tPlayer.getPlayerSkill(ps)));
		}
		
		add(jbDone);
	}
	
	private Player getMostGold(SkipIter<Player> tPlayer)
	{
		Player billGates = tPlayer.element();
		
		while(tPlayer.next())
			if(tPlayer.element().getTotalGold() > billGates.getTotalGold())
				billGates = tPlayer.element();
		
		return billGates;
	}
	
	private Player getHighestPlayerSkill(PlayerSkill skill, SkipIter<Player> tPlayer)
	{
		Player elCrafty = tPlayer.element();
		
		while(tPlayer.next())
			if(tPlayer.element().getPlayerSkill(skill) > elCrafty.getPlayerSkill(skill))
				elCrafty = tPlayer.element();
		
		return elCrafty;
	}
	
	private Player getHighestExperience(SkipIter<Player> tPlayer)
	{
		Player merlin = tPlayer.element();
		
		while(tPlayer.next())
			if(tPlayer.element().getTotalExperience() > merlin.getTotalExperience())
				merlin = tPlayer.element();
		
		return merlin;
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == jbDone)
			parent.dispose();
	}

}
