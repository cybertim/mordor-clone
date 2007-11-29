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

import structures.QuadNode;
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

		Player tPlayer = getMostGold(players.firstNode());
		add(new JLabel(tPlayer.getName() + " has the most gold with " + tPlayer.getTotalGold()));
		tPlayer = getHighestExperience(players.firstNode());
		add(new JLabel(tPlayer.getName() + " has the most experience with " + tPlayer.getTotalExperience()));
		for(PlayerSkill ps : PlayerSkill.values())
		{
			tPlayer = getHighestPlayerSkill(ps, players.firstNode());
			add(new JLabel(tPlayer.getName() + " is the bast at " + ps.name() + "  with " + tPlayer.getPlayerSkill(ps)));
		}
		
		add(jbDone);
	}
	
	private Player getMostGold(QuadNode<Player> tPlayer)
	{
		Player billGates = tPlayer.getElement();
		
		while(tPlayer.getRight() != null)
		{
			if(tPlayer.getElement().getTotalGold() > billGates.getTotalGold())
				billGates = tPlayer.getElement();
			
			tPlayer = tPlayer.getRight();
		}
		
		return billGates;
	}
	
	private Player getHighestPlayerSkill(PlayerSkill skill, QuadNode<Player> tPlayer)
	{
		Player elCrafty = tPlayer.getElement();
		
		while(tPlayer.getRight() != null)
		{
			if(tPlayer.getElement().getPlayerSkill(skill) > elCrafty.getPlayerSkill(skill))
				elCrafty = tPlayer.getElement();
			
			tPlayer = tPlayer.getRight();
		}
		
		return elCrafty;
	}
	
	private Player getHighestExperience(QuadNode<Player> tPlayer)
	{
		Player merlin = tPlayer.getElement();
		
		while(tPlayer.getRight() != null)
		{
			if(tPlayer.getElement().getTotalExperience() > merlin.getTotalExperience())
				merlin = tPlayer.getElement();
			
			tPlayer = tPlayer.getRight();
		}
		
		return merlin;
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == jbDone)
			parent.dispose();
	}

}
