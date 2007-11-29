package mordorGame;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mordorData.DataBank;
import mordorData.Guild;
import mordorData.Player;
import mordorData.Race;
import mordorEnums.Alignment;
import mordorEnums.PlayerState;
import mordorEnums.Stats;
import mordorHelpers.Util;

public class NewPlayer extends JPanel implements ActionListener, ChangeListener
{
	private static final String[] SEXES = {"Male", "Female"};
	private static final byte MALE = 0;
	private static final byte FEMALE = 1;
	
	private StartingScreen parent;
	private DataBank dataBank;
	private JFrame frame;
	private JButton jbCancel, jbDone, jbRaceStats, jbGuildStats, jbTutorial;
	private JTextField tfName;
	private JLabel jlStatsLeft;
	private JSpinner[] jsStats;
	private SpinnerNumberModel[] jsmStats;
	private JComboBox jcRace, jcSex, jcAlignment;
	private JLabel[] jlGuilds;
	
	private boolean updatingStats;
	private boolean invalidRace;
	private Race selRace;

	private static final int DEFAULTSTATVAL = 8;
	private static final int TOTALSTATPOINTS = (Stats.values().length * (DEFAULTSTATVAL + 2)) - 1; 
	private int sPointsLeft = TOTALSTATPOINTS;
	
	NewPlayer(StartingScreen nParent, JFrame nFrame, DataBank nDataBank)
	{
		parent = nParent;
		dataBank = nDataBank;
		frame = nFrame;
		
		invalidRace = false;
		
		JPanel basicsPane = new JPanel();
		JPanel statsPane = new JPanel();
		JPanel guildsPane = new JPanel();
		JPanel mainPane = new JPanel();
		JPanel buttonBar = new JPanel();
		
		jbCancel = new JButton("Exit");
		jbDone = new JButton("Save Character");
		jbRaceStats = new JButton("Race Stats");
		jbGuildStats = new JButton("Guild Stats");
		jbTutorial = new JButton("Tutorial");
		
		jbCancel.setToolTipText("Exit without creating the player.");
		jbDone.setToolTipText("Save this character and exit.");
		jbRaceStats.setToolTipText("View the race stats.");
		jbGuildStats.setToolTipText("View the guild stat requirements.");
		jbTutorial.setToolTipText("View the tutorial.");
		
		jbCancel.addActionListener(this);
		jbDone.addActionListener(this);
		jbRaceStats.addActionListener(this);
		jbGuildStats.addActionListener(this);
		jbTutorial.addActionListener(this);
		
		buttonBar.add(jbDone);
		buttonBar.add(jbTutorial);
		buttonBar.add(jbCancel);
		
		JPanel basicsButtons = new JPanel();
		basicsButtons.add(jbRaceStats);
		basicsButtons.add(jbGuildStats);
		
		tfName = new JTextField(8);
		jcRace = new JComboBox(dataBank.getRaceNames());
		jcSex = new JComboBox(SEXES);
		jcAlignment = new JComboBox();
		
		tfName.setToolTipText("Enter the player's name.");
		jcRace.setToolTipText("Select player's race.");
		jcSex.setToolTipText("Select player's sex.");
		jcAlignment.setToolTipText("Select player's alignment.");
		tfName.addActionListener(this);
		
		tfName.addActionListener(this);
		jcRace.addActionListener(this);
		jcAlignment.addActionListener(this);
		
		basicsPane.setLayout(new GridLayout(9, 1));
		basicsPane.add(new JLabel("Name:"));
		basicsPane.add(tfName);
		basicsPane.add(new JLabel("Race:"));
		basicsPane.add(jcRace);
		basicsPane.add(new JLabel("Sex:"));
		basicsPane.add(jcSex);
		basicsPane.add(new JLabel("Alignment:"));
		basicsPane.add(jcAlignment);
		basicsPane.add(basicsButtons);
		
		JPanel statsPaneSuper = new JPanel();
		statsPaneSuper.setLayout(new BorderLayout());
		statsPaneSuper.add(new JLabel("Stats"), BorderLayout.NORTH);
		
		statsPane.setLayout(new GridLayout(Stats.values().length + 1, 1));
		
		jlStatsLeft = new JLabel();
		
		jsStats = new JSpinner[Stats.values().length];
		jsmStats = new SpinnerNumberModel[Stats.values().length];
		for(Stats st : Stats.values())
		{
			JPanel nPane = new JPanel();
			jsmStats[st.value()] = new SpinnerNumberModel(DEFAULTSTATVAL, Stats.DEFAULTMINSTAT, DEFAULTSTATVAL + 2, 1);
			jsStats[st.value()] = new JSpinner(jsmStats[st.value()]);
			
			jsStats[st.value()].addChangeListener(this);
			
			nPane.add(new JLabel(st.name()));
			nPane.add(jsStats[st.value()]);
			
			sPointsLeft -= DEFAULTSTATVAL;
			
			statsPane.add(nPane);
		}
		
		jlStatsLeft.setText("Stats left: " + sPointsLeft);
		statsPane.add(jlStatsLeft);
		
		statsPaneSuper.add(statsPane, BorderLayout.CENTER);
		
		guildsPane.add(new JLabel("Guilds Allowed"));
		String[] guildNames = dataBank.getGuildNames();
		jlGuilds = new JLabel[guildNames.length];
		guildsPane.setLayout(new GridLayout(guildNames.length + 1, 1));
		for(int i = 0; i < guildNames.length; i++)
		{
			jlGuilds[i] = new JLabel(guildNames[i]);
			guildsPane.add(jlGuilds[i]);
		}
		
		mainPane.add(basicsPane);
		mainPane.add(statsPaneSuper);
		mainPane.add(guildsPane);
		
		setLayout(new BorderLayout());
		add(mainPane, BorderLayout.CENTER);
		add(buttonBar, BorderLayout.SOUTH);
		
		changeRace();
	}
	
	/**
	 * Takes the information in the panel and creates a new player.
	 * @return Player the new player
	 */
	private Player createPlayer()
	{
		if(tfName.getText().trim().length() < 1)
		{
			JOptionPane.showMessageDialog(this, "No player name.");
			return null;
		}
		else if(dataBank.getPlayer(tfName.getText().trim()) != null)
		{
			JOptionPane.showMessageDialog(this, "Player already exists.");
			return null;
		}
		else if(invalidRace)
		{
			JOptionPane.showMessageDialog(this, "Invalid race.");
			return null;
		}
		else if(sPointsLeft < 0)
		{
			JOptionPane.showMessageDialog(this, "Too many stat points used.");
			return null;
		}
		Player newPlayer = dataBank.newPlayer();
		
		newPlayer.setName(tfName.getText().trim());
		newPlayer.setRace(dataBank.getRace((String)jcRace.getSelectedItem()));
		newPlayer.setAlignment((Alignment)jcAlignment.getSelectedItem());
		newPlayer.setState(PlayerState.Female, jcSex.getSelectedIndex() == FEMALE, Player.STATENATURAL);
		
		for(Stats st : Stats.values())
			newPlayer.setNaturalStat(st, ((Integer)jsStats[st.value()].getValue()).byteValue());
		
		dataBank.getDefaultGuild().joinGuild(newPlayer);
		
		return newPlayer;
	}
	
	/**
	 * The race has changed.
	 */
	private void changeRace()
	{		
		int statsRequirements = 0;
		if(selRace == null)
			selRace = dataBank.getRace((String)jcRace.getSelectedItem());
		for(Stats st : Stats.values())
			statsRequirements += selRace.getBaseStat(st, true);
		
		if(statsRequirements > TOTALSTATPOINTS)
		{
			JOptionPane.showMessageDialog(this, "Race is invalid.");
			invalidRace = true;
		}
		else
			invalidRace = false;
		
		DefaultComboBoxModel tModel = new DefaultComboBoxModel();
		tModel.removeAllElements();
		for(Alignment al : Alignment.values())
			if(selRace.canBeAligned(al))
				tModel.addElement(al);
		jcAlignment.setModel(tModel);
		
		updateGuilds();
		updateStatPoints(selRace);
	}
	
	/**
	 * Updates the appearance of the guilds list.
	 */
	private void updateGuilds()
	{
		String fontName = jlGuilds[0].getFont().getFontName();
		int fontSize = jlGuilds[0].getFont().getSize();
		
		for(byte i = 0; i < jlGuilds.length; i++)
		{
			Guild tGuild = dataBank.getGuild(jlGuilds[i].getText());
			
			if(tGuild.allowedRace(selRace) && tGuild.allowedAlignment((Alignment)jcAlignment.getSelectedItem()))
			{
				// This is an allowed race and alignment
				
				if(guildStatRequirements(tGuild))
					// Meets stat requirements. Bold.
					jlGuilds[i].setFont(new Font(fontName, Font.BOLD, fontSize));
				else 
					// Allowed, but not enough stats. Plain
					jlGuilds[i].setFont(new Font(fontName, Font.PLAIN, fontSize));
			}
			else
				// Not allowed at all, Italic.
				jlGuilds[i].setFont(new Font(fontName, Font.ITALIC, fontSize));
		}
	}
	
	/**
	 * Checks if the current values in the stats spinners are enough for this guild.
	 * @param tGuild
	 * @return false if any value isn't high enough.
	 */
	private boolean guildStatRequirements(Guild tGuild)
	{
		for(byte i = 0; i < jsStats.length; i++)
			if((Integer)jsmStats[i].getValue() < tGuild.getStatRequired(Stats.type(i)))
				return false;
		
		return true;
	}
	
	/**
	 * Updates stat points min/max and if need be, their current values.
	 * @param tRace
	 */
	private void  updateStatPoints(Race tRace)
	{
		updatingStats = true;
		int low;
		int high;
		for(Stats st : Stats.values())
		{
			low = tRace.getBaseStat(st, true);
			high = tRace.getBaseStat(st, false);
			
			if((Integer)jsmStats[st.value()].getValue() < low)
			{
				sPointsLeft -= (low - (Integer)jsmStats[st.value()].getValue());
				jsmStats[st.value()].setValue(low);
			}
			jsmStats[st.value()].setMinimum(low);
			
			if((Integer)jsmStats[st.value()].getValue() > high)
			{
				sPointsLeft += ((Integer)jsmStats[st.value()].getValue() - high);
				jsmStats[st.value()].setValue(high);
			}
			jsmStats[st.value()].setMaximum(high);
		}
		
		while(sPointsLeft < 0)
		{
			for(byte i = 0; i < jsStats.length; i++)
				if((Integer)jsmStats[i].getValue() > (Integer)jsmStats[i].getMinimum())
				{
					sPointsLeft++;
					jsmStats[i].setValue(jsmStats[i].getPreviousValue());
					break;
				}
		}
		updatingStats = false;
	}
	
	/**
	 * Calculates how many stat points have been used.
	 * @return
	 */
	private int getStatPointsLeft()
	{
		int left = TOTALSTATPOINTS;
		
		for(byte i = 0; i < jsStats.length; i++)
			left -= (Integer)jsStats[i].getValue();
		
		return left;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		if(e.getSource() == jcAlignment)
		{
			updateGuilds();
		}
		else if(e.getSource() == jcRace)
		{
			selRace = dataBank.getRace((String)jcRace.getSelectedItem());
			changeRace();
		}
		else if(e.getSource() == jbDone)
		{
			Player tPlayer = createPlayer();
			if(tPlayer != null)
			{
				parent.updatePlayer(tPlayer);
				frame.dispose();
			}
		}
		else if(e.getSource() == jbCancel)
		{
			frame.dispose();
		}
		else if(e.getSource() == jbRaceStats)
		{
			// TODO Need a race stats window
		}
		else if(e.getSource() == jbGuildStats)
		{
			// TODO Need a guild stats window
		}
		else if(e.getSource() == jbTutorial)
		{
			// TODO Need a tutorial
		}
	}

	public void stateChanged(ChangeEvent e)
	{
		if(updatingStats)
			return;
		
		for(byte i = 0; i < jsStats.length; i++)
			if(e.getSource() == jsStats[i])
			{
				sPointsLeft = getStatPointsLeft();
				
				if(sPointsLeft < 0)
				{
					jsmStats[i].setValue((Integer)jsmStats[i].getValue() - 1);
					sPointsLeft = getStatPointsLeft();
				}
				updateGuilds();
				jlStatsLeft.setText("Stats left: " + sPointsLeft);
			}
	}

}
