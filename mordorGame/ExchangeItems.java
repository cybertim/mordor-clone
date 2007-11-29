package mordorGame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import mordorData.DataBank;
import mordorData.ItemInstance;
import mordorData.Player;
import mordorHelpers.Util;

public class ExchangeItems extends JPanel implements ActionListener
{
	private JComboBox playersRight, playersLeft;
	private JList itemsRight, itemsLeft;
	private JButton jbMoveRight, jbMoveLeft, jbDone;
	private DefaultListModel itemsListRight, itemsListLeft;
	private JTextField tfRightGold, tfLeftGold, tfGiveGold;
	
	private DataBank dataBank;
	private Player rightPlayer, leftPlayer;
	private JDialog parent;
	
	ExchangeItems(JDialog nParent, DataBank nDataBank)
	{
		dataBank = nDataBank;
		parent = nParent;
		if(dataBank.getPlayers().getSize() < 2)
		{
			add(new JLabel("Not enough players."));
			return;
		}
		
		playersRight = new JComboBox(dataBank.getPlayerNames());
		playersLeft = new JComboBox(dataBank.getPlayerNames());
		
		playersRight.addActionListener(this);
		playersLeft.addActionListener(this);
		
		playersRight.setToolTipText("Choose a player.");
		playersLeft.setToolTipText("Choose a player.");
		
		itemsListRight = new DefaultListModel();
		itemsListLeft = new DefaultListModel();
		
		itemsListRight.addElement(Util.NOSTRING);
		itemsListLeft.addElement(Util.NOSTRING);
		
		itemsRight = new JList(itemsListRight);
		itemsLeft = new JList(itemsListLeft);
		
		jbMoveRight = new JButton("->");
		jbMoveLeft = new JButton("<-");
		jbDone = new JButton("Done");
		
		jbMoveRight.addActionListener(this);
		jbMoveLeft.addActionListener(this);
		jbDone.addActionListener(this);
		
		jbMoveRight.setToolTipText("Move selected items on left and gold in give field to the right.");
		jbMoveLeft.setToolTipText("Move selected items on right and gold in give field to the left.");
		jbDone.setToolTipText("Exit exchange.");
		
		tfRightGold = new JTextField(10);
		tfLeftGold = new JTextField(10);
		tfGiveGold = new JTextField(6);
		
		tfRightGold.setToolTipText("Gold");
		tfLeftGold.setToolTipText("Gold");
		tfGiveGold.setToolTipText("Gold to give.");
		tfGiveGold.setText("0");
		
		JPanel rightPanel = new JPanel();
		JPanel leftPanel = new JPanel();
		JPanel centrePanel = new JPanel();
		
		rightPanel.setLayout(new BorderLayout());
		JPanel tRightPanel = new JPanel();
		tRightPanel.setLayout(new GridLayout(3, 1));
		tRightPanel.add(playersRight);
		tRightPanel.add(new JLabel("Gold:"));
		tRightPanel.add(tfRightGold);
		rightPanel.add(tRightPanel, BorderLayout.NORTH);
		rightPanel.add(new JScrollPane(itemsRight), BorderLayout.CENTER);
		
		leftPanel.setLayout(new BorderLayout());
		JPanel tLeftPanel = new JPanel();
		tLeftPanel.setLayout(new GridLayout(3, 1));
		tLeftPanel.add(playersLeft);
		tLeftPanel.add(new JLabel("Gold:"));
		tLeftPanel.add(tfLeftGold);
		leftPanel.add(tLeftPanel, BorderLayout.NORTH);
		leftPanel.add(new JScrollPane(itemsLeft), BorderLayout.CENTER);
		
		centrePanel.setLayout(new GridLayout(5, 1));
		centrePanel.add(new JLabel("Give\nGold"));
		centrePanel.add(tfGiveGold);
		centrePanel.add(jbMoveRight);
		centrePanel.add(jbMoveLeft);
		centrePanel.add(jbDone);
		
		add(leftPanel);
		add(centrePanel);
		add(rightPanel);
	}
	
	private void updatePlayers()
	{
		rightPlayer = dataBank.getPlayer((String)playersRight.getSelectedItem());
		leftPlayer = dataBank.getPlayer((String)playersLeft.getSelectedItem());
		
		updateItemLists();
		
		tfRightGold.setText("" + rightPlayer.getGoldOnHand());
		tfLeftGold.setText("" + leftPlayer.getGoldOnHand());
		
		jbMoveRight.setToolTipText("Move selected items and gold in give field from " + leftPlayer.getName() + " to " + rightPlayer.getName() + ".");
		jbMoveLeft.setToolTipText("Move selected items and gold in give field from " + rightPlayer.getName() + " to " + leftPlayer.getName() + ".");
		
		tfRightGold.setToolTipText(rightPlayer.getName() + "'s gold");
		tfLeftGold.setToolTipText(leftPlayer.getName() + "'s gold");
	}
	
	private void updateItemLists()
	{
		ItemInstance[] items = rightPlayer.getItems();
		
		itemsListRight.removeAllElements();
		itemsListLeft.removeAllElements();
		
		if(items != null)
			for(byte i = 0; i < items.length; i++)
				itemsListRight.addElement(items[i]);
		
		items = leftPlayer.getItems();
		if(items != null)
			for(byte i = 0; i < items.length; i++)
				itemsListLeft.addElement(items[i]);
	}
	
	private void moveStuff(boolean right)
	{
		long maxGold = (!right) ? rightPlayer.getGoldOnHand() : leftPlayer.getGoldOnHand();
		if(!Util.validityCheckTf(this, tfGiveGold, 0, maxGold, "give gold."))
			return;
		
		long giveGold = Long.parseLong(tfGiveGold.getText().trim());
		
		if(right)
		{
			leftPlayer.changeGoldOnHand(-giveGold);
			rightPlayer.changeGoldOnHand(giveGold);
		}
		else
		{
			rightPlayer.changeGoldOnHand(-giveGold);
			leftPlayer.changeGoldOnHand(giveGold);
		}
		
		tfRightGold.setText("" + rightPlayer.getGoldOnHand());
		tfLeftGold.setText("" + leftPlayer.getGoldOnHand());
		tfGiveGold.setText("0");
		
		// Now the items.
		if(right)
		{
			Object[] items = itemsLeft.getSelectedValues();
			for(byte i = 0; i < items.length; i++)
			{
				if(!leftPlayer.isItemEquipped((ItemInstance)items[i]))
				{
					leftPlayer.removeItem((ItemInstance)items[i]);
					rightPlayer.addItem((ItemInstance)items[i]);
				}
			}
		}
		else
		{
			Object[] items = itemsRight.getSelectedValues();
			for(byte i = 0; i < items.length; i++)
			{
				if(!rightPlayer.isItemEquipped((ItemInstance)items[i]))
				{
					rightPlayer.removeItem((ItemInstance)items[i]);
					leftPlayer.addItem((ItemInstance)items[i]);
				}
			}
		}
		
		updateItemLists();
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == jbDone)
			parent.dispose();
		else if(e.getSource() == jbMoveRight)
			moveStuff(true);
		else if(e.getSource() == jbMoveLeft)
			moveStuff(false);
		else if(e.getSource() == playersRight)
			updatePlayers();
		else if(e.getSource() == playersLeft)
			updatePlayers();
	}

}
