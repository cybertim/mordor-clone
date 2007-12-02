package mordorGame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import structures.ListIter;
import structures.ListNode;

import mordorData.DataBank;
import mordorData.GuildReference;
import mordorData.Item;
import mordorData.ItemInstance;
import mordorData.ItemSpecials;
import mordorData.MonsterInstance;
import mordorEnums.BodyParts;
import mordorEnums.MonsterAbility;
import mordorEnums.Resistance;
import mordorEnums.Stats;
import mordorGame.ItemLabel.DTListener;
import mordorHelpers.Util;

public class MiscPane extends JPanel
{
	private static final byte LASTNOTHING = 0;
	private static final byte LASTITEM = 1;
	private static final byte LASTMONSTER = 2;
	
	public static final byte MONHEAD_STORE = 0;
	public static final byte MONHEAD_COMPANION = 1;
	private static final String[] MONHEAD_STRINGS = {"STORE", "COMPANION"};
	
	private byte lastPane; // What type of plane was shown last.
	private DataBank databank;
	
	MiscPane(DataBank theDB)
	{
		lastPane = LASTNOTHING;
		databank = theDB;
	}
	
	public void showNothing()
	{
		lastPane = LASTNOTHING;
		this.removeAll();
	}
	
	public void showItem(ItemInstance item)
	{
		if(item == null)
		{
			showNothing();
			return;
		}
		lastPane = LASTITEM;
		this.removeAll();
		
		// TODO This should consider ID Levels
		
		setLayout(new BorderLayout());
		JPanel nPane = new JPanel();
		JPanel sPane = new JPanel();
		sPane.setLayout(new BorderLayout());
		// TODO: Put these first 3 lines as Table
		String nText = "<HTML><FONT SIZE=2><B>Item  " + item.toString();
		nText += "<BR>Att/Def  " + item.getItem().getAttackModifier() + "/" + item.getItem().getDefenseModifier() + " [" + item.getItem().getDamageModifier() + "]";
		nText += "<BR>Class: " + item.getItem().getItemType().toString();
		nText += "<BR><BR><TABLE CELLPADDING=0 CELLSPACING=1><TR><TD> </TD>";
		for(Stats s : Stats.values())
			nText += "<TD> " + s.toString().substring(0, 3) + "</TD>";
		nText += "</TR><TR><TD>Req</TD>";
		for(Stats s : Stats.values())
			nText += "<TD> " + item.getItem().getStatRequirement(s) + "</TD>";
		nText += "</TR><TR><TD>Mod</TD>";
		for(Stats s : Stats.values())
			nText += "<TD> " + item.getItem().getStatAdjustment(s) + "</TD>";
		nText += "</TR></TABLE></HTML>";
		
		String swText = "<HTML><FONT SIZE=2>";
		int count = 0;
		if(item.getItem().getItemType().getEquippingPart() == BodyParts.Weapon)
		{
			swText += (item.getItem().isTwoHanded()) ? "Two-handed weapon, <BR>" : "One-handed weapon, <BR>";
			swText += item.getItem().getSwings() + " swing";
			swText += (item.getItem().getSwings() > 1) ? "s<BR>" : "<BR>";
			count++;
		}
		// Is cursed.
		if(item.isCursed())
		{
			swText += "<I>Cursed</I><BR>";
			count++;
		}
		
		// Item specials.
		for(int i = 0; i < Item.ITEMSPECIAL_MAX && count < 6; i++)
		{
			ItemSpecials t = item.getItem().getSpecials()[i];
			switch(t.getType())
			{
			case ItemSpecials.ITEMSPECIAL_RESISTANCE:
				swText += "<BR>";
				swText += (t.getResistanceAmount() > 0) ? "Adds " : "Removes ";
				swText += t.getResistanceType().toString() + " resistance.";
				count++;
				break;
			case ItemSpecials.ITEMSPECIAL_STATE:
				swText += "<BR>";
				swText += (t.isStateOn()) ? "Adds " : "Removes ";
				swText += t.getState().toString();
				count++;
				break;
			case ItemSpecials.ITEMSPECIAL_SPELL:
				swText += "<BR>Cast " + t.getSpell(databank).getSpell().getName() + "(" + item.getChargesLeft() + ")";
				count++;
				break;
			case ItemSpecials.ITEMSPECIAL_OTHER:
				swText += "<BR>" + ItemSpecials.ITEMSPECIAL_OTHERNAMES[t.getOtherType()];
				count++;
				break;
			}
		}
		if(count >= 6)
			swText += "...";
		swText += "<BR><BR><B>ID Lev:</B><BR>" + item.getIDLevel().toString() + "</FONT></HTML>";
		
		String seText = "<HTML><FONT SIZE=2><B>Allowed Guilds</B></FONT><BR><TABLE CELLSPACING=0 CELLPADDING=0>";
		//ListNode<GuildReference> gNode = item.getItem().getGuilds().getFirstNode();
		ListIter<GuildReference> gNode = item.getItem().getGuilds().getIterator();
		count = 0;
		while(gNode.next() && count < 8)
		{
			seText += "<TR><TD>" + gNode.element().getGuild().getName() + " </TD>";
			seText += "<TD>" + gNode.element().getLevel() + "</TD></TR>";
			count++;
		}
		
		seText += "</TABLE>";
		if(count >= 8)
			seText += "...";
		
		seText += "</HTML>";
		
		
		nPane.add(new JLabel(nText));
		sPane.add(new JLabel(swText), BorderLayout.WEST);
		sPane.add(new JLabel(seText), BorderLayout.EAST);
		
		add(nPane, BorderLayout.NORTH);
		add(sPane, BorderLayout.CENTER);
		
		this.revalidate();
	}

	public void showMonster(DataBank dataBank, MonsterInstance monster, byte from)
	{
		if(monster == null)
		{
			showNothing();
			return;
		}
		
		lastPane = LASTMONSTER;
		byte nFrom = Util.FITBYTE(from, 0, 1);
		this.removeAll();
		
		setLayout(new BorderLayout());
		JPanel nPane = new JPanel();
		JPanel sPane = new JPanel();
		nPane.setLayout(new BorderLayout());
		sPane.setLayout(new BorderLayout());
		
		// TODO: The id level should affect how much is displayed.
		// What does each level give?
		
		String northText = "<HTML><FONT SIZE=2><B>" + MONHEAD_STRINGS[nFrom] + ": " + monster.getMonster().getName() + "</B><BR><BR>";
		northText += "<TABLE CELLPADDING=0 CELLSPACING=0><TR><TD><B>Size:</B></TD><TD>" +  monster.getMonster().getSize().toString() + "</TD></TR>";
		northText += "<TR><TD><B>Type:</B></TD><TD>" + monster.getMonster().getMonsterClass().toString() + "</TD></TR>";
		northText += "<TR><TD><B>Align:</B></TD><TD>" + monster.getMonster().getAlignment().toString() + "</TD></TR>";
		northText += "<TR><TD><B>A/D:</B></TD><TD>" + monster.getMonster().getAttack() + "/" + monster.getMonster().getDefense() + "</TD></TR>";
		northText += "<TR><TD><B>Avg. Hits:</B></TD><TD>" + monster.getMonster().getAvgHits() + "</TD></TR></TABLE></FONT></HTML>";
		
		nPane.add(new JLabel(northText), BorderLayout.WEST);
		nPane.add(new JLabel(new ImageIcon(dataBank.getImages().getMonsterImage(monster.getMonster().getMonsterImageID()).getScaledInstance(50, 50, 0))), BorderLayout.EAST);
		
		String swText = "<HTML><FONT SIZE=2><B>Stats</B><BR>";
		swText += monster.getMonster().getStat(Stats.Strength) + "/0/0/" + monster.getMonster().getStat(Stats.Constitution) + "/0/" + monster.getMonster().getStat(Stats.Dexterity) + "<BR><BR>";
		swText += "<B>ID Lev:<B><BR>" + monster.getIDLevel().idString() + "</FONT></HTML>";
		
		String seText = "<HTML><FONT SIZE=2><B>Special</B>";
		int count = 0;
		for(Resistance re : Resistance.values())
			if(monster.getMonster().getResistance(re) > 0 && count < 9)
			{
				seText += "<BR>Is " + re.toString() + " resistant.";
				count++;
			}
		for(MonsterAbility ma : MonsterAbility.values())
			if(monster.getMonster().hasAbility(ma) && count < 9)
			{
				seText += "<BR>" + ma.toString();
				count++;
			}
		if(count >= 9)
			seText += "<BR>+";
		seText += "</FONT></HTML>";
		
		sPane.add(new JLabel(swText), BorderLayout.WEST);
		sPane.add(new JLabel(seText), BorderLayout.EAST);
		
		add(nPane, BorderLayout.NORTH);
		add(sPane, BorderLayout.CENTER);
	}
	
	public void updatePanel()
	{
		// Does nothing.
	}
}
