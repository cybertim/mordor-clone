package mordorGame;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import structures.SkipIter;
import structures.SkipList;

import mordorData.Player;
import mordorEnums.PlayerState;

public class PlayerList extends JPanel 
{
	PlayerList(SkipList<Player> players)
	{
		//QuadNode<Player> tPlayer = players.firstNode();
		SkipIter<Player> tPlayer = players.getIterator();
		if(tPlayer == null)
			add(new JLabel("No players."));
		else
		{
			setLayout(new GridLayout(players.getSize(), 1));
			while(tPlayer.next())
				createNewPane(tPlayer.element());
		}
		
	}
	
	private void createNewPane(Player newPlayer)
	{
		JPanel newPane = new JPanel();
		newPane.setLayout(new GridLayout(3, 1));
		newPane.add(new JLabel(newPlayer.getName()));
		newPane.add(new JLabel("Current guild: " + newPlayer.getActiveGuild().getGuild().getName() + " (" + newPlayer.getActiveGuild().getLevel() + ")"));
		String locString;
		if(newPlayer.isInState(PlayerState.InDejenol))
			locString = "Is in Dejenol.";
		else
			locString = "(" + newPlayer.getCoord().getX() + ", " + newPlayer.getCoord().getY() + ", " + newPlayer.getCoord().getZ() + ")";
		
		locString += " HP/MP " + newPlayer.getHP() + "/" + newPlayer.getMP();
		newPane.add(new JLabel(locString));
		
		add(newPane);
	}
}
