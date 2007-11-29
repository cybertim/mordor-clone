package mordorGame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import structures.ListNode;

import mordorData.BankAccount;
import mordorData.DataBank;
import mordorData.ItemInstance;
import mordorData.Player;
import mordorHelpers.Util;

public class BankPane extends JPanel implements ActionListener
{
	private Mordor parent;
	private InformationPanel infoPane;
	private Player player;
	private DataBank dataBank;
	private SICPane sicPane;
	
	private JTextField tfDeposit, tfWithdraw;
	private JButton jbDepAll, jbWdAll, jbPartyPool, jbPartyDeposit, jbPartyPoolDeposit, jbInfo, jbExit;
	private JList liItemList;
	private DefaultListModel items;
	private JLabel jlAccountSummary;
	private JButton jbRemoveItem, jbAddItem;
	
	BankPane(Mordor nParent, InformationPanel nInfoPane, Player nPlayer, SICPane nSicPane, DataBank nDataBank)
	{
		parent = nParent;
		infoPane = nInfoPane;
		player = nPlayer;
		dataBank = nDataBank;
		sicPane = nSicPane;
		
		setLayout(new BorderLayout());
		
		JPanel tPane = new JPanel();
		JPanel cPane = new JPanel();
		JPanel bPane = new JPanel();
		
		// tPane holds the title text, acount line, and stats on what is in the account
		tPane.setLayout(new GridLayout(3, 1));
		tPane.add(new JLabel("Welcome to the Bank!"));
		tPane.add(new JLabel("Account status:"));
		jlAccountSummary = new JLabel();
		tPane.add(jlAccountSummary);
		
		jbExit = new JButton("Exit");
		jbExit.addActionListener(this);
		jbExit.setToolTipText("Exit the bank.");
		bPane.add(jbExit);

		JPanel lcPane = new JPanel();
		JPanel rcPane = new JPanel();
		lcPane.setLayout(new GridLayout(7,1));
		rcPane.setLayout(new BorderLayout());
		
		lcPane.add(new JLabel("Deposit"));
		JPanel depositPane = new JPanel();
		tfDeposit = new JTextField(10);
		tfDeposit.addActionListener(this);
		jbDepAll = new JButton("All");
		jbDepAll.setToolTipText("Deposit all gold on hand.");
		jbDepAll.addActionListener(this);
		depositPane.add(tfDeposit);
		depositPane.add(jbDepAll);
		lcPane.add(depositPane);
		
		lcPane.add(new JLabel("Withdraw"));
		JPanel withdrawPane = new JPanel();
		tfWithdraw = new JTextField(10);
		tfWithdraw.addActionListener(this);
		jbWdAll = new JButton("All");
		jbWdAll.setToolTipText("Withdraw all gold from bank.");
		jbWdAll.addActionListener(this);
		withdrawPane.add(tfWithdraw);
		withdrawPane.add(jbWdAll);
		lcPane.add(withdrawPane);
		
		lcPane.add(new JLabel("Party"));
		JPanel partyPane = new JPanel();
		jbPartyPool = new JButton("Party Pool");
		jbPartyDeposit = new JButton("Party Deposit");
		jbPartyPoolDeposit = new JButton("Party Pool & Deposit");
		jbPartyPool.setToolTipText("Pool the party's gold with this player.");
		jbPartyDeposit.setToolTipText("Deposit each members gold individually.");
		jbPartyPoolDeposit.setToolTipText("Pool party's gold and deposit in this account.");
		jbPartyPool.addActionListener(this);
		jbPartyDeposit.addActionListener(this);
		jbPartyPoolDeposit.addActionListener(this);
		partyPane.add(jbPartyPool);
		partyPane.add(jbPartyDeposit);
		lcPane.add(partyPane);
		lcPane.add(jbPartyPoolDeposit);
		
		// Item area.
		// Title
		items = new DefaultListModel();
		items.addElement(Util.NOSTRING);
		rcPane.add(new JLabel("Deposit/Withdraw Items"), BorderLayout.NORTH);
		// List
		liItemList = new JList(items);
		liItemList.setVisibleRowCount(6);
		liItemList.setPrototypeCellValue("000000000000000");
		rcPane.add(new JScrollPane(liItemList), BorderLayout.CENTER);
		// Buttons
		JPanel itemButtons = new JPanel();
		jbInfo = new JButton("Info");
		jbRemoveItem = new JButton("-");
		jbAddItem = new JButton("+");
		jbInfo.setToolTipText("Display item in information window.");
		jbRemoveItem.setToolTipText("Remove item.");
		jbAddItem.setToolTipText("Add an item.");
		jbInfo.addActionListener(this);
		jbRemoveItem.addActionListener(this);
		jbAddItem.addActionListener(this);
		itemButtons.add(jbInfo);
		itemButtons.add(jbRemoveItem);
		itemButtons.add(jbAddItem);
		rcPane.add(itemButtons, BorderLayout.SOUTH);
		
		cPane.setLayout(new BorderLayout());
		cPane.add(lcPane, BorderLayout.WEST);
		cPane.add(rcPane, BorderLayout.EAST);
		
		add(tPane, BorderLayout.NORTH);
		add(cPane, BorderLayout.CENTER);
		add(bPane, BorderLayout.SOUTH);
		
		updatePane();
	}
	
	public void updatePane()
	{
		// Can't really display any info if there is no account!
		if(player == null)
			return;
		
		BankAccount account = player.getBankAccount();
		
		// TODO Expand to handle parites.
		updateSummary(account);
		items.clear();
		ListNode<ItemInstance> iNode = account.getItems().getFirstNode();
		while(iNode != null)
		{
			items.addElement(iNode.getElement());
			iNode = iNode.getNext();
		}
	}
	
	private void updateSummary(BankAccount account)
	{
		String accSummary = "You have " + account.getGold() + " and " + (BankAccount.MAXITEMSINBANK - account.getItems().getSize()) + " free Bank slots available.";
		jlAccountSummary.setText(accSummary);
	}

	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		if(e.getSource() == jbExit)
		{
			parent.exitBank();
		}
		else if(e.getSource() == tfDeposit)
		{
			Util.validityCheckTf(this, tfDeposit, 0, player.getGoldOnHand(), null);
			long gold = Long.parseLong(tfDeposit.getText());
			player.changeGoldOnHand(-gold);
			player.getBankAccount().changeGold(gold);
			updateSummary(player.getBankAccount());
			infoPane.updatePanes(player);
		}
		else if(e.getSource() == jbDepAll)
		{
			player.getBankAccount().changeGold(player.getGoldOnHand());
			player.setGoldOnHand(0);
			updateSummary(player.getBankAccount());
			infoPane.updatePanes(player);
		}
		else if(e.getSource() == tfWithdraw)
		{
			Util.validityCheckTf(this, tfDeposit, 0, player.getBankAccount().getGold(), null);
			long gold = Long.parseLong(tfDeposit.getText());
			player.changeGoldOnHand(gold);
			player.getBankAccount().changeGold(-gold);
			updateSummary(player.getBankAccount());
			infoPane.updatePanes(player);
		}
		else if(e.getSource() == jbWdAll)
		{
			player.changeGoldOnHand(player.getBankAccount().getGold());
			player.getBankAccount().setGold(0);
			updateSummary(player.getBankAccount());
			infoPane.updatePanes(player);
		}
		else if(e.getSource() == jbPartyPool)
		{
			// TODO: Party commands
		}
		else if(e.getSource() == jbPartyDeposit)
		{
			
		}
		else if(e.getSource() == jbPartyPoolDeposit)
		{
			
		}
		else if(e.getSource() == jbInfo)
		{
			// If an item is selected
			if(liItemList.getSelectedValue() != null)
				infoPane.showItem(dataBank, (ItemInstance)liItemList.getSelectedValue());
		}
		else if(e.getSource() == jbRemoveItem)
		{
			// If an item is selected and the player can hold more items.
			if(liItemList.getSelectedValue() != null && player.getItemCount() < Player.MAXITEMSONHAND - 1)
			{
				ItemInstance tItem = (ItemInstance)liItemList.getSelectedValue();
				player.addItem(tItem);
				items.removeElement(tItem);
				player.getBankAccount().removeItem(tItem);
				sicPane.updateItems();
				updateSummary(player.getBankAccount());
			}
		}
		else if(e.getSource() == jbAddItem)
		{
			// Can the player put anymore items in her account?
			if(items.getSize() < BankAccount.MAXITEMSINBANK - 1)
			{
				// Get the item selected in SIC.
				ItemInstance tItem = sicPane.getItemSelected();
				// Is an item selected? And if so, is it unequipped?
				if(tItem == null || sicPane.isSelectedEquipped())
					return;
				
				items.addElement(tItem);
				player.getBankAccount().addItem(tItem);
				player.removeItem(tItem);
				sicPane.updateItems();
				updateSummary(player.getBankAccount());
			}
		}
	}
}
