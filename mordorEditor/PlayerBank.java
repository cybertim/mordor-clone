package mordorEditor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import structures.LinkedList;

import mordorData.BankAccount;
import mordorData.Item;
import mordorData.ItemInstance;
import mordorData.DataBank;
import mordorHelpers.Util;

public class PlayerBank extends JPanel implements ActionListener
{
	private PlayerPanel parent;
	private BankAccount bankAccount;
	
	private JTextField tfGoldInBank;
	private PlayerItemList itemListPane;
	
	public static final byte MAXITEMSINBANK = 40;
	
	public PlayerBank(PlayerPanel nParent)
	{
		parent = nParent;
		
		setLayout(new BorderLayout());
		
		JPanel goldBar = new JPanel();
		tfGoldInBank = new JTextField(10);
		
		goldBar.add(new JLabel("Gold in bank account:"));
		goldBar.add(tfGoldInBank);
		
		itemListPane = new PlayerItemList(parent.dataBank, false, parent.currentPlayer, BankAccount.MAXITEMSINBANK);
		JScrollPane itemScrollPane = new JScrollPane(itemListPane);
		itemScrollPane.setBackground(nParent.getBackground());
		JPanel deBorderPane = new JPanel(); // BorderLayout ignores preferred size.
		deBorderPane.add(itemScrollPane);
		
		add(goldBar, BorderLayout.NORTH);
		add(deBorderPane, BorderLayout.CENTER);
	}
	
	public boolean updatePanel()
	{
		bankAccount = parent.currentPlayer.getBankAccount();
		tfGoldInBank.setText("" + bankAccount.getGold());
		itemListPane.setItemList(bankAccount.getItems(), parent.currentPlayer);
		return true;
	}
	
	public boolean updatePlayer(boolean alreadyValidated)
	{
		if(!alreadyValidated)
			if(!validateBankAccount())
				return false;
		
		bankAccount.setGold(Util.FITLONG(Long.parseLong(tfGoldInBank.getText().trim()), 0, Long.MAX_VALUE));
		itemListPane.updateItemList();
		bankAccount.setItemList(itemListPane.getItemList());
		
		return true;
	}
	
	public boolean validateBankAccount()
	{
		if(!MordorEditor.validityCheckTf(this, tfGoldInBank, 0, Long.MAX_VALUE, "invalid gold amount"))
			return false;
		
		// TODO check all items
		if(!itemListPane.checkItems())
			return false;
		
		return true;
	}
	
	/**
	 * Adjust for any changes that may have occurred in other panels.
	 */
	public void updateLists()
	{
		itemListPane.updateLists();
	}

	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		
	}

}
