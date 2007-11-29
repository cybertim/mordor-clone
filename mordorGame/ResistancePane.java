package mordorGame;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import mordorData.Player;
import mordorEnums.Resistance;

/**
 * Panel for showing resistances in info window.
 * @author August Junkala (Sept. 16, 2007)
 *
 */
public class ResistancePane extends JPanel
{
	private JLabel[] jlResistance;
	
	ResistancePane()
	{
		jlResistance = new JLabel[Resistance.values().length];
		
		setLayout(new GridLayout(jlResistance.length + 2, 1));
		
		add(new JLabel("Current Resistances"));
		add(new JLabel(""));
		
		for(Resistance re : Resistance.values())
		{
			jlResistance[re.value()] = new JLabel("-");
			JPanel rPane = new JPanel();
			rPane.setLayout(new BorderLayout());
			rPane.add(new JLabel(re.name()), BorderLayout.WEST);
			rPane.add(jlResistance[re.value()], BorderLayout.EAST);
			add(rPane);
		}
	}
	
	public void updatePanel(Player player)
	{
		for(Resistance re : Resistance.values())
		{
			byte tr = player.getTotalResistance(re);
			if(tr == 0)
				jlResistance[re.value()].setText("-");
			else
				jlResistance[re.value()].setText("" + tr);
		}
	}
}
