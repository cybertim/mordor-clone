package mordorGame;

import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;

import mordorData.DataBank;
import mordorData.ItemInstance;
import mordorData.MonsterInstance;
import mordorData.Player;

public class InformationPanel extends JInternalFrame
{
	private PlayerPane playerPane;
	private BuffersPane buffersPane;
	private ResistancePane resistPane;
	private MiscPane miscPane;
	private JTabbedPane tabs;
	// Look, misc, resist, char, guild
	
	InformationPanel(String title, boolean first, boolean second, boolean third, boolean fourth)
	{
		super(title, first, second, third, fourth);
		tabs = new JTabbedPane();
		
		playerPane = new PlayerPane();
		buffersPane = new BuffersPane();
		resistPane = new ResistancePane();
		miscPane = new MiscPane();
		
		tabs.addTab("Stats", playerPane);
		tabs.addTab("Resist.", resistPane);
		tabs.addTab("Buffers", buffersPane);
		tabs.addTab("Misc.", miscPane);
		
		add(tabs);
	}
	
	public void showMonster(DataBank dataBank, MonsterInstance monster, byte from)
	{
		miscPane.showMonster(dataBank, monster, from);
		tabs.setSelectedComponent(miscPane);
		// TODO refocus on misc pane
	}
	
	public void showItem(DataBank dataBank, ItemInstance item)
	{
		miscPane.showItem(item, dataBank);
		tabs.setSelectedComponent(miscPane);
		// TODO refocus on misc pane
	}
	
	public void updatePanes(Player player)
	{
		playerPane.updatePanel(player, false); // TODO Teams
		buffersPane.updatePanel(player);
		resistPane.updatePanel(player);
		miscPane.updatePanel();
	}
}
