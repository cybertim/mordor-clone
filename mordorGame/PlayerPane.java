package mordorGame;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import mordorData.Player;
import mordorData.Race;
import mordorEnums.PlayerState;
import mordorEnums.Stats;


public class PlayerPane extends JPanel
{
	private JLabel jlSpecies, jlGuild, jlTeam;
	private JLabel jlAge, jlSpells, jlSpellLevel;
	private JLabel jlHits, jlAD, jlXP, jlGold;
	private JLabel[] jlStats;
	PlayerPane()
	{
		jlSpecies = new JLabel();
		jlGuild = new JLabel();
		jlTeam = new JLabel();
		jlAge = new JLabel();
		jlSpells = new JLabel();
		jlSpellLevel = new JLabel();
		jlHits = new JLabel();
		jlAD = new JLabel();
		jlXP = new JLabel();
		jlGold = new JLabel();
		
		jlSpecies.setHorizontalAlignment(SwingConstants.CENTER);
		jlGuild.setHorizontalAlignment(SwingConstants.CENTER);
		jlTeam.setHorizontalAlignment(SwingConstants.CENTER);
		
		add(jlSpecies);
		add(jlGuild);
		add(jlTeam);
		
		JPanel ageBar = new JPanel();
		JPanel spellsBar = new JPanel();
		JPanel sLevelBar = new JPanel();
		JPanel hitsBar = new JPanel();
		JPanel adBar = new JPanel();
		JPanel xpBar = new JPanel();
		JPanel goldBar = new JPanel();
		
		ageBar.setLayout(new BorderLayout());
		spellsBar.setLayout(new BorderLayout());
		sLevelBar.setLayout(new BorderLayout());
		hitsBar.setLayout(new BorderLayout());
		adBar.setLayout(new BorderLayout());
		xpBar.setLayout(new BorderLayout());
		goldBar.setLayout(new BorderLayout());
		
		ageBar.add(new JLabel("Age"), BorderLayout.WEST);
		ageBar.add(jlAge, BorderLayout.EAST);
		spellsBar.add(new JLabel("Spells"), BorderLayout.WEST);
		spellsBar.add(jlSpells, BorderLayout.EAST);
		sLevelBar.add(new JLabel("Spell Level"), BorderLayout.WEST);
		sLevelBar.add(jlSpellLevel, BorderLayout.EAST);
		hitsBar.add(new JLabel("Hits"), BorderLayout.WEST);
		hitsBar.add(jlHits, BorderLayout.EAST);
		adBar.add(new JLabel("Att/Def"), BorderLayout.WEST);
		adBar.add(jlAD, BorderLayout.EAST);
		xpBar.add(new JLabel("Experience"), BorderLayout.WEST);
		xpBar.add(jlXP, BorderLayout.EAST);
		goldBar.add(new JLabel("Gold"), BorderLayout.WEST);
		goldBar.add(jlGold, BorderLayout.EAST);
		
		add(ageBar);
		add(new JPanel());
		add(spellsBar);
		add(sLevelBar);
		add(new JPanel());
		add(hitsBar);
		add(adBar);
		add(xpBar);
		add(goldBar);
		add(new JPanel());
		
		jlStats = new JLabel[Stats.values().length];
		for(Stats st : Stats.values())
		{
			jlStats[st.value()] = new JLabel("0");
			JPanel tPane = new JPanel();
			tPane.setLayout(new BorderLayout());
			tPane.add(new JLabel(st.name()), BorderLayout.WEST);
			tPane.add(jlStats[st.value()], BorderLayout.EAST);
			add(tPane);
		}
		
		setLayout(new GridLayout(10 + 3 + Stats.values().length, 1));
	}
	
	public void updatePanel(Player player, boolean onATeam)
	{
		String speciesString = (player.isInState(PlayerState.Female)) ? "Female" : "Male";
		speciesString += " " + player.getRace().getName() + " (" + player.getAlignment().name() + ")";
		
		jlSpecies.setText(speciesString);
		jlGuild.setText(player.getActiveGuild().getGuild().getName() + " (" + player.getActiveGuild().getLevel() +")");
		if(onATeam)
			jlTeam.setText("You are on a team.");
		else
			jlTeam.setText("You are alone.");
		jlAge.setText("" + (player.getAge() / 365));
		jlSpells.setText(player.getMP() + "/" + player.getMaxMP());
		jlSpellLevel.setText("" + player.getActiveGuild().getSpellLevel());
		jlHits.setText(player.getHP() + "/" + player.getMaxHP());
		jlAD.setText(player.getAttack() + "/" + player.getDefense());
		jlXP.setText("" + player.getActiveGuild().getExperience());
		jlGold.setText("" + player.getGoldOnHand());
		
		for(Stats st : Stats.values())
			jlStats[st.value()].setText("" + player.getTotalStat(st));
	}
}
