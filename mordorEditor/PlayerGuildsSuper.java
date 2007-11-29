package mordorEditor;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PlayerGuildsSuper extends JPanel
{
	private PlayerGuilds guildsPane;
	
	PlayerGuildsSuper(PlayerPanel nParent)
	{
		guildsPane = new PlayerGuilds(nParent);
		JScrollPane sPane = new JScrollPane(guildsPane);
		add(sPane);
	}
	
	public boolean updatePanel()
	{
		return guildsPane.updatePanel();
	}
	
	public boolean updateGuilds(boolean alreadyValidated)
	{
		return guildsPane.updateGuilds(alreadyValidated);
	}
	
	public boolean validateGuilds()
	{
		return guildsPane.validateGuilds();
	}
	
	public void updateLists()
	{
		guildsPane.updateLists();
	}
}
