package mordorGame;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import mordorData.Player;
import mordorData.SpellReference;

public class BuffersPane extends JPanel
{
	private JLabel[] jlBuffers;
	
	BuffersPane()
	{
		jlBuffers = new JLabel[Player.MAXSPELLBUFFERS];
		
		setLayout(new GridLayout(Player.MAXSPELLBUFFERS, 1));
		for(int i = 0; i < jlBuffers.length; i++)
		{
			jlBuffers[i] = new JLabel(i + ". (Nothing)");
			add(jlBuffers[i]);
		}
	}
	
	public void updatePanel(Player player)
	{
		for(byte i = 0; i < jlBuffers.length; i++)
		{
			SpellReference tSpell = player.getSpellBuffers(i);
			if(tSpell != null)
				jlBuffers[i].setText(i + ". " + tSpell.getSpell().getName());
			else
				jlBuffers[i].setText(i + ". (Nothing)");
		}
	}

}
